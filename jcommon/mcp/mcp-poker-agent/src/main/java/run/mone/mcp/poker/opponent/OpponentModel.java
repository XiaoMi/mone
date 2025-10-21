package run.mone.mcp.poker.opponent;

import lombok.Data;
import run.mone.mcp.poker.strategy.PokerStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * 对手建模工具
 * 通过分析对手的行为模式来预测其打法风格
 * @author poker-agent
 * @date 2025/10/21
 */
@Data
public class OpponentModel {
    
    private String opponentId;
    private int handsPlayed;                    // 观察的手数
    private int vpip;                           // 入池率（Voluntarily Put In Pot）
    private int pfr;                            // 翻牌前加注率（Pre-Flop Raise）
    private int aggression;                     // 激进度（加注/跟注比）
    private int threeBet;                       // 3-bet频率
    private int foldToContinuationBet;          // 对持续下注的弃牌率
    private int checkRaise;                     // Check-Raise频率
    
    // 行为历史统计
    private int totalActions;
    private int raiseCount;
    private int callCount;
    private int foldCount;
    private int checkCount;
    
    /**
     * 玩家类型
     */
    public enum PlayerType {
        TIGHT_PASSIVE("紧弱型", "保守谨慎，很少加注，通常只跟注"),
        TIGHT_AGGRESSIVE("紧凶型", "选择性高，但一旦入池就强势进攻"),
        LOOSE_PASSIVE("松弱型", "经常入池但很少加注，容易被欺负"),
        LOOSE_AGGRESSIVE("松凶型", "经常入池且频繁加注，激进好斗"),
        UNKNOWN("未知型", "数据不足，无法判断");
        
        private final String name;
        private final String description;
        
        PlayerType(String name, String description) {
            this.name = name;
            this.description = description;
        }
        
