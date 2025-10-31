package run.mone.mcp.poker.strategy;

import lombok.Data;
import run.mone.mcp.poker.calculator.WinRateCalculator;
import run.mone.mcp.poker.model.Card;

import java.util.List;

/**
 * 德州扑克策略决策器
 * @author poker-agent
 * @date 2025/10/21
 */
public class PokerStrategy {
    
    /**
     * 游戏阶段
     */
    public enum GameStage {
        PRE_FLOP("翻牌前"),
        FLOP("翻牌"),
        TURN("转牌"),
        RIVER("河牌");
        
        private final String name;
        
        GameStage(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
    
    /**
     * 行动建议
     */
    public enum Action {
        FOLD("弃牌"),
        CHECK("过牌"),
        CALL("跟注"),
        RAISE("加注"),
        ALL_IN("全下");
        
        private final String name;
        
        Action(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
    
    /**
     * 位置
     */
    public enum Position {
        EARLY("前位"),
        MIDDLE("中位"),
        LATE("后位"),
        BLIND("盲注位");
        
        private final String name;
        
        Position(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
    
    /**
     * 决策结果
     */
    @Data
    public static class DecisionResult {
        private Action recommendedAction;
        private double betSize;           // 建议下注大小（占底池的比例）
        private String reasoning;         // 决策理由
        private double confidence;        // 信心度（0-1）
        private double winRate;           // 胜率
        private double expectedValue;     // 期望收益
        
        public DecisionResult(Action action, double betSize, String reasoning, 
                            double confidence, double winRate, double expectedValue) {
            this.recommendedAction = action;
            this.betSize = betSize;
            this.reasoning = reasoning;
            this.confidence = confidence;
            this.winRate = winRate;
            this.expectedValue = expectedValue;
        }
        
        @Override
        public String toString() {
            return String.format("建议行动: %s (下注%.2f倍底池), 胜率: %.2f%%, 期望收益: %.2f, 信心度: %.2f%%\n理由: %s",
                    recommendedAction.getName(), betSize, winRate * 100, expectedValue, confidence * 100, reasoning);
        }
    }
    
    /**
     * 决策上下文
     */
    @Data
    public static class DecisionContext {
        private List<Card> holeCards;
        private List<Card> communityCards;
        private GameStage stage;
        private Position position;
        private int numOpponents;
        private double potSize;
        private double currentBet;
        private double stackSize;
        private boolean isAggressive;  // 对手是否激进
    }
    
    /**
     * 制定决策
     * @param context 决策上下文
     * @return 决策结果
     */
    public static DecisionResult makeDecision(DecisionContext context) {
        // 计算胜率
        WinRateCalculator.WinRateResult winRateResult = WinRateCalculator.calculateWinRate(
                context.getHoleCards(),
                context.getCommunityCards(),
                context.getNumOpponents()
        );
        
        double winRate = winRateResult.getWinRate();
        double potOdds = WinRateCalculator.calculatePotOdds(context.getPotSize(), context.getCurrentBet());
        double ev = WinRateCalculator.calculateExpectedValue(winRate, context.getPotSize(), context.getCurrentBet());
        
        // 根据游戏阶段做出决策
        switch (context.getStage()) {
            case PRE_FLOP:
                return preFlopDecision(context, winRate, potOdds, ev);
            case FLOP:
            case TURN:
            case RIVER:
                return postFlopDecision(context, winRate, potOdds, ev, winRateResult);
            default:
                return new DecisionResult(Action.FOLD, 0, "未知阶段", 0, 0, 0);
        }
    }
    
    /**
     * 翻牌前决策
     */
    private static DecisionResult preFlopDecision(DecisionContext context, double winRate, 
                                                 double potOdds, double ev) {
        double preFlopStrength = WinRateCalculator.evaluatePreFlopStrength(context.getHoleCards());
        Position position = context.getPosition();
        
        // 强牌（胜率 > 0.8 或手牌强度 > 0.85）
        if (preFlopStrength > 0.85 || winRate > 0.8) {
            if (context.getCurrentBet() > context.getStackSize() * 0.3) {
                // 面对大额加注，考虑全下
                return new DecisionResult(Action.ALL_IN, 1.0,
                        "持有顶级手牌，面对大额加注选择全下",
                        0.95, winRate, ev);
            } else {
                // 正常加注
                double raiseSize = context.getPosition() == Position.LATE ? 3.0 : 3.5;
                return new DecisionResult(Action.RAISE, raiseSize,
                        "持有强牌，主动加注建立底池",
                        0.9, winRate, ev);
            }
        }
        
        // 中等牌（手牌强度 0.6-0.85）
        if (preFlopStrength > 0.6) {
            if (position == Position.LATE) {
                if (context.getCurrentBet() <= context.getStackSize() * 0.05) {
                    return new DecisionResult(Action.RAISE, 2.5,
                            "后位持中等牌，小额加注或跟注",
                            0.7, winRate, ev);
                } else {
                    return new DecisionResult(Action.CALL, 0,
                            "后位持中等牌，跟注看翻牌",
                            0.7, winRate, ev);
                }
            } else if (position == Position.MIDDLE) {
                if (winRate > potOdds) {
                    return new DecisionResult(Action.CALL, 0,
                            "中位持中等牌，底池成败比合适，跟注",
                            0.6, winRate, ev);
                } else {
                    return new DecisionResult(Action.FOLD, 0,
                            "中位持中等牌，底池成败比不合适，弃牌",
                            0.6, winRate, ev);
                }
            } else {
                // 前位谨慎
                if (context.getCurrentBet() > context.getStackSize() * 0.08) {
                    return new DecisionResult(Action.FOLD, 0,
                            "前位持中等牌面对加注，谨慎弃牌",
                            0.7, winRate, ev);
                } else {
                    return new DecisionResult(Action.CALL, 0,
                            "前位持中等牌，小额跟注",
                            0.5, winRate, ev);
                }
            }
        }
        
        // 弱牌（手牌强度 < 0.6）
        if (context.getCurrentBet() == 0 && position == Position.LATE) {
            return new DecisionResult(Action.CALL, 0,
                    "后位弱牌但无需投入筹码，可以看翻牌",
                    0.4, winRate, ev);
        }
        
        return new DecisionResult(Action.FOLD, 0,
                "手牌较弱，选择弃牌",
                0.8, winRate, ev);
    }
    
    /**
     * 翻牌后决策
     */
    private static DecisionResult postFlopDecision(DecisionContext context, double winRate, 
                                                  double potOdds, double ev,
                                                  WinRateCalculator.WinRateResult winRateResult) {
        int outs = WinRateCalculator.estimateOuts(context.getHoleCards(), context.getCommunityCards());
        int cardsTocome = 5 - context.getCommunityCards().size();
        double improveProbability = WinRateCalculator.calculateImproveProbability(outs, cardsTocome);
        
        // 非常强的牌（胜率 > 0.75）
        if (winRate > 0.75) {
            if (context.getCurrentBet() == 0) {
                // 没有人下注，我们主动下注
                double betSize = context.getStage() == GameStage.RIVER ? 0.7 : 0.6;
                return new DecisionResult(Action.RAISE, betSize,
                        String.format("持有强牌（胜率%.2f%%），主动下注获取价值", winRate * 100),
                        0.9, winRate, ev);
            } else {
                // 有人下注，我们加注
                if (context.getCurrentBet() < context.getStackSize() * 0.5) {
                    return new DecisionResult(Action.RAISE, 2.5,
                            String.format("持有强牌（胜率%.2f%%），加注获取更多价值", winRate * 100),
                            0.9, winRate, ev);
                } else {
                    // 面对大额下注，全下
                    return new DecisionResult(Action.ALL_IN, 1.0,
                            String.format("持有强牌（胜率%.2f%%），全下", winRate * 100),
                            0.85, winRate, ev);
                }
            }
        }
        
        // 中等牌力（胜率 0.45-0.75）
        if (winRate > 0.45) {
            if (context.getCurrentBet() == 0) {
                // 没有人下注
                if (winRate > 0.6) {
                    return new DecisionResult(Action.RAISE, 0.5,
                            String.format("中等牌力（胜率%.2f%%），下注保护手牌", winRate * 100),
                            0.7, winRate, ev);
                } else {
                    return new DecisionResult(Action.CHECK, 0,
                            String.format("中等牌力（胜率%.2f%%），过牌看免费牌", winRate * 100),
                            0.6, winRate, ev);
                }
            } else {
                // 有人下注，检查底池成败比
                if (winRate > potOdds) {
                    return new DecisionResult(Action.CALL, 0,
                            String.format("中等牌力（胜率%.2f%%），底池成败比合适，跟注", winRate * 100),
                            0.7, winRate, ev);
                } else {
                    if (outs > 8 && improveProbability > 0.3) {
                        return new DecisionResult(Action.CALL, 0,
                                String.format("听牌（%d个出牌，改进概率%.2f%%），跟注", outs, improveProbability * 100),
                                0.6, winRate, ev);
                    } else {
                        return new DecisionResult(Action.FOLD, 0,
                                String.format("中等牌力但底池成败比不合适，弃牌", winRate * 100),
                                0.7, winRate, ev);
                    }
                }
            }
        }
        
        // 听牌（胜率 0.25-0.45）
        if (winRate > 0.25 && outs > 4) {
            double totalOdds = winRate + improveProbability;
            if (totalOdds > potOdds * 1.2) {
                return new DecisionResult(Action.CALL, 0,
                        String.format("听牌（%d个出牌，胜率+改进=%.2f%%），跟注", outs, totalOdds * 100),
                        0.6, winRate, ev);
            }
        }
        
        // 弱牌（胜率 < 0.25）
        if (context.getCurrentBet() == 0) {
            return new DecisionResult(Action.CHECK, 0,
                    String.format("弱牌（胜率%.2f%%），过牌", winRate * 100),
                    0.7, winRate, ev);
        }
        
        // 尝试诈唬（在某些情况下）
        if (context.getPosition() == Position.LATE && !context.isAggressive() 
                && context.getCurrentBet() == 0 && winRate < 0.3) {
            return new DecisionResult(Action.RAISE, 0.6,
                    "后位诈唬，尝试拿下底池",
                    0.4, winRate, ev);
        }
        
        return new DecisionResult(Action.FOLD, 0,
                String.format("弱牌（胜率%.2f%%），面对下注选择弃牌", winRate * 100),
                0.8, winRate, ev);
    }
    
    /**
     * 调整策略的激进程度
     * @param baseDecision 基础决策
     * @param aggressionFactor 激进因子（0.5-2.0，1.0为标准）
     * @return 调整后的决策
     */
    public static DecisionResult adjustAggression(DecisionResult baseDecision, double aggressionFactor) {
        if (aggressionFactor < 0.7) {
            // 保守打法：降低加注频率
            if (baseDecision.getRecommendedAction() == Action.RAISE && baseDecision.getWinRate() < 0.6) {
                return new DecisionResult(Action.CALL, 0,
                        "采用保守策略，降级为跟注",
                        baseDecision.getConfidence() * 0.9,
                        baseDecision.getWinRate(),
                        baseDecision.getExpectedValue());
            }
        } else if (aggressionFactor > 1.3) {
            // 激进打法：增加加注频率和大小
            if (baseDecision.getRecommendedAction() == Action.CALL && baseDecision.getWinRate() > 0.5) {
                return new DecisionResult(Action.RAISE, 0.6,
                        "采用激进策略，升级为加注",
                        baseDecision.getConfidence() * 0.9,
                        baseDecision.getWinRate(),
                        baseDecision.getExpectedValue());
            }
            if (baseDecision.getRecommendedAction() == Action.RAISE) {
                return new DecisionResult(Action.RAISE, baseDecision.getBetSize() * 1.3,
                        "采用激进策略，增加下注额",
                        baseDecision.getConfidence(),
                        baseDecision.getWinRate(),
                        baseDecision.getExpectedValue());
            }
        }
        
        return baseDecision;
    }
}
