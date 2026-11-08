package com.wedding.member02_wedding_plan.model;

/**
 * Groups used to organise the guest list. The enum name() is stored in guests.csv.
 *
 * @author Member 02
 */
public enum GuestGroup {

    FAMILY("Family"), FRIENDS("Friends"), COLLEAGUES("Colleagues"), OTHER("Other");

    private final String label;

    GuestGroup(String label) { this.label = label; }

    public String getCode() { return name(); }
    public String getLabel() { return label; }

    public static GuestGroup fromCode(String code) {
        if (code != null) {
            for (GuestGroup g : values()) {
                if (g.name().equalsIgnoreCase(code.trim())) return g;
            }
        }
        return OTHER;
    }

    public static boolean isValidCode(String code) {
        if (code == null) return false;
        for (GuestGroup g : values()) {
            if (g.name().equalsIgnoreCase(code.trim())) return true;
        }
        return false;
    }
}
