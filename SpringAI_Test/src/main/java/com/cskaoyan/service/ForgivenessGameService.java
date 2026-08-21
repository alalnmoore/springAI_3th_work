package com.cskaoyan.service;

import com.cskaoyan.model.ForgivenessChatRequest;
import com.cskaoyan.model.ForgivenessChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ForgivenessGameService {

    @Autowired
    private ChatClient chatClient;
    @Autowired
    private MessageChatMemoryAdvisor messageChatMemoryAdvisor;
    @Autowired
    private ChatMemory chatMemory;

    //创建一个map，key是会话的id，Interge是当前女友的情绪分数
    private Map<String,Integer> forgivenessMap = new HashMap<>();
    //女友的情绪起始分数
    private static final int INITIAL_EMOTION_SCORE = 20;

    public ForgivenessChatResponse chat(ForgivenessChatRequest request){

        String id = request.getConversationId();
        if(id == null || id.isEmpty()){
            id = UUID.randomUUID().toString();
        }
        //会话的id使用 final 变量承接，避免 lambda 引用报错
        final String conversationId = id;
        //从map中获取当前女友的情绪分数
        int currentNum = forgivenessMap.getOrDefault(conversationId,INITIAL_EMOTION_SCORE);
        if(currentNum <= 0){
            ForgivenessChatResponse forgivenessChatResponse = new ForgivenessChatResponse();
            forgivenessChatResponse.setConversationId(conversationId);
            forgivenessChatResponse.setReply("游戏结束，你被甩了！");
            return forgivenessChatResponse;

        }
        if(currentNum >=100){

            ForgivenessChatResponse forgivenessChatResponse = new ForgivenessChatResponse();
            forgivenessChatResponse.setConversationId(conversationId);
            forgivenessChatResponse.setReply("恭喜通关！她原谅你了！");
            return forgivenessChatResponse;
        }

        //构建系统提示词
        String systemPrompt = String.format("""
            你是生气的对象，当前原谅值%d。
            根据用户说的话，你的原谅值会变化（范围-10到+10）。
            严格按JSON格式返回：{"emotion":"心情","change":变化量,"reply":"你说的话"}
            不要返回其他内容。
            """, currentNum);

        String message = request.getMessage();
        if(message == null || message.isEmpty()){
            //如果用户没有说话，就给一个默认的提示词
            message = "我来了，别生气了！！！！听见了吗？？再生气给你一耳光，惯的！！";
        }

        String content = chatClient.prompt()
                .user(message)
                .system(systemPrompt)
                .advisors(messageChatMemoryAdvisor)
                .advisors(advisor -> advisor.param(chatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();

        log.info("AI 回复: {}", content);

        int change = parseChange(content);
        currentNum += change;
        forgivenessMap.put(conversationId, currentNum);
        String reply = extractReply(content);
        if (currentNum >= 100) {
            reply = reply + "🎉 恭喜通关！原谅值达到100！";
        } else if (currentNum <= 0) {
            reply = reply + "💔 原谅值为0，你被甩了！";
        } else {
            reply = reply + String.format("当前原谅值：%d/100", currentNum);
        }

        ForgivenessChatResponse forgivenessChatResponse = new ForgivenessChatResponse();
        forgivenessChatResponse.setConversationId(conversationId);
        forgivenessChatResponse.setReply(reply);
        return forgivenessChatResponse;
    }

    private int parseChange(String json) {
        try {
            Pattern p = Pattern.compile("\"change\"\\s*:\\s*([-+]?\\d+)");
            Matcher m = p.matcher(json);
            if (m.find()) {
                int val = Integer.parseInt(m.group(1));
                return Math.max(-10, Math.min(10, val));
            }
        } catch (Exception e) {
            log.warn("解析 change 失败: {}", e.getMessage());
        }
        return 0;
    }

    private String extractReply(String json) {
        try {
            Pattern p = Pattern.compile("\"reply\"\\s*:\\s*\"([^\"]*)\"");
            Matcher m = p.matcher(json);
            if (m.find()) {
                return m.group(1);
            }
        } catch (Exception e) {
            log.warn("解析 reply 失败: {}", e.getMessage());
        }
        return json;
    }

}
