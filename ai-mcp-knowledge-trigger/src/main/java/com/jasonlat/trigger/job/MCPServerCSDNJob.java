package com.jasonlat.trigger.job;

import com.jasonlat.utils.FileUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MCPServerCSDNJob {

    @Resource(name = "glmChatClient")
    private ChatClient chatClient;

    @Scheduled(cron = "0 0 * * * ?")
    public void exec() {
        // 检查当前时间是否在允许执行的时间范围内（8点到23点之间）
        int currentHour = java.time.LocalDateTime.now().getHour();
        if (currentHour >= 23 || currentHour < 8) {
            log.info("当前时间 {}点 不在任务执行时间范围内，跳过执行", currentHour);
            return;
        }
        try {
            String userInput = FileUtils.readFromClasspath("/prompt/Java面试文章生成提示词.md");

            log.info("执行结果:{} {}", userInput, chatClient.prompt(userInput).call().content());
        } catch (Exception e) {
            log.error("定时任务，回调通知 发布文章到CSDN 任务失败", e);
        }
    }

}
