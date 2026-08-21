package com.cskaoyan;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OllamaTest {
    @Autowired
    private ChatClient chatClient;
    @Autowired
    private ChatMemory chatMemory;
    @Autowired
    private MessageChatMemoryAdvisor messageChatMemoryAdvisor;

    @Test

    //这个是进行记忆的测试！！！！
    public void testChat() {
        String content1 = chatClient.prompt()
                .user("你好你是什么模型？？？？我的名字叫萝你叫什么？？？")
                .advisors(messageChatMemoryAdvisor)
                .advisors(advisor -> advisor.param(chatMemory.CONVERSATION_ID,"001"))
                .call()
                .content();

        System.out.println("content = " + content1);
        System.out.println("==========================");
        String content2 = chatClient.prompt()
                .user("我叫什么名字吗？？")
                .advisors(messageChatMemoryAdvisor)
                .advisors(advisor -> advisor.param(chatMemory.CONVERSATION_ID,"001"))
                .call()
                .content();
        System.out.println("content = " + content2);


    }

}
