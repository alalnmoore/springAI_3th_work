package com.cskaoyan.homeWork.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
public class ParseResult {
    @JsonPropertyDescription("用户输入的报销的金额")
    int amount;
    @JsonPropertyDescription("用户输入的报销原因")
    String reason;
}
