package com.cskaoyan.node;


import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;

import java.util.Map;

public class ReviewerNode implements NodeAction {
    private ReactAgent reactAgent;
    public ReviewerNode(ChatModel chatModel) {
        this.reactAgent = ReactAgent.builder()
                .name("reviewer_node")
                .model(chatModel)
                .description("专业文章评审 Agent")
                .instruction("""
                        你是一位知名评论家，擅长对文章进行评论和修改。
                        请对用户提供的文章进行审核和修改。
                        最终只返回修改后的文章，不要包含任何评论信息。
                        """)
                .build();
    }
    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {

        //先获取前一个节点存入map的内容
        String articleMessage = state.value("article")
                .orElseThrow(() -> new RuntimeException("article is null"))
                .toString();
        String userMessage = "请审核并修改以下文章：\n" + articleMessage;
        AssistantMessage call = reactAgent.call(userMessage);
        String reviewMessage = call.getText();
        return Map.of("reviewMessage", reviewMessage);
    }
}
