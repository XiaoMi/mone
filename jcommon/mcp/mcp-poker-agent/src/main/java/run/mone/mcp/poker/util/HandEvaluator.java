package run.mone.mcp.poker.util;

import run.mone.mcp.poker.model.Card;
import run.mone.mcp.poker.model.HandRank;
import run.mone.mcp.poker.model.HandRank.HandType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 德州扑克牌型评估工具
 * @author poker-agent
 * @date 2025/10/21
 */
public class HandEvaluator {
    
    /**
     * 评估7张牌中的最佳5张牌组合
     * @param cards 7张牌（2张手牌 + 5张公共牌）
     * @return 最佳牌型
     */
    public static HandRank evaluateBestHand(List<Card> cards) {
        if (cards.size() < 5) {
            throw new IllegalArgumentException("至少需要5张牌进行评估");
        }
        
        HandRank bestHand = null;
        
        // 生成所有5张牌的组合
        List<List<Card>> combinations = generateCombinations(cards, 5);
        
        for (List<Card> combo : combinations) {
            HandRank currentHand = evaluateHand(combo);
            if (bestHand == null || currentHand.compareTo(bestHand) > 0) {
                bestHand = currentHand;
            }
        }
        
        return bestHand;
    }
    
    /**
     * 评估5张牌的牌型
     */
    private static HandRank evaluateHand(List<Card> cards) {
        if (cards.size() != 5) {
            throw new IllegalArgumentException("必须是5张牌");
        }
        
        // 排序
        List<Card> sortedCards = cards.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        
        // 检查各种牌型
        HandRank flush = checkFlush(sortedCards);
        HandRank straight = checkStraight(sortedCards);
        
        // 同花顺/皇家同花顺
        if (flush != null && straight != null) {
            if (straight.getValues()[0] == 14) { // A高的同花顺
                return new HandRank(HandType.ROYAL_FLUSH, straight.getValues());
            }
            return new HandRank(HandType.STRAIGHT_FLUSH, straight.getValues());
        }
        
        // 四条
        HandRank fourOfKind = checkFourOfAKind(sortedCards);
        if (fourOfKind != null) {
            return fourOfKind;
        }
        
        // 葫芦
        HandRank fullHouse = checkFullHouse(sortedCards);
        if (fullHouse != null) {
            return fullHouse;
        }
        
        // 同花
        if (flush != null) {
            return flush;
        }
        
        // 顺子
        if (straight != null) {
            return straight;
        }
        
        // 三条
        HandRank threeOfKind = checkThreeOfAKind(sortedCards);
        if (threeOfKind != null) {
            return threeOfKind;
        }
        
        // 两对
        HandRank twoPair = checkTwoPair(sortedCards);
        if (twoPair != null) {
            return twoPair;
        }
        
        // 一对
        HandRank onePair = checkOnePair(sortedCards);
        if (onePair != null) {
            return onePair;
        }
        
        // 高牌
        return checkHighCard(sortedCards);
    }
    
    /**
     * 检查同花
     */
    private static HandRank checkFlush(List<Card> cards) {
        Card.Suit suit = cards.get(0).getSuit();
        boolean isFlush = cards.stream().allMatch(c -> c.getSuit() == suit);
        
        if (isFlush) {
            int[] values = cards.stream()
                    .mapToInt(c -> c.getRank().getValue())
                    .toArray();
            return new HandRank(HandType.FLUSH, values);
        }
        return null;
    }
    
    /**
     * 检查顺子
     */
    private static HandRank checkStraight(List<Card> cards) {
        List<Integer> ranks = cards.stream()
                .map(c -> c.getRank().getValue())
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        
        if (ranks.size() != 5) {
            return null;
        }
        
        // 检查普通顺子
        boolean isStraight = true;
        for (int i = 0; i < ranks.size() - 1; i++) {
            if (ranks.get(i) - ranks.get(i + 1) != 1) {
                isStraight = false;
                break;
            }
        }
        
        if (isStraight) {
            return new HandRank(HandType.STRAIGHT, new int[]{ranks.get(0)});
        }
        
        // 检查A-2-3-4-5的特殊顺子
        if (ranks.get(0) == 14 && ranks.get(1) == 5 && ranks.get(2) == 4 
                && ranks.get(3) == 3 && ranks.get(4) == 2) {
            return new HandRank(HandType.STRAIGHT, new int[]{5}); // A当作1，顺子最高是5
        }
        
        return null;
    }
    
    /**
     * 检查四条
     */
    private static HandRank checkFourOfAKind(List<Card> cards) {
        Map<Integer, Long> rankCounts = getRankCounts(cards);
        
        Optional<Integer> fourKind = rankCounts.entrySet().stream()
                .filter(e -> e.getValue() == 4)
                .map(Map.Entry::getKey)
                .findFirst();
        
        if (fourKind.isPresent()) {
            int kicker = rankCounts.entrySet().stream()
                    .filter(e -> e.getValue() != 4)
                    .map(Map.Entry::getKey)
                    .max(Integer::compare)
                    .orElse(0);
            
            return new HandRank(HandType.FOUR_OF_A_KIND, new int[]{fourKind.get(), kicker});
        }
        
        return null;
    }
    
