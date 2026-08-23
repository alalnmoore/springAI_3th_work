package com.cskaoyan.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringAIConfig {

    @Bean(name = "CommonchatClient")
    public ChatClient chatClient(ChatClient.Builder builder,MessageChatMemoryAdvisor messageChatMemoryAdvisor) {
        return builder
                // 这只默认的系统消息
                //.defaultSystem("你是一名{language}讲师，请使用{style}的方式回答问题。")
                // 输出模型交互日志信息(包含给模型发送的请求，以及响应)
                .defaultAdvisors(messageChatMemoryAdvisor)
                .build();
    }

    @Bean
    // 内存存储
    public ChatMemoryRepository chatMemoryRepository() {
        return new InMemoryChatMemoryRepository();
    }

    @Bean
    //管理内存存储
    public ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .build();
    }

    @Bean
    // 内存存储管理器
    public MessageChatMemoryAdvisor messageChatMemoryAdvisor(ChatMemory chatMemory) {
        return MessageChatMemoryAdvisor.builder(chatMemory)
                .build();
    }
}
