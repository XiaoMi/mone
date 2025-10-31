package run.mone.mcp.poker.tool;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;
import run.mone.mcp.poker.model.Card;
import run.mone.mcp.poker.strategy.PokerStrategy;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 德州扑克策略决策工具
 * @author poker-agent
 * @date 2025/10/21
 */
@Slf4j
public class MakeDecisionTool implements ITool {
    
    public static final String name = "make_poker_decision";

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
                根据当前牌局情况制定最佳策略决策的工具。综合考虑手牌、位置、底池大小等因素，给出专业的行动建议。
                
                **主要功能：**
                - 分析当前牌局情况
                - 计算期望价值（EV）
                - 评估手牌胜率
                - 考虑位置优势
                - 计算合理下注大小
                - 提供详细决策理由
                - 支持激进度调整
                
                **决策因素：**
                - 手牌强度和类型
                - 游戏阶段（翻牌前/翻牌/转牌/河牌）
                - 位置（早/中/晚/盲注）
                - 对手数量
                - 底池大小和当前下注
                - 筹码深度
                - 对手激进度
                
                **输出：** 返回建议的行动、下注大小、胜率、期望价值和详细的决策理由。
                """;
    }

    @Override
    public String parameters() {
        return """
                - hole_cards: (必需) 手牌，格式为"牌1,牌2"，如"As,Kh"
                - community_cards: (可选) 公共牌，格式为"牌1,牌2,..."，默认为空（翻牌前）
                - stage: (可选) 游戏阶段，可选值：PRE_FLOP, FLOP, TURN, RIVER，默认PRE_FLOP
                - position: (可选) 位置，可选值：
                  * EARLY: 早位（枪口位、枪口+1）
                  * MIDDLE: 中位
                  * LATE: 晚位（庄位前）
                  * BUTTON: 庄位
                  * SMALL_BLIND: 小盲
                  * BIG_BLIND: 大盲
                  默认为MIDDLE
                - num_opponents: (可选) 对手数量，范围1-9，默认为1
                - pot_size: (可选) 底池大小，默认为100.0
                - current_bet: (可选) 当前需要跟注的金额，默认为0.0
                - stack_size: (可选) 自己的筹码量，默认为1000.0
                - is_aggressive: (可选) 牌桌是否激进，默认为false
                - aggression_factor: (可选) 激进因子（1.0-2.0），用于调整策略的激进程度
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
                <make_poker_decision>
                <hole_cards>手牌</hole_cards>
                <community_cards>公共牌（可选）</community_cards>
                <stage>游戏阶段（可选）</stage>
                <position>位置（可选）</position>
                <num_opponents>对手数量（可选）</num_opponents>
                <pot_size>底池大小（可选）</pot_size>
                <current_bet>当前下注（可选）</current_bet>
                <stack_size>筹码量（可选）</stack_size>
                <is_aggressive>是否激进（可选）</is_aggressive>
                <aggression_factor>激进因子（可选）</aggression_factor>
                %s
                </make_poker_decision>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例1: 翻牌前在庄位拿到AK
                <make_poker_decision>
                <hole_cards>As,Kh</hole_cards>
                <position>BUTTON</position>
                <num_opponents>2</num_opponents>
                <pot_size>150</pot_size>
                <current_bet>50</current_bet>
                <stack_size>1000</stack_size>
                </make_poker_decision>
                
                示例2: 翻牌后在早位拿到顶对
                <make_poker_decision>
                <hole_cards>Ah,Qs</hole_cards>
                <community_cards>Ad,Jc,7h</community_cards>
                <stage>FLOP</stage>
                <position>EARLY</position>
                <num_opponents>3</num_opponents>
                <pot_size>200</pot_size>
                <stack_size>800</stack_size>
                </make_poker_decision>
                
                示例3: 转牌后的听牌决策
                <make_poker_decision>
                <hole_cards>Kd,Qd</hole_cards>
                <community_cards>Jd,Td,3s,7c</community_cards>
                <stage>TURN</stage>
                <position>LATE</position>
                <num_opponents>1</num_opponents>
                <pot_size>500</pot_size>
                <current_bet>200</current_bet>
                <stack_size>600</stack_size>
                <is_aggressive>true</is_aggressive>
                </make_poker_decision>
                
                示例4: 河牌的价值下注决策
                <make_poker_decision>
                <hole_cards>9h,9d</hole_cards>
                <community_cards>9s,Kc,7h,4d,2s</community_cards>
                <stage>RIVER</stage>
                <position>BUTTON</position>
                <num_opponents>1</num_opponents>
                <pot_size>800</pot_size>
                <stack_size>1200</stack_size>
                <aggression_factor>1.5</aggression_factor>
                </make_poker_decision>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();
        