        public String getName() {
            return name;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    public OpponentModel(String opponentId) {
        this.opponentId = opponentId;
        this.handsPlayed = 0;
        this.totalActions = 0;
        this.raiseCount = 0;
        this.callCount = 0;
        this.foldCount = 0;
        this.checkCount = 0;
    }
    
    /**
     * 记录对手行动
     */
    public void recordAction(PokerStrategy.Action action, boolean isPreFlop) {
        totalActions++;
        
        switch (action) {
            case RAISE:
            case ALL_IN:
                raiseCount++;
                if (isPreFlop) {
                    pfr++;
                }
                break;
            case CALL:
                callCount++;
                break;
            case FOLD:
                foldCount++;
                break;
            case CHECK:
                checkCount++;
                break;
        }
        
        // 更新统计数据
        updateStatistics();
    }
    
    /**
     * 记录入池
     */
    public void recordVPIP() {
        handsPlayed++;
        vpip++;
        updateStatistics();
    }
    
    /**
     * 记录3-bet
     */
    public void record3Bet() {
        threeBet++;
        updateStatistics();
    }
    
    /**
     * 记录对持续下注的反应
     */
    public void recordFoldToCBet() {
        foldToContinuationBet++;
        updateStatistics();
    }
    
    /**
     * 记录Check-Raise
     */
    public void recordCheckRaise() {
        checkRaise++;
        updateStatistics();
    }
    
    /**
     * 更新统计数据（百分比）
     */
    private void updateStatistics() {
        if (handsPlayed > 0) {
            // VPIP = 自愿入池次数 / 总手数
            // 这里简化计算，实际应该单独记录
        }
        
        if (totalActions > 0) {
            int totalAggressive = raiseCount;
            int totalPassive = callCount;
            if (totalAggressive + totalPassive > 0) {
                aggression = (totalAggressive * 100) / (totalAggressive + totalPassive);
            }
        }
    }
    
    /**
     * 判断玩家类型
     */
    public PlayerType getPlayerType() {
        if (handsPlayed < 20) {
            return PlayerType.UNKNOWN; // 数据不足
        }
        
        // 计算VPIP百分比
        int vpipPercent = (vpip * 100) / handsPlayed;
        
        // 判断松紧（VPIP > 30% 为松，< 20% 为紧）
        boolean isLoose = vpipPercent > 30;
        boolean isTight = vpipPercent < 20;
        
        // 判断凶弱（激进度 > 60% 为凶，< 40% 为弱）
        boolean isAggressive = aggression > 60;
        boolean isPassive = aggression < 40;
        
        if (isTight && isAggressive) {
            return PlayerType.TIGHT_AGGRESSIVE;
        } else if (isTight && isPassive) {
            return PlayerType.TIGHT_PASSIVE;
        } else if (isLoose && isAggressive) {
            return PlayerType.LOOSE_AGGRESSIVE;
        } else if (isLoose && isPassive) {
            return PlayerType.LOOSE_PASSIVE;
        }
        
        return PlayerType.UNKNOWN;
    }
    
    /**
     * 获取对手激进程度（用于调整我们的策略）
     */
    public double getAggressionFactor() {
        PlayerType type = getPlayerType();
        switch (type) {
            case TIGHT_PASSIVE:
                return 0.6; // 对手保守，我们可以更激进
            case TIGHT_AGGRESSIVE:
                return 0.9; // 对手选择性高且凶，需要谨慎
            case LOOSE_PASSIVE:
                return 1.3; // 对手松弱，可以压制
            case LOOSE_AGGRESSIVE:
                return 0.8; // 对手松凶，需要更谨慎但也要看牌
            default:
                return 1.0; // 默认标准打法
        }
    }
    
    /**
     * 预测对手是否在诈唬
     */
    public double getBluffProbability(PokerStrategy.GameStage stage) {
        PlayerType type = getPlayerType();
        
        double baseBluffRate = 0.2; // 基础诈唬率
        
        switch (type) {
            case LOOSE_AGGRESSIVE:
                baseBluffRate = 0.4; // 松凶玩家诈唬频率高
                break;
            case TIGHT_AGGRESSIVE:
                baseBluffRate = 0.25; // 紧凶玩家选择性诈唬
                break;
            case LOOSE_PASSIVE:
                baseBluffRate = 0.1; // 松弱玩家很少诈唬
                break;
            case TIGHT_PASSIVE:
                baseBluffRate = 0.05; // 紧弱玩家几乎不诈唬
                break;
        }
        
        // 根据阶段调整
        switch (stage) {
            case PRE_FLOP:
                baseBluffRate *= 0.7; // 翻牌前诈唬较少
                break;
            case FLOP:
                baseBluffRate *= 1.0; // 翻牌后标准
                break;
            case TURN:
                baseBluffRate *= 0.9; // 转牌略降
                break;
            case RIVER:
                baseBluffRate *= 1.2; // 河牌诈唬增加
                break;
        }
        
        return Math.min(baseBluffRate, 0.6); // 最高60%
    }
    
    /**
     * 建议针对此对手的策略调整
     */
    public String getStrategyAdvice() {
        PlayerType type = getPlayerType();
        StringBuilder advice = new StringBuilder();
        
        advice.append("对手类型: ").append(type.getName()).append("\n");
        advice.append("特征: ").append(type.getDescription()).append("\n");
        advice.append("统计数据: VPIP=").append(vpip).append("/").append(handsPlayed)
              .append(", 激进度=").append(aggression).append("%\n");
        
        switch (type) {
            case TIGHT_PASSIVE:
                advice.append("策略建议: 可以频繁诈唬，他们会经常弃牌。但如果他们加注，要非常警惕。");
                break;
            case TIGHT_AGGRESSIVE:
                advice.append("策略建议: 尊重他们的加注，不要轻易对抗。有好牌时可以慢打（slow play）引诱加注。");
                break;
            case LOOSE_PASSIVE:
                advice.append("策略建议: 用价值下注榨取，避免诈唬（他们会跟注）。等待好牌然后重注。");
                break;
            case LOOSE_AGGRESSIVE:
                advice.append("策略建议: 需要更好的牌才能对抗。可以用陷阱玩法（trap）。注意他们的诈唬倾向。");
                break;
            case UNKNOWN:
                advice.append("策略建议: 数据不足，采用标准GTO策略，继续观察收集信息。");
                break;
        }
        
        return advice.toString();
    }
    
    /**
     * 获取简要报告
     */
    @Override
    public String toString() {
        return String.format("对手[%s]: 类型=%s, 手数=%d, VPIP=%d, 激进度=%d%%",
                opponentId, getPlayerType().getName(), handsPlayed, vpip, aggression);
    }
}

/**
 * 对手模型管理器
 */
class OpponentModelManager {
    private Map<String, OpponentModel> opponents = new HashMap<>();
    
    public OpponentModel getOrCreateModel(String opponentId) {
        return opponents.computeIfAbsent(opponentId, OpponentModel::new);
    }
    
    public OpponentModel getModel(String opponentId) {
        return opponents.get(opponentId);
    }
    
    public void clearModels() {
        opponents.clear();
    }
    
    public Map<String, OpponentModel> getAllModels() {
        return new HashMap<>(opponents);
    }
}
