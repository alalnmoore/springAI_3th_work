package com.cskaoyan.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class KemonomimiTools {
    @Tool(description = "这是一个描述各种兽耳娘性格的工具")
    public String Kemonomimi(@ToolParam(description = "兽耳娘的种类，例如：兔耳娘，猫耳娘，狼耳娘，鹿角娘，半人马族，人鱼族等") String name) {
        //Java 中 == 比较的是对象引用地址，不是内容，用equals比较内容
        if ("兔耳娘".equals(name)) {
            return "兔耳娘的代表是：春，性格温和，骁勇善战，喜欢和人交朋友";
        } else if ("猫耳娘".equals(name)) {
            return "猫耳娘是群居的动物，性格温顺，喜欢和其他动物玩耍，喜欢舔舐";
        } else if ("狼耳娘".equals(name)) {
            return "代表人物是萝，是一只发育不完全的，瘦小，但是温顺的一只小狼耳娘！！";
        } else if ("半人马娘".equals(name)) {
            return "跑得快，路遥知马力！！";
        } else {
            return "未知的兽耳娘";
        }

    }

}
