package com.cskaoyan.neo4j;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.neo4j.core.Neo4jClient;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class demoTest {

    @Autowired
    Neo4jClient neo4jClient;

    @Test
    public void test1() {
        String cypher = """
                match (m:Movie) 
                return m.title as movie,m.released as 上映时间 
                order by m.released desc
              
                """;
        Collection<Map<String, Object>> all =
                neo4jClient.query(cypher).fetch().all();
        all.forEach(System.out::println);
    }

    @Test
    public void test2() {

        String cypher= """
                MATCH (p:Person)-[:ACTED_IN]->(m:Movie {title: $movieTitle})
                        RETURN p.name AS actor
                        ORDER BY actor
                """;
        Collection<Map<String, Object>> all = neo4jClient.query(cypher)
                .bind("功夫").to("movieTitle")
                .fetch().all();

        all.forEach(System.out::println);
    }

    @Test
    public void test3() {
        String cypher= """
                MATCH (p:Person)-[:ACTED_IN]->(m:Movie)
                WHERE p.name IN $names
                        RETURN m.title AS 电影名称,
                        collect(distinct p.name) as 演员合集
                        ORDER BY size(演员合集) desc
                        LIMIT $limit
                """;
        List<String> actors = List.of("周星驰", "吴孟达", "元华");

        Collection<Map<String, Object>> all = neo4jClient.query(cypher).bindAll(Map.of("names", actors, "limit", 10))
                .fetch()
                .all();

        all.forEach(System.out::println);
    }

}

