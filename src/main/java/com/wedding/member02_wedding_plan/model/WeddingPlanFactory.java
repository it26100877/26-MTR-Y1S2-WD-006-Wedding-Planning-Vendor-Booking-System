package com.wedding.member02_wedding_plan.model;

import java.math.BigDecimal;

/**
 * Factory Method: builds the correct WeddingPlan subclass for a WeddingType.
 * Callers (repository, service) work with the WeddingPlan type and never use
 * <code>new BeachWedding(...)</code> directly.
 *
 * @author Member 02
 */
public final class WeddingPlanFactory {

    private WeddingPlanFactory() { }

    public static WeddingPlan create(WeddingType type, String planId, String customerId, String planName,
                                     String weddingDate, String venue, String theme, String description,
                                     BigDecimal totalBudget, String status, String createdAt) {
        WeddingType t = type == null ? WeddingType.STANDARD : type;
        return switch (t) {
            case BEACH -> new BeachWedding(planId, customerId, planName, weddingDate, venue, theme,
                    description, totalBudget, status, createdAt);
            case TRADITIONAL -> new TraditionalWedding(planId, customerId, planName, weddingDate, venue, theme,
                    description, totalBudget, status, createdAt);
            case GARDEN -> new GardenWedding(planId, customerId, planName, weddingDate, venue, theme,
                    description, totalBudget, status, createdAt);
            case HOTEL -> new HotelWedding(planId, customerId, planName, weddingDate, venue, theme,
                    description, totalBudget, status, createdAt);
            case STANDARD -> new StandardWedding(planId, customerId, planName, weddingDate, venue, theme,
                    description, totalBudget, status, createdAt);
        };
    }

    /** A throw-away plan used only to ask a wedding type for its estimate or advice. */
    public static WeddingPlan createPreview(WeddingType type, int guestCount) {
        WeddingPlan preview = create(type, "PREVIEW", "", "Preview", "", "", "", "",
                BigDecimal.ZERO, WeddingPlan.STATUS_ACTIVE, "");
        preview.setGuestCount(guestCount);
        return preview;
    }
}
