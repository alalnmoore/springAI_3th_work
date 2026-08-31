package com.cskaoyan.config;

import com.alibaba.cloud.ai.graph.CompileConfig;
import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncEdgeAction;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.checkpoint.config.SaverConfig;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.cskaoyan.node.orderNode.ProcessOrderNode;
import com.cskaoyan.node.orderNode.RiskAssessNode;
import com.cskaoyan.node.orderNode.WaitNode;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Optional;

@Configuration
public class OrderGraph {
    @Bean
public CompiledGraph compiledGraph(ChatModel chatModel) throws GraphStateException {
        StateGraph compileStateGraph = new StateGraph(()-> Map.of(
                "order_info", KeyStrategy.REPLACE,
                "risk_level",KeyStrategy.REPLACE,
                "risk_reason",KeyStrategy.REPLACE
        ));
        //添加节点
        compileStateGraph.addNode("risk_access_node", AsyncNodeAction.node_async(new RiskAssessNode(chatModel)));
        compileStateGraph.addNode("order_wait_node",new WaitNode());
        compileStateGraph.addNode("process_order_node",AsyncNodeAction.node_async(new ProcessOrderNode()));
        //添加边
        compileStateGraph.addEdge(compileStateGraph.START, "risk_access_node");
        compileStateGraph.addConditionalEdges("risk_access_node",
                AsyncEdgeAction.edge_async(overAllSate -> {
                    String value = overAllSate.value("risk_level", "HIGH");
                    if ("LOW".equals(value)) {
                        return "process_order";
                    }

                    return "need_review";
                }),Map.of(
                        //如果是low就直接走处理的节点
                        "process_order", "process_order_node",
                        //如果是high就走中断节点
                        "need_review", "order_wait_node"
                ));
        //继续添加剩余的边
        compileStateGraph.addEdge("order_wait_node", "process_order_node");
        compileStateGraph.addEdge("process_order_node", compileStateGraph.END);

        //创建saverConfig
        SaverConfig saverConfig = SaverConfig.builder()
                .register(new MemorySaver())
                .build();
        //创建compileConfig
        CompileConfig compileConfig = CompileConfig.builder()
                .saverConfig(saverConfig)
                .build();
        CompiledGraph compile = compileStateGraph.compile(compileConfig);
        return compile;
    }
}
