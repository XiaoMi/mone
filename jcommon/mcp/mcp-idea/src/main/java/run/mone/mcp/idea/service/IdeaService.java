package run.mone.mcp.idea.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.AiMessage;

@Service
public class IdeaService {

    @Autowired
    private LLM llm;

    public String reviewCode(String code) {
        String prompt = "请对以下代码进行review，提供改进建议(尽量一句话描述清楚)：\n\n" + code;
        return llm.chat(List.of(new AiMessage("user", prompt)));
    }

    public Flux<String> createComment(String code) {
        String prompt = "请对以下代码生成注释(尽量一句话,你的comment放到<comment></comment>中)：\n\n" + code;
        return Flux.create(sink -> {
            llm.chat(List.of(new AiMessage("user", prompt)), (content, jsonResponse) -> {
                sink.next(content);
            });
        });
    }

    public String gitPush(String code) {
        String prompt = "请对以下代码生成git提交的commit信息(你的commit信息放到<commit></commit>中)：\n\n" + code;
        return llm.chat(List.of(new AiMessage("user", prompt)));
    }

    public String methodRename(String code) {
        String prompt = "请对以下方法重命名(你只需返回方法名即可,你的方法名放到<methodName></methodName>中)：\n\n" + code;
        return llm.chat(List.of(new AiMessage("user", prompt)));
    }

    public String extractContent(String text, String tag) {
        String startTag = "<" + tag + ">";
        String endTag = "</" + tag + ">";
        int startIndex = text.indexOf(startTag);
        int endIndex = text.indexOf(endTag);
        
        if (startIndex != -1 && endIndex != -1) {
            return text.substring(startIndex + startTag.length(), endIndex);
        }
        return "";
    }

}
