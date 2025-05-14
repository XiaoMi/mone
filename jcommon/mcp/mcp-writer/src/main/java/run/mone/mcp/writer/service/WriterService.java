package run.mone.mcp.writer.service;

import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import run.mone.hive.configs.Const;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.schema.AiMessage;

@Service
public class WriterService {

    @Autowired
    private LLM llm;

    public Flux<String> expandArticle(String article, String originalRequest) {
        String prompt = getString("请扩写以下文章，增加更多细节和例子，使其更加丰富和具体：\n\n", originalRequest, article);
        return llm.call(List.of(new AiMessage("user", prompt)));
    }

    @NotNull
    private static String getString(String prompt, String originalRequest, String article) {
        if (originalRequest != null && !originalRequest.isEmpty()) {
            prompt += "用户原始需求: " + originalRequest + "\n\n";
        }
        prompt += article;
        return prompt;
    }

    // Overload for backward compatibility
    public Flux<String> expandArticle(String article) {
        return expandArticle(article, null);
    }

    public Flux<String> summarizeArticle(String article, String originalRequest) {
        String prompt = getString("请对以下文章进行总结，提炼出主要观点和关键信息：\n\n", originalRequest, article);
        return llm.call(List.of(new AiMessage("user", prompt)));
    }
    
    // Overload for backward compatibility
    public Flux<String> summarizeArticle(String article) {
        return summarizeArticle(article, null);
    }

    public Flux<String> writeNewArticle(String topic, Map<String, Object> arguments) {
        String prompt = "请以'" + topic + "'为主题，写一篇详细的文章(支持 散文 诗歌 小说片段 文档 作文 周报)";
        
        String originalRequest = (String) arguments.get("originalRequest");
        if (originalRequest != null && !originalRequest.isEmpty()) {
            prompt = "用户原始需求: " + originalRequest + "\n\n" + prompt;
        }
        
        LLM curLLm = llm;
        if (arguments.containsKey(Const.ROLE)) {
            ReactorRole role = (ReactorRole) arguments.get(Const.ROLE);
            if (null != role && role.getRoleConfig().containsKey("agent_llm")) {
                curLLm = new LLM(LLMConfig.builder().llmProvider(LLMProvider.valueOf(role.getRoleConfig().get("agent_llm"))).build());
            }
        }
        final String finalPrompt = prompt;
        final LLM finalLlm = curLLm;
        return finalLlm.call(List.of(new AiMessage("user", finalPrompt)));
    }

    public Flux<String> polishArticle(String article) {
        String prompt = "请对以下文章进行润色，提高其文笔和表达，使其更加优雅和专业：\n\n" + article;
        return llm.call(List.of(new AiMessage("user", prompt)));
    }

    public Flux<String> suggestImprovements(String article) {
        String prompt = "请阅读以下文章，并提出具体的修改建议，包括结构、内容、论证等方面：\n\n" + article;
        return llm.call(List.of(new AiMessage("user", prompt)));
    }

    public Flux<String> createOutline(String topic) {
        String prompt = "请为主题'" + topic + "'拟定一个详细的文章大纲，包括主要章节和各章节的要点：";
        return llm.call(List.of(new AiMessage("user", prompt)));
    }

    public Flux<String> editArticle(String article, String instructions) {
        String prompt = "请根据以下修改指示，编辑和改进文章：\n\n原文：\n" + article + "\n\n修改指示：\n" + instructions;
        return llm.call(List.of(new AiMessage("user", prompt)));
    }

    public Flux<String> translateText(String text, String targetLanguage) {
        String prompt = String.format("请将以下文本翻译成%s：\n\n%s", targetLanguage, text);
        return llm.call(List.of(new AiMessage("user", prompt)));
    }

    public Flux<String> generateCreativeIdeas(String topic, Object numberOfIdeasObj) {
        String numberOfIdeas = numberOfIdeasObj + "";
        String prompt = String.format("请为主题'%s'提供%s个创意写作构思，包括可能的角度、故事情节或独特观点：", topic, numberOfIdeas);
        return llm.call(List.of(new AiMessage("user", prompt)));
    }

    public Flux<String> createCharacterProfile(String characterDescription) {
        String prompt = "请根据以下描述，创建一个详细的人物形象，包括背景故事、性格特点、动机、外貌特征和说话方式：\n\n" + characterDescription;
        return llm.call(List.of(new AiMessage("user", prompt)));
    }

    public Flux<String> analyzeWritingStyle(String text) {
        String prompt = "请分析以下文本的写作风格，包括语言特点、修辞手法、句式结构、情感基调和整体风格特征：\n\n" + text;
        return llm.call(List.of(new AiMessage("user", prompt)));
    }

    public Flux<String> generateSeoContent(String keyword, String contentType) {
        String prompt = String.format("请以'%s'为关键词，创建一篇针对搜索引擎优化的%s，确保自然地融入关键词，并提供有价值的信息：", keyword, contentType);
        return llm.call(List.of(new AiMessage("user", prompt)));
    }

    public Flux<String> createResearchSummary(String researchText) {
        String prompt = "请对以下研究内容进行专业总结，提取关键发现、方法论、结论和潜在影响：\n\n" + researchText;
        return llm.call(List.of(new AiMessage("user", prompt)));
    }

    public Flux<String> rewriteForAudience(String content, String targetAudience) {
        String prompt = String.format("请将以下内容重写，使其适合%s阅读，调整语言、复杂度和表达方式：\n\n%s", targetAudience, content);
        return llm.call(List.of(new AiMessage("user", prompt)));
    }

    public Flux<String> generateDialogue(String scenario, int numberOfExchanges) {
        String prompt = String.format("请根据以下场景，创建一段包含%d个对话交流的真实对话：\n\n%s", numberOfExchanges, scenario);
        return llm.call(List.of(new AiMessage("user", prompt)));
    }

    public Flux<String> createMetaphorsAndAnalogies(String concept, String count) {
        String prompt = String.format("请为'%s'这个概念创建%s个生动的比喻和类比，帮助读者更好地理解：", concept, count);
        return llm.call(List.of(new AiMessage("user", prompt)));
    }

    public Flux<String> tellJoke(String jokeType) {
        String prompt = String.format("请讲一个%s类型的笑话，要求幽默、有趣且适合大众场合：", jokeType);
        return llm.call(List.of(new AiMessage("user", prompt)));
    }
}