package run.mone.hive.utils;

public class NoMoneyException extends RuntimeException {
    private final double cost;

    public NoMoneyException(double cost, String message) {
        super(message);
        this.cost = cost;
    }

    public double getCost() {
        return cost;
    }
} 