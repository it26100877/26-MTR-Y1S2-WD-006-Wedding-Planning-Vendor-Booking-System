package com.wedding.member02_wedding_plan.model;

/**
 * An immutable suggestion produced by a wedding plan type.
 * Used to auto-generate a checklist template (see ChecklistService#generateFromTemplate).
 *
 * @author Member 02
 */
public class RecommendedTask {

    private final String name;
    private final String description;
    private final int daysBeforeWedding;
    private final String priority;

    public RecommendedTask(String name, String description, int daysBeforeWedding, String priority) {
        this.name = name;
        this.description = description;
        this.daysBeforeWedding = Math.max(0, daysBeforeWedding);
        this.priority = priority;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getDaysBeforeWedding() { return daysBeforeWedding; }
    public String getPriority() { return priority; }
}
