package com.wedding.member02_wedding_plan.model;

/**
 * Calculated headcount figures for the guest list (not stored in a file).
 * All headcounts add up party sizes, not just guest entries.
 *
 * @author Member 02
 */
public class GuestStats {

    private final int entries;
    private final int confirmedHeadcount;
    private final int pendingHeadcount;
    private final int declinedHeadcount;
    private final int expectedGuests;

    public GuestStats(int entries, int confirmedHeadcount, int pendingHeadcount,
                      int declinedHeadcount, int expectedGuests) {
        this.entries = entries;
        this.confirmedHeadcount = confirmedHeadcount;
        this.pendingHeadcount = pendingHeadcount;
        this.declinedHeadcount = declinedHeadcount;
        this.expectedGuests = expectedGuests;
    }

    public int getEntries() { return entries; }
    public int getConfirmedHeadcount() { return confirmedHeadcount; }
    public int getPendingHeadcount() { return pendingHeadcount; }
    public int getDeclinedHeadcount() { return declinedHeadcount; }
    public int getExpectedGuests() { return expectedGuests; }

    /** Everyone who was invited, whatever their answer. */
    public int getTotalHeadcount() { return confirmedHeadcount + pendingHeadcount + declinedHeadcount; }

    /** Confirmed plus still-waiting guests: the number to plan seats and food for. */
    public int getAttendingEstimate() { return confirmedHeadcount + pendingHeadcount; }

    /** Percentage of invited people who have answered (confirmed or declined). */
    public int getResponseRate() {
        int total = getTotalHeadcount();
        return total == 0 ? 0 : (int) Math.round((confirmedHeadcount + declinedHeadcount) * 100.0 / total);
    }

    /** True when the plan has an expected guest count and the list exceeds it. */
    public boolean isOverExpected() { return expectedGuests > 0 && getAttendingEstimate() > expectedGuests; }
}
