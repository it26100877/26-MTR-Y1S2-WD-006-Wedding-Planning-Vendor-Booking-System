package com.wedding.member02_wedding_plan.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * A beach wedding. Inherits all common behaviour from {@link WeddingPlan} and
 * overrides the polymorphic methods with its own cost model, tasks and advice.
 *
 * @author Member 02
 */
public class BeachWedding extends WeddingPlan {

    public BeachWedding(String planId, String customerId, String planName,
            String weddingDate, String venue, String theme,
            String description, BigDecimal totalBudget,
            String status, String createdAt) {
        super(planId, customerId, planName, weddingDate, venue, theme,
                description, totalBudget, status, createdAt);
    }

    @Override
    public WeddingType getType() { return WeddingType.BEACH; }

    @Override
    public BigDecimal calculateEstimatedCost() {
        int guests = getGuestCount();
        // Extra tents, chairs and generator capacity are needed for every 100 guests above 100.
        long extraLogistics = guests > 100 ? ((guests - 100 + 99) / 100) * 40000L : 0L;
        // Per guest 9,500 + fixed 250,000 (permit, tent, sound) + logistics, plus 8% weather contingency.
        return estimate(guests, 9500L, 250000L + extraLogistics, "0.08");
    }

    @Override
    public String getSpecialConsiderations() {
        return "Check tide and sunset times, arrange a weather backup and shade, and obtain the beach permit early.";
    }

    @Override
    protected List<RecommendedTask> typeSpecificTasks() {
        List<RecommendedTask> list = new ArrayList<>();
        list.add(task("Obtain beach permit and local authority approval", "Apply for the venue or beach permit and any noise or fire clearance.", 120, "HIGH"));
        list.add(task("Arrange a weather backup (tent or indoor option)", "Agree a rain plan and the decision cut-off time.", 60, "HIGH"));
        list.add(task("Book sound system and generator", "Outdoor power and sound need a dedicated supplier.", 45, "MEDIUM"));
        list.add(task("Plan guest comfort: shade, water, footwear", "Provide shade, drinking water and a sand-friendly pathway.", 21, "MEDIUM"));
        list.add(task("Check tide and sunset times with the planner", "Schedule the ceremony around the best light and safe tide.", 14, "MEDIUM"));
        return list;
    }
}
