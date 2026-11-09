package com.wedding.member02_wedding_plan.model;

/**
 * Abstract base class for everything that belongs to a wedding plan:
 * checklist tasks, budget items and guests.
 *
 * <ul>
 *   <li><b>Encapsulation</b>: id, plan link and creation time are private.</li>
 *   <li><b>Inheritance</b>: ChecklistTask, BudgetItem and Guest extend this class.</li>
 *   <li><b>Polymorphism</b>: getItemType(), getDisplayTitle() and getDetailLine()
 *       are implemented by each subclass and used by the report/export code
 *       without knowing the concrete type.</li>
 * </ul>
 *
 * @author Member 02
 */
public abstract class PlanItem {

    private String itemId;
    private String planId;
    private String createdAt;

    protected PlanItem(String itemId, String planId, String createdAt) {
        this.itemId = itemId;
        this.planId = planId;
        this.createdAt = createdAt;
    }

    public String getItemId() { return itemId; }
    public String getPlanId() { return planId; }
    public String getCreatedAt() { return createdAt; }

    public void setItemId(String itemId) { this.itemId = itemId; }
    public void setPlanId(String planId) { this.planId = planId; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    /** CHECKLIST_TASK | BUDGET_ITEM | GUEST */
    public abstract String getItemType();

    /** Main text shown for the item in lists and reports. */
    public abstract String getDisplayTitle();

    /** Secondary one-line description of the item. */
    public abstract String getDetailLine();
}
