package run.mone.mcp.idea.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.AiMessage;

import java.util.List;

@Service
public class IdeaService {

    @Autowired
    private LLM llm;

    public String reviewCode(String code) {
        String prompt = "请对以下代码进行review，提供改进建议：\n\n" + code;
        return llm.chat(List.of(new AiMessage("user", prompt)));
    }

    public String createComment(String code) {
        String prompt = "请对以下代码生成注释：\n\n" + code;
        return llm.chat(List.of(new AiMessage("user", prompt)));
    }

    public String gitPush(String code) {
        String prompt = "请对以下代码生成git提交的commit信息：\n\n" + code;
        return llm.chat(List.of(new AiMessage("user", prompt)));
    }

    public String methodRename(String code) {
        String prompt = "请对以下方法重命名：\n\n" + code;
        return llm.chat(List.of(new AiMessage("user", prompt)));
    }

}
