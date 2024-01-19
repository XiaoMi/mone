package run.mone.ultraman.test;

import com.google.gson.JsonObject;
import org.junit.Test;

/**
 * @author goodjava@qq.com
 * @date 2023/12/5 14:32
 */
public class GsonTest {


    @Test
    public void test1() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("a","1");
        System.out.println(jsonObject.get("a").isJsonPrimitive());
        System.out.println(jsonObject.get("a").getAsString());
    }

    //计算两数和1



}
