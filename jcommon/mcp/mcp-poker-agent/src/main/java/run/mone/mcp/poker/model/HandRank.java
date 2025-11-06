package run.mone.mcp.poker.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 德州扑克牌型等级
 * @author poker-agent
 * @date 2025/10/21
 */
@Data
@AllArgsConstructor
public class HandRank implements Comparable<HandRank> {
    
    /**
     * 牌型类型
     */
    private HandType type;
    
    /**
     * 牌型的具体点数（用于比较相同牌型的大小）
     */
    private int[] values;
    
    /**
     * 牌型类型枚举
     */
    public enum HandType {
        HIGH_CARD(0, "高牌"),
        ONE_PAIR(1, "一对"),
        TWO_PAIR(2, "两对"),
        THREE_OF_A_KIND(3, "三条"),
        STRAIGHT(4, "顺子"),
        FLUSH(5, "同花"),
        FULL_HOUSE(6, "葫芦"),
        FOUR_OF_A_KIND(7, "四条"),
        STRAIGHT_FLUSH(8, "同花顺"),
        ROYAL_FLUSH(9, "皇家同花顺");
        
        private final int rank;
        private final String name;
        
        HandType(int rank, String name) {
            this.rank = rank;
            this.name = name;
        }
        
        public int getRank() {
            return rank;
        }
        
        public String getName() {
            return name;
        }
    }
    
    @Override
    public int compareTo(HandRank other) {
        // 首先比较牌型类型
        int typeComparison = Integer.compare(this.type.getRank(), other.type.getRank());
        if (typeComparison != 0) {
            return typeComparison;
        }
        
        // 如果牌型相同，比较具体点数
        int minLength = Math.min(this.values.length, other.values.length);
        for (int i = 0; i < minLength; i++) {
            int valueComparison = Integer.compare(this.values[i], other.values[i]);
            if (valueComparison != 0) {
                return valueComparison;
            }
        }
        
        return 0;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type.getName());
        sb.append(" [");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(values[i]);
        }
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * 获取牌型强度分数（用于胜率计算）
     */
    public double getStrength() {
        // 基础分数根据牌型等级
        double baseScore = type.getRank() * 1000;
        
        // 加上具体牌值的分数
        double valueScore = 0;
        for (int i = 0; i < values.length; i++) {
            valueScore += values[i] * Math.pow(15, values.length - i - 1);
        }
        
        return baseScore + valueScore;
    }
}
