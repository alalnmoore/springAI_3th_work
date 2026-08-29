package com.cskaoyan.node.second;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.cskaoyan.model.ClassificationResult;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.converter.BeanOutputConverter;

import java.util.Map;

public class ClassifyNode implements NodeAction {

    private ReactAgent reactAgent;
    private BeanOutputConverter<ClassificationResult> converter;

    public ClassifyNode(ChatModel chatModel) {
        ReactAgent agent = ReactAgent.builder()
                .name("question_classifier")
                .model(chatModel)
                .instruction("""
                        你是一个问题分类专家，请根据用户问题的真实意图进行分类：
                        1. TECH：编程、软件、硬件、网络、人工智能等技术问题；
                        2. LIFE：健康、饮食、出行、学习、职场等日常生活问题。
                        不要只依赖关键词，要结合上下文和语义判断。
                        """)
                .outputType(ClassificationResult.class)
                .build();
        this.reactAgent = agent;
        this.converter = new BeanOutputConverter<>(ClassificationResult.class);
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        String input = state.value("input")
                .orElseThrow(() -> new RuntimeException("用户输入为空！！"))
                .toString();

        String userMessage = "请对以下用户问题进行分类：\n" + input;
        AssistantMessage questionResponse = reactAgent.call(userMessage);
        //将reactAgent返回的json转化为对象
        ClassificationResult classificationResult = converter.convert(questionResponse.getText());
        Map<String, Object> mapChoice = Map.of(
                "category", classificationResult.getCategory()
                , "reason", classificationResult.getReason());
        return mapChoice;
    }
}
