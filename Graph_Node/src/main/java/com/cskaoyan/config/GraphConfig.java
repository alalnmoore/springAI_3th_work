package com.cskaoyan.config;

import com.alibaba.cloud.ai.graph.CompileConfig;
import com.cskaoyan.myAgent.MyAgent;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class GraphConfig {

    @Autowired
    private ChatModel chatModel;

    @Bean(name = "myagent")
    public MyAgent myAgent(){

        CompileConfig compileConfig = CompileConfig.builder().build();
        MyAgent myOwnFuckingAgent = new MyAgent("my_own_fucking_agent", "这他妈是我自己定义的工作流！！！", compileConfig, chatModel);
        return myOwnFuckingAgent;
    }

}