    /**
     * 检查葫芦
     */
    private static HandRank checkFullHouse(List<Card> cards) {
        Map<Integer, Long> rankCounts = getRankCounts(cards);
        
        Optional<Integer> threeKind = rankCounts.entrySet().stream()
                .filter(e -> e.getValue() == 3)
                .map(Map.Entry::getKey)
                .findFirst();
        
        Optional<Integer> pair = rankCounts.entrySet().stream()
                .filter(e -> e.getValue() == 2)
                .map(Map.Entry::getKey)
                .findFirst();
        
        if (threeKind.isPresent() && pair.isPresent()) {
            return new HandRank(HandType.FULL_HOUSE, new int[]{threeKind.get(), pair.get()});
        }
        
        return null;
    }
    
    /**
     * 检查三条
     */
    private static HandRank checkThreeOfAKind(List<Card> cards) {
        Map<Integer, Long> rankCounts = getRankCounts(cards);
        
        Optional<Integer> threeKind = rankCounts.entrySet().stream()
                .filter(e -> e.getValue() == 3)
                .map(Map.Entry::getKey)
                .findFirst();
        
        if (threeKind.isPresent()) {
            List<Integer> kickers = rankCounts.entrySet().stream()
                    .filter(e -> e.getValue() != 3)
                    .map(Map.Entry::getKey)
                    .sorted(Comparator.reverseOrder())
                    .limit(2)
                    .collect(Collectors.toList());
            
            int[] values = new int[3];
            values[0] = threeKind.get();
            values[1] = kickers.size() > 0 ? kickers.get(0) : 0;
            values[2] = kickers.size() > 1 ? kickers.get(1) : 0;
            
            return new HandRank(HandType.THREE_OF_A_KIND, values);
        }
        
        return null;
    }
    
    /**
     * 检查两对
     */
    private static HandRank checkTwoPair(List<Card> cards) {
        Map<Integer, Long> rankCounts = getRankCounts(cards);
        
        List<Integer> pairs = rankCounts.entrySet().stream()
                .filter(e -> e.getValue() == 2)
                .map(Map.Entry::getKey)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        
        if (pairs.size() >= 2) {
            int kicker = rankCounts.entrySet().stream()
                    .filter(e -> e.getValue() == 1)
                    .map(Map.Entry::getKey)
                    .max(Integer::compare)
                    .orElse(0);
            
            return new HandRank(HandType.TWO_PAIR, new int[]{pairs.get(0), pairs.get(1), kicker});
        }
        
        return null;
    }
    
    /**
     * 检查一对
     */
    private static HandRank checkOnePair(List<Card> cards) {
        Map<Integer, Long> rankCounts = getRankCounts(cards);
        
        Optional<Integer> pair = rankCounts.entrySet().stream()
                .filter(e -> e.getValue() == 2)
                .map(Map.Entry::getKey)
                .findFirst();
        
        if (pair.isPresent()) {
            List<Integer> kickers = rankCounts.entrySet().stream()
                    .filter(e -> e.getValue() == 1)
                    .map(Map.Entry::getKey)
                    .sorted(Comparator.reverseOrder())
                    .limit(3)
                    .collect(Collectors.toList());
            
            int[] values = new int[4];
            values[0] = pair.get();
            for (int i = 0; i < 3 && i < kickers.size(); i++) {
                values[i + 1] = kickers.get(i);
            }
            
            return new HandRank(HandType.ONE_PAIR, values);
        }
        
        return null;
    }
    
    /**
     * 检查高牌
     */
    private static HandRank checkHighCard(List<Card> cards) {
        int[] values = cards.stream()
                .mapToInt(c -> c.getRank().getValue())
                .toArray();
        return new HandRank(HandType.HIGH_CARD, values);
    }
    
    /**
     * 获取每个点数的出现次数
     */
    private static Map<Integer, Long> getRankCounts(List<Card> cards) {
        return cards.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getRank().getValue(),
                        Collectors.counting()
                ));
    }
    
    /**
     * 生成所有n张牌的组合
     */
    private static List<List<Card>> generateCombinations(List<Card> cards, int n) {
        List<List<Card>> result = new ArrayList<>();
        generateCombinationsHelper(cards, n, 0, new ArrayList<>(), result);
        return result;
    }
    
    private static void generateCombinationsHelper(List<Card> cards, int n, int start, 
                                                   List<Card> current, List<List<Card>> result) {
        if (current.size() == n) {
            result.add(new ArrayList<>(current));
            return;
        }
        
        for (int i = start; i < cards.size(); i++) {
            current.add(cards.get(i));
            generateCombinationsHelper(cards, n, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }
}
