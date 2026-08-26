package com.cskaoyan.reactAgent.controller;

import com.cskaoyan.reactAgent.DTO.LibraryChatRequest;
import com.cskaoyan.reactAgent.service.LibraryAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/react-agent/library")
public class LibraryReactAgentController {

        @Autowired
        private LibraryAgentService libraryAgentService;

        @PostMapping("/chat")
        public String chat(@RequestBody LibraryChatRequest request) {
            return libraryAgentService.chat(request.getThreadId(), request.getMessage());
        }

        @DeleteMapping("/memory/{threadId}")
        public String clearMemory(@PathVariable String threadId) {
            libraryAgentService.clearMemory(threadId);
            return "会话记忆已清除";
        }

}
