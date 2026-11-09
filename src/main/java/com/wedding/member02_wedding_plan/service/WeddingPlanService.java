package com.wedding.member02_wedding_plan.service;

import com.wedding.common.exceptions.BusinessRuleException;
import com.wedding.common.exceptions.EntityNotFoundException;
import com.wedding.common.exceptions.ValidationException;
import com.wedding.common.service.BaseService;
import com.wedding.common.utils.FileUtil;
import com.wedding.common.utils.ValidationUtil;
import com.wedding.member02_wedding_plan.model.PlanSearchCriteria;
import com.wedding.member02_wedding_plan.model.WeddingPlan;
import com.wedding.member02_wedding_plan.model.WeddingPlanFactory;
import com.wedding.member02_wedding_plan.model.WeddingType;
import com.wedding.member02_wedding_plan.repository.WeddingPlanRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Business-logic service for Wedding Plan management.
 * Extends BaseService for shared validation helpers and logging.
 * Demonstrates Inheritance (extends BaseService) and Encapsulation (private repo).
 *
 * Validation rules: name 3-100 characters and unique per customer (cancelled plans
 * excluded), wedding date in the future and within 10 years, budget between 0 and
 * 999,999,999,999, guest count between 0 and 5,000.
 *
 * @author Member 02
 */
public class WeddingPlanService extends BaseService {

    public static final int MAX_GUESTS = 5000;
    public static final BigDecimal MAX_BUDGET = new BigDecimal("999999999999");
    public static final int MAX_YEARS_AHEAD = 10;

    private final WeddingPlanRepository repository;

    public WeddingPlanService(WeddingPlanRepository repository) {
        this.repository = repository;
    }

    // ─── Backward-compatible overloads (other modules and tests call these) ──────

    public WeddingPlan createPlan(String customerId, String planName, String weddingDate,
                                  String venue, String theme, String description, String totalBudget) {
        return createPlan(customerId, planName, weddingDate, venue, theme, description, totalBudget, 0);
    }

    public WeddingPlan createPlan(String customerId, String planName, String weddingDate,
                                  String venue, String theme, String description,
                                  String totalBudget, int guestCount) {
        return createPlan(customerId, planName, weddingDate, venue, theme, description,
                totalBudget, guestCount, WeddingType.STANDARD.getCode());
    }

    public WeddingPlan updatePlan(String planId, String planName, String weddingDate,
                                  String venue, String theme, String description, String totalBudget) {
        WeddingPlan existing = findOrThrow(planId);
        return updatePlan(planId, planName, weddingDate, venue, theme, description, totalBudget,
                existing.getGuestCount(), existing.getType().getCode());
    }

    public WeddingPlan updatePlan(String planId, String planName, String weddingDate,
                                  String venue, String theme, String description,
                                  String totalBudget, int guestCount) {
        WeddingPlan existing = findOrThrow(planId);
        return updatePlan(planId, planName, weddingDate, venue, theme, description, totalBudget,
                guestCount, existing.getType().getCode());
    }

    // ─── Create ──────────────────────────────────────────────────────────────────

    /**
     * Creates and persists a new WeddingPlan of the requested type.
     *
     * @throws ValidationException   if any field is invalid
     * @throws BusinessRuleException if the date is not in the future or the name is already used
     */
    public WeddingPlan createPlan(String customerId, String planName, String weddingDate,
                                  String venue, String theme, String description,
                                  String totalBudget, int guestCount, String planType) {
        requireNonEmpty(customerId, "Customer ID");
        validateDetails(planName, weddingDate, venue, theme, description, guestCount);
        requireFutureDate(weddingDate);
        BigDecimal budget = parseBudget(totalBudget);
        WeddingType type = parseType(planType);
        ensureNameAvailable(customerId, planName, null);

        String planId = FileUtil.generateId("PLN");
        WeddingPlan plan = WeddingPlanFactory.create(type,
                planId, customerId, planName.trim(), weddingDate.trim(),
                venue.trim(),
                theme == null ? "" : theme.trim(),
                description == null ? "" : description.trim(),
                budget,
                WeddingPlan.STATUS_ACTIVE,
                ValidationUtil.nowDateTime());
        plan.setGuestCount(guestCount);
        repository.save(plan);
        log("Created " + type + " plan: " + planId + " for customer: " + customerId);
        return plan;
    }

