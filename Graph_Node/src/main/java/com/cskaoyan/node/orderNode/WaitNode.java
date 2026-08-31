package com.cskaoyan.node.orderNode;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.action.InterruptableAction;
import com.alibaba.cloud.ai.graph.action.InterruptionMetadata;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class WaitNode implements InterruptableAction, AsyncNodeAction {

    /*
         对于一个人工干预过程而言，该节点执行两次：
         1. 第一次执行，需要触发中断，返回非空Optional<InterruptionMetadata>
         2. 第二次执行，不需要触发中断，空Optional<InterruptionMetadata>
     */
    @Override
    public Optional<InterruptionMetadata> interrupt(String nodeId, OverAllState state, RunnableConfig config) {

        // 判断，当前要不要触发中断，非空Optional<InterruptionMetadata>
        if (config.metadata(RunnableConfig.HUMAN_FEEDBACK_METADATA_KEY).isPresent()) {
            // 有图的中断执行的标志，不触发中断
            // 返回空的Optional对象

            // 使用完了标志位，删除，不影响下一次的中断
            config.metadata().ifPresent(map -> map.remove(RunnableConfig.HUMAN_FEEDBACK_METADATA_KEY));
            return Optional.empty();
        }

        // 返回非空的Optional对象
        return Optional.of(InterruptionMetadata.builder(nodeId, state).build());
    }


    @Override
    public CompletableFuture<Map<String, Object>> apply(OverAllState state) {
        // 放回空map对象，通常在这个方法里不会执行业务逻辑
        return CompletableFuture.completedFuture(Map.of());
    }
}
