package com.cskaoyan.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpClientConfig {




    /*
         ToolCallbackProvider  provider 获取到MCP Server工具列表
     */
    @Bean(name = "mcpChatClient")
    public ChatClient chatClient(ChatClient.Builder builder,
                                 ToolCallbackProvider provider) {

        ToolCallback[] toolCallbacks = provider.getToolCallbacks();

        return builder
                // 向ChatClient注册MCP Server的工具列表
                .defaultToolCallbacks(toolCallbacks)
                .build();

    }
}
