package com.cskaoyan;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SpringAiTest {

    @Autowired
    private ChatClient chatClient;

    @Test
    public void testChatClient() {
        String content = chatClient.prompt()
                .system("你是一名温柔的老师")
                .user("你好")
                .call()
                .content();
        System.out.println(content);

    }

}
