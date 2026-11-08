package com.wedding.member02_wedding_plan.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * A hotel ballroom wedding. Inherits all common behaviour from {@link WeddingPlan} and
 * overrides the polymorphic methods with its own cost model, tasks and advice.
 *
 * @author Member 02
 */
public class HotelWedding extends WeddingPlan {

    public HotelWedding(String planId, String customerId, String planName,
            String weddingDate, String venue, String theme,
            String description, BigDecimal totalBudget,
            String status, String createdAt) {
        super(planId, customerId, planName, weddingDate, venue, theme,
                description, totalBudget, status, createdAt);
    }

    @Override
    public WeddingType getType() { return WeddingType.HOTEL; }

    @Override
    public BigDecimal calculateEstimatedCost() {
        int guests = getGuestCount();
        // Hotels give a banquet discount on the per-guest rate above 300 guests.
        long perGuest = guests > 300 ? 11400L : 12000L;
        // Plus fixed hall and decor cost 150,000 and a 10% service charge.
        return estimate(guests, perGuest, 150000L, "0.10");
    }

    @Override
    public String getSpecialConsiderations() {
        return "Confirm what the hotel package includes, the service charge and taxes, and the room block for family.";
    }

    @Override
    protected List<RecommendedTask> typeSpecificTasks() {
        List<RecommendedTask> list = new ArrayList<>();
        list.add(task("Confirm hotel package inclusions and service charge", "Get a written list of what the package covers and extra charges.", 200, "HIGH"));
        list.add(task("Reserve guest rooms for out-of-town family", "Negotiate a room block and cut-off date.", 120, "MEDIUM"));
        list.add(task("Schedule hall decoration with the hotel coordinator", "Agree setup time, access and any decor restrictions.", 30, "MEDIUM"));
        list.add(task("Finalize ballroom floor plan and seating", "Approve table layout, head table and dance floor.", 21, "MEDIUM"));
        return list;
    }
}
