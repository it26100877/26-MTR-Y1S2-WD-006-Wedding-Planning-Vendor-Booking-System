package com.wedding.member02_wedding_plan.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Calculated totals for the budget tracker page (not stored in a file).
 *
 * @author Member 02
 */
public class BudgetSummary {

    /** Planned and spent totals for one category. */
    public static class CategoryTotal {
        private final BudgetCategory category;
        private final BigDecimal planned;
        private final BigDecimal spent;
        private final int itemCount;

        public CategoryTotal(BudgetCategory category, BigDecimal planned, BigDecimal spent, int itemCount) {
            this.category = category;
            this.planned = planned;
            this.spent = spent;
            this.itemCount = itemCount;
        }

        public BudgetCategory getCategory() { return category; }
        public BigDecimal getPlanned() { return planned; }
        public BigDecimal getSpent() { return spent; }
        public int getItemCount() { return itemCount; }
        public boolean isOverBudget() { return spent.compareTo(planned) > 0; }

        public int getBarPercent() {
            if (planned.signum() <= 0) return spent.signum() > 0 ? 100 : 0;
            int pct = spent.multiply(BigDecimal.valueOf(100)).divide(planned, 0, RoundingMode.HALF_UP).intValue();
            return Math.min(100, pct);
        }
    }

    private final BigDecimal totalBudget;
    private final BigDecimal totalPlanned;
    private final BigDecimal totalSpent;
    private final List<CategoryTotal> categories;

    public BudgetSummary(BigDecimal totalBudget, BigDecimal totalPlanned, BigDecimal totalSpent,
                         List<CategoryTotal> categories) {
        this.totalBudget = totalBudget == null ? BigDecimal.ZERO : totalBudget;
        this.totalPlanned = totalPlanned;
        this.totalSpent = totalSpent;
        this.categories = categories;
    }

    public BigDecimal getTotalBudget() { return totalBudget; }
    /** Sum of planned amounts of all items (the money allocated so far). */
    public BigDecimal getTotalPlanned() { return totalPlanned; }
    public BigDecimal getTotalSpent() { return totalSpent; }
    public List<CategoryTotal> getCategories() { return categories; }

    /** Budget not yet allocated to any item (negative if over-allocated). */
    public BigDecimal getUnallocated() { return totalBudget.subtract(totalPlanned); }

    /** Budget not yet spent (negative if overspent). */
    public BigDecimal getRemaining() { return totalBudget.subtract(totalSpent); }

    public boolean isAllocationOverBudget() { return totalPlanned.compareTo(totalBudget) > 0; }
    public boolean isSpendingOverBudget() { return totalSpent.compareTo(totalBudget) > 0; }

    private int percentOfBudget(BigDecimal amount) {
        if (totalBudget.signum() <= 0) return amount.signum() > 0 ? 100 : 0;
        return amount.multiply(BigDecimal.valueOf(100)).divide(totalBudget, 0, RoundingMode.HALF_UP).intValue();
    }

    public int getSpentPercent() { return percentOfBudget(totalSpent); }
    public int getAllocatedPercent() { return percentOfBudget(totalPlanned); }
    public int getSpentBarPercent() { return Math.min(100, getSpentPercent()); }
    public int getAllocatedBarPercent() { return Math.min(100, getAllocatedPercent()); }
}
