package com.cskaoyan.homeWork.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.model.ChatModel;

import java.util.HashMap;
import java.util.Map;

public class ProcessExpenseNode implements NodeAction {
    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        String needReview = state.value("need_review", "NULL").toString();
        String reviewResult = state.value("review_result", "AGREE").toString();
        String processStatus = state.value("process_status", "NULL").toString();
        String processResult = state.value("process_result", "NULL").toString();
        String userMessage = state.value("user_message", "NULL").toString();

        //这个map中的内容会自动装载到state中
        Map<String, Object> output = new HashMap<>();

        // 高金额报销经过人工审核后到达本节点：审核结果为拒绝时，终止履约
        if ("REJECTED".equals(reviewResult) && reviewResult.contains("拒绝") || "REJECT".equals(reviewResult)  && reviewResult.toLowerCase().contains("reject")) {
            output.put("process_status", "REJECTED");
            output.put("process_result", "该报销事务未通过人工审核，系统已取消后续履约。订单信息：" + userMessage);
            output.put("next_action", "通知用户订单审核未通过，并记录审核结果：" + reviewResult);
            return output;
        }
        //这个是中断判断如果可以继续报销的话，也就是说审核通过了，可以继续履约
        if ("AGREED".equals(reviewResult)) {
            output.put("process_status", "MANUAL_APPROVED");
            output.put("process_result", "高风险报销事务已通过人工审核，可以继续履约，报销事务信息：" + userMessage);
            output.put("next_action", "标记为人工审核通过");
            output.put("audit_note", reviewResult);
            return output;
        }

        output.put("process_status", "AUTO_APPROVED");
        output.put("process_result", "低风险报销事务自动通过，系统直接进入履约流程，待报销信息：" + userMessage);
        output.put("next_action", "确认进行报销");
        return output;
    }
}
