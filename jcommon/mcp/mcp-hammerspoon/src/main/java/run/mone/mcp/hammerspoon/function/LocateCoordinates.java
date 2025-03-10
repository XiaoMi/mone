package run.mone.mcp.hammerspoon.function;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.AiMessage;

@Slf4j
@Service
public class LocateCoordinates {

    @Autowired
    private LLM llm;

    public String locateCoordinates(String objectDesc, String imageBase64) {
        String prompt = """
            请从图片%s, 中找到如下对象的位置: %s. 
            请定位到对象的中心位置. 请返回包含'x','y'两个key的json格式结果. 比如: {"x": 100, "y": 200}. 
            如果有任何异常情况，比如找不到对象，或描述有歧义，无法定位到唯一结果，请返回包含'error' key的json结果, 例如: {"error":"你的反馈"}.
        """.formatted(imageBase64, objectDesc);
        return llm.chat(List.of(new AiMessage("user", prompt)));
    }

    
}
