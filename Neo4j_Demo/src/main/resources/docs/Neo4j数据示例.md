// 创建演员节点
CREATE (:Person {name: '周星驰', born: 1962});
CREATE (:Person {name: '吴孟达', born: 1952});
CREATE (:Person {name: '元华', born: 1950});
CREATE (:Person {name: '黄圣依', born: 1983});
CREATE (:Person {name: '张柏芝', born: 1980});

// 创建电影节点
CREATE (:Movie {title: '功夫', released: 2004});
CREATE (:Movie {title: '少林足球', released: 2001});
CREATE (:Movie {title: '喜剧之王', released: 1999});
CREATE (:Movie {title: '大话西游', released: 1995});
CREATE (:Movie {title: '长江七号', released: 2008});

// 创建电影类型节点
CREATE (:Type {name: '喜剧'});
CREATE (:Type {name: '动作'});
CREATE (:Type {name: '爱情'});
CREATE (:Type {name: '科幻'});

// 创建演员出演电影的关系
MATCH (p:Person {name: '周星驰'}), (m:Movie {title: '功夫'})
CREATE (p)-[:ACTED_IN {role: '阿星'}]->(m);

MATCH (p:Person {name: '元华'}), (m:Movie {title: '功夫'})
CREATE (p)-[:ACTED_IN {role: '包租公'}]->(m);

MATCH (p:Person {name: '黄圣依'}), (m:Movie {title: '功夫'})
CREATE (p)-[:ACTED_IN {role: '芳儿'}]->(m);

MATCH (p:Person {name: '周星驰'}), (m:Movie {title: '少林足球'})
CREATE (p)-[:ACTED_IN {role: '五师兄'}]->(m);

MATCH (p:Person {name: '吴孟达'}), (m:Movie {title: '少林足球'})
CREATE (p)-[:ACTED_IN {role: '明锋'}]->(m);

MATCH (p:Person {name: '周星驰'}), (m:Movie {title: '喜剧之王'})
CREATE (p)-[:ACTED_IN {role: '尹天仇'}]->(m);

MATCH (p:Person {name: '张柏芝'}), (m:Movie {title: '喜剧之王'})
CREATE (p)-[:ACTED_IN {role: '柳飘飘'}]->(m);

MATCH (p:Person {name: '吴孟达'}), (m:Movie {title: '喜剧之王'})
CREATE (p)-[:ACTED_IN {role: '卧底'}]->(m);

MATCH (p:Person {name: '周星驰'}), (m:Movie {title: '大话西游'})
CREATE (p)-[:ACTED_IN {role: '至尊宝'}]->(m);

MATCH (p:Person {name: '吴孟达'}), (m:Movie {title: '大话西游'})
CREATE (p)-[:ACTED_IN {role: '二当家'}]->(m);

MATCH (p:Person {name: '周星驰'}), (m:Movie {title: '长江七号'})
CREATE (p)-[:ACTED_IN {role: '周铁'}]->(m);

// 创建电影所属类型的关系
MATCH (m:Movie {title: '功夫'}), (t:Type {name: '喜剧'})
CREATE (m)-[:BELONGS_TO]->(t);

MATCH (m:Movie {title: '功夫'}), (t:Type {name: '动作'})
CREATE (m)-[:BELONGS_TO]->(t);

MATCH (m:Movie {title: '少林足球'}), (t:Type {name: '喜剧'})
CREATE (m)-[:BELONGS_TO]->(t);

MATCH (m:Movie {title: '少林足球'}), (t:Type {name: '动作'})
CREATE (m)-[:BELONGS_TO]->(t);

MATCH (m:Movie {title: '喜剧之王'}), (t:Type {name: '喜剧'})
CREATE (m)-[:BELONGS_TO]->(t);

MATCH (m:Movie {title: '喜剧之王'}), (t:Type {name: '爱情'})
CREATE (m)-[:BELONGS_TO]->(t);

MATCH (m:Movie {title: '大话西游'}), (t:Type {name: '喜剧'})
CREATE (m)-[:BELONGS_TO]->(t);

MATCH (m:Movie {title: '大话西游'}), (t:Type {name: '爱情'})
CREATE (m)-[:BELONGS_TO]->(t);

MATCH (m:Movie {title: '长江七号'}), (t:Type {name: '喜剧'})
CREATE (m)-[:BELONGS_TO]->(t);

MATCH (m:Movie {title: '长江七号'}), (t:Type {name: '科幻'})
CREATE (m)-[:BELONGS_TO]->(t);