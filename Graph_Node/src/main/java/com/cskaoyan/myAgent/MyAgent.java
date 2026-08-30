package com.cskaoyan.myAgent;

import com.alibaba.cloud.ai.graph.CompileConfig;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncEdgeAction;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.agent.Agent;
import com.alibaba.cloud.ai.graph.agent.flow.agent.FlowAgent;
import com.alibaba.cloud.ai.graph.agent.flow.builder.FlowGraphBuilder;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.cskaoyan.node.second.ClassifyNode;
import com.cskaoyan.node.second.LifeNode;
import com.cskaoyan.node.second.TechNode;
import org.springframework.ai.chat.model.ChatModel;

import java.util.List;
import java.util.Map;

public class MyAgent extends FlowAgent {

    private ChatModel chatModel;
    public MyAgent(String name, String description, CompileConfig compileConfig, ChatModel chatModel) {
        super(name, description, compileConfig, List.of());
        this.chatModel = chatModel;
    }

    @Override
    protected StateGraph buildSpecificGraph(FlowGraphBuilder.FlowGraphConfig config) throws GraphStateException {
        //在这里面定义图
        StateGraph stateGraph = new StateGraph();
                // 1.1 添加节点
                stateGraph.addNode("classify_node",
                        AsyncNodeAction.node_async(new ClassifyNode(chatModel)));
                stateGraph.addNode("tech_node",
                        AsyncNodeAction.node_async(new TechNode(chatModel)));
                stateGraph.addNode("life_node",
                        AsyncNodeAction.node_async(new LifeNode(chatModel)));

                // 1.2 添加边
                // 1.2.1 普通边
                stateGraph.addEdge(StateGraph.START, "classify_node");
                // 1.2.2 条件边
                stateGraph.addConditionalEdges("classify_node",
                        // 条件函数: 方法参数为状态对象  返回字符串
                        AsyncEdgeAction.edge_async(overAllState -> {
                            // 获取分类节点分类的结果
                            String categoryResult = overAllState.value("category")
                                    .orElseThrow(() -> new RuntimeException("没有分类结果！！"))
                                    .toString();
                            if ("TECH".equals(categoryResult)) {

                                return "tech";
                            }

                            return "life";
                        }),
                        // 路由映射

                        Map.of("tech", "tech_node",
                                "life", "life_node"));

                // 普通边
                stateGraph.addEdge("tech_node", StateGraph.END);
                // 普通边
                stateGraph.addEdge("life_node", StateGraph.END);

                //返回定义好的图对象！！
                return stateGraph;
    }

}
