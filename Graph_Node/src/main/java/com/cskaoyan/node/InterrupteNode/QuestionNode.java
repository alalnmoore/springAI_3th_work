package com.cskaoyan.node.InterrupteNode;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;

import java.util.Map;

public class QuestionNode implements NodeAction {

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        String questionMessage = "what's your name?";
        System.out.println(questionMessage);

        return Map.of("question",questionMessage);
    }
}
