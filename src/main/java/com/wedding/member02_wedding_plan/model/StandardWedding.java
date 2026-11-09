package com.wedding.member02_wedding_plan.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * A classic or custom wedding. Inherits all common behaviour from {@link WeddingPlan} and
 * overrides the polymorphic methods with its own cost model, tasks and advice.
 *
 * @author Member 02
 */
public class StandardWedding extends WeddingPlan {

    public StandardWedding(String planId, String customerId, String planName,
            String weddingDate, String venue, String theme,
            String description, BigDecimal totalBudget,
            String status, String createdAt) {
        super(planId, customerId, planName, weddingDate, venue, theme,
                description, totalBudget, status, createdAt);
    }

    @Override
    public WeddingType getType() { return WeddingType.STANDARD; }

    @Override
    public BigDecimal calculateEstimatedCost() {
        // Per guest 8,500 + fixed 180,000 setup, plus 5% contingency.
        return estimate(getGuestCount(), 8500L, 180000L, "0.05");
    }

    @Override
    public String getSpecialConsiderations() {
        return "Keep a contingency reserve of about 5% and confirm every vendor in writing.";
    }

    @Override
    protected List<RecommendedTask> typeSpecificTasks() {
        List<RecommendedTask> list = new ArrayList<>();
        // A classic wedding has no extra type-specific tasks beyond the common template.
        return list;
    }
}
