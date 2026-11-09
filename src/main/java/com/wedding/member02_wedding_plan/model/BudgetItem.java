package com.wedding.member02_wedding_plan.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * One line of a wedding budget: how much was planned and how much has been spent.
 * Encapsulation + Inheritance (extends {@link PlanItem}).
 *
 * @author Member 02
 */
public class BudgetItem extends PlanItem {

    private BudgetCategory category;
    private String itemName;
    private BigDecimal plannedAmount;
    private BigDecimal spentAmount;
    private String note;

    public BudgetItem(String itemId, String planId, BudgetCategory category, String itemName,
                      BigDecimal plannedAmount, BigDecimal spentAmount, String note, String createdAt) {
        super(itemId, planId, createdAt);
        this.category = category == null ? BudgetCategory.OTHER : category;
        this.itemName = itemName;
        this.plannedAmount = plannedAmount == null ? BigDecimal.ZERO : plannedAmount;
        this.spentAmount = spentAmount == null ? BigDecimal.ZERO : spentAmount;
        this.note = note;
    }

    public BudgetCategory getCategory() { return category; }
    public String getItemName() { return itemName; }
    public BigDecimal getPlannedAmount() { return plannedAmount; }
    public BigDecimal getSpentAmount() { return spentAmount; }
    public String getNote() { return note; }

    public void setCategory(BudgetCategory category) { this.category = category == null ? BudgetCategory.OTHER : category; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public void setPlannedAmount(BigDecimal plannedAmount) { this.plannedAmount = plannedAmount == null ? BigDecimal.ZERO : plannedAmount; }
    public void setSpentAmount(BigDecimal spentAmount) { this.spentAmount = spentAmount == null ? BigDecimal.ZERO : spentAmount; }
    public void setNote(String note) { this.note = note; }

    /** Planned minus spent; negative when over budget. */
    public BigDecimal getRemaining() { return plannedAmount.subtract(spentAmount); }

    public boolean isOverBudget() { return spentAmount.compareTo(plannedAmount) > 0; }

    /** Spent as a percentage of planned (can exceed 100). */
    public int getUsagePercent() {
        if (plannedAmount.signum() <= 0) return spentAmount.signum() > 0 ? 100 : 0;
        return spentAmount.multiply(BigDecimal.valueOf(100))
                .divide(plannedAmount, 0, RoundingMode.HALF_UP).intValue();
    }

    /** Usage percentage limited to 100 for progress bars. */
    public int getBarPercent() { return Math.min(100, getUsagePercent()); }

    @Override
    public String getItemType() { return "BUDGET_ITEM"; }

    @Override
    public String getDisplayTitle() { return itemName; }

    @Override
    public String getDetailLine() {
        return category.getLabel() + ", planned " + plannedAmount.toPlainString()
                + ", spent " + spentAmount.toPlainString();
    }
}