    // ─── Update ──────────────────────────────────────────────────────────────────

    /**
     * Updates an existing WeddingPlan. If the wedding type changes, the stored object is
     * replaced by an instance of the new subclass (same id, customer, status and created time).
     * Nothing is changed unless every field is valid.
     *
     * @throws EntityNotFoundException if the plan does not exist
     * @throws ValidationException     if any field is invalid
     * @throws BusinessRuleException   if plan is cancelled/completed or the name is already used
     */
    public WeddingPlan updatePlan(String planId, String planName, String weddingDate,
                                  String venue, String theme, String description,
                                  String totalBudget, int guestCount, String planType) {
        WeddingPlan plan = findOrThrow(planId);
        requireEditable(plan);

        validateDetails(planName, weddingDate, venue, theme, description, guestCount);
        // An unchanged date is always allowed, so notes can still be edited after the date passes.
        if (!weddingDate.trim().equals(plan.getWeddingDate())) {
            requireFutureDate(weddingDate);
        }
        BigDecimal budget = parseBudget(totalBudget);
        WeddingType type = parseType(planType);
        ensureNameAvailable(plan.getCustomerId(), planName, planId);

        WeddingPlan target = plan;
        if (plan.getType() != type) {
            target = WeddingPlanFactory.create(type, plan.getPlanId(), plan.getCustomerId(),
                    plan.getPlanName(), plan.getWeddingDate(), plan.getVenue(), plan.getTheme(),
                    plan.getDescription(), plan.getTotalBudget(), plan.getStatus(), plan.getCreatedAt());
        }
        target.setPlanName(planName.trim());
        target.setWeddingDate(weddingDate.trim());
        target.setVenue(venue.trim());
        target.setTheme(theme == null ? "" : theme.trim());
        target.setDescription(description == null ? "" : description.trim());
        target.setGuestCount(guestCount);
        target.setTotalBudget(budget);

        repository.save(target);
        log("Updated plan: " + planId);
        return target;
    }

    /**
     * Plan edit with checklist consistency check. Kept separate to preserve existing
     * service overloads used by other modules. The controller uses this safe variant.
     */
    public WeddingPlan updatePlanWithChecklistValidation(String planId, String planName,
            String weddingDate, String venue, String theme, String description,
            String totalBudget, int guestCount, String planType, ChecklistService checklistService) {
        if (checklistService == null) {
            throw new ValidationException("Checklist validation service is required.");
        }
        // Only the plan's date may shift; reject changes that would leave tasks beyond it.
        WeddingPlan existing = findOrThrow(planId);
        requireEditable(existing);
        if (weddingDate != null && !existing.getWeddingDate().equals(weddingDate.trim())) {
            checklistService.validateDeadlinesForWeddingDate(planId, weddingDate);
        }
        return updatePlan(planId, planName, weddingDate, venue, theme, description,
                totalBudget, guestCount, planType);
    }

    /** Sets only the expected guest count (used when it is synced from the guest list). */
    public WeddingPlan updateGuestCount(String planId, int guestCount) {
        WeddingPlan plan = findOrThrow(planId);
        requireEditable(plan);
        validateGuestCount(guestCount);
        plan.setGuestCount(guestCount);
        repository.save(plan);
        log("Guest count of " + planId + " set to " + guestCount);
        return plan;
    }

    // ─── Status Changes ──────────────────────────────────────────────────────────

    /**
     * Cancels a plan (sets status to CANCELLED).
     *
     * @throws EntityNotFoundException if not found
     * @throws BusinessRuleException   if already cancelled or completed
     */
    public WeddingPlan cancelPlan(String planId) {
        WeddingPlan plan = findOrThrow(planId);
        if (WeddingPlan.STATUS_CANCELLED.equals(plan.getStatus())) {
            throw new BusinessRuleException("Plan is already cancelled.");
        }
        if (WeddingPlan.STATUS_COMPLETED.equals(plan.getStatus())) {
            throw new BusinessRuleException("A completed plan cannot be cancelled.");
        }
        plan.setStatus(WeddingPlan.STATUS_CANCELLED);
        repository.save(plan);
        log("Cancelled plan: " + planId);
        return plan;
    }

