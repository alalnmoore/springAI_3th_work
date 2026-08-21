package com.cskaoyan;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DemoTest {

    @Autowired
    ChatClient chatClient;

    @Test
    public void testChatClient() {

        String content = chatClient.prompt()
                .user("你好呀？？")
                .call()
                .content();
        System.out.println("content = " + content);
    }
}
