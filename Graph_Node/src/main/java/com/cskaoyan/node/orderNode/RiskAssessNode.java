package com.cskaoyan.node.orderNode;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.cskaoyan.model.RiskResult;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.converter.BeanOutputConverter;

import java.util.HashMap;
import java.util.Map;

public class RiskAssessNode implements NodeAction {
    private final ReactAgent riskAgent;
    private final BeanOutputConverter<RiskResult> converter;

    //执行构造方法，给成员变量赋值
    public RiskAssessNode(ChatModel chatModel) {
        ReactAgent riskAgent = ReactAgent.builder()
                .instruction("你是订单风险评估员，请根据订单金额和商品信息判断风险等级。" +
                        "满足以下任一条件判定为HIGH：订单金额大于5000元，或订单包含敏感商品；" +
                        "订单金额不大于5000元且不包含敏感商品时判定为LOW。" +
                        "reason必须说明判定依据；低风险时也要说明金额和商品均未触发高风险规则。" +
                        "请严格按照以下JSON格式返回，不要输出JSON以外的内容：" +
                        "{\"riskLevel\":\"HIGH或LOW\",\"reason\":\"具体判定依据\"}")
                .name("risk_assess_agent")
                .model(chatModel)
                .outputType(RiskResult.class)
                .build();
        this.riskAgent = riskAgent;
        this.converter = new BeanOutputConverter<>(RiskResult.class);
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        //取前端传来的订单信息
        String orderInfo = state.value("order_info", "NULL").toString();
        String userInfo = "用户的订单信息如下：" + orderInfo + "!!!";
        //交给agent进行判断
        AssistantMessage orderMessage = riskAgent.call(userInfo);
        RiskResult riskResult = converter.convert(orderMessage.getText());
        HashMap<String,Object> orderHashMap = new HashMap<>();
        //存入全局的state中
        orderHashMap.put("risk_level", riskResult.getRiskLevel());
        orderHashMap.put("risk_reason", riskResult.getReason());
        //如果风险等级为HIGH，需要进行中断操作
        if(riskResult.getRiskLevel().equals("HIGH")){
            String message = orderInfo + ", 该订单风险较高，请输入approve或者reject, 审核通过或者拒绝该订单";
            orderHashMap.put("review_message", message);
        }
        return orderHashMap;
    }
}
