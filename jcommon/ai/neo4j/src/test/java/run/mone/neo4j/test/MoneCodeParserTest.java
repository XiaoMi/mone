package run.mone.neo4j.test;

import org.junit.Test;
import run.mone.neo4j.MoneCodeParser;

/**
 * @author goodjava@qq.com
 * @date 2024/8/16 09:42
 */
public class MoneCodeParserTest {


    @Test
    public void testWriteCatServiceToNeo4j() {
//        new MoneCodeParser().writeJavaFilesToNeo4j("/Users/zhangzhiyong/IdeaProjects/ai/m78/m78-service/src/main/java/run/mone/m78/service");
//        MoneCodeParser.writeJavaFilesToNeo4j("/Users/zhangzhiyong/IdeaProjects/ai/m78/m78-service/src/main/java/run/mone/m78/service/database");
//        MoneCodeParser.writeJavaFilesToNeo4j("/Users/zhangzhiyong/IdeaProjects/ai/m78/m78-service/src/main/java/run/mone/m78/service/database/SqlParseUtil.java");
//        new MoneCodeParser().writeJavaFilesToNeo4j("/Users/zhangzhiyong/IdeaProjects/goodjava/mone/jcommon/ai/neo4j/src/test/java/run/mone/neo4j/test/A.java");
        new MoneCodeParser().writeJavaFilesToNeo4j("/Users/zhangzhiyong/IdeaProjects/goodjava/mone/jcommon/ai/neo4j/src/test/java/run/mone/neo4j/test/m");
    }

    @Test
    public void test1() {
        new MoneCodeParser().queryEntityClasses();
    }
}
