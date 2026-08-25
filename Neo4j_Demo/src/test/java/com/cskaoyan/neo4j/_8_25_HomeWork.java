package com.cskaoyan.neo4j;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.neo4j.core.Neo4jClient;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class _8_25_HomeWork {
    @Autowired
    private Neo4jClient neo4jClient;

    @Test
    //查询所有疾病以及
    public void test1() {
        String cypher = """
                match (s:Symptom)
                return s.name as 疾病名称
                
                """;
        Collection<Map<String, Object>> all =
                neo4jClient.query(cypher).fetch().all();
        all.forEach(System.out::println);
        System.out.println("==============================================");

        //以及每个疾病所属的科室。
        String cypher2= """
                match (d1:Disease)-[:belongs_to]->(d2:Department)
                return d1.name as 疾病名称,d2.name as 科室名称
                """;
        neo4jClient.query(cypher2).fetch().all().forEach(System.out::println);
    }

    @Test
    //查询具有“发热”症状的疾病，返回疾病名称、疾病描述
    public void test2() {

        String cypher = """
                match (d:Disease)-[:has_symptom]->(s:Symptom)
                where s.name = '发热'
                return d.name as 疾病名称,d.desc as 疾病描述
                """;
        neo4jClient.query(cypher).fetch().all().forEach(System.out::println);
    }

    @Test
    //假设用户输入的症状是"发热、咳嗽、咽痛",
    //请查询可能的疾病，并按照匹配到的症状数量从高到低排序,
    //返回字段包括：疾病名称、所属科室、匹配到的症状列表、匹配数量
    public void Test3() {

        List<String> symptom = List.of("发热、咳嗽、咽痛");

        String cypher = """
                with['发热','咳嗽','咽痛'] as inputSymptoms
                match (d:Disease)-[:has_symptom]->(s:Symptom)
                where s.name in inputSymptoms
                with d,collect(distinct s.name) as matchedSymptoms
                match (d)-[:belongs_to]->(dept:Department)
                return d.name as 可能的疾病,
                dept.name as 科室名称,
                matchedSymptoms as 症状列表,
                 size(matchedSymptoms) AS 匹配数量
                ORDER BY 匹配数量 DESC;
                """;
        neo4jClient.query(cypher).fetch().all().forEach(System.out::println);
    }
}
