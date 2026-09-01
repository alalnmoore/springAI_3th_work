package com.cskaoyan.controller;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.cskaoyan.homeWork.model.ExpenseChatRequest;
import com.cskaoyan.homeWork.model.ExpenseChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/state-graph/expense")
/**
 * Controller 卡在这里等待审核人员输入~~ → Controller 立即返回中断响应，HTTP 结束
 * Controller 触发第二次请求~~ → 前端/审核员发起第二次请求，Controller 只是接住它，这是完整的第二次http请求
 * 他会获取前端传进来审核员填写的审批结果，然后再次运行图
 * 第二次请求里 Controller 调 invoke 时，图才继续跑 process_order_node → END，跑完后 Controller 从返回的 OverAllState 取值返回
 */
public class ExpenseGraphController {
    @Autowired
    private CompiledGraph compiledGraph;
    @PostMapping("/chat")
    public ExpenseChatResponse chat(@RequestBody ExpenseChatRequest request) {

        // 判断是第一次请求还是恢复请求：resume 为空 = 第一次请求
        if (request.getResume()==null) {
            // ========== 第一次请求：处理新报销 ==========
            RunnableConfig runnableConfig = RunnableConfig.builder()
                    .threadId(request.getThreadId())
                    .build();

            Optional<NodeOutput> nodeOutput = compiledGraph.invokeAndGetOutput(
                    //先运行图，获取overallstate中的更新后的键值对再放进map中返回给前端查看！！！
                    Map.of("user_amount", String.valueOf(request.getAmount()),
                           "user_reason", request.getReason()),
                    runnableConfig
            );

            NodeOutput nodeMessage = nodeOutput.get();
            OverAllState stateMessage = nodeMessage.state();

            if (nodeMessage.isEND()) {
                // 没有触发中断，图直接跑完了（低风险）
                return ExpenseChatResponse.builder()
                        .type("normal")
                        .threadId(request.getThreadId())
                        .message(stateMessage.value("process_result", "审核通过").toString())
                        .build();
            } else {
                // 触发了中断，等人审批,这里其实第一次的HTTP请求就结束了！！！
                return ExpenseChatResponse.builder()
                        .type("interrupt")
                        .threadId(request.getThreadId())
                        .message(stateMessage.value("need_review", "").toString())
                        .build();
                //等待审核员的输入审核的信息
            }

        } else {
            //第二次的HTTP请求，用于恢复执行
            // ========== 第二次请求：恢复执行（审批结果） ==========
            RunnableConfig runnableConfig = RunnableConfig.builder()
                    .threadId(request.getThreadId())
                    .resume()
                    // 将审核员选择的审批结果添加到resume中进行更新
                    .addStateUpdate(Map.of("review_result", request.getResume()))
                    .build();
            //只有拿到审核员传进来的结果，解析之后将结果放近allOverState中,然后运行图，用于下一个节点进行解析！！！！
            Optional<OverAllState> result = compiledGraph.invoke((Map<String, Object>) null, runnableConfig);
            OverAllState overAllState = result.get();

            return ExpenseChatResponse.builder()
                    .type("normal")
                    .threadId(request.getThreadId())
                    //下面的信息来自节点ProcessExpenseNode的处理结果！
                    .message(overAllState.value("review_result", "").toString())
                    .build();
        }

        

    }
}

