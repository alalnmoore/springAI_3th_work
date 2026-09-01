package com.cskaoyan.node.orderNode;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;

import java.util.HashMap;
import java.util.Map;

public class ProcessOrderNode implements NodeAction {
    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        String orderInfo = state.value("order_info", "NULL").toString();
        String riskLevel = state.value("risk_level", "LOW").toString();
        String riskReason = state.value("risk_reason", "NULL").toString();
        String reviewResult = state.value("review_result", "NULL").toString();

        Map<String, Object> output = new HashMap<>();

        // 高风险订单经过人工审核后到达本节点：审核结果为拒绝时，终止履约
        if ("HIGH".equals(riskLevel) && reviewResult.contains("拒绝") || "HIGH".equals(riskLevel)  && reviewResult.toLowerCase().contains("reject")) {
            output.put("process_status", "REJECTED");
            output.put("process_result", "订单未通过人工审核，系统已取消后续履约。订单信息：" + orderInfo);
            output.put("next_action", "通知用户订单审核未通过，并记录审核结果：" + reviewResult);
            return output;
        }

        if ("HIGH".equals(riskLevel)) {
            output.put("process_status", "MANUAL_APPROVED");
            output.put("process_result", "高风险订单已通过人工审核，可以继续履约。订单信息：" + orderInfo);
            output.put("next_action", "标记为人工审核通过，进入仓库拣货和支付确认流程");
            output.put("audit_note", reviewResult);
            return output;
        }

        output.put("process_status", "AUTO_APPROVED");
        output.put("process_result", "低风险订单自动通过，系统直接进入履约流程。订单信息：" + orderInfo);
        output.put("next_action", "进入仓库拣货和支付确认流程");
        return output;
    }
}
