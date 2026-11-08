package com.wedding.member02_wedding_plan.model;

/**
 * One entry on the planning timeline: a checklist deadline or the wedding day itself.
 *
 * @author Member 02
 */
public class TimelineEvent {

    public static final String KIND_TASK = "TASK";
    public static final String KIND_WEDDING = "WEDDING";

    private final String date;      // yyyy-MM-dd
    private final String kind;      // TASK | WEDDING
    private final String title;
    private final String detail;
    private final String status;    // display status for tasks, ACTIVE/... for the wedding
    private final String priority;  // HIGH | MEDIUM | LOW (tasks only)
    private final String whenLabel; // e.g. "Due in 5 days"

    public TimelineEvent(String date, String kind, String title, String detail,
                         String status, String priority, String whenLabel) {
        this.date = date;
        this.kind = kind;
        this.title = title;
        this.detail = detail;
        this.status = status;
        this.priority = priority;
        this.whenLabel = whenLabel;
    }

    public static TimelineEvent forTask(ChecklistTask t) {
        return new TimelineEvent(t.getDeadline(), KIND_TASK, t.getTaskName(), t.getDescription(),
                t.getDisplayStatus(), t.getPriority(), t.getDueLabel());
    }

    public static TimelineEvent forWedding(WeddingPlan plan) {
        return new TimelineEvent(plan.getWeddingDate(), KIND_WEDDING, "Wedding day",
                plan.getVenue(), plan.getStatus(), "HIGH", plan.getCountdownLabel());
    }

    public String getDate() { return date; }
    public String getKind() { return kind; }
    public String getTitle() { return title; }
    public String getDetail() { return detail; }
    public String getStatus() { return status; }
    public String getPriority() { return priority; }
    public String getWhenLabel() { return whenLabel; }

    public boolean isWedding() { return KIND_WEDDING.equals(kind); }
    public boolean isDone() { return ChecklistTask.STATUS_COMPLETED.equals(status); }
    public boolean isOverdue() { return ChecklistTask.STATUS_OVERDUE.equals(status); }

    /** Day number and short month, e.g. "07 Nov". Falls back to the raw date. */
    public String getShortDate() {
        try {
            java.time.LocalDate d = java.time.LocalDate.parse(date);
            return String.format("%02d %s", d.getDayOfMonth(),
                    d.getMonth().getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH));
        } catch (Exception e) {
            return date;
        }
    }
}
