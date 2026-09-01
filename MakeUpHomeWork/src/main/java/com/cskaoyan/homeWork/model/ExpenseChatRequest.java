package com.cskaoyan.homeWork.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseChatRequest {

    private String threadId;
    // 报销金额
    private Double amount;
    // 报销原因
    private String reason;
    // 用户针对人工审核消息的回复
    private String resume;
}
