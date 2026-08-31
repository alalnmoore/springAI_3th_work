package com.cskaoyan.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RiskResult {
    @JsonPropertyDescription("订单风险评估结果，取HIGH或者LOW")
    private String riskLevel;  // "HIGH" 或 "LOW"
    @JsonPropertyDescription("给出风险评估结论的原因")
    private String reason;
}
