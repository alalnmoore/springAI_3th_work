package com.cskaoyan.node.second;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;

import java.util.Map;

public class LifeNode implements NodeAction {
    private ReactAgent reactAgent;
    public LifeNode(ChatModel chatModel) {
        ReactAgent agent = ReactAgent.builder()
                .name("life_node")
                .model(chatModel)
                .instruction("你是一个生活节点专家，请根据用户问题的真实意图进行判断：")
                .build();
        this.reactAgent = agent;
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {

        String string = state.value("input")
                .orElseThrow(() -> new RuntimeException("TechNode罢工了卧槽！！"))
                .toString();
        String userMessage = "请回答以下问题：\n" + string;
        AssistantMessage call = reactAgent.call(userMessage);
        String lifeMessage = call.getText();
        Map<String, Object> lifeNode = Map.of("answer", lifeMessage);
        return lifeNode;
    }
}
