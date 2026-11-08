package com.wedding.member02_wedding_plan.model;

/**
 * Spending categories for the budget tracker.
 * The enum name() is the code stored in budget_items.csv.
 *
 * @author Member 02
 */
public enum BudgetCategory {

    VENUE("Venue & Reception", "bi-building", "#7e3b58"),
    CATERING("Catering & Cake", "bi-cup-hot", "#c77d6a"),
    PHOTOGRAPHY("Photography & Video", "bi-camera", "#4f7cac"),
    DECOR("Flowers & Decor", "bi-flower2", "#d98fa5"),
    ATTIRE("Attire & Beauty", "bi-handbag", "#8c6bb1"),
    ENTERTAINMENT("Music & Entertainment", "bi-music-note-beamed", "#e0a458"),
    TRANSPORT("Transport", "bi-car-front", "#5a9e8f"),
    STATIONERY("Invitations & Stationery", "bi-envelope", "#a77b5a"),
    OTHER("Other", "bi-three-dots", "#8a8a8a");

    private final String label;
    private final String icon;
    private final String color;

    BudgetCategory(String label, String icon, String color) {
        this.label = label;
        this.icon = icon;
        this.color = color;
    }

    public String getCode() { return name(); }
    public String getLabel() { return label; }
    public String getIcon() { return icon; }
    public String getColor() { return color; }

    /** Lenient lookup used when reading files: unknown codes become OTHER. */
    public static BudgetCategory fromCode(String code) {
        if (code != null) {
            for (BudgetCategory c : values()) {
                if (c.name().equalsIgnoreCase(code.trim())) return c;
            }
        }
        return OTHER;
    }

    public static boolean isValidCode(String code) {
        if (code == null) return false;
        for (BudgetCategory c : values()) {
            if (c.name().equalsIgnoreCase(code.trim())) return true;
        }
        return false;
    }
}
