# 🌸 Spring AI 学习手册

> 🚀 一份关于 Spring AI 的详细学习笔记，涵盖核心概念、常用组件、实战要点与最佳实践。
>
> 📅 最后更新：2026-08-20

---

## 📑 目录

- [🌟 项目简介](#-项目简介)
- [⚙️ 环境配置](#️-环境配置)
- [💡 核心概念](#-核心概念)
- [🔧 核心组件](#-核心组件)
- [💬 ChatClient 详解](#-chatclient-详解)
- [🧠 记忆机制 ChatMemory](#-记忆机制-chatmemory)
- [🎓 Advisors 顾问](#-advisors-顾问)
- [📖 RAG 检索增强生成](#-rag-检索增强生成)
- [🛠️ Function Calling 函数调用](#️-function-calling-函数调用)
- [🖼️ 多模态](#️-多模态)
- [🔄 流式响应 Streaming](#-流式响应-streaming)
- [📊 向量数据库](#-向量数据库)
- [🎯 最佳实践](#-最佳实践)
- [📚 参考资源](#-参考资源)

---

## 🌟 项目简介

**Spring AI** 是 Spring 官方推出的 AI 应用开发框架，旨在简化 AI 大模型（LLM）的集成与应用开发。它提供了一套统一的 API 抽象，让你可以像使用 Spring Data、Spring Security 一样，优雅地在 Spring Boot 应用中集成各种 AI 能力。

### ✨ 核心特性

- 🌐 **统一的 API 抽象**：一套代码，支持 OpenAI、Anthropic、Ollama、通义千问、智谱 等多种大模型
- 🔌 **Spring 生态无缝集成**：与 Spring Boot、Spring Cloud 深度融合
- 🧠 **内置记忆机制**：轻松实现对话上下文管理
- 📖 **RAG 支持**：开箱即用的检索增强生成能力
- 🛠️ **Function Calling**：让大模型调用你的 Java 方法
- 🖼️ **多模态**：支持文本、图片、音频等多种输入输出
- 📊 **向量数据库集成**：支持 Milvus、Pinecone、Chroma、Redis 等

---

## ⚙️ 环境配置

### 📦 依赖管理

- 🟢 **Spring Boot Parent**：继承 Spring Boot 父项目，统一管理 Spring 全家桶版本
- 📦 **Spring AI BOM**：通过 BOM（物料清单）统一管理 Spring AI 相关依赖版本，避免版本冲突
- 🔌 **模型 Starter**：按需选择对应大模型的 Starter，例如 OpenAI、Ollama、通义千问 等

### 🔑 配置要点

- 🌍 **API 地址**：配置对应服务商的接口地址（base-url）
- 🔐 **API Key**：强烈推荐使用环境变量注入，**绝对不要硬编码到代码或提交到 Git**
- 🌡️ **模型参数**：可调整 temperature（创造性）、max-tokens（长度）、top-p（采样范围）等
- 🧪 **本地配置**：把含密钥的配置放到 `application-local.yml`，并在 `.gitignore` 中忽略

> ⚠️ **安全提示**：API Key 一旦泄露到 Git 历史中，即使后续删除也仍可被找回。请务必从源头防护。

---

## 💡 核心概念

### 🏗️ 整体架构

整个 Spring AI 应用的调用链路可以抽象为以下层级：

1. 🎯 **业务层**：Controller → Service，处理用户请求
2. 💬 **ChatClient**：业务开发的高层入口，类似 `RestTemplate`
3. 🎓 **Advisors + Memory**：顾问拦截器与对话记忆
4. 📦 **ChatModel**：真正调用大模型的底层抽象
5. 🌐 **大模型服务商**：OpenAI / Ollama / 通义千问 / 智谱 等

### 🎭 核心抽象一览

| 抽象 | 说明 | 使用场景 |
|------|------|---------|
| 🎯 **ChatModel** | 底层模型抽象，直接与 LLM 通信 | 需要细粒度控制时使用 |
| 💬 **ChatClient** | 高层 API，类似 `RestTemplate` | 业务开发推荐使用 |
| 📝 **EmbeddingModel** | 文本向量化模型抽象 | RAG、相似度计算 |
| 🖼️ **ImageModel** | 图像生成模型抽象 | 文生图 |
| 🔊 **AudioModel** | 语音模型抽象 | TTS / ASR |
| 📊 **VectorStore** | 向量存储抽象 | 知识库检索 |

---

## 🔧 核心组件

### 📦 组件协作流程

- 💬 **ChatClient** 是业务层入口，所有请求从它发起
- 🎓 **Advisors** 像 AOP 拦截器，在请求前后插入横切逻辑（日志、记忆、RAG 等）
- 🧠 **ChatMemory** 负责存取历史对话，由 Advisor 自动维护
- 📦 **ChatModel** 是真正调用大模型的地方
- 📊 **VectorStore + EmbeddingModel** 提供知识库检索能力

整体调用链：`ChatClient → Advisors → ChatMemory → ChatModel → LLM`

---

## 💬 ChatClient 详解

`ChatClient` 是业务开发的主要入口，API 设计借鉴了 Spring 的 `RestTemplate` / `WebClient`。

### 🌱 关键能力

- 🎤 **Prompt 构建**：流式 API 链式构建请求（user / system / advisors / call）
- 🤖 **默认配置**：可设置默认系统提示词、默认 Advisor，避免每次重复
- 📦 **结构化输出**：直接把 AI 回复映射为 Java 对象（record / POJO），无需手动解析 JSON
- 🌊 **流式响应**：支持返回 `Flux`，逐字输出
- 🛠️ **函数调用**：注册 Java 函数，让 LLM 自动决定是否调用

### 📋 调用流程

1. 🧱 通过 `ChatClient.Builder` 创建 `ChatClient` 实例
2. 📝 使用 `prompt()` 方法开始构建请求
3. 🎤 链式调用 `.user()`、`.system()`、`.advisors()` 等方法
4. 🚀 调用 `.call()` 同步获取结果，或 `.stream()` 流式获取
5. 📦 通过 `.content()` 拿字符串，或 `.entity(Class)` 拿对象

---

## 🧠 记忆机制 (ChatMemory)

让大模型"记住"之前的对话，是多轮对话场景的关键。

### 📚 三种内置实现

| 实现 | 存储位置 | 适用场景 |
|------|---------|---------|
| 🗃️ **InMemoryChatMemory** | JVM 内存 | 开发测试、单机应用 |
| 🟥 **RedisChatMemory** | Redis | 生产环境、分布式 |
| 🟦 **JdbcChatMemory** | 关系型数据库 | 持久化存储 |

### 🎯 工作原理

- 📥 **请求前**：Advisor 自动从 `ChatMemory` 中取出该会话的历史消息，拼接到 prompt 中
- 📤 **响应后**：把新一轮的 user 消息和 AI 回复存回 `ChatMemory`
- 🆔 **会话隔离**：通过 `CONVERSATION_ID` 区分不同用户/会话，避免串话

### 💡 使用要点

- 🆔 为每个用户/会话生成唯一 ID（UUID）
- 🗃️ 生产环境优先选择 Redis 或 JDBC 实现，避免节点重启丢失上下文
- 🧹 注意设置历史消息保留窗口，避免 prompt 过长导致 token 超限

---

## 🎓 Advisors (顾问)

Advisor 是 Spring AI 的"AOP"——拦截请求和响应，实现横切逻辑，是 Spring AI 最优雅的设计之一。

### 🔧 常用 Advisor

| Advisor | 作用 |
|---------|------|
| 🔍 **SimpleLoggerAdvisor** | 打印请求 / 响应日志，调试必备 |
| 🧠 **MessageChatMemoryAdvisor** | 管理对话记忆，自动拼接历史消息 |
| 📖 **QuestionAnswerAdvisor** | RAG 检索增强，自动注入知识库内容 |
| 🛠️ **FunctionCallbackAdvisor** | 函数调用，让 LLM 调用 Java 方法 |
| 🛡️ **SafeAdvisor** | 内容安全过滤（需自定义） |

### 🎯 Advisor 的两个钩子

- 🔼 **before**：在请求发送给 LLM 之前执行，可修改 prompt、注入上下文
- 🔽 **after**：在 LLM 响应之后执行，可处理输出、记录日志

### 💡 使用要点

- 🧩 可同时配置多个 Advisor，按顺序执行
- 📝 自定义 Advisor 可实现敏感词过滤、审计日志、限流等功能
- ⚠️ 注意 Advisor 顺序，记忆 Advisor 应在 RAG 之前

---

## 📖 RAG 检索增强生成

> **RAG (Retrieval-Augmented Generation)** 是目前企业 AI 落地最主流的方案，让 LLM 基于私有知识库回答，减少幻觉。

### 🔄 RAG 工作流程

1. 📚 **文档预处理**：把 PDF / Word / Markdown 切分成 chunks（片段）
2. 🔢 **向量化**：用 `EmbeddingModel` 把每个 chunk 转为向量
3. 🗄️ **存入向量库**：把向量 + 原文存到 `VectorStore`
4. ❓ **用户提问**：把问题同样向量化
5. 🔍 **相似度检索**：在向量库找出最相似的 Top-K 文档
6. 📝 **拼装 Prompt**：问题 + 检索到的文档 + 指令
7. 🚀 **LLM 生成**：调用 ChatModel，输出答案（可附引用来源）

### 💡 使用要点

- 📏 **chunk 大小**：通常 500-1000 字符，过大噪声多，过小语义割裂
- 🔢 **Top-K**：检索 3-5 篇即可，太多会冲淡注意力
- 🎯 **重排序（Rerank）**：可选步骤，对召回结果二次排序提升精度
- 📑 **引用来源**：在 prompt 中要求 LLM 标注引用，提升可信度

---

## 🛠️ Function Calling (函数调用)

让 LLM 调用你写好的 Java 方法（查数据库、调 API、执行业务逻辑），是 AI Agent 的基础。

### 🎯 工作原理

1. 🛠️ 你定义 Java 函数，并描述其功能
2. 📋 Spring AI 把函数描述注册到 LLM
3. 🤔 LLM 根据用户问题，判断是否需要调用某函数
4. 🚀 LLM 返回函数名 + 参数（JSON）
5. ⚙️ Spring AI 反射调用对应 Java 方法
6. 📦 把方法返回值再喂给 LLM
7. 💬 LLM 综合函数结果给出最终回答

### 💡 使用要点

- 📝 **描述清晰**：函数描述写得越清晰，LLM 越能正确调用
- 🔒 **参数校验**：LLM 可能传错参数，务必校验
- 🧩 **组合调用**：可注册多个函数，LLM 会自动编排
- ⏱️ **超时控制**：函数执行可能慢，注意设置超时

---

## 🖼️ 多模态

支持图片、音频等多种输入输出，让 AI 应用更丰富。

### 🎨 支持的模态

| 模态 | 输入 | 输出 |
|------|------|------|
| 📝 文本 | ✅ | ✅ |
| 🖼️ 图片 | ✅（视觉理解） | ✅（文生图） |
| 🔊 音频 | ✅（语音识别） | ✅（语音合成） |
| 🎥 视频 | 部分 | 部分 |

### 💡 使用要点

- 🖼️ **视觉理解**：可上传图片让 LLM 描述内容、识别物体
- 🎨 **文生图**：调用 `ImageModel`，通过文字描述生成图片
- 📦 **资源传递**：支持本地文件、URL、ClassPath 资源
- 💰 **成本注意**：多模态调用 token 消耗显著高于纯文本

---

## 🔄 流式响应 (Streaming)

实现类似 ChatGPT 的打字机效果，显著提升用户体验。

### 🌊 工作原理

- 📡 LLM 是逐 token 生成的，流式调用把这些 token 实时推送给客户端
- 🌊 后端返回 `Flux<String>`，前端用 SSE（Server-Sent Events）接收
- ⚡ 用户无需等待完整响应，首字时间大幅缩短

### 💡 使用要点

- 📡 后端用 `.stream()` 替代 `.call()`，返回类型为 `Flux`
- 🌐 控制器需设置 `produces = "text/event-stream"`
- 📺 前端使用 `EventSource` 或 `fetch` + `ReadableStream` 接收
- ❌ 注意错误处理与连接断开重连
- 🧠 流式也可配合 Memory、Advisor 一起使用

---

## 📊 向量数据库

RAG 场景下的核心基础设施，用于存储和检索向量化后的知识。

### 🗄️ 常见向量库对比

| 向量库 | 特点 | 适用场景 |
|--------|------|---------|
| 🟢 **Milvus** | 开源、高性能、可扩展 | 大规模生产 |
| 🍍 **Pinecone** | 全托管 SaaS | 快速上线、无运维 |
| 🟦 **Chroma** | 轻量、易上手 | 开发测试、小项目 |
| 🟥 **Redis** | 内存级速度、通用 | 已有 Redis 基础设施 |
| 🟪 **Weaviate** | 开源、支持混合检索 | 复杂检索场景 |
| 🟧 **PgVector** | 基于 PostgreSQL | 已有 PG 数据库 |
| 🟦 **Elasticsearch** | 全文 + 向量混合 | 已有 ES 基础设施 |

### 💡 选型建议

- 🧪 **开发测试**：Chroma 或 InMemory 即可
- 🏭 **生产单机**：PgVector（如果已有 Postgres）或 Redis
- 🚀 **大规模生产**：Milvus 或 Pinecone
- 📚 **混合检索**：Weaviate 或 Elasticsearch

---

## 🎯 最佳实践

### 🔐 安全

- 🚫 **API Key 不入库**：使用环境变量 / 配置中心 / Vault
- 🛡️ **输入校验**：对用户输入做长度、敏感词过滤，避免 prompt 注入
- 📋 **权限隔离**：按用户/租户隔离记忆与知识库
- 🔍 **审计日志**：记录每次 LLM 调用，便于追溯

### ⚡ 性能

- 🌊 **流式优先**：长回答用 Streaming，首字延迟更低
- 📦 **结构化输出**：需要解析的场景用 `entity(Class)`，省去 JSON 解析
- 🧠 **记忆窗口**：限制历史消息数量，避免 prompt 过长
- 🔢 **Embedding 缓存**：相同文本的向量缓存复用
- 💤 **异步调用**：非交互场景用响应式（Reactor）提高吞吐

### 💰 成本

- 📏 **控制 token**：合理设置 `max-tokens`，避免无效消耗
- 🧠 **选小模型**：简单任务用 GPT-4o-mini / Qwen-turbo，复杂任务再上大模型
- 📦 **缓存答案**：高频相同问题，缓存命中直接返回
- 📊 **RAG 优于微调**：知识更新用 RAG，不要轻易微调

### 🏗️ 工程化

- 🧪 **可测试**：抽出 LLM 调用层，方便 Mock
- 🔁 **可降级**：主模型不可用时切换备选模型
- 📈 **可监控**：监控调用量、延迟、错误率、成本
- 🔄 **可灰度**：新 prompt 上线前先小流量验证

---

## 📚 参考资源

### 📖 官方文档

- 🏠 [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)
- 💻 [Spring AI GitHub](https://github.com/spring-projects/spring-ai)
- 📰 [Spring 官方博客](https://spring.io/blog)

### 🎓 学习资源

- 📚 [Spring 官方示例项目](https://github.com/spring-projects/spring-ai-examples)
- 🎥 [Spring Academy 视频课程](https://spring.academy)
- 📝 [Baeldung Spring AI 教程](https://www.baeldung.com/spring-ai)

### 🌏 国内大模型适配

- 🇨🇳 [阿里通义千问 Spring AI Alibaba](https://github.com/alibaba/spring-ai-alibaba)
- 🇨🇳 智谱 ChatGLM 适配
- 🇨🇳 百度文心一言适配
- 🇨🇳 讯飞星火适配

### 🤝 社区

- 💬 [Spring AI 社区讨论](https://github.com/spring-projects/spring-ai/discussions)
- 🐛 [Issue 反馈](https://github.com/spring-projects/spring-ai/issues)
- 📧 Stack Overflow 标签 `spring-ai`

---

## 🎉 结语

Spring AI 把复杂的 AI 能力封装成了 Spring 生态熟悉的样子——`ChatClient` 像 `RestTemplate`，`Advisor` 像 AOP，`VectorStore` 像 `Repository`。

对 Java 开发者而言，这是上手 AI 应用开发最低成本、最顺畅的路径。从最简单的 ChatClient 调用开始，逐步加入 Memory、RAG、Function Calling，你就能构建出功能丰富的 AI 应用。🚀

---

> 📝 本文档持续更新中，如有疏漏欢迎指正！
