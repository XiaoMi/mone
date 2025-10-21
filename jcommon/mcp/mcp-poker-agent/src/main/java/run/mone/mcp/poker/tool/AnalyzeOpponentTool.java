package run.mone.mcp.poker.tool;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;
import run.mone.mcp.poker.opponent.OpponentModel;
import run.mone.mcp.poker.strategy.PokerStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * 对手分析工具
 * @author poker-agent
 * @date 2025/10/21
 */
@Slf4j
public class AnalyzeOpponentTool implements ITool {
    
    public static final String name = "analyze_opponent";
    
    private static final Map<String, OpponentModel> opponentModels = new HashMap<>();

    @Override
    public String getName() {
        return name;
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
                分析对手的打法风格和特征的工具。可以记录对手行为或查询对手的统计信息和建议策略。
                
                **主要功能：**
                - 记录对手的行动（加注、跟注、弃牌等）
                - 跟踪对手的VPIP（入池率）
                - 记录对手的3-bet频率
                - 分析对手的打法类型（紧凶、松凶等）
                - 计算对手的激进度和诈唬概率
                - 提供针对性的策略建议
                
                **使用场景：**
                - 观察到对手采取行动时记录
                - 需要查询对手特征时查询
                - 制定针对性策略时参考
                
                **输出：** 工具将返回操作结果，包括对手统计信息和策略建议。
                """;
    }

    @Override
    public String parameters() {
        return """
                - opponent_id: (必需) 对手的唯一标识符
                - operation: (可选) 操作类型，可选值：
                  * record_action: 记录对手行动
                  * record_vpip: 记录对手入池
                  * record_3bet: 记录对手3-bet
                  * query: 查询对手信息（默认）
                - action: (record_action操作时必需) 对手的行动，可选值：FOLD, CHECK, CALL, RAISE, ALL_IN
                - is_pre_flop: (record_action操作时可选) 是否在翻牌前，默认false
                - stage: (query操作时可选) 当前游戏阶段，用于计算诈唬概率，可选值：PRE_FLOP, FLOP, TURN, RIVER
                """;
    }

    @Override
    public String usage() {
        String taskProgress = """
                <task_progress>
                任务进度清单（可选）
                </task_progress>
                """;
        if (!taskProgress()) {
            taskProgress = "";
        }
        return """
                <analyze_opponent>
                <opponent_id>对手ID</opponent_id>
                <operation>操作类型</operation>
                <action>行动类型（可选）</action>
                <is_pre_flop>true或false（可选）</is_pre_flop>
                <stage>游戏阶段（可选）</stage>
                %s
                </analyze_opponent>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例1: 记录对手在翻牌前加注
                <analyze_opponent>
                <opponent_id>player_1</opponent_id>
                <operation>record_action</operation>
                <action>RAISE</action>
                <is_pre_flop>true</is_pre_flop>
                </analyze_opponent>
                
                示例2: 记录对手入池
                <analyze_opponent>
                <opponent_id>player_1</opponent_id>
                <operation>record_vpip</operation>
                </analyze_opponent>
                
                示例3: 查询对手信息和策略建议
                <analyze_opponent>
                <opponent_id>player_1</opponent_id>
                <operation>query</operation>
                <stage>FLOP</stage>
                </analyze_opponent>
                
                示例4: 简单查询（使用默认操作）
                <analyze_opponent>
                <opponent_id>player_1</opponent_id>
                </analyze_opponent>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();
        
        try {
            // 检查必要参数
            if (!inputJson.has("opponent_id") || StringUtils.isBlank(inputJson.get("opponent_id").getAsString())) {
                log.error("analyze_opponent操作缺少必需的opponent_id参数");
                result.addProperty("error", "缺少必需参数'opponent_id'");
                return result;
            }
            
            String opponentId = inputJson.get("opponent_id").getAsString();
            String operation = inputJson.has("operation") ? inputJson.get("operation").getAsString() : "query";
            
            OpponentModel model = opponentModels.computeIfAbsent(opponentId, OpponentModel::new);
            
            switch (operation.toLowerCase()) {
                case "record_action":
                    return recordAction(model, inputJson);
                case "record_vpip":
                    return recordVPIP(model);
                case "record_3bet":
                    return record3Bet(model);
                case "query":
                default:
                    return queryOpponent(model, inputJson);
            }
            
        } catch (Exception e) {
            log.error("对手分析失败", e);
            result.addProperty("error", "分析失败: " + e.getMessage());
            return result;
        }
    }
    
