
package run.mone.mcp.coder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.AiMessage;

import java.util.List;

@Service
public class CoderService {

    @Autowired
    private LLM llm;

    public String writeUnitTest(String code) {
        String prompt = "请为以下代码编写单元测试：\n\n" + code;
        return llm.chat(List.of(new AiMessage("user", prompt)));
    }

    public String findCodeIssues(String code) {
        String prompt = "请分析以下代码，找出潜在的问题和改进建议：\n\n" + code;
        return llm.chat(List.of(new AiMessage("user", prompt)));
    }

    public String answerTechQuestion(String question) {
        String prompt = "请回答以下技术问题：\n\n" + question;
        return llm.chat(List.of(new AiMessage("user", prompt)));
    }

    public String writeCode(String description) {
        String prompt = "请根据以下描述编写代码：\n\n" + description;
        return llm.chat(List.of(new AiMessage("user", prompt)));
    }

    public String reviewCode(String code) {
        String prompt = "请对以下代码进行review，提供改进建议：\n\n" + code;
        return llm.chat(List.of(new AiMessage("user", prompt)));
    }

    public String designArchitecture(String description) {
        String prompt = "请根据以下需求设计系统架构：\n\n" + description;
        return llm.chat(List.of(new AiMessage("user", prompt)));
    }

    public String convertLanguage(String code, String sourceLanguage, String targetLanguage) {
        String prompt = "请将以下" + sourceLanguage + "代码转换为" + targetLanguage + "：\n\n" + code;
        return llm.chat(List.of(new AiMessage("user", prompt)));
    }
}
