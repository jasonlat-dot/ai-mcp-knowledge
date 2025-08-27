package com.jasonlat.knowledeg.config;

import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.DefaultChatClientBuilder;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingModel;
import org.springframework.ai.zhipuai.ZhiPuAiImageModel;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.ai.zhipuai.api.ZhiPuAiImageApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class GLMConfig {

    private static final Logger log = LoggerFactory.getLogger(GLMConfig.class);
    @Value("${spring.ai.zhipuai.chat.api-key}")
    private String apiKey;

    @Value("${spring.ai.zhipuai.chat.base-url}")
    private String chatBaseUl;

    @Value("${spring.ai.zhipuai.chat.options.model}")
    private String glmChatModel;

    @Value("${spring.ai.zhipuai.chat.options.temperature}")
    private double temperature;

    @Value("${spring.ai.zhipuai.vector-table-name}")
    private String vectorTableName;




    @Bean
    public ZhiPuAiApi zhiPuAiApi() {
        log.info("zhiPuAiApi chat baseUrl: {}, apiKey: {}", chatBaseUl, apiKey);
        return new ZhiPuAiApi(chatBaseUl, apiKey);
    }

    @Bean
    public ZhiPuAiImageApi zhiPuAiImageApi() {
        log.info("zhiPuAiImageApi  apiKey: {}", apiKey);
        return new ZhiPuAiImageApi(apiKey);
    }

    @Bean
    public ZhiPuAiChatModel zhiPuAiChatModel(ZhiPuAiApi zhiPuAiApi) {
        int maxTokens = 2048;
        if (glmChatModel.toUpperCase().contains("GLM-4-Flash".toUpperCase()) || glmChatModel.toUpperCase().contains("GLM-4.5-Flash".toUpperCase())) {
            maxTokens = 1024 * 16;
        }
        return new ZhiPuAiChatModel(zhiPuAiApi, ZhiPuAiChatOptions.builder()
               .model(glmChatModel)
                .maxTokens(maxTokens)
                .temperature(temperature)
                // 可以按需配置其他参数，如温度等
               .build());
    }

    @Bean(name = "glmChatClient")
    public ChatClient glmChatClient(ZhiPuAiChatModel zhiPuAiChatModel, @Qualifier("syncMcpToolCallbackProvider") SyncMcpToolCallbackProvider tools, ChatMemory chatMemory) {

        DefaultChatClientBuilder defaultChatClientBuilder = new DefaultChatClientBuilder(zhiPuAiChatModel, ObservationRegistry.NOOP, null);
        return defaultChatClientBuilder
               .defaultTools(tools)
               .defaultOptions(ZhiPuAiChatOptions.builder()
                        .model(glmChatModel)
                        .build())
//                .defaultAdvisors(new PromptChatMemoryAdvisor(chatMemory))
               .build();
    }



    @Bean
    public ZhiPuAiEmbeddingModel zhiPuAiEmbeddingModel(ZhiPuAiApi zhiPuAiApi) {
        return new ZhiPuAiEmbeddingModel(zhiPuAiApi);
    }

    @Bean
    public ZhiPuAiImageModel zhiPuAiImageModel(ZhiPuAiImageApi zhiPuAiImageApi) {
        return new ZhiPuAiImageModel(zhiPuAiImageApi);
    }


    @Bean("glmSimpleVectorStore")
    public SimpleVectorStore vectorStore(ZhiPuAiApi zhiPuAiApi) {
        ZhiPuAiEmbeddingModel embeddingModel = new ZhiPuAiEmbeddingModel(zhiPuAiApi);
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    /**
     * CREATE EXTENSION IF NOT EXISTS vector;
     * -- 删除旧的表（如果存在）
     * DROP TABLE IF EXISTS public.vector_store_zhipuai;
     * -- 创建新的表，使用UUID作为主键
     * CREATE TABLE public.vector_store_zhipuai (
     *     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
     *     content TEXT NOT NULL,
     *     metadata JSONB,
     *     embedding VECTOR(1536)
     * );
     * SELECT * FROM vector_store_zhipuai
     */
    @Bean("glmPgVectorStore")
    public PgVectorStore pgVectorStore(ZhiPuAiApi zhiPuAiApi, JdbcTemplate jdbcTemplate) {
        ZhiPuAiEmbeddingModel embeddingModel = new ZhiPuAiEmbeddingModel(zhiPuAiApi);
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .vectorTableName(vectorTableName)
                .build();
    }

}