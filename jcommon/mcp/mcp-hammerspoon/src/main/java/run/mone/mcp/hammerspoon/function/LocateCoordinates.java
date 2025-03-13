package run.mone.mcp.hammerspoon.function;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.AiMessage;

@Slf4j
@Data
@Component
public class LocateCoordinates {

    @Autowired
    private LLM llm;

    public String locateCoordinates(String objectDesc, String imageBase64) {
        String mimeType = "image/png";

        //1728×988
        String prompt = """
            请从图片中找到如下对象的位置: %s. 
            请定位到对象的中心位置(px). 请返回包含'x','y'两个key的json格式结果. 比如: {"x": 100, "y": 200}. 
            请注意图片的像素大小为长1728px，宽988px. 你返回的位置信息一定在这个范围内！
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
        return llm.chat(List.of(new AiMessage("user", prompt)));
    }

    
}
