package run.mone.local.docean.test;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import run.mone.local.docean.util.GsonUtils;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author goodjava@qq.com
 * @date 2024/3/4 11:36
 */
public class GsonTest {


    @Test
    public void testObjectToJsonObject() {
        // 创建一个测试对象
        TestObject testObj = new TestObject();
        testObj.setName("TestName");
        testObj.setValue(123);

        // 调用待测试的方法
        JsonObject jsonObject = GsonUtils.objectToJsonObject(testObj);

        // 验证结果是否符合预期
        assertNotNull("The result should not be null", jsonObject);
        assertTrue("The result should be a JsonObject", jsonObject.isJsonObject());
        assertEquals("TestName", jsonObject.get("name").getAsString());
        assertEquals(123, jsonObject.get("value").getAsInt());


        List<Integer> list = Lists.newArrayList(1,2,3,4);
        System.out.println(GsonUtils.objectToJsonObject(list));

    }

    // 测试用的内部类
    private static class TestObject {
        private String name;
        private int value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}

