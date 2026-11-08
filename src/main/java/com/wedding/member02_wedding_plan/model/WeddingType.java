package com.wedding.member02_wedding_plan.model;

/**
 * The kinds of wedding a customer can plan. Each constant maps to a concrete
 * subclass of {@link WeddingPlan} (see {@link WeddingPlanFactory}).
 * The enum <code>name()</code> is the code stored in wedding_plans.csv.
 *
 * @author Member 02
 */
public enum WeddingType {

    STANDARD("Classic / Custom", "bi-gem"),
    BEACH("Beach Wedding", "bi-water"),
    TRADITIONAL("Traditional (Poruwa)", "bi-brightness-high"),
    GARDEN("Garden Wedding", "bi-flower1"),
    HOTEL("Hotel Ballroom", "bi-building");

    private final String label;
    private final String icon;

    WeddingType(String label, String icon) {
        this.label = label;
        this.icon = icon;
    }

    public String getCode() { return name(); }
    public String getLabel() { return label; }
    public String getIcon() { return icon; }

    /** Lenient lookup used when reading files: unknown or blank codes become STANDARD. */
    public static WeddingType fromCode(String code) {
        if (code == null || code.trim().isEmpty()) return STANDARD;
        for (WeddingType t : values()) {
            if (t.name().equalsIgnoreCase(code.trim())) return t;
        }
        return STANDARD;
    }

    /** Strict check used when validating user input. */
    public static boolean isValidCode(String code) {
        if (code == null) return false;
        for (WeddingType t : values()) {
            if (t.name().equalsIgnoreCase(code.trim())) return true;
        }
        return false;
    }
}
