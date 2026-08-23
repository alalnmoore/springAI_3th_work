package com.cskaoyan.controller;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

@RestController
public class MultiModalController {


    @Autowired
    ChatClient chatClient;


    @PostMapping(value = "/img", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> testMultiModal(@RequestParam String message
            , MultipartFile img) {


        // 1. 把上传的图片包装为 Spring AI 中的 Media 对象
        //    第一个参数是图片的 MIME 类型（image/jpeg、image/png 等）
        //    第二个参数是图片的内容资源
        Media imageMedia = Media.builder()
                // 文件类型
                .mimeType(MimeType.valueOf(img.getContentType()))
                // 文件内容
                .data(img.getResource())
                .build();

       return chatClient.prompt()
                .user(user -> user
                        // 用户消息
                        .text(message)
                        // 非文本数据
                        .media(imageMedia)
                )
                .stream()
                .content();



    }
}
