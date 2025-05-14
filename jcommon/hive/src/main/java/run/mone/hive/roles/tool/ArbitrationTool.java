package run.mone.hive.roles.tool;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.hive.roles.ReactorRole;

/**
 * 仲裁询问工具
 * 用于向外部仲裁Agent发起仲裁询问，如果没有可供询问的仲裁Agent则忽略本次仲裁结果
 */
@Slf4j
public class ArbitrationTool implements ITool {

    @Override
    public String getName() {
        return "arbitration";
    }

    @Override
    public boolean needExecute() {
        return true;
    }

    @Override
    public boolean show() {
        return true;
    }

    @Override
    public String description() {
        return """
                一个用于向外部仲裁Agent发起仲裁询问的工具。
                当需要外部仲裁时使用此工具，如果没有可供询问的仲裁Agent则忽略本次仲裁结果。
                
                **使用时机：** 当需要外部仲裁Agent对某个问题或决策进行仲裁时使用此工具。
                
                **输出：** 工具将返回仲裁结果，如果没有可用的仲裁Agent则返回忽略结果。
                """;
    }

    @Override
    public String parameters() {
        return """
                - question: (必需) 需要仲裁的问题或决策内容
                - context: (可选) 提供仲裁所需的上下文信息
                - timeout: (可选) 仲裁超时时间（毫秒），默认30000
                """;
    }

    @Override
    public String usage() {
        return """
                (注意：如果您使用此工具，必须在 <arbitration> 标签内返回仲裁结果):
                
                示例: 发起仲裁询问
                <arbitration>
                  <question>这个决策是否正确？</question>
                  <context>决策背景信息...</context>
                  <result>
                    [仲裁结果]
                  </result>
                </arbitration>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 检查必需参数
            if (!inputJson.has("question") || StringUtils.isBlank(inputJson.get("question").getAsString())) {
                log.error("仲裁请求缺少必需的问题参数");
                result.addProperty("error", "缺少必需参数'question'");
                return result;
            }

            String question = inputJson.get("question").getAsString();
            String context = inputJson.has("context") ? inputJson.get("context").getAsString() : "";
            long timeout = inputJson.has("timeout") ? inputJson.get("timeout").getAsLong() : 30000;

            log.info("开始仲裁询问，问题：{}，上下文：{}", question, context);

            // TODO: 实现与外部仲裁Agent的通信逻辑
            // 这里需要根据实际情况实现与外部仲裁Agent的通信
            // 如果找不到可用的仲裁Agent，则返回忽略结果

            // 模拟仲裁结果
            boolean hasArbitrationAgent = false; // 这里需要根据实际情况判断是否有可用的仲裁Agent

            if (!hasArbitrationAgent) {
                log.info("没有可用的仲裁Agent，忽略本次仲裁结果");
                result.addProperty("result", "忽略仲裁结果：没有可用的仲裁Agent");
                result.addProperty("ignored", true);
                return result;
            }

            // 如果有可用的仲裁Agent，则返回仲裁结果
            result.addProperty("result", "仲裁结果：同意");
            result.addProperty("ignored", false);
            log.info("仲裁询问完成，结果：{}", result.get("result").getAsString());

            return result;

        } catch (Exception e) {
            log.error("仲裁询问处理发生异常", e);
            result.addProperty("error", "仲裁失败: " + e.getMessage());
            return result;
        }
    }
} 