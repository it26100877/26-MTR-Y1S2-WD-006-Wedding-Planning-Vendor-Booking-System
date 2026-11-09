package com.wedding.member02_wedding_plan.service;

import com.wedding.common.exceptions.BusinessRuleException;
import com.wedding.common.exceptions.EntityNotFoundException;
import com.wedding.common.exceptions.ValidationException;
import com.wedding.common.service.BaseService;
import com.wedding.common.utils.FileUtil;
import com.wedding.common.utils.ValidationUtil;
import com.wedding.member02_wedding_plan.model.BudgetCategory;
import com.wedding.member02_wedding_plan.model.BudgetItem;
import com.wedding.member02_wedding_plan.model.BudgetSummary;
import com.wedding.member02_wedding_plan.repository.BudgetItemRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Business-logic service for the budget tracker (planned vs spent, by category).
 * Extends BaseService (Inheritance). Amounts must be between 0 and 999,999,999,999.
 * Going over the plan's total budget is allowed but is flagged in the BudgetSummary.
 *
 * @author Member 02
 */
public class BudgetService extends BaseService {

    private static final BigDecimal MAX_AMOUNT = new BigDecimal("999999999999");

    private final BudgetItemRepository repository;

    public BudgetService(BudgetItemRepository repository) {
        this.repository = repository;
    }

    // ─── Create ──────────────────────────────────────────────────────────────────

    public BudgetItem addItem(String planId, String categoryCode, String itemName,
                              String plannedAmount, String spentAmount, String note) {
        requireNonEmpty(planId, "Plan ID");
        validateFields(categoryCode, itemName, note);
        BigDecimal planned = parseAmount(plannedAmount, "Planned amount", true);
        BigDecimal spent = parseAmount(spentAmount, "Spent amount", false);

        BudgetItem item = new BudgetItem(FileUtil.generateId("BUD"), planId,
                BudgetCategory.fromCode(categoryCode), itemName.trim(), planned, spent,
                note == null ? "" : note.trim(), ValidationUtil.nowDateTime());
        repository.save(item);
        log("Added budget item " + item.getItemId() + " to plan " + planId);
        return item;
    }

    // ─── Update ──────────────────────────────────────────────────────────────────

    public BudgetItem updateItem(String planId, String itemId, String categoryCode, String itemName,
                                 String plannedAmount, String spentAmount, String note) {
        BudgetItem item = findInPlan(planId, itemId);
        validateFields(categoryCode, itemName, note);
        BigDecimal planned = parseAmount(plannedAmount, "Planned amount", true);
        BigDecimal spent = parseAmount(spentAmount, "Spent amount", false);

        item.setCategory(BudgetCategory.fromCode(categoryCode));
        item.setItemName(itemName.trim());
        item.setPlannedAmount(planned);
        item.setSpentAmount(spent);
        item.setNote(note == null ? "" : note.trim());
        repository.save(item);
        log("Updated budget item " + itemId);
        return item;
    }

    // ─── Delete ──────────────────────────────────────────────────────────────────

    public boolean deleteItem(String planId, String itemId) {
        findInPlan(planId, itemId);
        boolean removed = repository.deleteById(itemId);
        if (removed) log("Deleted budget item " + itemId);
        return removed;
    }

    public int deleteByPlan(String planId) {
        int removed = repository.deleteByPlanId(planId);
        if (removed > 0) log("Deleted " + removed + " budget item(s) of plan " + planId);
        return removed;
    }

    // ─── Queries ─────────────────────────────────────────────────────────────────

    /** Items of a plan ordered by category and then name. */
    public List<BudgetItem> getItemsByPlan(String planId) {
        List<BudgetItem> items = repository.findByPlanId(planId);
        items.sort(Comparator.comparing((BudgetItem i) -> i.getCategory().ordinal())
                .thenComparing(i -> i.getItemName().toLowerCase()));
        return items;
    }

    /** Totals for the plan: overall and per category (only categories that have items). */
    public BudgetSummary buildSummary(BigDecimal totalBudget, List<BudgetItem> items) {
        BigDecimal planned = BigDecimal.ZERO;
        BigDecimal spent = BigDecimal.ZERO;
        List<BudgetSummary.CategoryTotal> categories = new ArrayList<>();

        for (BudgetCategory category : BudgetCategory.values()) {
            BigDecimal catPlanned = BigDecimal.ZERO;
            BigDecimal catSpent = BigDecimal.ZERO;
            int count = 0;
            for (BudgetItem item : items) {
                if (item.getCategory() == category) {
                    catPlanned = catPlanned.add(item.getPlannedAmount());
                    catSpent = catSpent.add(item.getSpentAmount());
                    count++;
                }
            }
            if (count > 0) {
                categories.add(new BudgetSummary.CategoryTotal(category, catPlanned, catSpent, count));
                planned = planned.add(catPlanned);
                spent = spent.add(catSpent);
            }
        }
        return new BudgetSummary(totalBudget, planned, spent, categories);
    }

    // ─── Private Helpers ─────────────────────────────────────────────────────────

    private BudgetItem findInPlan(String planId, String itemId) {
        BudgetItem item = repository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Budget item not found: " + itemId));
        if (planId == null || !planId.equals(item.getPlanId())) {
            throw new BusinessRuleException("This budget item does not belong to the selected plan.");
        }
        return item;
    }

    private void validateFields(String categoryCode, String itemName, String note) {
        requireNonEmpty(itemName, "Item name");
        if (itemName.trim().length() > 100) {
            throw new ValidationException("Item name must be 100 characters or fewer.");
        }
        if (note != null && note.trim().length() > 300) {
            throw new ValidationException("Note must be 300 characters or fewer.");
        }
        if (categoryCode != null && !categoryCode.trim().isEmpty() && !BudgetCategory.isValidCode(categoryCode)) {
            throw new ValidationException("Unknown budget category.");
        }
    }

    private BigDecimal parseAmount(String value, String field, boolean required) {
        if (value == null || value.trim().isEmpty()) {
            if (required) throw new ValidationException(field + " is required.");
            return BigDecimal.ZERO.setScale(2);
        }
        BigDecimal amount;
        try {
            amount = new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException(field + " must be a valid number.");
        }
        if (amount.signum() < 0) {
            throw new ValidationException(field + " cannot be negative.");
        }
        if (amount.compareTo(MAX_AMOUNT) > 0) {
            throw new ValidationException(field + " is too large.");
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
