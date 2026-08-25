package com.cskaoyan;

import com.cskaoyan.tool.DateTimeTools;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DemoTest {

    @Autowired
    @Qualifier("CommonchatClient")
    ChatClient chatClient;

    @Autowired
    DateTimeTools dateTimeTools;

    @Test
    public void testChatClient() {

        String content = chatClient.prompt()
                .user("你好呀？？")
                .call()
                .content();
        System.out.println("content = " + content);
    }

    @Test
    public void testChatClient2() {

        String content = chatClient.prompt()
                .user("当前的时间是多少？？？")
                .tools(dateTimeTools)
                .call()
                .content();
        System.out.println("content = " + content);
    }
}
