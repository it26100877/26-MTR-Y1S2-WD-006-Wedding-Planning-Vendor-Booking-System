package com.wedding.member02_wedding_plan.model;

import java.util.List;

/**
 * Read-only snapshot of everything about one wedding plan, used by the checklist,
 * budget, guest and printable summary pages. Task counts are calculated here.
 *
 * @author Member 02
 */
public class PlanSummary {

    private final WeddingPlan plan;
    private final List<ChecklistTask> tasks;
    private final List<BudgetItem> budgetItems;
    private final BudgetSummary budget;
    private final List<Guest> guests;
    private final GuestStats guestStats;

    private final int pendingTasks;
    private final int inProgressTasks;
    private final int completedTasks;
    private final int overdueTasks;
    private final int dueTodayTasks;
    private final int dueInThreeDaysTasks;
    private final int dueInSevenDaysTasks;

    public PlanSummary(WeddingPlan plan, List<ChecklistTask> tasks, List<BudgetItem> budgetItems,
                       BudgetSummary budget, List<Guest> guests, GuestStats guestStats) {
        this.plan = plan;
        this.tasks = tasks;
        this.budgetItems = budgetItems;
        this.budget = budget;
        this.guests = guests;
        this.guestStats = guestStats;

        int pending = 0, inProgress = 0, completed = 0, overdue = 0;
        int today = 0, threeDays = 0, sevenDays = 0;
        for (ChecklistTask t : tasks) {
            switch (t.getReminderCategory()) {
                case ChecklistTask.REMINDER_TODAY: today++; break;
                case ChecklistTask.REMINDER_THREE_DAYS: threeDays++; break;
                case ChecklistTask.REMINDER_SEVEN_DAYS: sevenDays++; break;
                default: break;
            }
            switch (t.getDisplayStatus()) {
                case ChecklistTask.STATUS_COMPLETED: completed++; break;
                case ChecklistTask.STATUS_OVERDUE: overdue++; break;
                case ChecklistTask.STATUS_IN_PROGRESS: inProgress++; break;
                default: pending++;
            }
        }
        this.pendingTasks = pending;
        this.inProgressTasks = inProgress;
        this.completedTasks = completed;
        this.overdueTasks = overdue;
        this.dueTodayTasks = today;
        this.dueInThreeDaysTasks = threeDays;
        this.dueInSevenDaysTasks = sevenDays;
    }

    public WeddingPlan getPlan() { return plan; }
    public List<ChecklistTask> getTasks() { return tasks; }
    public List<BudgetItem> getBudgetItems() { return budgetItems; }
    public BudgetSummary getBudget() { return budget; }
    public List<Guest> getGuests() { return guests; }
    public GuestStats getGuestStats() { return guestStats; }

    public int getTotalTasks() { return tasks.size(); }
    public int getPendingTasks() { return pendingTasks; }
    public int getInProgressTasks() { return inProgressTasks; }
    public int getCompletedTasks() { return completedTasks; }
    public int getOverdueTasks() { return overdueTasks; }
    public int getDueTodayTasks() { return dueTodayTasks; }
    public int getDueInThreeDaysTasks() { return dueInThreeDaysTasks; }
    public int getDueInSevenDaysTasks() { return dueInSevenDaysTasks; }
    public int getReminderCount() {
        return overdueTasks + dueTodayTasks + dueInThreeDaysTasks + dueInSevenDaysTasks;
    }

    /** Completed tasks divided by all tasks, as a whole percentage (0 when there are no tasks). */
    public int getProgressPercent() {
        return tasks.isEmpty() ? 0 : (int) Math.round(completedTasks * 100.0 / tasks.size());
    }
}