    /**
     * Marks a plan as completed.
     *
     * @throws EntityNotFoundException if not found
     * @throws BusinessRuleException   if already completed or cancelled
     */
    public WeddingPlan completePlan(String planId) {
        WeddingPlan plan = findOrThrow(planId);
        if (WeddingPlan.STATUS_COMPLETED.equals(plan.getStatus())) {
            throw new BusinessRuleException("Plan is already completed.");
        }
        if (WeddingPlan.STATUS_CANCELLED.equals(plan.getStatus())) {
            throw new BusinessRuleException("A cancelled plan cannot be completed.");
        }
        plan.setStatus(WeddingPlan.STATUS_COMPLETED);
        repository.save(plan);
        log("Completed plan: " + planId);
        return plan;
    }

    // ─── Queries ─────────────────────────────────────────────────────────────────

    /** Returns all plans belonging to a customer. */
    public List<WeddingPlan> getByCustomerId(String customerId) {
        return repository.findByCustomerId(customerId);
    }

    /**
     * Returns a plan by its ID.
     *
     * @throws EntityNotFoundException if not found
     */
    public WeddingPlan getById(String planId) {
        return findOrThrow(planId);
    }

    /** Returns all wedding plans. */
    public List<WeddingPlan> getAllPlans() {
        return repository.findAll();
    }

    /**
     * Filters and sorts the given plans: keyword (name, venue, theme, notes, id), status,
     * wedding type, wedding date range, and sort order. Invalid dates in the criteria are ignored.
     */
    public List<WeddingPlan> searchPlans(List<WeddingPlan> source, PlanSearchCriteria criteria) {
        PlanSearchCriteria c = criteria == null ? new PlanSearchCriteria() : criteria;
        String keyword = c.getKeyword().toLowerCase();
        LocalDate from = parseDateOrNull(c.getFromDate());
        LocalDate to = parseDateOrNull(c.getToDate());

        List<WeddingPlan> result = source.stream()
                .filter(p -> keyword.isEmpty() || containsIgnoreCase(p, keyword))
                .filter(p -> "ALL".equals(c.getStatus()) || c.getStatus().equalsIgnoreCase(p.getStatus()))
                .filter(p -> "ALL".equals(c.getType()) || p.getType().getCode().equalsIgnoreCase(c.getType()))
                .filter(p -> from == null || !isBefore(p.getWeddingDate(), from))
                .filter(p -> to == null || !isAfter(p.getWeddingDate(), to))
                .collect(Collectors.toList());

        Comparator<WeddingPlan> byDate = Comparator.comparing(WeddingPlan::getWeddingDate);
        Comparator<WeddingPlan> order;
        switch (c.getSortBy()) {
            case PlanSearchCriteria.SORT_DATE_DESC:
                order = byDate.reversed();
                break;
            case PlanSearchCriteria.SORT_NAME:
                order = Comparator.comparing(p -> p.getPlanName().toLowerCase());
                break;
            case PlanSearchCriteria.SORT_BUDGET:
                order = Comparator.comparing((WeddingPlan p) ->
                        p.getTotalBudget() == null ? BigDecimal.ZERO : p.getTotalBudget()).reversed();
                break;
            case PlanSearchCriteria.SORT_NEWEST:
                order = Comparator.comparing(WeddingPlan::getCreatedAt).reversed();
                break;
            default:
                order = byDate;
        }
        result.sort(order);
        return result;
    }

    /**
     * Deletes a plan permanently (only the plan record; use PlanReportService#deletePlanCascade
     * to remove its checklist, budget and guests as well).
     *
     * @return true if deleted, false if plan did not exist
     */
    public boolean deletePlan(String planId) {
        boolean removed = repository.deleteById(planId);
        if (removed) log("Deleted plan: " + planId);
        return removed;
    }

    /** Returns the total number of plans stored. */
    public int count() {
        return repository.count();
    }

    // ─── Private Helpers ─────────────────────────────────────────────────────────

