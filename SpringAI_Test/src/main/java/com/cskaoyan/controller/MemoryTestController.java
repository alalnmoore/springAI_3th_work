package com.cskaoyan.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemoryTestController {
    @Autowired
    @Qualifier("CommonchatClient")
    private ChatClient chatClient;
    @Autowired
    private MessageChatMemoryAdvisor messageChatMemoryAdvisor;
    @Autowired
    private ChatMemory chatMemory;

    @GetMapping("/memory/test")
    public String test() {
        String content1 = chatClient.prompt()
                .user("你好，我是一名程序员，喜欢写C#")
                .advisors(messageChatMemoryAdvisor)
                .advisors(advisor -> advisor.param(chatMemory.CONVERSATION_ID,"001"))
                .call()
                .content();
        System.out.println("content1 = " + content1);
        System.out.println("======================================");

        String content2 = chatClient.prompt()
                .user("我是谁，我的爱好是啥？？")
                .advisors(messageChatMemoryAdvisor)
                .advisors(advisor -> advisor.param(chatMemory.CONVERSATION_ID,"001"))
                .call()
                .content();
        System.out.println("content2 = " + content2);
        return "LOVE";
    }
}
