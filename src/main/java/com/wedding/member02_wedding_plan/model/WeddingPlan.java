package com.wedding.member02_wedding_plan.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Abstract base class of all wedding plans.
 *
 * <ul>
 *   <li><b>Encapsulation</b>: every attribute is private; access is through getters/setters.</li>
 *   <li><b>Inheritance</b>: StandardWedding, BeachWedding, TraditionalWedding, GardenWedding
 *       and HotelWedding extend this class.</li>
 *   <li><b>Polymorphism</b>: calculateEstimatedCost(), typeSpecificTasks(),
 *       getSpecialConsiderations() and getType() are implemented differently by every subclass.</li>
 *   <li><b>Abstraction</b>: implements the {@link Plannable} contract and uses the
 *       Template Method pattern in {@link #getRecommendedChecklist()}.</li>
 * </ul>
 *
 * @author Member 02
 */
public abstract class WeddingPlan implements Plannable {

    // ─── Status Constants ────────────────────────────────────────────────────────
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_COMPLETED = "COMPLETED";

    // ─── Private Fields ──────────────────────────────────────────────────────────
    private String planId;
    private String customerId;
    private String planName;
    private String weddingDate; // yyyy-MM-dd
    private String venue;
    private String theme;
    private String description;
    private BigDecimal totalBudget;
    private String status; // ACTIVE | CANCELLED | COMPLETED
    private String createdAt;
    private int guestCount;

    // ─── Constructor (subclasses call super) ─────────────────────────────────────
    protected WeddingPlan(String planId, String customerId, String planName,
                          String weddingDate, String venue, String theme,
                          String description, BigDecimal totalBudget,
                          String status, String createdAt) {
        this.planId = planId;
        this.customerId = customerId;
        this.planName = planName;
        this.weddingDate = weddingDate;
        this.venue = venue;
        this.theme = theme;
        this.description = description;
        this.totalBudget = totalBudget;
        this.status = status;
        this.createdAt = createdAt;
        this.guestCount = 0;
    }

    // ─── Getters ─────────────────────────────────────────────────────────────────
    public String getPlanId() { return planId; }
    public String getCustomerId() { return customerId; }
    public String getPlanName() { return planName; }
    public String getWeddingDate() { return weddingDate; }
    public String getVenue() { return venue; }
    public String getTheme() { return theme; }
    public String getDescription() { return description; }
    public BigDecimal getTotalBudget() { return totalBudget; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public int getGuestCount() { return guestCount; }

    // ─── Setters ─────────────────────────────────────────────────────────────────
    public void setPlanId(String planId) { this.planId = planId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public void setPlanName(String planName) { this.planName = planName; }
    public void setWeddingDate(String weddingDate) { this.weddingDate = weddingDate; }
    public void setVenue(String venue) { this.venue = venue; }
    public void setTheme(String theme) { this.theme = theme; }
    public void setDescription(String description) { this.description = description; }
    public void setTotalBudget(BigDecimal budget) { this.totalBudget = budget; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setGuestCount(int guestCount) { this.guestCount = guestCount; }

    // ─── Polymorphic behaviour (each wedding type decides) ───────────────────────

    /** Wedding-type specific tasks (e.g. beach permit, poruwa ceremony items). */
    protected abstract List<RecommendedTask> typeSpecificTasks();

    // ─── Shared behaviour ────────────────────────────────────────────────────────

    public String getTypeLabel() { return getType().getLabel(); }
    public String getTypeIcon() { return getType().getIcon(); }

    /** JavaBean-style accessor used by JSP; delegates to the polymorphic calculation. */
    public BigDecimal getEstimatedCost() { return calculateEstimatedCost(); }

    @Override
    public String getSummary() {
        return planName + " (" + getTypeLabel() + ") on " + weddingDate
                + " at " + venue + " - " + guestCount + " guests";
    }

    /**
     * Template Method: the common tasks every wedding needs plus whatever the
     * concrete type adds, ordered by how early they should be done.
     */
    @Override
    public final List<RecommendedTask> getRecommendedChecklist() {
        List<RecommendedTask> all = new ArrayList<>(commonTasks());
        all.addAll(typeSpecificTasks());
        all.sort(Comparator.comparingInt(RecommendedTask::getDaysBeforeWedding).reversed());
        return all;
    }

    public int getRecommendedTaskCount() { return getRecommendedChecklist().size(); }

    /** How far the entered budget is from the estimated cost (budget minus estimate). */
    public BigDecimal getBudgetGap() {
        BigDecimal budget = totalBudget == null ? BigDecimal.ZERO : totalBudget;
        return budget.subtract(calculateEstimatedCost());
    }

    /** NOT_SET | COMFORTABLE | TIGHT | SHORT, comparing estimate with the planned budget. */
    public String getBudgetOutlook() {
        BigDecimal budget = totalBudget == null ? BigDecimal.ZERO : totalBudget;
        if (budget.signum() <= 0) return "NOT_SET";
        BigDecimal ratio = calculateEstimatedCost().divide(budget, 4, RoundingMode.HALF_UP);
        if (ratio.compareTo(new BigDecimal("0.90")) <= 0) return "COMFORTABLE";
        if (ratio.compareTo(BigDecimal.ONE) <= 0) return "TIGHT";
        return "SHORT";
    }

    public String getBudgetOutlookLabel() {
        switch (getBudgetOutlook()) {
            case "COMFORTABLE": return "Budget looks comfortable";
            case "TIGHT": return "Budget is tight";
            case "SHORT": return "Budget may fall short";
            default: return "Budget not set";
        }
    }

    // ─── Countdown ───────────────────────────────────────────────────────────────

    private LocalDate parsedDate() {
        try {
            return LocalDate.parse(weddingDate);
        } catch (Exception e) {
            return null;
        }
    }

    /** Days from today until the wedding (negative when the date has passed). */
    public long getDaysUntilWedding() { return getDaysUntilWedding(LocalDate.now()); }

    public long getDaysUntilWedding(LocalDate today) {
        LocalDate date = parsedDate();
        return date == null ? 0 : ChronoUnit.DAYS.between(today, date);
    }

    public String getCountdownLabel() { return getCountdownLabel(LocalDate.now()); }

    public String getCountdownLabel(LocalDate today) {
        if (STATUS_CANCELLED.equals(status)) return "Cancelled";
        if (STATUS_COMPLETED.equals(status)) return "Completed";
        if (parsedDate() == null) return "Date not set";
        long days = getDaysUntilWedding(today);
        if (days > 1) return "In " + days + " days";
        if (days == 1) return "Tomorrow";
        if (days == 0) return "Today - the big day!";
        long ago = -days;
        return ago + (ago == 1 ? " day ago" : " days ago");
    }

    /** soon (30 days or less) | near (90 days or less) | far | past | closed. Used for CSS colouring. */
    public String getCountdownUrgency() {
        if (!STATUS_ACTIVE.equals(status)) return "closed";
        long days = getDaysUntilWedding();
        if (days < 0) return "past";
        if (days <= 30) return "soon";
        if (days <= 90) return "near";
        return "far";
    }

    // ─── Helpers for subclasses ──────────────────────────────────────────────────

    /** estimate = (perGuest x guests + fixedCost) x (1 + surchargeRate), rounded to whole rupees. */
    protected static BigDecimal estimate(int guests, long perGuest, long fixedCost, String surchargeRate) {
        BigDecimal subtotal = BigDecimal.valueOf(perGuest)
                .multiply(BigDecimal.valueOf(Math.max(0, guests)))
                .add(BigDecimal.valueOf(fixedCost));
        return subtotal.multiply(BigDecimal.ONE.add(new BigDecimal(surchargeRate)))
                .setScale(0, RoundingMode.HALF_UP);
    }

    protected static RecommendedTask task(String name, String description, int daysBefore, String priority) {
        return new RecommendedTask(name, description, daysBefore, priority);
    }

    private List<RecommendedTask> commonTasks() {
        List<RecommendedTask> list = new ArrayList<>();
        list.add(task("Confirm total budget and priorities", "Agree the overall budget and what matters most to both families.", 330, "HIGH"));
        list.add(task("Draft the guest list", "List family, friends and colleagues to get a first headcount.", 300, "HIGH"));
        list.add(task("Reserve the ceremony and reception venue", "Visit, compare and pay the booking deposit.", 290, "HIGH"));
        list.add(task("Book photographer and videographer", "Confirm packages, coverage hours and delivery dates.", 240, "HIGH"));
        list.add(task("Choose wedding attire", "Select and schedule fittings for the couple and wedding party.", 150, "MEDIUM"));
        list.add(task("Finalize catering menu", "Complete the menu tasting and confirm dietary needs.", 120, "MEDIUM"));
        list.add(task("Send invitations", "Print and send invitations; start tracking RSVPs.", 60, "HIGH"));
        list.add(task("Confirm final guest count with venue and caterer", "Share the confirmed RSVP headcount.", 14, "HIGH"));
        list.add(task("Finalize the day-of timeline with vendors", "Give every vendor the same schedule.", 7, "MEDIUM"));
        list.add(task("Rehearsal and final walkthrough", "Walk through the ceremony and settle final payments.", 2, "MEDIUM"));
        return list;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "planId='" + planId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", planName='" + planName + '\'' +
                ", weddingDate='" + weddingDate + '\'' +
                ", venue='" + venue + '\'' +
                ", theme='" + theme + '\'' +
                ", totalBudget=" + totalBudget +
                ", status='" + status + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
