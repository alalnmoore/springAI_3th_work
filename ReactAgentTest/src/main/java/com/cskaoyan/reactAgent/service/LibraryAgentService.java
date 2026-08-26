package com.cskaoyan.reactAgent.service;

import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LibraryAgentService {
    @Autowired
    private ReactAgent libraryReactAgent;

    /**
     * 存储每个会话的配置（包含 threadId）
     */
    private final Map<String, RunnableConfig> configCache = new ConcurrentHashMap<>();

    /**
     * 同步对话
     */
    public String chat(String threadId, String message) {
        try {
            // 获取或创建会话配置
            RunnableConfig config = configCache.computeIfAbsent(threadId, id ->
                    RunnableConfig.builder()
                            .threadId(id)
                            .build()
            );

            // 调用 Agent
            AssistantMessage response = libraryReactAgent.call(message, config);
            return response.getText();

        } catch (GraphRunnerException e) {
            return "抱歉，处理您的请求时出现了错误：" + e.getMessage();
        }
    }

    /**
     * 清除会话记忆
     */
    public void clearMemory(String threadId) {
        configCache.remove(threadId);
    }
}
