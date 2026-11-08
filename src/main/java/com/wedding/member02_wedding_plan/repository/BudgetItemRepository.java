package com.wedding.member02_wedding_plan.repository;

import com.wedding.member02_wedding_plan.model.BudgetCategory;
import com.wedding.member02_wedding_plan.model.BudgetItem;

import java.math.BigDecimal;

/**
 * File-based repository for BudgetItem entities (budget_items.csv).
 * Order: itemId|planId|category|itemName|plannedAmount|spentAmount|note|createdAt
 *
 * @author Member 02
 */
public class BudgetItemRepository extends PlanItemRepository<BudgetItem> {

    private static final String FILE_NAME = "budget_items.csv";

    public BudgetItemRepository() {
        super(FILE_NAME);
    }

    /** Uses a different file name (for unit tests). */
    public BudgetItemRepository(String filename) {
        super(filename);
    }

    @Override
    protected String serialize(BudgetItem item) {
        return String.join(SEPARATOR,
                safe(item.getItemId()),
                safe(item.getPlanId()),
                item.getCategory().getCode(),
                safe(item.getItemName()),
                item.getPlannedAmount().toPlainString(),
                item.getSpentAmount().toPlainString(),
                safe(item.getNote()),
                safe(item.getCreatedAt())
        );
    }

    @Override
    protected BudgetItem deserialize(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] p = line.split(SAFE_SEP, -1);
        if (p.length < 8) return null;
        return new BudgetItem(
                p[0].trim(),
                p[1].trim(),
                BudgetCategory.fromCode(p[2]),
                p[3].trim(),
                parseAmount(p[4]),
                parseAmount(p[5]),
                p[6].trim(),
                p[7].trim()
        );
    }

    private BigDecimal parseAmount(String value) {
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
