package com.wedding.member02_wedding_plan.controller;

import com.wedding.common.exceptions.EntityNotFoundException;
import com.wedding.common.exceptions.ValidationException;
import com.wedding.common.model.User;
import com.wedding.member02_wedding_plan.model.PlanSummary;
import com.wedding.member02_wedding_plan.model.WeddingPlan;
import com.wedding.member02_wedding_plan.service.WeddingPlanService;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.util.HtmlUtils;

import java.util.Optional;

/**
 * Shared behaviour of the three Member 02 controllers (Inheritance): reading the logged-in
 * user, the access rules for a plan, input parsing and HTML-escaping of flash messages.
 *
 * Access rules: an ADMIN can open any plan; a CUSTOMER can only open their own plans;
 * changes are only allowed while the plan is ACTIVE.
 *
 * @author Member 02
 */
abstract class PlanControllerSupport {

    protected final WeddingPlanService planService;

    protected PlanControllerSupport(WeddingPlanService planService) {
        this.planService = planService;
    }

    protected User currentUser(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }

    protected boolean isAdmin(User user) {
        return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
    }

    protected boolean isCustomer(User user) {
        return user != null && "CUSTOMER".equalsIgnoreCase(user.getRole());
    }

    protected boolean canAccess(User user, WeddingPlan plan) {
        return user != null && plan != null
                && (isAdmin(user)
                || (isCustomer(user) && user.getUserId().equalsIgnoreCase(plan.getCustomerId())));
    }

    protected boolean canModify(User user, WeddingPlan plan) {
        return canAccess(user, plan) && WeddingPlan.STATUS_ACTIVE.equals(plan.getStatus());
    }

    /** The plan if it exists and the user may open it; otherwise empty (never throws). */
    protected Optional<WeddingPlan> findAccessiblePlan(User user, String planId) {
        try {
            WeddingPlan plan = planService.getById(planId);
            return canAccess(user, plan) ? Optional.of(plan) : Optional.empty();
        } catch (EntityNotFoundException e) {
            return Optional.empty();
        }
    }

    /** Escapes text before it is put in a flash message (the alert fragment prints it as HTML). */
    protected String esc(String text) {
        return HtmlUtils.htmlEscape(text == null ? "" : text);
    }

    protected int parseInt(String value, String fieldName, int fallback) {
        if (value == null || value.trim().isEmpty()) return fallback;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " must be a whole number.");
        }
    }

    /** Adds the attributes every plan workspace page (checklist, budget, guests...) needs. */
    protected void addWorkspaceModel(Model model, PlanSummary summary, String title) {
        model.addAttribute("plan", summary.getPlan());
        model.addAttribute("summary", summary);
        model.addAttribute("pageTitle", title + " - " + summary.getPlan().getPlanName());
        model.addAttribute("activePage", "plans");
    }
}
