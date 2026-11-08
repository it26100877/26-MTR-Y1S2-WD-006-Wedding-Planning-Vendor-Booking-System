package com.wedding.member02_wedding_plan.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * A pre-wedding checklist task.
 * Encapsulation: private fields with public accessors.
 * Inheritance: extends {@link PlanItem} (shared id / plan link / created time).
 *
 * The stored status is PENDING, IN_PROGRESS or COMPLETED. OVERDUE is never written
 * to the file: it is calculated from the deadline whenever it is needed
 * (see {@link #isOverdue()} and {@link #getDisplayStatus()}).
 *
 * @author Member 02
 */
public class ChecklistTask extends PlanItem {

    // ─── Status Constants ────────────────────────────────────────────────────────
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String STATUS_COMPLETED = "COMPLETED";
    /** Display-only status; calculated, never persisted. */
    public static final String STATUS_OVERDUE = "OVERDUE";

    // ─── Priority Constants ──────────────────────────────────────────────────────
    public static final String PRIORITY_HIGH = "HIGH";
    public static final String PRIORITY_MEDIUM = "MEDIUM";
    public static final String PRIORITY_LOW = "LOW";

    // ─── Private Fields ──────────────────────────────────────────────────────────
    private String taskName;
    private String description;
    private String deadline;  // yyyy-MM-dd
    private String status;    // PENDING | IN_PROGRESS | COMPLETED
    private String priority;  // HIGH | MEDIUM | LOW

    // ─── Constructor ─────────────────────────────────────────────────────────────
    public ChecklistTask(String taskId, String planId, String taskName,
                         String description, String deadline,
                         String status, String priority, String createdAt) {
        super(taskId, planId, createdAt);
        this.taskName = taskName;
        this.description = description;
        this.deadline = deadline;
        this.status = status;
        this.priority = priority;
    }

    // ─── Getters ─────────────────────────────────────────────────────────────────
    public String getTaskId() { return getItemId(); }
    public String getTaskName() { return taskName; }
    public String getDescription() { return description; }
    public String getDeadline() { return deadline; }
    public String getStatus() { return status; }
    public String getPriority() { return priority; }

    // ─── Setters ─────────────────────────────────────────────────────────────────
    public void setTaskId(String taskId) { setItemId(taskId); }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public void setDescription(String description) { this.description = description; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public void setStatus(String status) { this.status = status; }
    public void setPriority(String priority) { this.priority = priority; }

    // ─── Calculated state ────────────────────────────────────────────────────────

    private LocalDate parsedDeadline() {
        try {
            return LocalDate.parse(deadline);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isCompleted() { return STATUS_COMPLETED.equals(status); }

    public boolean isOverdue() { return isOverdue(LocalDate.now()); }

    public boolean isOverdue(LocalDate today) {
        LocalDate due = parsedDeadline();
        return !isCompleted() && due != null && due.isBefore(today);
    }

    /** COMPLETED, OVERDUE, IN_PROGRESS or PENDING. Safe to show in the UI. */
    public String getDisplayStatus() { return getDisplayStatus(LocalDate.now()); }

    public String getDisplayStatus(LocalDate today) {
        if (isCompleted()) return STATUS_COMPLETED;
        if (isOverdue(today)) return STATUS_OVERDUE;
        return STATUS_IN_PROGRESS.equals(status) ? STATUS_IN_PROGRESS : STATUS_PENDING;
    }

    /** Days until the deadline (negative when late); Long.MAX_VALUE if there is no valid date. */
    public long getDaysUntilDue() { return getDaysUntilDue(LocalDate.now()); }

    public long getDaysUntilDue(LocalDate today) {
        LocalDate due = parsedDeadline();
        return due == null ? Long.MAX_VALUE : ChronoUnit.DAYS.between(today, due);
    }

    public String getDueLabel() { return getDueLabel(LocalDate.now()); }

    public String getDueLabel(LocalDate today) {
        if (isCompleted()) return "Completed";
        long days = getDaysUntilDue(today);
        if (days == Long.MAX_VALUE) return "No due date";
        if (days < 0) return "Overdue by " + (-days) + (days == -1 ? " day" : " days");
        if (days == 0) return "Due today";
        if (days == 1) return "Due tomorrow";
        return "Due in " + days + " days";
    }

    // ─── In-app deadline reminders (computed only; no CSV schema changes) ────────
    public static final String REMINDER_NONE = "NONE";
    public static final String REMINDER_OVERDUE = "OVERDUE";
    public static final String REMINDER_TODAY = "TODAY";
    public static final String REMINDER_THREE_DAYS = "THREE_DAYS";
    public static final String REMINDER_SEVEN_DAYS = "SEVEN_DAYS";

    /** Reminder bucket for unfinished tasks only; windows do not overlap. */
    public String getReminderCategory() { return getReminderCategory(LocalDate.now()); }

    public String getReminderCategory(LocalDate today) {
        if (isCompleted()) return REMINDER_NONE;
        long days = getDaysUntilDue(today);
        if (days == Long.MAX_VALUE) return REMINDER_NONE;
        if (days < 0) return REMINDER_OVERDUE;
        if (days == 0) return REMINDER_TODAY;
        if (days <= 3) return REMINDER_THREE_DAYS;
        if (days <= 7) return REMINDER_SEVEN_DAYS;
        return REMINDER_NONE;
    }

    public boolean isReminderDue() { return !REMINDER_NONE.equals(getReminderCategory()); }

    // ─── PlanItem contract ───────────────────────────────────────────────────────
    @Override
    public String getItemType() { return "CHECKLIST_TASK"; }

    @Override
    public String getDisplayTitle() { return taskName; }

    @Override
    public String getDetailLine() {
        return priority + " priority, due " + deadline + ", " + getDisplayStatus();
    }

    @Override
    public String toString() {
        return "ChecklistTask{" +
                "taskId='" + getItemId() + '\'' +
                ", planId='" + getPlanId() + '\'' +
                ", taskName='" + taskName + '\'' +
                ", deadline='" + deadline + '\'' +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                '}';
    }
}
