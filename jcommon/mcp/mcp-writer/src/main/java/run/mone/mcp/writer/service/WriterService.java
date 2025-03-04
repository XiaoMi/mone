package run.mone.mcp.writer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.AiMessage;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class WriterService {

    @Autowired
    private LLM llm;

    // TODO: 需要修改
    public Flux<String> expandArticle(String article) {
        String prompt = "请扩写以下文章，增加更多细节和例子，使其更加丰富和具体：\n\n" + article;
        return Flux.create(sink -> {
            llm.chat(List.of(new AiMessage("user", prompt)), (content, jsonResponse) -> {
                sink.next(content);
                if ("[DONE]".equals(content.trim())) {
                    sink.complete();
                }
            });
        });
    }

    public Flux<String> summarizeArticle(String article) {
        String prompt = "请对以下文章进行总结，提炼出主要观点和关键信息：\n\n" + article;
        return Flux.create(sink -> {
            llm.chat(List.of(new AiMessage("user", prompt)), (content, jsonResponse) -> {
                sink.next(content);
                if ("[DONE]".equals(content.trim())) {
                    sink.complete();
                }
            });
        });
    }

    public Flux<String> writeNewArticle(String topic) {
        String prompt = "请以'" + topic + "'为主题，写一篇详细的文章，包括引言、主要论点、结论等部分。";
        return Flux.create(sink -> {
            llm.chat(List.of(new AiMessage("user", prompt)), (content, jsonResponse) -> {
                sink.next(content);
                if ("[DONE]".equals(content.trim())) {
                    sink.complete();
                }
            });
        });
    }

    public Flux<String> polishArticle(String article) {
        String prompt = "请对以下文章进行润色，提高其文笔和表达，使其更加优雅和专业：\n\n" + article;
        return Flux.create(sink -> {
            llm.chat(List.of(new AiMessage("user", prompt)), (content, jsonResponse) -> {
                sink.next(content);
                if ("[DONE]".equals(content.trim())) {
                    sink.complete();
                }
            });
        });
    }

    public Flux<String> suggestImprovements(String article) {
        String prompt = "请阅读以下文章，并提出具体的修改建议，包括结构、内容、论证等方面：\n\n" + article;
        return Flux.create(sink -> {
            llm.chat(List.of(new AiMessage("user", prompt)), (content, jsonResponse) -> {
                sink.next(content);
                if ("[DONE]".equals(content.trim())) {
                    sink.complete();
                }
            });
        });
    }

    public Flux<String> createOutline(String topic) {
        String prompt = "请为主题'" + topic + "'拟定一个详细的文章大纲，包括主要章节和各章节的要点：";
        return Flux.create(sink -> {
            llm.chat(List.of(new AiMessage("user", prompt)), (content, jsonResponse) -> {
                sink.next(content);
                if ("[DONE]".equals(content.trim())) {
                    sink.complete();
                }
            });
        });
    }

    public Flux<String> editArticle(String article, String instructions) {
        String prompt = "请根据以下修改指示，编辑和改进文章：\n\n原文：\n" + article + "\n\n修改指示：\n" + instructions;
        return Flux.create(sink -> {
            llm.chat(List.of(new AiMessage("user", prompt)), (content, jsonResponse) -> {
                sink.next(content);
                if ("[DONE]".equals(content.trim())) {
                    sink.complete();
                }
            });
        });
    }

    public Flux<String> translateText(String text, String targetLanguage) {
        String prompt = String.format("请将以下文本翻译成%s：\n\n%s", targetLanguage, text);
        return Flux.create(sink -> {
            llm.chat(List.of(new AiMessage("user", prompt)), (content, jsonResponse) -> {
                sink.next(content);
                if ("[DONE]".equals(content.trim())) {
                    sink.complete();
                }
            });
        });
    }
}