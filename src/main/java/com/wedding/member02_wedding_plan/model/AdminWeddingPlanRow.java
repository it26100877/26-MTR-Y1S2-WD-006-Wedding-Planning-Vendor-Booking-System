package com.wedding.member02_wedding_plan.model;

/** Read-only presentation data for a single wedding in the Member 02 admin workspace. */
public final class AdminWeddingPlanRow {
    private final WeddingPlan plan;
    private final String customerName;
    private final String customerEmail;
    private final int taskCount;
    private final int completedTasks;
    private final int progress;

    public AdminWeddingPlanRow(WeddingPlan plan, String customerName, String customerEmail,
                               int taskCount, int completedTasks) {
        this.plan = plan;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.taskCount = taskCount;
        this.completedTasks = completedTasks;
        this.progress = taskCount == 0 ? 0 : (int) Math.round(completedTasks * 100.0 / taskCount);
    }

    public WeddingPlan getPlan() { return plan; }
    public String getCustomerName() { return customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public int getTaskCount() { return taskCount; }
    public int getCompletedTasks() { return completedTasks; }
    public int getProgress() { return progress; }

    public String getPreviewImage() {
        switch (plan.getType()) {
            case BEACH: return "wedding-beach.jpg";
            case TRADITIONAL: return "wedding-traditional.jpg";
            case GARDEN: return "wedding-garden.jpg";
            case HOTEL: return "wedding-ballroom.jpg";
            default: return "wedding-classic.jpg";
        }
    }
}
