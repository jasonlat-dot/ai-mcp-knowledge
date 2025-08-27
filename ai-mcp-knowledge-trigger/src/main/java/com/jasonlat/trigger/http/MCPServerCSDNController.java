package com.jasonlat.trigger.http;


import com.jasonlat.utils.FileUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@RestController
@RequestMapping("/scdn")
@RequiredArgsConstructor
@CrossOrigin("*")
public class MCPServerCSDNController {

    @Resource(name = "openaiChatClient")
    private ChatClient chatClient;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @PostConstruct
    public void init() {
        log.info("MCPServerCSDNController init");
    }

    @RequestMapping(value = "/publish", method = RequestMethod.GET)
    public String publish() {

        threadPoolExecutor.execute(() -> {
            String userInput;
            try {
                userInput = FileUtils.readFromClasspath("/prompt/Java面试文章生成提示词.md");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            log.info("执行结果:{} {}", userInput, chatClient.prompt(userInput).call().content());
        });

        return "success";
    }
}
