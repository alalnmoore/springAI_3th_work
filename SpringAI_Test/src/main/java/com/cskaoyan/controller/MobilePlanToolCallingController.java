package com.cskaoyan.controller;

import com.cskaoyan.model.MobilePlanRecommendRequest;
import com.cskaoyan.model.MobilePlanRecommendation;
import com.cskaoyan.tool.MobilePlanTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tools/mobile-plan")
public class MobilePlanToolCallingController {
    //注入容器中的依赖
    @Autowired
    private ChatClient chatClient; //用的是本地ollama的！！！
    @Autowired
    private ChatMemory chatMemory;
    @Autowired
    private MessageChatMemoryAdvisor messageChatMemoryAdvisor;
    @Autowired
    private MobilePlanTools mobilePlanTools;

    @PostMapping("/recommend")
    public MobilePlanRecommendation recommend(@RequestBody MobilePlanRecommendRequest request) {
    /**
    * 原因：
    *
    * qwen2.5:3b 小模型不支持 Tool Calling：3B 参数的模型没有 function calling 能力，@Tool 注册了也调用不起来，模型要么忽略工具返回空，要么返回乱七八糟的内容。
    * 小模型不擅长结构化 JSON 输出：.entity(Class) 要求模型严格返回 JSON 格式，小模型经常返回自然语言或空内容。
    * 解决：
    *
    * 放弃 @Tool 机制，把套餐数据直接拼到 prompt 文本中
    * 在 prompt 中明确要求返回严格 JSON 格式，并给出 JSON 模板示例
    * 移除 .tools(mobilePlanTools)，保留 .entity(MobilePlanRecommendation.class) 做结构化解析
    */

        // 可用套餐数据直接拼到 prompt 中，不依赖 Tool Calling（适配小模型）
        String plansInfo = """
                可选套餐列表：
                - 青春卡：月费29元，流量30GB，通话100分钟
                - 畅享卡：月费59元，流量80GB，通话300分钟
                - 流量王卡：月费79元，流量150GB，通话100分钟
                - 通话王卡：月费69元，流量50GB，通话1000分钟
                - 家庭共享卡：月费99元，流量150GB，通话700分钟
                """;

        // 构建用户消息
        String userMessage = String.format("""
                我的需求：
                - 预算：%d元/月
                - 流量需求：%dGB/月
                - 通话需求：%d分钟/月

                %s

                请帮我推荐最合适的套餐，并说明推荐理由。
                返回结果必须是严格 JSON 格式，不要包含任何其他内容：
                {"planName":"套餐名称","monthlyFee":月费数字,"reason":"推荐理由"}
                """, request.getBudget(), request.getDataNeeded(), request.getVoiceNeeded(), plansInfo);

        // 系统提示
        String systemPrompt = """
                你是一个专业的手机套餐推荐助手。

                推荐原则：
                - 优先考虑预算约束
                - 流量和通话要满足或略高于用户需求
                - 推荐性价比最高的套餐

                返回结果必须是合法的 JSON，不要返回其他任何内容。
                """;

        MobilePlanRecommendation result = chatClient.prompt()
                .system(systemPrompt)
                .user(userMessage)
                .advisors(messageChatMemoryAdvisor)
                .advisors(advisor -> advisor.param(chatMemory.CONVERSATION_ID, "001"))
                .call()
                .entity(MobilePlanRecommendation.class);
        return result;
    }


}
