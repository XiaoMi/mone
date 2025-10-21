package run.mone.mcp.poker.tool;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;
import run.mone.mcp.poker.calculator.WinRateCalculator;
import run.mone.mcp.poker.model.Card;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 计算德州扑克胜率的工具
 * @author poker-agent
 * @date 2025/10/21
 */
@Slf4j
public class CalculateWinRateTool implements ITool {
    
    public static final String name = "calculate_win_rate";

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
                计算德州扑克胜率的工具。通过蒙特卡洛模拟计算当前手牌在给定情况下的获胜概率。
                
                **主要功能：**
                - 计算手牌对抗多个对手的胜率
                - 支持翻牌前、翻牌后、转牌、河牌各阶段的胜率计算
                - 提供详细的胜率、平局率、输率分析
                - 计算当前最佳牌型和牌力强度
                - 基于蒙特卡洛模拟，结果准确可靠
                
                **使用场景：**
                - 决策前评估手牌强度
                - 分析特定公共牌下的胜率
                - 比较不同对手数量下的胜率变化
                - 制定下注策略时的数学依据
                
                **输出：** 返回详细的胜率分析，包括胜率、平局率、输率、当前牌型等信息。
                """;
    }

    @Override
    public String parameters() {
        return """
                - hole_cards: (必需) 手牌，格式为"牌1,牌2"，如"As,Kh"
                  * 牌的格式：点数+花色，如As表示黑桃A，Kh表示红心K
                  * 点数：A,K,Q,J,T(10),9,8,7,6,5,4,3,2
                  * 花色：s(黑桃),h(红心),d(方块),c(梅花)
                - community_cards: (可选) 公共牌，格式为"牌1,牌2,牌3,..."，如"Jd,Tc,9s"
                  * 可以是空字符串（翻牌前）
                  * 翻牌：3张牌
                  * 转牌：4张牌
                  * 河牌：5张牌
                - num_opponents: (可选) 对手数量，范围1-9，默认为1
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
                <calculate_win_rate>
                <hole_cards>手牌</hole_cards>
                <community_cards>公共牌（可选）</community_cards>
                <num_opponents>对手数量（可选）</num_opponents>
                %s
                </calculate_win_rate>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例1: 计算翻牌前AA对一个对手的胜率
                <calculate_win_rate>
                <hole_cards>As,Ad</hole_cards>
                <num_opponents>1</num_opponents>
                </calculate_win_rate>
                
                示例2: 计算翻牌后的胜率
                <calculate_win_rate>
                <hole_cards>Kh,Qh</hole_cards>
                <community_cards>Jh,Th,2s</community_cards>
                <num_opponents>2</num_opponents>
                </calculate_win_rate>
                
                示例3: 计算转牌的胜率
                <calculate_win_rate>
                <hole_cards>7d,8d</hole_cards>
                <community_cards>6d,9c,Ts,Jh</community_cards>
                <num_opponents>3</num_opponents>
                </calculate_win_rate>
                
                示例4: 计算河牌的胜率
                <calculate_win_rate>
                <hole_cards>Ac,Kc</hole_cards>
                <community_cards>Qc,Jc,Tc,5h,2d</community_cards>
                <num_opponents>1</num_opponents>
                </calculate_win_rate>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();
        
        try {
            // 检查必要参数
            if (!inputJson.has("hole_cards") || StringUtils.isBlank(inputJson.get("hole_cards").getAsString())) {
                log.error("calculate_win_rate操作缺少必需的hole_cards参数");
                result.addProperty("error", "缺少必需参数'hole_cards'");
                return result;
            }
            
            // 解析手牌（格式如 "As,Kh"）
            String holeCardsStr = inputJson.get("hole_cards").getAsString();
            List<Card> holeCards = parseCards(holeCardsStr);
            
            if (holeCards.size() != 2) {
                result.addProperty("error", "手牌必须是2张，格式如: As,Kh");
                return result;
            }
            
            // 解析公共牌（格式如 "Jd,Tc,9s" 或空字符串）
            String communityCardsStr = inputJson.has("community_cards") 
                    ? inputJson.get("community_cards").getAsString() 
                    : "";
            List<Card> communityCards = communityCardsStr.isEmpty() 
                    ? List.of() 
                    : parseCards(communityCardsStr);
            
            if (communityCards.size() > 5) {
                result.addProperty("error", "公共牌最多5张");
                return result;
            }
            
            // 解析对手数量
            int numOpponents = inputJson.has("num_opponents") 
                    ? inputJson.get("num_opponents").getAsInt() 
                    : 1;
            
            if (numOpponents < 1 || numOpponents > 9) {
                result.addProperty("error", "对手数量必须在1-9之间");
                return result;
            }
            
            // 计算胜率
            WinRateCalculator.WinRateResult winRateResult = WinRateCalculator.calculateWinRate(
                    holeCards, communityCards, numOpponents
            );
            
            // 构建结果
            result.addProperty("success", true);
            result.addProperty("win_rate", String.format("%.2f%%", winRateResult.getWinRate() * 100));
            result.addProperty("tie_rate", String.format("%.2f%%", winRateResult.getTieRate() * 100));
            result.addProperty("lose_rate", String.format("%.2f%%", winRateResult.getLoseRate() * 100));
            result.addProperty("simulations", winRateResult.getSimulations());
            result.addProperty("num_opponents", numOpponents);
            
            if (winRateResult.getCurrentBestHand() != null) {
                result.addProperty("current_hand", winRateResult.getCurrentBestHand().toString());
                result.addProperty("hand_strength", String.format("%.2f", winRateResult.getHandStrength()));
            }
            
            result.addProperty("message", winRateResult.toString());
            
            log.info("计算胜率：手牌={}, 公共牌={}, 对手={}, 胜率={}", 
                    holeCardsStr, communityCardsStr, numOpponents, 
                    String.format("%.2f%%", winRateResult.getWinRate() * 100));
            
        } catch (IllegalArgumentException e) {
            log.error("参数解析失败", e);
            result.addProperty("error", "参数错误: " + e.getMessage() + "。牌的格式应为：点数+花色，如As,Kh");
        } catch (Exception e) {
            log.error("计算胜率失败", e);
            result.addProperty("error", "计算失败: " + e.getMessage());
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
