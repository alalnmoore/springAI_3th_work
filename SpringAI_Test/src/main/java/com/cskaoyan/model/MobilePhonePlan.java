package com.cskaoyan.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MobilePhonePlan {

    @JsonPropertyDescription("套餐名称")
    private String name;
    @JsonPropertyDescription("套餐月费")
    private Integer monthlyFee;
    @JsonPropertyDescription("套餐流量，单位GB")
    private Integer dataGb;
    @JsonPropertyDescription("套餐通话时长，单位分钟")
    private Integer voiceMinutes;


}