    private WeddingPlan findOrThrow(String planId) {
        return repository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("Wedding Plan not found: " + planId));
    }

    private void requireEditable(WeddingPlan plan) {
        if (WeddingPlan.STATUS_CANCELLED.equals(plan.getStatus())) {
            throw new BusinessRuleException("Cannot edit a cancelled plan.");
        }
        if (WeddingPlan.STATUS_COMPLETED.equals(plan.getStatus())) {
            throw new BusinessRuleException("Cannot edit a completed plan.");
        }
    }

    private void validateDetails(String planName, String weddingDate, String venue,
                                 String theme, String description, int guestCount) {
        requireNonEmpty(planName, "Plan Name");
        requireNonEmpty(weddingDate, "Wedding Date");
        requireNonEmpty(venue, "Venue");

        int nameLength = planName.trim().length();
        if (nameLength < 3 || nameLength > 100) {
            throw new ValidationException("Plan Name must be between 3 and 100 characters.");
        }
        if (venue.trim().length() > 140) {
            throw new ValidationException("Venue must be 140 characters or fewer.");
        }
        if (theme != null && theme.trim().length() > 100) {
            throw new ValidationException("Theme must be 100 characters or fewer.");
        }
        if (description != null && description.trim().length() > 1200) {
            throw new ValidationException("Notes must be 1,200 characters or fewer.");
        }
        if (!isStrictDate(weddingDate.trim())) {
            throw new ValidationException("Wedding Date must be in yyyy-MM-dd format.");
        }
        validateGuestCount(guestCount);
    }

    private void validateGuestCount(int guestCount) {
        if (guestCount < 0 || guestCount > MAX_GUESTS) {
            throw new ValidationException("Guest count must be between 0 and 5,000.");
        }
    }

    private void requireFutureDate(String weddingDate) {
        String date = weddingDate.trim();
        if (!ValidationUtil.isFutureDate(date)) {
            throw new BusinessRuleException("Wedding Date must be a future date.");
        }
        if (LocalDate.parse(date, ValidationUtil.DATE_FMT).isAfter(LocalDate.now().plusYears(MAX_YEARS_AHEAD))) {
            throw new BusinessRuleException("Wedding Date must be within the next " + MAX_YEARS_AHEAD + " years.");
        }
    }

    private void ensureNameAvailable(String customerId, String planName, String excludePlanId) {
        String wanted = planName.trim();
        boolean taken = repository.findByCustomerId(customerId).stream()
                .filter(p -> !WeddingPlan.STATUS_CANCELLED.equals(p.getStatus()))
                .filter(p -> excludePlanId == null || !excludePlanId.equals(p.getPlanId()))
                .anyMatch(p -> p.getPlanName().trim().equalsIgnoreCase(wanted));
        if (taken) {
            throw new BusinessRuleException("You already have a wedding plan with this name. Please use a different name.");
        }
    }

    private WeddingType parseType(String planType) {
        if (planType == null || planType.trim().isEmpty()) return WeddingType.STANDARD;
        if (!WeddingType.isValidCode(planType)) {
            throw new ValidationException("Unknown wedding type.");
        }
        return WeddingType.fromCode(planType);
    }

    private BigDecimal parseBudget(String value) {
        if (value == null || value.trim().isEmpty()) return BigDecimal.ZERO;
        BigDecimal bd;
        try {
            bd = new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Total Budget must be a valid number.");
        }
        if (bd.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Total Budget cannot be negative.");
        }
        if (bd.compareTo(MAX_BUDGET) > 0) {
            throw new ValidationException("Total Budget is too large.");
        }
        return bd;
    }

    /** Strict yyyy-MM-dd check (rejects impossible dates such as 2027-02-30). */
    private static boolean isStrictDate(String value) {
        try {
            LocalDate.parse(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean containsIgnoreCase(WeddingPlan p, String keyword) {
        return contains(p.getPlanName(), keyword) || contains(p.getVenue(), keyword)
                || contains(p.getTheme(), keyword) || contains(p.getDescription(), keyword)
                || contains(p.getPlanId(), keyword) || contains(p.getTypeLabel(), keyword);
    }

    private boolean contains(String text, String keywordLower) {
        return text != null && text.toLowerCase().contains(keywordLower);
    }

    private LocalDate parseDateOrNull(String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            return LocalDate.parse(value);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isBefore(String planDate, LocalDate limit) {
        LocalDate d = parseDateOrNull(planDate);
        return d != null && d.isBefore(limit);
    }

    private boolean isAfter(String planDate, LocalDate limit) {
        LocalDate d = parseDateOrNull(planDate);
        return d != null && d.isAfter(limit);
    }
}
