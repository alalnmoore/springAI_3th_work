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
import com.cskaoyan.homeWork.node.ParseExpense;
import com.cskaoyan.homeWork.node.ProcessExpenseNode;
import com.cskaoyan.homeWork.node.WaitNode;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class UserAmountGraphConfig {
    @Bean
    public CompiledGraph compiledGraph(ChatModel chatModel) throws GraphStateException {

        StateGraph stateGraph = new StateGraph(()->Map.of(
                "review_result", KeyStrategy.REPLACE
        ));
        //添加节点
        stateGraph.addNode("parse_expense_node", AsyncNodeAction.node_async(new ParseExpense(chatModel)));
        stateGraph.addNode("wait_node", new WaitNode());
        stateGraph.addNode("process_expense_node", AsyncNodeAction.node_async(new ProcessExpenseNode()));
        //添加边
        stateGraph.addEdge(stateGraph.START, "parse_expense_node");
        stateGraph.addConditionalEdges("parse_expense_node",
                AsyncEdgeAction.edge_async(overAllState -> {
                    Integer amount = Integer.parseInt(String.valueOf(overAllState.value("amount", 0)));
                    if (amount > 1000) {
                        return "need_review";
                    }
                    else{
                        return "process_expense_node";
                    }
                }), Map.of("need_review","wait_node",
                        "process_expense_node","process_expense_node"));
        stateGraph.addEdge("wait_node", "process_expense_node");
        stateGraph.addEdge("process_expense_node",stateGraph.END);

        SaverConfig saverConfig = SaverConfig.builder()
                .register(new MemorySaver())
                .build();
        CompileConfig compileConfig = CompileConfig.builder()
                .saverConfig(saverConfig)
                .build();
        CompiledGraph compile = stateGraph.compile(compileConfig);
        return compile;

    }
}
