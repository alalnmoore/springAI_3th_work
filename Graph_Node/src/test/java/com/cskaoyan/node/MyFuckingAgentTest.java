package com.cskaoyan.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.cskaoyan.myAgent.MyAgent;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.Optional;

@SpringBootTest
public class MyFuckingAgentTest {
    @Autowired
    private MyAgent myAgent;

    @Test
    public void TestGraph() throws GraphRunnerException {
        Map<String, Object> input = Map.of("input", "2008年的华裔中国人的诺贝尔化学奖的项目是啥？？");
        RunnableConfig runnableConfig = RunnableConfig
                .builder()
                .threadId("002")
                .build();
        Optional<OverAllState> invoke = myAgent.invoke(input, runnableConfig);
        invoke.ifPresent(state -> {
            String ResultMessage = state.value("answer")
                    .orElseThrow(() -> new RuntimeException("No answer"))
                    .toString();
            System.out.println(ResultMessage);
        });
    }
}
