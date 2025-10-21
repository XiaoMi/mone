package run.mone.mcp.poker.calculator;

import lombok.Data;
import run.mone.mcp.poker.model.Card;
import run.mone.mcp.poker.model.HandRank;
import run.mone.mcp.poker.util.HandEvaluator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 德州扑克胜率计算器
 * 使用蒙特卡洛模拟方法计算胜率
 * @author poker-agent
 * @date 2025/10/21
 */
public class WinRateCalculator {
    
    private static final int DEFAULT_SIMULATIONS = 10000; // 默认模拟次数
    private static final Random random = new Random();
    
    /**
     * 计算胜率结果
     */
    @Data
    public static class WinRateResult {
        private double winRate;        // 胜率
        private double tieRate;        // 平局率
        private double loseRate;       // 输率
        private int simulations;       // 模拟次数
        private HandRank currentBestHand; // 当前最佳牌型
        private double handStrength;   // 牌力强度
        
        public WinRateResult(double winRate, double tieRate, double loseRate, 
                           int simulations, HandRank currentBestHand) {
            this.winRate = winRate;
            this.tieRate = tieRate;
            this.loseRate = loseRate;
            this.simulations = simulations;
            this.currentBestHand = currentBestHand;
            this.handStrength = currentBestHand != null ? currentBestHand.getStrength() : 0;
        }
        
        @Override
        public String toString() {
            return String.format("胜率: %.2f%%, 平局率: %.2f%%, 输率: %.2f%% (模拟%d次), 当前牌型: %s, 牌力: %.2f",
                    winRate * 100, tieRate * 100, loseRate * 100, simulations, 
                    currentBestHand != null ? currentBestHand.toString() : "无",
                    handStrength);
        }
    }
    
    /**
     * 计算胜率（使用默认模拟次数）
     * @param holeCards 手牌（2张）
     * @param communityCards 公共牌（0-5张）
     * @param numOpponents 对手数量
     * @return 胜率结果
     */
    public static WinRateResult calculateWinRate(List<Card> holeCards, 
                                                 List<Card> communityCards, 
                                                 int numOpponents) {
        return calculateWinRate(holeCards, communityCards, numOpponents, DEFAULT_SIMULATIONS);
    }
    
    /**
     * 计算胜率（指定模拟次数）
     * @param holeCards 手牌（2张）
     * @param communityCards 公共牌（0-5张）
     * @param numOpponents 对手数量
     * @param simulations 模拟次数
     * @return 胜率结果
     */
    public static WinRateResult calculateWinRate(List<Card> holeCards, 
                                                 List<Card> communityCards, 
                                                 int numOpponents,
                                                 int simulations) {
        if (holeCards.size() != 2) {
            throw new IllegalArgumentException("手牌必须是2张");
        }
        if (communityCards.size() > 5) {
            throw new IllegalArgumentException("公共牌最多5张");
        }
        if (numOpponents < 1 || numOpponents > 9) {
            throw new IllegalArgumentException("对手数量必须在1-9之间");
        }
        
        // 创建剩余牌库
        Set<Card> usedCards = new HashSet<>(holeCards);
        usedCards.addAll(communityCards);
        List<Card> remainingDeck = createDeck().stream()
                .filter(card -> !usedCards.contains(card))
                .collect(Collectors.toList());
        
        int wins = 0;
        int ties = 0;
        int losses = 0;
        
        // 蒙特卡洛模拟
        for (int i = 0; i < simulations; i++) {
            // 洗牌
            Collections.shuffle(remainingDeck);
            
            // 补全公共牌到5张
            List<Card> simulatedCommunity = new ArrayList<>(communityCards);
            int cardsNeeded = 5 - communityCards.size();
            for (int j = 0; j < cardsNeeded; j++) {
                simulatedCommunity.add(remainingDeck.get(j));
            }
            
            // 计算玩家的最佳牌型
            List<Card> playerAllCards = new ArrayList<>(holeCards);
            playerAllCards.addAll(simulatedCommunity);
            HandRank playerHand = HandEvaluator.evaluateBestHand(playerAllCards);
            
            // 模拟对手的牌
            boolean playerWins = true;
            boolean tie = false;
            int deckIndex = cardsNeeded;
            
            for (int opp = 0; opp < numOpponents; opp++) {
                // 给对手发两张牌
                List<Card> opponentHoleCards = Arrays.asList(
                        remainingDeck.get(deckIndex++),
                        remainingDeck.get(deckIndex++)
                );
                
                List<Card> opponentAllCards = new ArrayList<>(opponentHoleCards);
                opponentAllCards.addAll(simulatedCommunity);
                HandRank opponentHand = HandEvaluator.evaluateBestHand(opponentAllCards);
                
                int comparison = playerHand.compareTo(opponentHand);
                if (comparison < 0) {
                    playerWins = false;
                    break;
                } else if (comparison == 0) {
                    tie = true;
                }
            }
            
            if (playerWins && !tie) {
                wins++;
            } else if (tie) {
                ties++;
            } else {
                losses++;
            }
        }
        
        // 计算当前最佳牌型（如果公共牌已经有的话）
        HandRank currentBestHand = null;
        if (communityCards.size() >= 3) {
            List<Card> currentAllCards = new ArrayList<>(holeCards);
            currentAllCards.addAll(communityCards);
            currentBestHand = HandEvaluator.evaluateBestHand(currentAllCards);
        }
        
        double winRate = (double) wins / simulations;
        double tieRate = (double) ties / simulations;
        double loseRate = (double) losses / simulations;
        
        return new WinRateResult(winRate, tieRate, loseRate, simulations, currentBestHand);
    }
    
