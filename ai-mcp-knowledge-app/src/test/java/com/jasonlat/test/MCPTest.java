package com.jasonlat.test;

import com.alibaba.fastjson.JSON;
import com.jasonlat.knowledeg.Application;
import com.jasonlat.utils.FileUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class MCPTest {
    /**
     * 需要好的模型
     */
    @Resource(name = "openaiChatClient")
//    @Resource(name = "glmChatClient")
//    @Resource(name = "ollamaChatClient")
    private ChatClient chatClient;

    @Test
    public void test_tool() {
        String userInput = "有哪些工具可以使用";

        System.out.println("\n>>> QUESTION: " + userInput);
        System.out.println("\n>>> ASSISTANT: " + chatClient.prompt(userInput).call().content());
    }

    @Test
    public void test_workflow() {
//        String userInput = "C:\\Users\\Administrator\\Desktop 文件夹下，创建 电脑.txt";
        String userInput = "在 C:\\Users\\Administrator\\Desktop 文件夹下创建 computer.txt， 然后调用工具获取获取本机配置，将获取到的本机配置信息写入 computer.txt， 其中queryComputerConfig的参数是：获取获取本机配置。" +
                "需要你先后调用两个工具, 本机配置信息请做好相关排版，不要直接写入。";


        System.out.println("\n>>> QUESTION: " + userInput);
        System.out.println("\n>>> ASSISTANT: " + chatClient.prompt(userInput).call().content());
    }


    @Test
    public void test_workflow_2() {
//        String userInput = "C:\\Users\\Administrator\\Desktop 文件夹下，创建 电脑.txt";
        String userInput = "C:\\Users\\Administrator\\Desktop\\电脑.txt已经存在，直接将文本：我爱我的爱人 写入 电脑.txt， queryComputerConfig的参数是：获取获取本机配置";

        System.out.println("\n>>> QUESTION: " + userInput);
        System.out.println("\n>>> ASSISTANT: " + chatClient.prompt(userInput).call().content());
    }

    @Test
    public void test_workflow_computer() {
        String userInput = "获取电脑配置, 参数是：获取电脑配置，等待工具返回的结果，然乎并将结果输出给我";


        System.out.println("\n>>> QUESTION: " + userInput);
        System.out.println("\n>>> ASSISTANT: " + chatClient.prompt(userInput).call().content());
    }

    @Test
    public void test_saveArticle() throws InterruptedException, IOException {
        // 读取MD文件内容作为提示词，这里假设文件路径为项目resources/prompt目录下，可根据实际调整
        String userInput = FileUtils.readFromClasspath("/prompt/Java面试文章生成提示词.md");
//
//        String userInput = """
//                    我需要你帮我生成一篇文章，要求如下；
//
//                    1. 场景为互联网大厂java求职者面试
//                    2. 提问的技术栈如下；
//
//                        核心语言与平台: Java SE (8/11/17), Jakarta EE (Java EE), JVM
//                        构建工具: Maven, Gradle, Ant
//                        Web框架: Spring Boot, Spring MVC, Spring WebFlux, Jakarta EE, Micronaut, Quarkus, Play Framework, Struts (Legacy)
//                        数据库与ORM: Hibernate, MyBatis, JPA, Spring Data JDBC, HikariCP, C3P0, Flyway, Liquibase
//                        测试框架: JUnit 5, TestNG, Mockito, PowerMock, AssertJ, Selenium, Cucumber
//                        微服务与云原生: Spring Cloud, Netflix OSS (Eureka, Zuul), Consul, gRPC, Apache Thrift, Kubernetes Client, OpenFeign, Resilience4j
//                        安全框架: Spring Security, Apache Shiro, JWT, OAuth2, Keycloak, Bouncy Castle
//                        消息队列: Kafka, RabbitMQ, ActiveMQ, JMS, Apache Pulsar, Redis Pub/Sub
//                        缓存技术: Redis, Ehcache, Caffeine, Hazelcast, Memcached, Spring Cache
//                        日志框架: Log4j2, Logback, SLF4J, Tinylog
//                        监控与运维: Prometheus, Grafana, Micrometer, ELK Stack, New Relic, Jaeger, Zipkin
//                        模板引擎: Thymeleaf, FreeMarker, Velocity, JSP/JSTL
//                        REST与API工具: Swagger/OpenAPI, Spring HATEOAS, Jersey, RESTEasy, Retrofit
//                        序列化: Jackson, Gson, Protobuf, Avro
//                        CI/CD工具: Jenkins, GitLab CI, GitHub Actions, Docker, Kubernetes
//                        大数据处理: Hadoop, Spark, Flink, Cassandra, Elasticsearch
//                        版本控制: Git, SVN
//                        工具库: Apache Commons, Guava, Lombok, MapStruct, JSch, POI
//                        其他: JUnit Pioneer, Dubbo, R2DBC, WebSocket
//                    3. 提问的场景方案可包括但不限于；音视频场景,内容社区与UGC,AIGC,游戏与虚拟互动,电商场景,本地生活服务,共享经济,支付与金融服务,互联网医疗,健康管理,医疗供应链,企业协同与SaaS,产业互联网,大数据与AI服务,在线教育,求职招聘,智慧物流,供应链金融,智慧城市,公共服务数字化,物联网应用,Web3.0与区块链,安全与风控,广告与营销,能源与环保。
//                    4. 按照故事场景，以严肃的面试官和搞笑的水货程序员谢飞机进行提问，谢飞机对简单问题可以回答出来，回答好了面试官还会夸赞和引导。复杂问题含糊其辞，回答的不清晰。
//                    5. 每次进行3轮提问，每轮可以有3-5个问题。这些问题要有技术业务场景上的衔接性，循序渐进引导提问。最后是面试官让程序员回家等通知类似的话术。
//                    6. 提问后把问题的答案详细的，写到文章最后，讲述出业务场景和技术点，让小白可以学习下来。
//
//                    根据以上内容，不要阐述其他信息，请直接提供；文章标题（需要含带技术点）、文章内容、文章标签（多个用英文逗号隔开）、文章简述（100字）
//
//                根据以上内容，不要阐述其他信息，请直接提供；文章标题（需要含带技术点）、文章内容、文章标签（多个用英文逗号隔开）、文章简述（100字）
//                                                  关键指令：
//                                                      1. 生成文章后，必须调用名为「saveCsdnArticle」的工具发布到 CSDN；
//                                                      2. 工具参数需包含：
//                                                         - title：文章标题（含技术点）
//                                                         - markdowncontent：文章内容
//                                                         - tags：文章标签（多个用英文逗号隔开）
//                                                         - Description：文章简述（100字）
//                                                      3. 仅需返回工具调用的请求结构，无需额外文本解释。
//                                                  将以上内容发布文章到CSDN
//                    """;
        System.out.println("\n>>> QUESTION: " + userInput);
        System.out.println("\n>>> ASSISTANT: " + chatClient.prompt(userInput).call().content());


//        CountDownLatch countDownLatch = new CountDownLatch(1);
//
//        Flux<ChatResponse> chatResponseFlux = chatClient.prompt(userInput).stream().chatResponse();
//
//        chatResponseFlux.subscribe(
//                chatResponse -> {
//                    AssistantMessage output = chatResponse.getResult().getOutput();
//                    log.info("测试结果(stream): {}", JSON.toJSONString(output));
//                },
//                Throwable::printStackTrace,
//                () -> {
//                    countDownLatch.countDown();
//                    log.info("测试结果(stream): done!");
//                }
//        );
//
//        countDownLatch.await();
    }

    @Test
    public void test_weixinNotice_chatMemory() throws IOException {

        String userInput = FileUtils.readFromClasspath("/prompt/Java面试文章生成提示词.md");
        System.out.println("\n>>> ASSISTANT: " + chatClient
                .prompt(userInput)
                .advisors(advisor -> advisor
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, "1001")
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .call()
                .content());

        System.out.println("\n>>> ASSISTANT: " + chatClient
                .prompt("""
                         之后进行，微信公众号消息通知，平台：CSDN、主题：为文章标题、描述：为文章简述、跳转地址：从发布文章到CSDN获取 url
                        """)
                .advisors(advisor -> advisor
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, "1001")
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .call()
                .content());
    }

}