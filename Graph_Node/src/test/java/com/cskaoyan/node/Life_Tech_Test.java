package com.cskaoyan.node;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncEdgeAction;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.cskaoyan.node.second.ClassifyNode;
import com.cskaoyan.node.second.LifeNode;
import com.cskaoyan.node.second.TechNode;
import jakarta.websocket.RemoteEndpoint;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.Optional;

@SpringBootTest
public class Life_Tech_Test {

    @Autowired
    private ChatModel chatModel;
    @Test
    public void test() throws GraphStateException {

        StateGraph stateGraph = new StateGraph();

        stateGraph.addNode("classify_node", AsyncNodeAction.node_async(new ClassifyNode(chatModel)));
        stateGraph.addNode("tech_node", AsyncNodeAction.node_async(new TechNode(chatModel)));
        stateGraph.addNode("life_node", AsyncNodeAction.node_async(new LifeNode(chatModel)));

        // 添加边
        stateGraph.addEdge(StateGraph.START, "classify_node");
        //添加条件边
        stateGraph.addConditionalEdges("classify_node",
                AsyncEdgeAction.edge_async(overAllState ->
                        {
                            String message = overAllState
                                    .value("category")
                                    .orElseThrow(() -> new RuntimeException("category not found"))
                                    .toString();
                                    if("TECH".equals(message))
                                    {
                                        return "tech";
                                    }
                                    else
                                    {
                                        return "life";
                                    }

                        }), Map.of("tech","tech_node"
                , "life","life_node"));

        stateGraph.addEdge("life_node", StateGraph.END);
        stateGraph.addEdge("tech_node", StateGraph.END);


        //编译图
        CompiledGraph compile = stateGraph.compile();
        //配置runnableConfig
        RunnableConfig build = RunnableConfig.builder()
                .threadId("0001")
                .build();

        Map<String, Object> input = Map.of("input", "什么是西格姆粒子或者西格姆定律？？？");
        Optional<OverAllState> result = compile.invoke(input, build);
        result.ifPresent(state->{
            String answer = state.value("answer")
                    .orElseThrow(() -> new RuntimeException("answer not found"))
                    .toString();
            System.out.println(answer);
        });

    }


}
