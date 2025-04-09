package run.mone.mcp.idea.composer.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.schema.AiMessage;

import java.util.List;

@Service
public class IdeaService {

    private LLM llm = new LLM(LLMConfig.builder().llmProvider(LLMProvider.DEEPSEEK).build());

    public Flux<String> reviewCode(String code) {
        String prompt = "请对以下代码进行review，提供改进建议：\n\n" + code;
        return llm.call(List.of(new AiMessage("user", prompt)));
    }

    public Flux<String> createComment(String code) {
        String prompt = "请对以下代码生成注释,如果代码包含类,则生成类注释,如果只有方法则生成方法注释(只需要生成类注释或者方法注释),你只需要返回注释即可(尽量一句话,你的comment放到<comment></comment>中)：\n\n" + code;
        return llm.call(List.of(new AiMessage("user", prompt)));
    }

    public String gitPush(String code) {
        String prompt = "请对以下代码生成git提交的commit信息,尽量简短,尽量一句话(你的commit信息放到<commit></commit>中)：\n\n" + code;
        return llm.chat(List.of(new AiMessage("user", prompt)));
    }

    public String methodRename(String code) {
        String prompt = "请对以下方法重命名(你只需返回方法名即可,你的方法名放到<methodName></methodName>中 老名字放到<old></old>中)：\n\n" + code;
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

    public Flux<String> createMethod(String requirements, String classCode) {
        String prompt = """
                请根据以下需求生成一个Java方法的代码实现
                你必须遵守的:
                1.你只需要生成方法
                2.不要用markdown包裹
                3.代码用<code></code>包裹
                                
                example:
                <code>
                public int sum(int a,int b) {
                    return a+b;
                }
                </code>
                                
                当前class:
                %s
                                
                需求:
                """ + requirements;
        return llm.call(List.of(new AiMessage("user", prompt.formatted(classCode))));
    }

}
