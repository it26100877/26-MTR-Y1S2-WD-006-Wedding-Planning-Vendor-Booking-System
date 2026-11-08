package com.wedding.member02_wedding_plan.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Abstraction: the contract every kind of wedding plan must fulfil.
 * Callers (services, controllers, JSP) depend on this behaviour, not on a
 * concrete wedding type.
 *
 * @author Member 02
 */
public interface Plannable {

    /** The wedding type this plan represents. */
    WeddingType getType();

    /** Rough cost estimate in LKR, calculated differently by each wedding type. */
    BigDecimal calculateEstimatedCost();

    /** Suggested pre-wedding tasks (common tasks plus type-specific ones). */
    List<RecommendedTask> getRecommendedChecklist();

    /** Short advice that is specific to this wedding type. */
    String getSpecialConsiderations();

    /** One-line description of the plan. */
    String getSummary();
}
