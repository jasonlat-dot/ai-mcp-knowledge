package com.jasonlat.test;

import com.jasonlat.knowledeg.Application;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class MCPTest {

    @Resource(name = "openAiChatClientBuilder")
    private ChatClient.Builder chatClientBuilder;

    @Autowired
    private ToolCallbackProvider tools;

    private final String model = "gpt-4.1-mini";
    @Test
    public void test_tool() {
        String userInput = "有哪些工具可以使用";
        var chatClient = chatClientBuilder
                .defaultTools(tools)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(model)
                        .build())
                .build();

        System.out.println("\n>>> QUESTION: " + userInput);
        System.out.println("\n>>> ASSISTANT: " + chatClient.prompt(userInput).call().content());
    }

    @Test
    public void test_workflow() {
//        String userInput = "C:\\Users\\Administrator\\Desktop 文件夹下，创建 电脑.txt";
        String userInput = "C:\\Users\\Administrator\\Desktop\\电脑.txt已经存在， 获取获取本机配置，将获取到的本机配置信息写入 电脑.txt， queryComputerConfig的参数是：获取获取本机配置。" +
                "需要你先后调用两个工具, 本机配置信息请做好相关排版，不要直接写入。";

        var chatClient = chatClientBuilder
                .defaultTools(tools)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(model)
                        .build())
                .build();

        System.out.println("\n>>> QUESTION: " + userInput);
        System.out.println("\n>>> ASSISTANT: " + chatClient.prompt(userInput).call().content());
    }


    @Test
    public void test_workflow_2() {
//        String userInput = "C:\\Users\\Administrator\\Desktop 文件夹下，创建 电脑.txt";
        String userInput = "C:\\Users\\Administrator\\Desktop\\电脑.txt已经存在，直接将文本：我爱我的爱人 写入 电脑.txt， queryComputerConfig的参数是：获取获取本机配置";

        var chatClient = chatClientBuilder
                .defaultTools(tools)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(model)
                        .build())
                .build();

        System.out.println("\n>>> QUESTION: " + userInput);
        System.out.println("\n>>> ASSISTANT: " + chatClient.prompt(userInput).call().content());
    }

    @Test
    public void test_workflow_computer() {
        String userInput = "获取电脑配置, 参数是：获取电脑配置，等待工具返回的结果，然乎并将结果输出给我";

        var chatClient = chatClientBuilder
                .defaultTools(tools)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(model)
                        .build())
                .build();

        System.out.println("\n>>> QUESTION: " + userInput);
        System.out.println("\n>>> ASSISTANT: " + chatClient.prompt(userInput).call().content());
    }


}