package com.cskaoyan.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MobilePlanRecommendation {
    @JsonPropertyDescription("套餐名称")
    private String planName;
    @JsonPropertyDescription("套餐月费")
    private Integer monthlyFee;
    @JsonPropertyDescription("推荐理由")
    private String reason;
}
