package com.cskaoyan.reactAgent.config;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.cskaoyan.reactAgent.tool.LibraryTools;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReactAgentConfig {

    @Bean
    public ReactAgent libraryReactAgent(ChatModel chatModel, LibraryTools libraryTools) {
        return ReactAgent.builder()
                // 设置 Agent 名字
                .name("图书推荐助手")
                // 设置 ChatModel
                .model(chatModel)
                // 系统提示词：定义角色
                .systemPrompt("""
                        你是一个专业的图书推荐助手，专门为学习编程和技术的人员推荐合适的书籍。
                        
                        你的职责：
                        1. 理解用户的学习方向或兴趣（如 Spring Boot、微服务、JVM、Redis、MySQL 等）
                        2. 根据用户的兴趣，从图书库中筛选出最匹配的书籍
                        3. 给出清晰的推荐理由，解释为什么这本书适合用户
                        
                        推荐规则：
                        - 如果用户提到具体技术名称，优先推荐相关的书籍
                        - 如果用户没有明确方向，可以先询问用户感兴趣的技术领域
                        - 推荐时尽量给出 2-3 本书，让用户有选择空间
                        - 回答要亲切、专业，用清晰的格式输出
                        """)
                // 指令：定义具体行为要求
                .instruction("""
                        当用户询问图书推荐时：
                        1. 首先理解用户的学习方向或兴趣点
                        2. 使用 findAllBooks 工具查询所有图书
                        3. 根据用户的兴趣，从图书列表中筛选匹配的书籍
                        4. 整理推荐结果，用友好的方式回复用户
                        
                        注意：如果用户的问题与图书推荐无关，请礼貌地引导用户回到图书推荐话题。
                        """)
                // 注册工具
                .methodTools(libraryTools)
                // 设置记忆存储器（支持多轮对话）
                .saver(new MemorySaver())
                .build();
    }
}