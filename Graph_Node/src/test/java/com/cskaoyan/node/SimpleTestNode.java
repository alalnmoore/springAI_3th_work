package com.cskaoyan.node;

import com.alibaba.cloud.ai.graph.*;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.checkpoint.config.SaverConfig;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.cskaoyan.node.InterrupteNode.QuestionNode;
import com.cskaoyan.node.InterrupteNode.SayHelloNode;
import com.cskaoyan.node.InterrupteNode.WaitNode;
import io.micrometer.common.KeyValue;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.Optional;

@SpringBootTest
public class SimpleTestNode {

    @Test
    public void test() throws GraphStateException {

        StateGraph stateGraph = new StateGraph(()-> Map.of("question", KeyStrategy.REPLACE));
        //添加节点
        stateGraph.addNode("question_node",AsyncNodeAction.node_async(new QuestionNode()));
        stateGraph.addNode("wait_node",new WaitNode());
        stateGraph.addNode("sayhello_node",AsyncNodeAction.node_async(new SayHelloNode()));

        //添加边
        stateGraph.addEdge(stateGraph.START, "question_node");
        stateGraph.addEdge("question_node", "wait_node");
        stateGraph.addEdge("wait_node", "sayhello_node");
        stateGraph.addEdge("sayhello_node", stateGraph.END);

        //编译图，需要saverConfig
        SaverConfig saverConfig = SaverConfig.builder()
                .register(new MemorySaver())
                .build();

        CompileConfig compileConfig = CompileConfig.builder()
                .saverConfig(saverConfig)
                .build();

        CompiledGraph compiledGraph = stateGraph.compile(compileConfig);

        //创建runnableConfig对象，记录threadId
        RunnableConfig runnableConfig = RunnableConfig.builder()
                .threadId("demo001")
                .build();

        //执行图，第一次执行
        Optional<NodeOutput> fistNodeOutput = compiledGraph.invokeAndGetOutput(Map.of(), runnableConfig);
        fistNodeOutput.ifPresent(node->{
            //node的名字
            String nodeName = node.node();
            System.out.println("节点的名字: " + nodeName);
            OverAllState nodeState = node.state();
            System.out.println("节点的状态: " + nodeState);
        });

        System.out.println("》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》");
        String userName = "张三";
        RunnableConfig runnableConfig1 = RunnableConfig.builder()
                .threadId("demo001")
                .resume()
                .addStateUpdate(Map.of("name", userName))
                .build();
        KeyValue map;

        //这个是在整个图的overAllState中查询key为"answer"的value，返回得结果就是SayHelloNode中添加到Map中的value
        Optional<OverAllState> invoke = compiledGraph.invoke(Map.of(), runnableConfig1);
        invoke.ifPresent(state->{
            String resultMessage = state.value("answer")
                    .orElseThrow(() -> new RuntimeException("No answer"))
                    .toString();
            System.out.println("结果: " + resultMessage);
        });

        //如果是想查看这个执行得细节，就用invokeAndGetOutput可以看到具体得细节，比如当前节点的名字以及当前节点的状态
        //以及overAllState中所有的键值对信息！！！
        Optional<NodeOutput> nodeOutput = compiledGraph.invokeAndGetOutput(Map.of(), runnableConfig1);
        nodeOutput.ifPresent(node->{
            String nodeName = node.node();
            System.out.println("节点的名字: " + nodeName);
            OverAllState nodeState = node.state();
            System.out.println("节点的状态: " + nodeState);
        });
    }
}
