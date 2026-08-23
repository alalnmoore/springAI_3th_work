package com.cskaoyan.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

@RestController
public class StreamController {

    @Autowired
    @Qualifier("CommonchatClient")
    private ChatClient chatClient;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<String, String>> stream(@RequestParam String message) {

        Flux<Map<String, String>> mapFlux = chatClient.prompt()
                .user(message)
                .stream()
                .content()
                .map(data -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("content", data);
                    return map;
                })
                .concatWith(Flux.just(Map.of("content", "end")));

        return mapFlux;
    }
}
