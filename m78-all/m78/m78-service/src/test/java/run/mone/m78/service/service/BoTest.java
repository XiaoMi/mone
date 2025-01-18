package run.mone.m78.service.service;

import org.junit.jupiter.api.Test;
import run.mone.m78.api.bo.flow.NodeInfo;
import run.mone.m78.service.bo.chat.ChatMessage;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.common.JsonElementUtils;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author goodjava@qq.com
 * @date 2024/5/9 10:20
 */
public class BoTest {



    @Test
    public void test1() {
        ChatMessage cm = ChatMessage.builder().message("msg").build();
        cm.setCmd("chat");
        String str = GsonUtils.gson.toJson(cm);

        ChatMessage cm2 = GsonUtils.gson.fromJson(str, ChatMessage.class);
        System.out.println(cm2);

    }



    @Test
    public void testFindValueTypeFromNodeInfo() {
        String jsonString = "{\"id\": 16151, \"nodeMetaInfo\": {\"desc\": \"\", \"extraInfo\": \"\", \"nodeName\": \"代码\", \"nodePosition\": {\"x\": \"1852\", \"y\": \"366\" } }, \"nodeType\": \"code\", \"outputs\": [{\"desc\": \"\", \"name\": \"numbers\", \"referenceInfo\": [], \"referenceName\": \"\", \"referenceNodeId\": 0, \"referenceSubName\": \"\", \"schema\": \"[{\\\"name\\\":\\\"a\\\",\\\"valueType\\\":\\\"Object\\\",\\\"children\\\":[],\\\"id\\\":\\\"160a4f52-2ac6-4714-9053-119c82f8d126\\\"}]\", \"subName\": \"\", \"type\": \"String\", \"value\": \"\", \"valueType\": \"Array<String>\" } ] }";

        NodeInfo nodeInfo = GsonUtils.gson.fromJson(jsonString, NodeInfo.class);
        // 设置nodeInfo的属性
        // 例如: nodeInfo.setOutputs(...);

        String referenceName = "numbers.0.a";
        String expectedValueType = "String";

        String actualValueType = JsonElementUtils.findValueTypeFromNodeInfo(nodeInfo, referenceName);
        System.out.println(actualValueType);

    }


	@Test
    public void testGetFieldType() {
        String jsonString = "[{\"name\":\"a\",\"valueType\":\"Object\",\"children\":[{\"name\":\"a1\",\"valueType\":\"String\",\"children\":[],\"id\":\"075c\"}],\"id\":\"f543\"}]";
        System.out.println(JsonElementUtils.getFieldType(jsonString, "a"));
        System.out.println(JsonElementUtils.getFieldType(jsonString, "a.a1"));


        String jsonString1 = "[{\"name\":\"a\",\"valueType\":\"Array<Object>\",\"children\":[{\"name\":\"a1\",\"valueType\":\"String\",\"children\":[],\"id\":\"c4c1977e-818a-40ac-bfb8-227ba0ee753e\"}],\"id\":\"0_outputList_0_a\"}]";
        System.out.println(JsonElementUtils.getFieldType(jsonString1, "a"));
        System.out.println(JsonElementUtils.getFieldType(jsonString1, "0.a.0.a1"));
    }


}
