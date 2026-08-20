package com.cskaoyan.controller;

import com.cskaoyan.model.ForgivenessChatRequest;
import com.cskaoyan.model.ForgivenessChatResponse;
import com.cskaoyan.service.ForgivenessGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/forgiveness")
public class ForgivenessGameController {

    @Autowired
    private ForgivenessGameService forgivenessGameService;

    @PostMapping("/chat")
    public ForgivenessChatResponse chat(@RequestBody ForgivenessChatRequest request) {
        return forgivenessGameService.chat(request);
    }
}