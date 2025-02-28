
package run.mone.mcp.novel.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.AiMessage;

import java.util.List;

@Service
public class NovelService {

    @Autowired
    private LLM llm;

    public String extractPlotPoints(String novel) {
        String prompt = """
                # 小说情节提取指南
                您是一位经验丰富的文学编辑，曾负责过多部经典作品的情节设计和编辑工作。请以专业编辑的视角，对这篇小说进行系统的情节分析。
                                
                # 分析要求
                 - 提取所有事件，情节必须详细，要让看到的人能知道所有的前因后果
                 - 提取原则：只记录客观事实，不加入主观评价，省略心理活动，使用简洁现代语言
                 - 回答不需要任何形式的分段，只是一个简单的文本描述
                 
                下面是需要提取情节的小说：
                """
                + novel;
        return llm.chat(List.of(new AiMessage("user", prompt)));
    }


}