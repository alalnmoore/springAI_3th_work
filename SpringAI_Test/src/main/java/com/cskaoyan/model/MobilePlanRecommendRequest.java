package com.cskaoyan.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MobilePlanRecommendRequest {

    private Integer budget;       // 预算（元/月）
    private Integer dataNeeded;   // 需要的流量（GB/月）
    private Integer voiceNeeded;  // 需要的通话时长（分钟/月）

}
