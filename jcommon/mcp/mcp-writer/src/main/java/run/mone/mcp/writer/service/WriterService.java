
package run.mone.mcp.writer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.AiMessage;

import java.util.List;

@Service
public class WriterService {

    @Autowired
    private LLM llm;

    public String expandArticle(String article) {
        String prompt = "请扩写以下文章，增加更多细节和例子，使其更加丰富和具体：\n\n" + article;
        return llm.chat(List.of(new AiMessage("user", prompt)));
    }

    public String summarizeArticle(String article) {
        String prompt = "请对以下文章进行总结，提炼出主要观点和关键信息：\n\n" + article;
        return llm.chat(List.of(new AiMessage("user", prompt)));
    }

    public String writeNewArticle(String topic) {
        String prompt = "请以'" + topic + "'为主题，写一篇详细的文章，包括引言、主要论点、结论等部分。";
        return llm.chat(List.of(new AiMessage("user", prompt)));
    }

    public String polishArticle(String article) {
        String prompt = "请对以下文章进行润色，提高其文笔和表达，使其更加优雅和专业：\n\n" + article;
        return llm.chat(List.of(new AiMessage("user", prompt)));
    }

    public String suggestImprovements(String article) {
        String prompt = "请阅读以下文章，并提出具体的修改建议，包括结构、内容、论证等方面：\n\n" + article;
        return llm.chat(List.of(new AiMessage("user", prompt)));
    }

    public String createOutline(String topic) {
        String prompt = "请为主题'" + topic + "'拟定一个详细的文章大纲，包括主要章节和各章节的要点：";
        return llm.chat(List.of(new AiMessage("user", prompt)));
    }

    public String editArticle(String article, String instructions) {
        String prompt = "请根据以下修改指示，编辑和改进文章：\n\n原文：\n" + article + "\n\n修改指示：\n" + instructions;
        return llm.chat(List.of(new AiMessage("user", prompt)));
    }

    public String translateText(String text, String targetLanguage) {
        // Implementation for translating text to the target language
        // This is a placeholder implementation. In a real-world scenario,
        // you would integrate with a translation service or API.
        return "Translated text: " + text + " (to " + targetLanguage + ")";
    }
}