        try {
            // 检查必要参数
            if (!inputJson.has("hole_cards") || StringUtils.isBlank(inputJson.get("hole_cards").getAsString())) {
                log.error("make_poker_decision操作缺少必需的hole_cards参数");
                result.addProperty("error", "缺少必需参数'hole_cards'");
                return result;
            }
            
            // 构建决策上下文
            PokerStrategy.DecisionContext context = new PokerStrategy.DecisionContext();
            
            // 解析手牌
            String holeCardsStr = inputJson.get("hole_cards").getAsString();
            List<Card> holeCards = parseCards(holeCardsStr);
            
            if (holeCards.size() != 2) {
                result.addProperty("error", "手牌必须是2张，格式如: As,Kh");
                return result;
            }
            context.setHoleCards(holeCards);
            
            // 解析公共牌
            String communityCardsStr = inputJson.has("community_cards") 
                    ? inputJson.get("community_cards").getAsString() 
                    : "";
            context.setCommunityCards(communityCardsStr.isEmpty() 
                    ? List.of() 
                    : parseCards(communityCardsStr));
            
            // 解析游戏阶段
            String stageStr = inputJson.has("stage") 
                    ? inputJson.get("stage").getAsString().toUpperCase() 
                    : "PRE_FLOP";
            try {
                context.setStage(PokerStrategy.GameStage.valueOf(stageStr));
            } catch (IllegalArgumentException e) {
                result.addProperty("error", "无效的游戏阶段，可选值：PRE_FLOP, FLOP, TURN, RIVER");
                return result;
            }
            
            // 解析位置
            String positionStr = inputJson.has("position") 
                    ? inputJson.get("position").getAsString().toUpperCase() 
                    : "MIDDLE";
            try {
                context.setPosition(PokerStrategy.Position.valueOf(positionStr));
            } catch (IllegalArgumentException e) {
                result.addProperty("error", "无效的位置，可选值：EARLY, MIDDLE, LATE, BUTTON, SMALL_BLIND, BIG_BLIND");
                return result;
            }
            
            // 解析其他参数
            context.setNumOpponents(inputJson.has("num_opponents") ? inputJson.get("num_opponents").getAsInt() : 1);
            context.setPotSize(inputJson.has("pot_size") ? inputJson.get("pot_size").getAsDouble() : 100.0);
            context.setCurrentBet(inputJson.has("current_bet") ? inputJson.get("current_bet").getAsDouble() : 0.0);
            context.setStackSize(inputJson.has("stack_size") ? inputJson.get("stack_size").getAsDouble() : 1000.0);
            context.setAggressive(inputJson.has("is_aggressive") && inputJson.get("is_aggressive").getAsBoolean());
            
            // 验证参数有效性
            if (context.getNumOpponents() < 1 || context.getNumOpponents() > 9) {
                result.addProperty("error", "对手数量必须在1-9之间");
                return result;
            }
            
            // 制定决策
            PokerStrategy.DecisionResult decision = PokerStrategy.makeDecision(context);
            
            // 如果有激进因子，调整策略
            if (inputJson.has("aggression_factor")) {
                double aggressionFactor = inputJson.get("aggression_factor").getAsDouble();
                if (aggressionFactor < 0.5 || aggressionFactor > 2.0) {
                    result.addProperty("warning", "激进因子建议范围0.5-2.0");
                }
                decision = PokerStrategy.adjustAggression(decision, aggressionFactor);
            }
            
            // 构建结果
            result.addProperty("success", true);
            result.addProperty("action", decision.getRecommendedAction().getName());
            result.addProperty("action_description", decision.getRecommendedAction().toString());
            result.addProperty("bet_size", decision.getBetSize());
            result.addProperty("win_rate", String.format("%.2f%%", decision.getWinRate() * 100));
            result.addProperty("expected_value", String.format("%.2f", decision.getExpectedValue()));
            result.addProperty("confidence", String.format("%.2f%%", decision.getConfidence() * 100));
            result.addProperty("reasoning", decision.getReasoning());
            result.addProperty("message", decision.toString());
            
            // 添加上下文信息用于日志
            result.addProperty("context_stage", context.getStage().name());
            result.addProperty("context_position", context.getPosition().name());
            
            log.info("策略决策：手牌={}, 阶段={}, 位置={}, 行动={}, 下注={}", 
                    holeCardsStr, context.getStage().name(), context.getPosition().name(),
                    decision.getRecommendedAction().getName(), decision.getBetSize());
            
        } catch (IllegalArgumentException e) {
            log.error("参数解析失败", e);
            result.addProperty("error", "参数错误: " + e.getMessage());
        } catch (Exception e) {
            log.error("策略决策失败", e);
            result.addProperty("error", "决策失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 解析牌字符串为Card列表
     */
    private List<Card> parseCards(String cardsStr) {
        if (cardsStr == null || cardsStr.trim().isEmpty()) {
            return List.of();
        }
        
        return Arrays.stream(cardsStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Card::fromString)
                .collect(Collectors.toList());
    }
}
