package com.cskaoyan;

import com.cskaoyan.tool.KemonomimiTools;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ToolTest {

    @Autowired
    @Qualifier("CommonchatClient")
    ChatClient chatClient;

    @Autowired
    KemonomimiTools kemonomimiTools;

    @Test
    public void testToolCallbackProvider() {
        String content = chatClient.prompt()
                .user("请帮我查一下兔耳娘的性格特点，以及代表人物，以及代表人物的特点")
                .tools(kemonomimiTools)
                .call()
                .content();
        System.out.println("content = " + content);

    }
}
