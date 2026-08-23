package com.cskaoyan;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MemoryTest {

    @Autowired
    @Qualifier("CommonchatClient")
    private ChatClient chatClient;

    @Test
    public void testChatClient() {
        String content = chatClient.prompt()
                .user("你好，我是一名程序员，喜欢写C#")
                .call()
                .content();
        System.out.println(content);

    }
}
