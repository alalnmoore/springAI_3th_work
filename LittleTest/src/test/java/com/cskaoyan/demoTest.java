package com.cskaoyan;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class demoTest {
    @Autowired
    private ChatClient.Builder builder;

    @Test
    public void test() {
        ChatClient build = builder.build();

        String content =
                build
                        // 开始设置提示词消息
                        .prompt()
                        // 设置用户消息
                        .user("哈哈哈你是什么模型")
                        // 向模型发送请求
                        .call()
                        // 获取模型返回的文本内容
                        .content();
        System.out.println(content);

    }
}
