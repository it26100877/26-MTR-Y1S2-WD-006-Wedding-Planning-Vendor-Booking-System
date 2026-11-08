package com.wedding.member02_wedding_plan.service;

import com.wedding.common.exceptions.BusinessRuleException;
import com.wedding.common.exceptions.EntityNotFoundException;
import com.wedding.common.exceptions.ValidationException;
import com.wedding.common.service.BaseService;
import com.wedding.common.utils.FileUtil;
import com.wedding.common.utils.ValidationUtil;
import com.wedding.member02_wedding_plan.model.WeddingPlan;
import com.wedding.member02_wedding_plan.repository.WeddingPlanRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Business-logic service for Wedding Plan management.
 * Extends BaseService for shared validation helpers and logging.
 * Demonstrates Inheritance (extends BaseService) and Encapsulation (private repo).
 *
 * @author Member 02
 */
public class WeddingPlanService extends BaseService {

 private final WeddingPlanRepository repository;

 public WeddingPlanService(WeddingPlanRepository repository) {
 this.repository = repository;
 }

 // ─── Create ──────────────────────────────────────────────────────────────────

 /**
 * Creates and persists a new WeddingPlan.
 *
 * @throws ValidationException if any field is invalid
 * @throws BusinessRuleException if the wedding date is not in the future
 */
 public WeddingPlan createPlan(String customerId, String planName, String weddingDate,
 String venue, String theme, String description,
 String totalBudget) {
 requireNonEmpty(customerId, "Customer ID");
 requireNonEmpty(planName, "Plan Name");
 requireNonEmpty(weddingDate, "Wedding Date");
 requireNonEmpty(venue, "Venue");

 if (!ValidationUtil.isValidDate(weddingDate)) {
 throw new ValidationException("Wedding Date must be in yyyy-MM-dd format.");
 }
 if (!ValidationUtil.isFutureDate(weddingDate)) {
 throw new BusinessRuleException("Wedding Date must be a future date.");
 }

 BigDecimal budget = parseBudget(totalBudget);

 String planId = FileUtil.generateId("PLN");
 WeddingPlan plan = new WeddingPlan(
 planId, customerId, planName.trim(), weddingDate.trim(),
 venue.trim(),
 theme == null ? "" : theme.trim(),
 description == null ? "" : description.trim(),
 budget,
 WeddingPlan.STATUS_ACTIVE,
 ValidationUtil.nowDateTime()
 );
 repository.save(plan);
 log("Created plan: " + planId + " for customer: " + customerId);
 return plan;
 }

 // ─── Update ──────────────────────────────────────────────────────────────────

 /**
 * Updates an existing WeddingPlan's editable fields.
 *
 * @throws EntityNotFoundException if the plan does not exist
 * @throws ValidationException if any field is invalid
 * @throws BusinessRuleException if plan is cancelled/completed
 */
 public WeddingPlan updatePlan(String planId, String planName, String weddingDate,
 String venue, String theme, String description,
 String totalBudget) {
 WeddingPlan plan = findOrThrow(planId);
 requireEditable(plan);

 requireNonEmpty(planName, "Plan Name");
 requireNonEmpty(weddingDate, "Wedding Date");
 requireNonEmpty(venue, "Venue");

 if (!ValidationUtil.isValidDate(weddingDate)) {
 throw new ValidationException("Wedding Date must be in yyyy-MM-dd format.");
 }
 if (!ValidationUtil.isFutureDate(weddingDate)) {
 throw new BusinessRuleException("Wedding Date must be a future date.");
 }

 plan.setPlanName(planName.trim());
 plan.setWeddingDate(weddingDate.trim());
 plan.setVenue(venue.trim());
 plan.setTheme(theme == null ? "" : theme.trim());
 plan.setDescription(description == null ? "" : description.trim());
 plan.setTotalBudget(parseBudget(totalBudget));

 repository.save(plan);
 log("Updated plan: " + planId);
 return plan;
 }

 // ─── Status Changes ──────────────────────────────────────────────────────────

 /**
 * Cancels a plan (sets status to CANCELLED).
 *
 * @throws EntityNotFoundException if not found
 * @throws BusinessRuleException if already cancelled or completed
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
 * @throws BusinessRuleException if already completed or cancelled
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

 /**
 * Returns all plans belonging to a customer.
 */
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

 /**
 * Returns all wedding plans.
 */
 public List<WeddingPlan> getAllPlans() {
 return repository.findAll();
 }

 /**
 * Deletes a plan permanently.
 *
 * @return true if deleted, false if plan did not exist
 */
 public boolean deletePlan(String planId) {
 boolean removed = repository.deleteById(planId);
 if (removed) log("Deleted plan: " + planId);
 return removed;
 }

 /**
 * Returns the total number of plans stored.
 */
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

 private BigDecimal parseBudget(String value) {
 if (value == null || value.trim().isEmpty()) return BigDecimal.ZERO;
 try {
 BigDecimal bd = new BigDecimal(value.trim());
 if (bd.compareTo(BigDecimal.ZERO) < 0) {
 throw new ValidationException("Total Budget must be a positive number.");
 }
 return bd;
 } catch (NumberFormatException e) {
 throw new ValidationException("Total Budget must be a valid number.");
 }
 }
}
