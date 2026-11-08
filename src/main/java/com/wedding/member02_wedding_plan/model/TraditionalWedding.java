package com.wedding.member02_wedding_plan.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * A traditional (poruwa) ceremony wedding. Inherits all common behaviour from {@link WeddingPlan} and
 * overrides the polymorphic methods with its own cost model, tasks and advice.
 *
 * @author Member 02
 */
public class TraditionalWedding extends WeddingPlan {

    public TraditionalWedding(String planId, String customerId, String planName,
            String weddingDate, String venue, String theme,
            String description, BigDecimal totalBudget,
            String status, String createdAt) {
        super(planId, customerId, planName, weddingDate, venue, theme,
                description, totalBudget, status, createdAt);
    }

    @Override
    public WeddingType getType() { return WeddingType.TRADITIONAL; }

    @Override
    public BigDecimal calculateEstimatedCost() {
        int guests = getGuestCount();
        // Caterers bill a minimum of 100 guests for a traditional banquet.
        int billable = guests == 0 ? 0 : Math.max(guests, 100);
        // Per guest 8,000 + fixed 300,000 (poruwa, rituals, drummers, dancers), plus 5% ritual extras.
        return estimate(billable, 8000L, 300000L, "0.05");
    }

    @Override
    public String getSpecialConsiderations() {
        return "Confirm the auspicious (nekath) times with the astrologer and brief everyone on the ritual order.";
    }

    @Override
    protected List<RecommendedTask> typeSpecificTasks() {
        List<RecommendedTask> list = new ArrayList<>();
        list.add(task("Confirm auspicious (nekath) times with the astrologer", "Fix the poruwa, ceremony and departure times.", 200, "HIGH"));
        list.add(task("Book the ashtaka reader and ceremony coordinator", "Confirm the ritual order and who leads each part.", 150, "HIGH"));
        list.add(task("Book traditional drummers and dancers", "Reserve the performers and agree arrival times.", 90, "MEDIUM"));
        list.add(task("Arrange poruwa decoration and ceremony items", "Order the poruwa, coconut flower, betel leaves and other items.", 60, "MEDIUM"));
        list.add(task("Plan the jayamangala gatha and ritual sequence", "Share the printed sequence with both families.", 30, "MEDIUM"));
        return list;
    }
}
