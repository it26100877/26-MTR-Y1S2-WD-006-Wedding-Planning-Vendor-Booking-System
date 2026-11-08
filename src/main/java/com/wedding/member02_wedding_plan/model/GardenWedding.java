package com.wedding.member02_wedding_plan.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * A garden wedding. Inherits all common behaviour from {@link WeddingPlan} and
 * overrides the polymorphic methods with its own cost model, tasks and advice.
 *
 * @author Member 02
 */
public class GardenWedding extends WeddingPlan {

    public GardenWedding(String planId, String customerId, String planName,
            String weddingDate, String venue, String theme,
            String description, BigDecimal totalBudget,
            String status, String createdAt) {
        super(planId, customerId, planName, weddingDate, venue, theme,
                description, totalBudget, status, createdAt);
    }

    @Override
    public WeddingType getType() { return WeddingType.GARDEN; }

    @Override
    public BigDecimal calculateEstimatedCost() {
        int guests = getGuestCount();
        // One tent (45,000) is needed for every 100 guests, on top of lighting and power (120,000).
        long tents = guests > 0 ? ((guests + 99) / 100) * 45000L : 0L;
        // Per guest 7,500 + fixed costs, plus 6% rental contingency.
        return estimate(guests, 7500L, 120000L + tents, "0.06");
    }

    @Override
    public String getSpecialConsiderations() {
        return "Inspect the grounds early, plan for rain, and arrange parking and restrooms for guests.";
    }

    @Override
    protected List<RecommendedTask> typeSpecificTasks() {
        List<RecommendedTask> list = new ArrayList<>();
        list.add(task("Inspect the garden and agree a rain contingency", "Walk the grounds with the owner and note flooding or shade issues.", 90, "HIGH"));
        list.add(task("Arrange tents, lighting and power supply", "Hire tents, string lighting and a safe power source.", 60, "MEDIUM"));
        list.add(task("Plan restrooms and parking for guests", "Make sure there is enough parking and mobile or fixed facilities.", 45, "MEDIUM"));
        list.add(task("Pest control and ground preparation", "Schedule grounds clean-up and insect control a few days before.", 14, "LOW"));
        return list;
    }
}
