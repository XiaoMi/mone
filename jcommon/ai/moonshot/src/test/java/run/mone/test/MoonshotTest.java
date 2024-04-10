package run.mone.test;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;
import run.mone.Moonshot;
import run.mone.bo.ChatCompletion;
import run.mone.bo.Message;

import java.nio.file.Paths;

/**
 * @author goodjava@qq.com
 * @date 2024/3/26 15:30
 */
public class MoonshotTest {


    @Test
    public void testGetFiles() {
        System.out.println(Moonshot.getFiles());
    }

    @Test
    public void test1() {
        System.out.println(Moonshot.uploadFile(Paths.get("/tmp/vv.png"), "file-extract"));
    }

    @Test
    public void testDeleteFile() {
        System.out.println(Moonshot.deleteFile("co18c5pkqq4ua4e5lb60"));
    }

    @Test
    public void testGetFileContent() {
        System.out.println(Moonshot.getFileContent("co18e6hkqq4ua4e5lcdg"));
    }


    @Test
    public void testGetModels() {
        System.out.println(Moonshot.getModels());
    }

    @Test
    public void testCall() {
        ChatCompletion data = Moonshot.call(Lists.newArrayList(Message.builder().role("system").content(Moonshot.getFileContent("co18e6hkqq4ua4e5lcdg")).build(), Message.builder().role("user").content("这个系统是什么系统").build()));
        System.out.println(data);
    }


    @Test
    public void test12() {
        JsonObject obj = new JsonObject();
        obj.add("a", new Gson().toJsonTree(Lists.newArrayList("1","2")));
    }

}
