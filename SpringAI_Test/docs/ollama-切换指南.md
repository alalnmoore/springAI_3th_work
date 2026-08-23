# 🦙 切换到本地 Ollama 部署的语言模型 - 配置指南

> 📅 文档创建：2026-08-23
>
> 🎯 适用场景：把 ChatClient 用的 LLM 从 DeepSeek 切换为本地 Ollama 部署的语言模型（如 `qwen2.5:3b`、`llama3.1:8b` 等），同时 Embedding 也由本地 Ollama 提供。

---

## 📑 目录

- [🔧 一、前提准备](#-一前提准备)
- [📦 二、pom.xml 修改](#-二pomxml-修改)
- [⚙️ 三、application.yml 修改](#️三applicationyml-修改)
- [🖥️ 四、SpringAiApplication.java 修改](#️四springaiapplicationjava-修改)
- [🧪 五、验证步骤](#-五验证步骤)
- [❓ 六、常见问题](#-六常见问题)
- [📌 七、配置项速查表](#-七配置项速查表)

---

## 🔧 一、前提准备

### 1️⃣ 安装并启动 Ollama

- 📥 [Ollama 官网下载](https://ollama.com/)
- ✅ 启动后默认监听 `http://127.0.0.1:11434`
- 🔍 用 `ollama list` 查看已下载的模型

### 2️⃣ 拉取 Chat 模型

```bash
# 小模型（推荐 16GB 内存起步）
ollama pull qwen2.5:3b

# 中等模型（需要更高配置）
ollama pull qwen2.5:7b
ollama pull llama3.1:8b

# 大模型（仅用于 Tool Calling、复杂结构化输出场景）
ollama pull qwen2.5:14b
```

### 3️⃣ 拉取 Embedding 模型

```bash
ollama pull bge-m3
# 或
ollama pull nomic-embed-text
```

> 💡 `bge-m3` 输出 1024 维向量，`nomic-embed-text` 输出 768 维向量，注意 ES 索引的 `dimensions` 要对应。

---

## 📦 二、pom.xml 修改

切换到本地 Ollama 后，**不再需要** OpenAI starter，但 Ollama starter 必须保留。

### ❌ 移除（或注释）OpenAI starter

```xml
<!-- 注释掉或删除 -->
<!--
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-openai</artifactId>
</dependency>
-->
```

### ✅ 确保保留 Ollama starter

```xml
<!-- 访问 ollama 部署的模型（Chat + Embedding 都用这个） -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-ollama</artifactId>
</dependency>
```

### 📋 完整依赖片段参考

```xml
<dependencies>
    <!-- Spring Boot 基础 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>

    <!-- ✅ Ollama 模型访问（Chat + Embedding） -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-ollama</artifactId>
    </dependency>

    <!-- ✅ 向量数据库（Elasticsearch） -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-elasticsearch</artifactId>
    </dependency>

    <!-- ✅ QuestionAnswerAdvisor（RAG 用） -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-advisors-vector-store</artifactId>
    </dependency>

    <!-- ❌ 不再需要 OpenAI starter -->
    <!--
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-openai</artifactId>
    </dependency>
    -->
</dependencies>
```

---

## ⚙️ 三、application.yml 修改

### 🎯 关键修改点

| 配置项 | 修改前（DeepSeek） | 修改后（本地 Ollama） |
|--------|------------------|-------------------|
| `spring.ai.model.chat` | `openai` | `ollama` |
| `spring.ai.model.embedding` | `ollama`（保持） | `ollama`（保持） |
| `spring.ai.openai.*` | 有配置 | **全部删除** |
| `spring.ai.ollama.chat.options.model` | `qwen2.5:3b` | 你要用的模型名 |

### 📝 完整 yml 模板

```yaml
server:
  port: 8080

spring:
  application:
    name: spring-ai

  # ===== Elasticsearch 连接配置 =====
  elasticsearch:
    uris: http://192.168.74.100:9200

  ai:
    # ===== 模型选择开关（关键！）=====
    model:
      chat: ollama        # ✅ Chat 用 Ollama
      embedding: ollama   # ✅ Embedding 也用 Ollama

    # ===== 本地 Ollama 配置 =====
    ollama:
      base-url: http://127.0.0.1:11434
      # Chat 模型配置
      chat:
        options:
          # 按你拉取的模型名修改！这里以 qwen2.5:3b 为例
          model: qwen2.5:3b
          # 可选参数
          # temperature: 0.7
          # top-p: 0.9
          # num-predict: 1000
      # Embedding 模型配置
      embedding:
        options:
          model: bge-m3

    # ===== 向量存储配置 =====
    vectorstore:
      elasticsearch:
        index-name: library-assistant
        # ⚠️ dimensions 必须和 embedding 模型输出维度一致！
        # bge-m3 = 1024
        # nomic-embed-text = 768
        dimensions: 1024
        initialize-schema: true

# ===== 日志（调试用）=====
logging:
  level:
    org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor: DEBUG
```

### ⚠️ 注意事项

1. **删除所有 `spring.ai.openai` 相关配置**：切换到纯 Ollama 后，OpenAI 配置完全不需要
2. **`dimensions` 必须和 embedding 模型输出维度匹配**：
   - `bge-m3` → 1024
   - `nomic-embed-text` → 768
   - `bge-large-en` → 1024
   - 如果维度不一致，写入向量库时会报错
3. **模型名要和 `ollama list` 中显示的完全一致**（包含 tag）

---

## 🖥️ 四、SpringAiApplication.java 修改

切换到纯 Ollama 后，**不再需要** `exclude = OpenAiEmbeddingAutoConfiguration.class`，可以恢复为最简形式：

```java
package com.cskaoyan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiApplication.class, args);
    }
}
```

> 💡 如果暂时不确定，保留 `exclude` 也没影响（找不到 OpenAI 类时会被忽略），但代码更干净。

---

## 🧪 五、验证步骤

### 1️⃣ 启动前自检

```bash
# 1. 确认 Ollama 服务运行中
ollama list

# 2. 确认模型已拉取
# 应该能看到 qwen2.5:3b 和 bge-m3

# 3. 测试 Chat 模型可用
ollama run qwen2.5:3b "你好"

# 4. 测试 Embedding 模型可用
curl http://127.0.0.1:11434/api/embeddings -d '{"model": "bge-m3", "prompt":"测试"}'
```

### 2️⃣ 启动应用

在 IDE 中运行 `SpringAiApplication`，观察日志：

- ✅ 应该看到 `OllamaChatModel` 的初始化（而非 OpenAI）
- ✅ 应该看到 `OllamaEmbeddingModel` 的初始化
- ✅ 不应出现任何 `OpenAi` 相关的 Bean 加载日志
- ✅ Tomcat 启动在 8080 端口

### 3️⃣ 调用测试

启动后用 Postman / curl 测试：

```bash
# 假设有一个 /ai/chat 接口
curl -X POST http://localhost:8080/ai/chat -H "Content-Type: application/json" -d '{"message":"你好"}'
```

---

## ❓ 六、常见问题

### Q1：启动报错 "Connection refused: 127.0.0.1:11434"

**原因**：Ollama 服务没启动，或者监听地址不是 127.0.0.1。

**解决**：
- Windows / macOS：从系统托盘确认 Ollama 图标存在
- Linux：执行 `systemctl status ollama`
- 如果 Ollama 在远程机器，把 `base-url` 改成对应地址

### Q2：启动报错 "model not found, try pulling it first"

**原因**：配置的模型名在本地 Ollama 中不存在。

**解决**：
```bash
ollama list              # 看实际有的模型名
ollama pull qwen2.5:3b   # 拉取你需要的模型
```

### Q3：向量化时报错 "dimension mismatch"

**原因**：`application.yml` 中的 `dimensions` 与 embedding 模型实际输出维度不一致。

**解决**：
- 用 `bge-m3` → 设置 `dimensions: 1024`
- 用 `nomic-embed-text` → 设置 `dimensions: 768`
- **修改后需要删除 ES 中已存在的索引**（旧的维度不对），让 Spring AI 重新创建：
  ```bash
  curl -X DELETE "http://192.168.74.100:9200/library-assistant"
  ```

### Q4：切换后报 Bean 冲突（Multiple ChatModel）

**原因**：pom.xml 没有正确注释掉 OpenAI starter，导致 classpath 中同时存在两个 ChatModel。

**解决**：确认 `spring-ai-starter-model-openai` 已注释或删除，重新执行 `mvn clean compile`。

### Q5：小模型 Tool Calling 失败

**原因**：3B 参数的小模型（如 `qwen2.5:3b`）不支持 Function Calling / Tool Calling。

**解决**：
- 换用 14B 及以上支持 tool use 的模型：`ollama pull qwen2.5:14b`
- 或者放弃 `@Tool`，把数据直接拼到 prompt 文本里（参见 `MobilePlanToolCallingController` 的做法）

### Q6：响应速度很慢

**原因**：本地推理受 CPU/GPU 限制，大模型特别慢。

**解决**：
- 换更小的模型
- 确认 Ollama 是否用了 GPU（执行 `ollama ps` 查看 PROCESSOR 列）
- 调小 `num-predict` 参数限制生成长度

---

## 📌 七、配置项速查表

### 🔧 模型选择开关

```yaml
spring:
  ai:
    model:
      chat: ollama       # openai / ollama / none
      embedding: ollama  # openai / ollama / none
```

### 🦙 Ollama 常用配置项

```yaml
spring:
  ai:
    ollama:
      base-url: http://127.0.0.1:11434    # Ollama 服务地址
      chat:
        options:
          model: qwen2.5:3b               # Chat 模型名
          temperature: 0.7                 # 创造性 0-1
          top-p: 0.9                       # 采样范围
          num-predict: 1000                 # 最大生成 token 数
      embedding:
        options:
          model: bge-m3                    # Embedding 模型名
```

### 📊 推荐的 Chat 模型

| 模型 | 大小 | 是否支持 Tool Calling | 推荐场景 |
|------|------|---------------------|---------|
| `qwen2.5:3b` | ~2GB | ❌ | 简单对话、学习测试 |
| `qwen2.5:7b` | ~4.7GB | ⚠️ 部分支持 | 一般对话 |
| `qwen2.5:14b` | ~9GB | ✅ | Tool Calling、结构化输出 |
| `llama3.1:8b` | ~4.7GB | ✅ | 通用场景 |
| `deepseek-r1:7b` | ~4.7GB | ❌ | 推理任务 |

### 📐 推荐的 Embedding 模型

| 模型 | 维度 | 大小 | 备注 |
|------|------|------|------|
| `bge-m3` | 1024 | ~1.2GB | 推荐，多语言支持好 |
| `nomic-embed-text` | 768 | ~274MB | 轻量，英文场景 |
| `bge-large-en` | 1024 | ~1.3GB | 英文场景 |

---

## 📚 相关文档

- 📖 [Ollama 官方文档](https://ollama.com/)
- 📖 [Spring AI Ollama 文档](https://docs.spring.io/spring-ai/reference/api/chat/ollama-chat.html)
- 📖 [Spring AI Embedding 文档](https://docs.spring.io/spring-ai/reference/api/embeddings/ollama-embeddings.html)

---

## 🎉 结语

切换到本地 Ollama 的核心就三步：

1. **改 pom.xml**：移除 OpenAI starter，保留 Ollama starter
2. **改 application.yml**：设置 `spring.ai.model.chat: ollama`，配置 `ollama.chat.options.model`
3. **改 SpringAiApplication.java**：移除 exclude 注解（可选）

整个过程**不需要改动任何业务代码**，Spring AI 的抽象层让模型切换变得非常简单！🚀