    /**
     * 快速评估手牌强度（翻牌前）
     * @param holeCards 手牌（2张）
     * @return 手牌强度评分（0-1）
     */
    public static double evaluatePreFlopStrength(List<Card> holeCards) {
        if (holeCards.size() != 2) {
            throw new IllegalArgumentException("手牌必须是2张");
        }
        
        Card card1 = holeCards.get(0);
        Card card2 = holeCards.get(1);
        
        int rank1 = card1.getRank().getValue();
        int rank2 = card2.getRank().getValue();
        boolean suited = card1.getSuit() == card2.getSuit();
        boolean paired = rank1 == rank2;
        
        double strength = 0.0;
        
        // 对子加分
        if (paired) {
            strength = 0.5 + (rank1 / 28.0); // AA=0.5+14/28=1.0, 22=0.5+2/28=0.57
        } else {
            // 高牌加分
            int highRank = Math.max(rank1, rank2);
            int lowRank = Math.min(rank1, rank2);
            strength = (highRank + lowRank) / 28.0; // 最大28 (A+A)
            
            // 同花加分
            if (suited) {
                strength += 0.1;
            }
            
            // 连牌加分
            if (Math.abs(rank1 - rank2) == 1) {
                strength += 0.05;
            } else if (Math.abs(rank1 - rank2) <= 3) {
                strength += 0.02;
            }
            
            // 有A或K加分
            if (highRank >= 13) {
                strength += 0.05;
            }
        }
        
        return Math.min(strength, 1.0);
    }
    
    /**
     * 计算底池成败比（Pot Odds）
     * @param potSize 底池大小
     * @param betSize 需要跟注的大小
     * @return 底池成败比
     */
    public static double calculatePotOdds(double potSize, double betSize) {
        return betSize / (potSize + betSize);
    }
    
    /**
     * 判断是否应该跟注
     * @param winRate 胜率
     * @param potOdds 底池成败比
     * @return 是否应该跟注
     */
    public static boolean shouldCall(double winRate, double potOdds) {
        return winRate > potOdds;
    }
    
    /**
     * 计算期望收益（EV）
     * @param winRate 胜率
     * @param potSize 底池大小
     * @param betSize 需要投入的筹码
     * @return 期望收益
     */
    public static double calculateExpectedValue(double winRate, double potSize, double betSize) {
        double winAmount = potSize + betSize;
        double loseAmount = -betSize;
        return (winRate * winAmount) + ((1 - winRate) * loseAmount);
    }
    
    /**
     * 创建一副完整的扑克牌
     */
    private static List<Card> createDeck() {
        List<Card> deck = new ArrayList<>();
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                deck.add(new Card(suit, rank));
            }
        }
        return deck;
    }
    
    /**
     * 计算出牌数（Outs）
     * 即能改进当前牌型使其获胜的牌数
     * @param holeCards 手牌
     * @param communityCards 公共牌
     * @return 出牌数估算
     */
    public static int estimateOuts(List<Card> holeCards, List<Card> communityCards) {
        if (communityCards.size() < 3) {
            return 0; // 翻牌前无法估算
        }
        
        List<Card> allCards = new ArrayList<>(holeCards);
        allCards.addAll(communityCards);
        HandRank currentHand = HandEvaluator.evaluateBestHand(allCards);
        
        // 简化的出牌数估算
        int outs = 0;
        
        // 检查听牌情况
        Map<Card.Suit, Long> suitCounts = allCards.stream()
                .collect(Collectors.groupingBy(Card::getSuit, Collectors.counting()));
        
        // 同花听牌 (4张同花色) = 9 outs
        boolean flushDraw = suitCounts.values().stream().anyMatch(count -> count == 4);
        if (flushDraw && currentHand.getType().getRank() < HandRank.HandType.FLUSH.getRank()) {
            outs += 9;
        }
        
        // 顺子听牌 (两端开口) = 8 outs
        // 顺子听牌 (内听) = 4 outs
        // 这里简化处理
        List<Integer> ranks = allCards.stream()
                .map(c -> c.getRank().getValue())
                .sorted()
                .distinct()
                .collect(Collectors.toList());
        
        boolean straightDraw = false;
        for (int i = 0; i < ranks.size() - 3; i++) {
            if (ranks.get(i + 3) - ranks.get(i) <= 4) {
                straightDraw = true;
                break;
            }
        }
        
        if (straightDraw && currentHand.getType().getRank() < HandRank.HandType.STRAIGHT.getRank()) {
            outs += 8; // 简化处理，假设两端开口
        }
        
        // 根据当前牌型估算改进空间
        switch (currentHand.getType()) {
            case HIGH_CARD:
                outs += 6; // 可以成对
                break;
            case ONE_PAIR:
                outs += 5; // 可以成两对或三条
                break;
            case TWO_PAIR:
                outs += 4; // 可以成葫芦
                break;
            case THREE_OF_A_KIND:
                outs += 7; // 可以成葫芦或四条
                break;
        }
        
        return outs;
    }
    
    /**
     * 根据出牌数计算改进概率
     * @param outs 出牌数
     * @param cardsTocome 还要发多少张牌
     * @return 改进概率
     */
    public static double calculateImproveProbability(int outs, int cardsTocome) {
        if (cardsTocome == 1) {
            // 河牌：outs / 46
            return outs / 46.0;
        } else if (cardsTocome == 2) {
            // 转牌+河牌：使用规则 "outs * 4"
            return Math.min(outs * 4.0 / 100.0, 1.0);
        }
        return 0.0;
    }
}
