package com.jasonlat.knowledeg.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.DefaultChatClientBuilder;
import org.springframework.ai.chat.client.observation.ChatClientObservationConvention;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class OllamaConfig {

    @Value("${spring.ai.ollama.vector-table-name}")
    private String vectorTableName;

    @Value("${spring.ai.ollama.embedding-model}")
    private String optionsModel;

    @Bean("ollamaSimpleVectorStore")
    public SimpleVectorStore vectorStore(OllamaApi ollamaApi) {
        OllamaEmbeddingModel embeddingModel = OllamaEmbeddingModel
                .builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(OllamaOptions.builder().model(optionsModel).build())
                .build();
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    /**
     * CREATE EXTENSION IF NOT EXISTS vector;
     * -- 删除旧的表（如果存在）
     * DROP TABLE IF EXISTS public.vector_store_ollama_deepseek;
     * -- 创建新的表，使用UUID作为主键
     * CREATE TABLE public.vector_store_ollama_deepseek (
     *     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
     *     content TEXT NOT NULL,
     *     metadata JSONB,
     *     embedding VECTOR(768)
     * );
     *
     * SELECT * FROM vector_store_ollama_deepseek
     */
    @Bean("ollamaPgVectorStore")
    public PgVectorStore pgVectorStore(OllamaApi ollamaApi, JdbcTemplate jdbcTemplate) {
        OllamaEmbeddingModel embeddingModel = OllamaEmbeddingModel
                .builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(OllamaOptions.builder().model(optionsModel).build())
                .build();
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .vectorTableName(vectorTableName)
                .build();
    }

    @Bean("ollamaChatClientBuilder")
    public ChatClient.Builder chatClientBuilder(OllamaChatModel ollamaChatModel) {
        return new DefaultChatClientBuilder(ollamaChatModel, ObservationRegistry.NOOP, (ChatClientObservationConvention) null);
    }

}
