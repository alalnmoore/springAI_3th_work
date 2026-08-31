package com.cskaoyan.controller;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
public class OrderController {
    @Autowired
    private CompiledGraph compiledGraph;
    @PostMapping("/order/message")
    public Map<String, Object> orderMessage(@RequestBody Map<String, Object> request) {

        // 判断是第一次请求还是恢复请求
        boolean hasInput = StringUtils.hasText((String) request.get("input"));

        if (hasInput) {
            // ========== 第一次请求：处理新订单 ==========
            RunnableConfig runnableConfig = RunnableConfig.builder()
                    .threadId((String) request.get("threadId"))
                    .build();

            Optional<NodeOutput> nodeOutput = compiledGraph.invokeAndGetOutput(
                    Map.of("order_info", (String) request.get("input")),
                    runnableConfig
            );

            NodeOutput nodeMessage = nodeOutput.get();
            OverAllState stateMessage = nodeMessage.state();

            if (nodeMessage.isEND()) {
                // 没有触发中断，直接结束
                return Map.of(
                        "type", "normal",
                        "threadId", request.get("threadId"),
                        "process_status", stateMessage.value("process_status", "NULL").toString(),
                        "process_result", stateMessage.value("process_result", "NULL").toString(),
                        "next_action", stateMessage.value("next_action", "NULL").toString()
                );
            } else {
                // 触发了中断，等人审批
                return Map.of(
                        "type", "interrupt",
                        "threadId", request.get("threadId"),
                        "node", nodeMessage.node(),
                        "message", stateMessage.value("review_message", "").toString(),
                        "risk_level", stateMessage.value("risk_level", "").toString(),
                        "risk_reason", stateMessage.value("risk_reason", "").toString()
                );
            }

        } else {
            // ========== 第二次请求：恢复执行（审批结果） ==========
            //拿到了resume
            RunnableConfig runnableConfig = RunnableConfig.builder()
                    .threadId((String) request.get("threadId"))
                    .resume()
                    .addStateUpdate(Map.of("review_result", (String) request.get("resume")))
                    .build();

            Optional<OverAllState> result = compiledGraph.invoke((Map<String, Object>) null, runnableConfig);
            //overAllState是一个公共的大背包
            OverAllState overAllState = result.get();

            return Map.of(
                    "type", "normal",
                    "threadId", request.get("threadId"),
                    "process_status", overAllState.value("process_status", "").toString(),
                    "process_result", overAllState.value("process_result", "").toString(),
                    "next_action", overAllState.value("next_action", "").toString()
            );
        }
    }
}
