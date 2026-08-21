package com.cskaoyan.tool;

import com.cskaoyan.model.MobilePhonePlan;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MobilePlanTools {

    // 预置5个套餐数据
    private static final List<MobilePhonePlan> PLANS = List.of(
            new MobilePhonePlan("青春卡", 29, 30, 100),
            new MobilePhonePlan("畅享卡", 59, 80, 300),
            new MobilePhonePlan("流量王卡", 79, 150, 100),
            new MobilePhonePlan("通话王卡", 69, 50, 1000),
            new MobilePhonePlan("家庭共享卡", 99, 150, 700)
    );
    //工具1：查询全部套餐
    @Tool(description = "查询系统中所有可用的手机套餐列表")
    public List<MobilePhonePlan> getAllPlans() {
        return PLANS;
    }

    @Tool(description = "根据用户设定的最高月费，筛选出不超过该费用的所有套餐")
    public List<MobilePhonePlan> getPlansByMaxMonthlyFee(int maxMonthlyFee) {
        return PLANS.stream()
                .filter(plan -> plan.getMonthlyFee() <= maxMonthlyFee)
                .toList();
    }

    @Tool(description = "根据套餐名称查询该套餐的详细信息")
    public MobilePhonePlan getPlanByName(@ToolParam(description = "套餐名称，如'青春卡'、'畅享卡'等") String name) {
        return PLANS.stream()
                .filter(plan -> plan.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

}