    /**
     * 记录对手行为
     */
    private JsonObject recordAction(OpponentModel model, JsonObject json) {
        JsonObject result = new JsonObject();
        
        try {
            if (!json.has("action")) {
                result.addProperty("error", "record_action操作缺少必需参数'action'");
                return result;
            }
            
            String actionStr = json.get("action").getAsString().toUpperCase();
            PokerStrategy.Action action = PokerStrategy.Action.valueOf(actionStr);
            boolean isPreFlop = json.has("is_pre_flop") && json.get("is_pre_flop").getAsBoolean();
            
            model.recordAction(action, isPreFlop);
            
            result.addProperty("success", true);
            result.addProperty("message", "已记录对手行为: " + action.getName());
            result.addProperty("opponent_info", model.toString());
            
            log.info("记录对手行为：{} -> {}", model.getOpponentId(), action.getName());
            
        } catch (IllegalArgumentException e) {
            result.addProperty("error", "无效的行动类型，可选值：FOLD, CHECK, CALL, RAISE, ALL_IN");
        } catch (Exception e) {
            log.error("记录对手行为失败", e);
            result.addProperty("error", "记录失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 记录对手入池
     */
    private JsonObject recordVPIP(OpponentModel model) {
        JsonObject result = new JsonObject();
        
        try {
            model.recordVPIP();
            
            result.addProperty("success", true);
            result.addProperty("message", "已记录对手入池");
            result.addProperty("opponent_info", model.toString());
            result.addProperty("current_vpip", model.getVpip());
            
            log.info("记录对手入池：{} -> VPIP: {}", model.getOpponentId(), model.getVpip());
            
        } catch (Exception e) {
            log.error("记录对手入池失败", e);
            result.addProperty("error", "记录失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 记录对手3-bet
     */
    private JsonObject record3Bet(OpponentModel model) {
        JsonObject result = new JsonObject();
        
        try {
            model.record3Bet();
            
            result.addProperty("success", true);
            result.addProperty("message", "已记录对手3-bet");
            result.addProperty("opponent_info", model.toString());
            
            log.info("记录对手3-bet：{}", model.getOpponentId());
            
        } catch (Exception e) {
            log.error("记录对手3-bet失败", e);
            result.addProperty("error", "记录失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 查询对手信息
     */
    private JsonObject queryOpponent(OpponentModel model, JsonObject json) {
        JsonObject result = new JsonObject();
        
        try {
            result.addProperty("success", true);
            result.addProperty("opponent_id", model.getOpponentId());
            result.addProperty("hands_played", model.getHandsPlayed());
            result.addProperty("player_type", model.getPlayerType().getName());
            result.addProperty("player_type_desc", model.getPlayerType().getDescription());
            result.addProperty("vpip", model.getVpip());
            result.addProperty("aggression", model.getAggression() + "%");
            result.addProperty("aggression_factor", model.getAggressionFactor());
            
            // 如果提供了游戏阶段，计算诈唬概率
            if (json.has("stage")) {
                String stageStr = json.get("stage").getAsString().toUpperCase();
                try {
                    PokerStrategy.GameStage stage = PokerStrategy.GameStage.valueOf(stageStr);
                    double bluffProb = model.getBluffProbability(stage);
                    result.addProperty("bluff_probability", String.format("%.2f%%", bluffProb * 100));
                } catch (IllegalArgumentException e) {
                    result.addProperty("stage_error", "无效的游戏阶段，可选值：PRE_FLOP, FLOP, TURN, RIVER");
                }
            }
            
            result.addProperty("strategy_advice", model.getStrategyAdvice());
            result.addProperty("summary", model.toString());
            
            log.info("查询对手信息：{} -> {}", model.getOpponentId(), result);
            
        } catch (Exception e) {
            log.error("查询对手信息失败", e);
            result.addProperty("error", "查询失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 清除所有对手模型（用于新游戏）
     */
    public static void clearAllModels() {
        opponentModels.clear();
        log.info("已清除所有对手模型");
    }
}
