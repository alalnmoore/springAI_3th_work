package com.cskaoyan.node.InterrupteNode;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;

import java.util.Map;

public class SayHelloNode implements NodeAction {
    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        String value = state.value("question", "unknown");

        String name = state.value("name", "同学").toString();
        return Map.of("answer", "你好，" + name);
    }
}
