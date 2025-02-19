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

}
