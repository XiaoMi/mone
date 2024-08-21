package run.mone.neo4j.test;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import org.junit.Test;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.internal.InternalRecord;
import org.neo4j.driver.internal.InternalResult;
import org.neo4j.driver.internal.InternalSession;
import run.mone.neo4j.BotCall;
import run.mone.neo4j.MoneCodeParser;
import run.mone.neo4j.test.MoneCodeParserTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


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
        new MoneCodeParser().setPassword(System.getenv("password")).writeJavaFilesToNeo4j("/Users/zhangzhiyong/IdeaProjects/goodjava/mone/jcommon/ai/neo4j/src/test/java/run/mone/neo4j/test/m");
    }

    @Test
    public void test1() {
        new MoneCodeParser().queryEntityClasses();
    }




    @Test
    public void testFindClassesWithAnnotation() {
        MoneCodeParser moneCodeParser = new MoneCodeParser().setPassword(System.getenv("password"));
        List<Map<String, Object>> actual = moneCodeParser.findClassesWithAnnotation(moneCodeParser.getSession(), "Table");
        System.out.println(actual);

//        String xuqiu = "获取狗的主人";
        String xuqiu = "管理宠物鸟";
        String res = BotCall.call(moneCodeParser.readResourceFileContent("entity.md") + xuqiu, new Gson().toJson(actual));
        System.out.println(res);
    }



    @Test
    public void testFindClassesWithAnnotation2() {
        MoneCodeParser moneCodeParser = new MoneCodeParser().setPassword(System.getenv("password"));
        List<Map<String, Object>> actual = moneCodeParser.findClassesWithAnnotation(moneCodeParser.getSession(), "RestController");
        System.out.println(actual);

        String xuqiu = "获取狗的主人";
//        String xuqiu = "管理宠物鸟";
        String res = BotCall.call(moneCodeParser.readResourceFileContent("service.md") + xuqiu, new Gson().toJson(actual));
        System.out.println(res);
    }


    @Test
    public void testJson() {
        Map<String, String> m = ImmutableMap.of("input", "a+b=?");
        System.out.println(new Gson().toJson(m));
    }


    @Test
    public void testReadResourceFileContent() {
        String fileName = "entity.md";
        // Assuming the test resource file is already placed in the resources directory
        String actualContent = new MoneCodeParser().readResourceFileContent(fileName);

        assertNotNull(actualContent);
    }




}
