package com.wedding.member02_wedding_plan.repository;

import com.wedding.common.repository.AbstractFileRepository;
import com.wedding.member02_wedding_plan.model.WeddingPlan;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * File-based repository for WeddingPlan entities.
 * Extends AbstractFileRepository to gain CRUD and file-persistence behaviour.
 * Demonstrates Inheritance and Polymorphism (overrides serialize/deserialize).
 *
 * Fields are separated by the pipe '|' character to safely handle
 * description text that may contain commas.
 *
 * @author Member 02
 */
public class WeddingPlanRepository extends AbstractFileRepository<WeddingPlan> {

 private static final String FILE_NAME = "wedding_plans.csv";
 private static final String SEPARATOR = "|";
 private static final String SAFE_SEP = "\\|"; // regex split

 public WeddingPlanRepository() {
 super(FILE_NAME);
 }

 // ─── AbstractFileRepository Contract ────────────────────────────────────────

 @Override
 protected String getId(WeddingPlan plan) {
 return plan.getPlanId();
 }

 /**
 * Serializes a WeddingPlan to a pipe-delimited line.
 * Order: planId|customerId|planName|weddingDate|venue|theme|description|totalBudget|status|createdAt
 */
 @Override
 protected String serialize(WeddingPlan plan) {
 return String.join(SEPARATOR,
 safe(plan.getPlanId()),
 safe(plan.getCustomerId()),
 safe(plan.getPlanName()),
 safe(plan.getWeddingDate()),
 safe(plan.getVenue()),
 safe(plan.getTheme()),
 safe(plan.getDescription()),
 plan.getTotalBudget() != null ? plan.getTotalBudget().toPlainString() : "0",
 safe(plan.getStatus()),
 safe(plan.getCreatedAt())
 );
 }

 /**
 * Deserializes a pipe-delimited line back to a WeddingPlan.
 */
 @Override
 protected WeddingPlan deserialize(String line) {
 if (line == null || line.trim().isEmpty()) return null;
 String[] p = line.split(SAFE_SEP, -1);
 if (p.length < 10) return null;
 return new WeddingPlan(
 p[0].trim(), // planId
 p[1].trim(), // customerId
 p[2].trim(), // planName
 p[3].trim(), // weddingDate
 p[4].trim(), // venue
 p[5].trim(), // theme
 p[6].trim(), // description
 parseBigDecimal(p[7].trim()), // totalBudget
 p[8].trim(), // status
 p[9].trim() // createdAt
 );
 }

 // ─── Additional Query ────────────────────────────────────────────────────────

 /**
 * Returns all plans belonging to the given customer.
 *
 * @param customerId the customer's ID
 * @return list of matching WeddingPlans
 */
 public List<WeddingPlan> findByCustomerId(String customerId) {
 return findAll().stream()
 .filter(p -> p.getCustomerId().equals(customerId))
 .collect(Collectors.toList());
 }

 // ─── Helpers ─────────────────────────────────────────────────────────────────

 private String safe(String value) {
 return value == null ? "" : value.replace("|", " ").replace("\n", " ").replace("\r", " ");
 }

 private BigDecimal parseBigDecimal(String value) {
 try {
 return new BigDecimal(value);
 } catch (NumberFormatException e) {
 return BigDecimal.ZERO;
 }
 }
}
