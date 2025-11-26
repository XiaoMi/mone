package run.mone.mcp.poker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 扑克牌类
 * @author poker-agent
 * @date 2025/10/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card implements Comparable<Card> {
    
    /**
     * 牌的花色
     */
    private Suit suit;
    
    /**
     * 牌的点数
     */
    private Rank rank;
    
    /**
     * 花色枚举
     */
    public enum Suit {
        CLUBS("♣", 0),      // 梅花
        DIAMONDS("♦", 1),   // 方块
        HEARTS("♥", 2),     // 红心
        SPADES("♠", 3);     // 黑桃
        
        private final String symbol;
        private final int value;
        
        Suit(String symbol, int value) {
            this.symbol = symbol;
            this.value = value;
        }
        
        public String getSymbol() {
            return symbol;
        }
        
        public int getValue() {
            return value;
        }
    }
    
    /**
     * 点数枚举
     */
    public enum Rank {
        TWO("2", 2),
        THREE("3", 3),
        FOUR("4", 4),
        FIVE("5", 5),
        SIX("6", 6),
        SEVEN("7", 7),
        EIGHT("8", 8),
        NINE("9", 9),
        TEN("T", 10),
        JACK("J", 11),
        QUEEN("Q", 12),
        KING("K", 13),
        ACE("A", 14);
        
        private final String symbol;
        private final int value;
        
        Rank(String symbol, int value) {
            this.symbol = symbol;
            this.value = value;
        }
        
        public String getSymbol() {
            return symbol;
        }
        
        public int getValue() {
            return value;
        }
    }
    
    @Override
    public int compareTo(Card other) {
        return Integer.compare(this.rank.getValue(), other.rank.getValue());
    }
    
    @Override
    public String toString() {
        return rank.getSymbol() + suit.getSymbol();
    }
    
    /**
     * 从字符串创建牌，例如 "As" 表示黑桃A
     */
    public static Card fromString(String cardStr) {
        if (cardStr == null || cardStr.length() < 2) {
            throw new IllegalArgumentException("Invalid card string: " + cardStr);
        }
        
        char rankChar = cardStr.charAt(0);
        char suitChar = cardStr.charAt(1);
        
        Rank rank = parseRank(rankChar);
        Suit suit = parseSuit(suitChar);
        
        return new Card(suit, rank);
    }
    
    private static Rank parseRank(char rankChar) {
        for (Rank rank : Rank.values()) {
            if (rank.getSymbol().equalsIgnoreCase(String.valueOf(rankChar))) {
                return rank;
            }
        }
        throw new IllegalArgumentException("Invalid rank: " + rankChar);
    }
    
    private static Suit parseSuit(char suitChar) {
        switch (Character.toLowerCase(suitChar)) {
            case 'c': return Suit.CLUBS;
            case 'd': return Suit.DIAMONDS;
            case 'h': return Suit.HEARTS;
            case 's': return Suit.SPADES;
            default: throw new IllegalArgumentException("Invalid suit: " + suitChar);
        }
    }
}
