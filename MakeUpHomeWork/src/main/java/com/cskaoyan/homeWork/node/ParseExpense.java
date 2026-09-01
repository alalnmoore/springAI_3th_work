package com.cskaoyan.homeWork.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.cskaoyan.homeWork.model.ParseResult;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.converter.BeanOutputConverter;

import java.util.HashMap;
import java.util.Map;

public class ParseExpense implements NodeAction {
    private ReactAgent reactAgent;
    private BeanOutputConverter<ParseResult> beanOutputConverter;
    public ParseExpense(ChatModel chatModel){
        ReactAgent riskReviewAgent = ReactAgent.builder()
                .instruction("你是一个报销审核员，请根据用户输入的报销金额以及报销事务进行判断" +
                        "是否要进行报销，满足以下任何条件判断为REJECT：报销的金额大于1000，报销的事务是生活费或者学习用的费用" +
                        "如果报销金额小于1000，且报销的事务是生活费或者学习用的费用，则判断为AGREE" +
                        "如果报销的金额小于1000但是不是生活费或者学习用的费用，则判断为REJECT,需要人工进行审核" +
                        "请严格按照以下的json返回，不要输出json以外的内容：" +
                        "{\"amount\":\"用户输入的报销的金额\",\"reason\":\"用户输入的报销原因\",\"review\":\"人工审核的结果：AGREE或者REJECT\"}")
                .model(chatModel)
                .name("risk_review_agent")
                .outputType(ParseResult.class)
                .build();

        this.reactAgent = riskReviewAgent;
        this.beanOutputConverter = new BeanOutputConverter<>(ParseResult.class);
    }
    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        String userAmount = state.value("user_amount","NULL").toString();
        String userReason = state.value("user_reason","NULL").toString();
        String userMessage = userAmount + " " + userReason;
        HashMap<String,Object> inputMap = new HashMap<>();
        AssistantMessage agentResultMessage = reactAgent.call(userMessage);
        ParseResult parseResult = beanOutputConverter.convert(agentResultMessage.getText());
        inputMap.put("amount", parseResult.getAmount());
        inputMap.put("reason", parseResult.getReason());
        if(Integer.parseInt(String.valueOf(parseResult.getAmount())) > 1000){
            String message = userMessage + ", 该订单风险较高，请输入AGREE或者REJECT, 审核通过或者拒绝该订单";
            inputMap.put("need_review", message);
            inputMap.put("user_message",userMessage);
        }
        return inputMap;
    }
}
