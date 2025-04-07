package run.mone.mcp.writer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.AiMessage;

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
        return llm.call(List.of(new AiMessage("user", prompt)));
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

    public Flux<String> generateCreativeIdeas(String topic, Object numberOfIdeasObj) {
        String numberOfIdeas = numberOfIdeasObj + "";
        String prompt = String.format("请为主题'%s'提供%s个创意写作构思，包括可能的角度、故事情节或独特观点：", topic, numberOfIdeas);
        return Flux.create(sink -> {
            llm.chat(List.of(new AiMessage("user", prompt)), (content, jsonResponse) -> {
                sink.next(content);
                if ("[DONE]".equals(content.trim())) {
                    sink.complete();
                }
            });
        });
    }

    public Flux<String> createCharacterProfile(String characterDescription) {
        String prompt = "请根据以下描述，创建一个详细的人物形象，包括背景故事、性格特点、动机、外貌特征和说话方式：\n\n" + characterDescription;
        return Flux.create(sink -> {
            llm.chat(List.of(new AiMessage("user", prompt)), (content, jsonResponse) -> {
                sink.next(content);
                if ("[DONE]".equals(content.trim())) {
                    sink.complete();
                }
            });
        });
    }

    public Flux<String> analyzeWritingStyle(String text) {
        String prompt = "请分析以下文本的写作风格，包括语言特点、修辞手法、句式结构、情感基调和整体风格特征：\n\n" + text;
        return Flux.create(sink -> {
            llm.chat(List.of(new AiMessage("user", prompt)), (content, jsonResponse) -> {
                sink.next(content);
                if ("[DONE]".equals(content.trim())) {
                    sink.complete();
                }
            });
        });
    }

    public Flux<String> generateSeoContent(String keyword, String contentType) {
        String prompt = String.format("请以'%s'为关键词，创建一篇针对搜索引擎优化的%s，确保自然地融入关键词，并提供有价值的信息：", keyword, contentType);
        return Flux.create(sink -> {
            llm.chat(List.of(new AiMessage("user", prompt)), (content, jsonResponse) -> {
                sink.next(content);
                if ("[DONE]".equals(content.trim())) {
                    sink.complete();
                }
            });
        });
    }

    public Flux<String> createResearchSummary(String researchText) {
        String prompt = "请对以下研究内容进行专业总结，提取关键发现、方法论、结论和潜在影响：\n\n" + researchText;
        return Flux.create(sink -> {
            llm.chat(List.of(new AiMessage("user", prompt)), (content, jsonResponse) -> {
                sink.next(content);
                if ("[DONE]".equals(content.trim())) {
                    sink.complete();
                }
            });
        });
    }

    public Flux<String> rewriteForAudience(String content, String targetAudience) {
        String prompt = String.format("请将以下内容重写，使其适合%s阅读，调整语言、复杂度和表达方式：\n\n%s", targetAudience, content);
        return Flux.create(sink -> {
            llm.chat(List.of(new AiMessage("user", prompt)), (responseContent, jsonResponse) -> {
                sink.next(responseContent);
                if ("[DONE]".equals(responseContent.trim())) {
                    sink.complete();
                }
            });
        });
    }

    public Flux<String> generateDialogue(String scenario, int numberOfExchanges) {
        String prompt = String.format("请根据以下场景，创建一段包含%d个对话交流的真实对话：\n\n%s", numberOfExchanges, scenario);
        return Flux.create(sink -> {
            llm.chat(List.of(new AiMessage("user", prompt)), (content, jsonResponse) -> {
                sink.next(content);
                if ("[DONE]".equals(content.trim())) {
                    sink.complete();
                }
            });
        });
    }

    public Flux<String> createMetaphorsAndAnalogies(String concept, String count) {
        String prompt = String.format("请为'%s'这个概念创建%s个生动的比喻和类比，帮助读者更好地理解：", concept, count);
        return Flux.create(sink -> {
            llm.chat(List.of(new AiMessage("user", prompt)), (content, jsonResponse) -> {
                sink.next(content);
                if ("[DONE]".equals(content.trim())) {
                    sink.complete();
                }
            });
        });
    }

    public Flux<String> tellJoke(String jokeType) {
        String prompt = String.format("请讲一个%s类型的笑话，要求幽默、有趣且适合大众场合：", jokeType);
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