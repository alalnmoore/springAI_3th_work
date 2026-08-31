package com.cskaoyan.node.InterrupteNode;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.action.InterruptableAction;
import com.alibaba.cloud.ai.graph.action.InterruptionMetadata;
import com.alibaba.cloud.ai.graph.action.NodeAction;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class WaitNode implements InterruptableAction, AsyncNodeAction {

    @Override
    public Optional<InterruptionMetadata> interrupt(String nodeId, OverAllState state, RunnableConfig config) {
        if (config.metadata(RunnableConfig.HUMAN_FEEDBACK_METADATA_KEY).isPresent()) {
            //删除这个标志位，避免影响下一次的独立执行的判断
            config.metadata().ifPresent(map -> map.remove(RunnableConfig.HUMAN_FEEDBACK_METADATA_KEY));
            return Optional.empty();
        }
        //如果没有这个标志位
        return Optional.of(InterruptionMetadata.builder(nodeId,state).build());

    }


    @Override
    public CompletableFuture<Map<String, Object>> apply(OverAllState state) {
        return CompletableFuture.completedFuture(Map.of());

    }
}
