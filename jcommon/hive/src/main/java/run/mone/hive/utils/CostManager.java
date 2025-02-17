
package run.mone.hive.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class CostManager {
    private double maxBudget;
    private double totalCost;

    public CostManager(double initialBudget) {
        this.maxBudget = initialBudget;
        this.totalCost = 0.0;
    }

    public void addCost(double cost) {
        this.totalCost += cost;
        log.debug("Added cost: ${}, Total cost: ${}", cost, this.totalCost);
    }

    public boolean isWithinBudget() {
        return this.totalCost < this.maxBudget;
    }

    public double getRemainingBudget() {
        return Math.max(0, this.maxBudget - this.totalCost);
    }

    @Override
    public String toString() {
        return String.format("CostManager(maxBudget=%.2f, totalCost=%.2f)", maxBudget, totalCost);
    }
}
