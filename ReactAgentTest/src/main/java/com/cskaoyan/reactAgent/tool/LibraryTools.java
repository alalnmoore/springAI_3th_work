package com.cskaoyan.reactAgent.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component

public class LibraryTools {
        private final List<String> books = List.of(
                "《Spring Boot 实战》：适合学习 Spring Boot 基础和项目开发",
                "《Spring Cloud 微服务实战》：适合学习注册中心、配置中心、网关等微服务组件",
                "《深入理解 Java 虚拟机》：适合学习 JVM、内存模型和性能调优",
                "《Redis 设计与实现》：适合学习缓存、数据结构和 Redis 底层原理",
                "《MySQL 技术内幕》：适合学习索引、事务和 SQL 优化"
        );

        /**
         * 查询所有图书
         */
        @Tool(description = "查询图书馆的藏书信息")
        public String findAllBooks() {
            return String.join("\n", books);
        }

        /**
         * 根据关键词搜索图书
         */
        @Tool(description = "根据关键词搜索图书")
        public String searchBooks(String keyword) {
            List<String> matched = books.stream()
                    .filter(book -> book.contains(keyword))
                    .toList();

            if (matched.isEmpty()) {
                return "未找到与 \"" + keyword + "\" 相关的书籍";
            }

            return "找到 " + matched.size() + " 本与 \"" + keyword + "\" 相关的书籍：\n"
                    + String.join("\n", matched);
        }
}
