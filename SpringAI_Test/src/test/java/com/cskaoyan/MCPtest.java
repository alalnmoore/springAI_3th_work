package com.cskaoyan;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MCPtest {

    @Autowired
    @Qualifier("mcpChatClient")
    ChatClient chatClient;

    @Test
    public void testToolCallbackProvider() {

        String content = chatClient.prompt()
                .user("请帮我查一下 IP地址117.253.50.2对应的城市是哪个")
                .call()
                .content();

        System.out.println("content = " + content);


    }
}
