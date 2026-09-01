package com.cskaoyan.homeWork.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseChatResponse {

    // 中断响应，或者普通响应
    private String type;

    private String threadId;

    // 返回给前端的消息
    private String message;
}
