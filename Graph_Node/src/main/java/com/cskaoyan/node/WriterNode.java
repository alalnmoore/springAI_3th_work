package com.cskaoyan.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class WriterNode implements NodeAction {

    private ReactAgent reactAgent;

    public WriterNode(ChatModel chatModel) {
        this.reactAgent = ReactAgent.builder()
                .name("writer_node")
                .model(chatModel)
                .description("专业写作 Agent")
                .instruction("你是一位知名作家，擅长写作和创作。请根据用户提供的要求完成文章。")
                .build();
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        String input = state.value("input")
                .orElseThrow(() -> new RuntimeException("用户输入为空！！"))
                .toString();

        String message = "请根据一下要求生成文章" + "\n" + input;
        AssistantMessage artical = reactAgent.call(message);
        String text = artical.getText();
        return Map.of("article", text);
    }
}
