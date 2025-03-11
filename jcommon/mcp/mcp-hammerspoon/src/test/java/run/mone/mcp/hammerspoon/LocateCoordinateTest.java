/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package run.mone.mcp.hammerspoon;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.schema.AiMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

/**
 * @author shanwb
 * @date 2025-03-10
 */
public class LocateCoordinateTest {

    @Test
    void testLocate() throws IOException {
        LLMConfig config = LLMConfig.builder().llmProvider(LLMProvider.OPENROUTER).build();
        LLM llm = new LLM(config);

        String imageBase64 = getImageAsBase64("/Users/zhangzhiyong/eee.png");
        String mimeType = "image/png";
        String objectDesc = "图中内容为期权‘看跌’的数据，请你找到排名第二的成交价格的坐标";

        String prompt = """
            请从图片中找到如下对象的位置: %s. 
            请定位到对象的中心位置. 请返回包含'x','y'两个key的json格式结果. 比如: {"x": 100, "y": 200}. 
            如果有任何异常情况，比如找不到对象，或描述有歧义，无法定位到唯一结果，请返回包含'error' key的json结果, 例如: {"error":"你的反馈"}.
            请注意返回json即可，不要返回解释性的内容，不然会有不好的事情发生.
        """.formatted(objectDesc);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("role", "user");

        JsonArray contentArray = new JsonArray();
        JsonObject textObject = new JsonObject();
        textObject.addProperty("type", "text");
        textObject.addProperty("text", prompt);
        contentArray.add(textObject);

        JsonObject imageObject = new JsonObject();
        imageObject.addProperty("type", "image_url");

        JsonObject imageUrlObject = new JsonObject();
        imageUrlObject.addProperty("url", String.format("data:%s;base64,%s", mimeType, imageBase64));
        imageObject.add("image_url", imageUrlObject);

        contentArray.add(imageObject);

        jsonObject.add("content", contentArray);
        AiMessage msg = new AiMessage();
        msg.setJsonContent(jsonObject);
        String res = llm.chat(List.of(msg));
        System.out.println("坐标:" + res);
    }

    public static String getImageAsBase64(String imagePath) throws IOException {
        // 读取图片文件
        File file = new File(imagePath);
        FileInputStream imageInFile = new FileInputStream(file);

        // 将文件转换为字节数组
        byte[] imageData = new byte[(int) file.length()];
        imageInFile.read(imageData);

        // 关闭文件输入流
        imageInFile.close();

        // 将字节数组编码为Base64字符串
        String base64Image = Base64.getEncoder().encodeToString(imageData);

        return base64Image;
    }
}




