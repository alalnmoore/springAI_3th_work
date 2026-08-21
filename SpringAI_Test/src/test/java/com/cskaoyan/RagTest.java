package com.cskaoyan;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.util.List;

@SpringBootTest
public class RagTest {
    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    VectorStore vectorStore;


    @Test
    public void buildVectorStore() {
        Resource resource = resourceLoader.getResource("classpath:document/library-handbook.txt");
        TextReader textReader = new TextReader(resource);
        textReader.getCustomMetadata().put("aaa", "bbb");
        List<Document> documents = textReader.get();
        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(150)
                // 小节段落通常在百字以上，80 个字符后再找边界，避免切得太碎
                .withMinChunkSizeChars(60)
                // 切完之后，长度小于 5 个字符的 chunk 会被直接丢弃（基本是噪声）
                .withMinChunkLengthToEmbed(5)
                // 示例文档较短，100 段已经足够作为保护上限
                .withMaxNumChunks(150)
                // 保留换行等分隔符，让段落格式更接近原文（方便后续阅读和调试）
                .withKeepSeparator(true)
                .build();


        List<Document> splitedDocuments = splitter.apply(documents);
        vectorStore.add(splitedDocuments);
        //增强
    }

    @Test
    public void testRetrieve() {

        SearchRequest searchRequest = SearchRequest.builder()
                .query("借书逾期怎么办？？")
                .similarityThreshold(0.7)
                .topK(3)
                .build();

        List<Document> documents = vectorStore.similaritySearch(searchRequest);
        documents.forEach(document -> System.out.println(document.getText()));
    }

    @Autowired
    ChatClient chatClient;
    @Autowired
    ChatMemory chatMemory;

    @Test
    public void testChat() {
        QuestionAnswerAdvisor answerAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder()
                        .similarityThreshold(0.7)
                        .topK(3)
                        .build())
                .build();

        String content = chatClient.prompt()
                .user("借书逾期怎么办？？")
                .advisors(answerAdvisor)
                .call()
                .content();
        System.out.println("content = " + content);

    }
    @Test
    public void testChat2() {
        QuestionAnswerAdvisor answerAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder()
                        .similarityThreshold(0.7)
                        .topK(3)
                        .build())
                .build();

        String content = chatClient.prompt()
                .user("自习座位如何预约？？")
                .advisors(answerAdvisor)
                .call()
                .content();
        System.out.println("content = " + content);

    }
}
