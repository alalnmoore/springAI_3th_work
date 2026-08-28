package com.cskaoyan.node;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.Optional;


@SpringBootTest
public class NodeTest {

    @Autowired
    ChatModel chatModel;

    @Test
    public void testNode() throws GraphStateException {

        StateGraph stateGraph = new StateGraph();
        AsyncNodeAction action1 = AsyncNodeAction.node_async(new WriterNode(chatModel));
        AsyncNodeAction action2 = AsyncNodeAction.node_async(new ReviewerNode(chatModel));

        stateGraph.addNode("writer_node",action1);
        stateGraph.addNode("reviewer_node",action2);
        //添加边
        stateGraph.addEdge(StateGraph.START,"writer_node");
        stateGraph.addEdge("writer_node","reviewer_node");
        stateGraph.addEdge("reviewer_node",stateGraph.END);

        CompiledGraph compile = stateGraph.compile();
        RunnableConfig build = RunnableConfig.builder()
                .threadId("No.1")
                .build();

        Optional<OverAllState> messageResult = compile.invoke(Map.of("input", "帮我写一篇150字的表白信"), build);
        messageResult.ifPresent(state -> {
            state.value("article").ifPresent(message->System.out.println(message));

            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

            state.value("reviewMessage").ifPresent(message->System.out.println(message));
        });
    }
}
