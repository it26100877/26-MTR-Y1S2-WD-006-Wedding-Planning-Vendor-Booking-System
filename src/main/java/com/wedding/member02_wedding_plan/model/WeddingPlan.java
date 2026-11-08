package com.wedding.member02_wedding_plan.model;

import java.math.BigDecimal;

/**
 * Represents a Wedding Plan entity.
 * Demonstrates Encapsulation: all fields are private with public getters/setters.
 *
 * @author Member 02
 */
public class WeddingPlan {

 // ─── Status Constants ────────────────────────────────────────────────────────
 public static final String STATUS_ACTIVE = "ACTIVE";
 public static final String STATUS_CANCELLED = "CANCELLED";
 public static final String STATUS_COMPLETED = "COMPLETED";

 // ─── Private Fields ──────────────────────────────────────────────────────────
 private String planId;
 private String customerId;
 private String planName;
 private String weddingDate; // yyyy-MM-dd
 private String venue;
 private String theme;
 private String description;
 private BigDecimal totalBudget;
 private String status; // ACTIVE | CANCELLED | COMPLETED
 private String createdAt;

 // ─── Full Constructor ────────────────────────────────────────────────────────
 public WeddingPlan(String planId, String customerId, String planName,
 String weddingDate, String venue, String theme,
 String description, BigDecimal totalBudget,
 String status, String createdAt) {
 this.planId = planId;
 this.customerId = customerId;
 this.planName = planName;
 this.weddingDate = weddingDate;
 this.venue = venue;
 this.theme = theme;
 this.description = description;
 this.totalBudget = totalBudget;
 this.status = status;
 this.createdAt = createdAt;
 }

 // ─── Getters ─────────────────────────────────────────────────────────────────
 public String getPlanId() { return planId; }
 public String getCustomerId() { return customerId; }
 public String getPlanName() { return planName; }
 public String getWeddingDate() { return weddingDate; }
 public String getVenue() { return venue; }
 public String getTheme() { return theme; }
 public String getDescription() { return description; }
 public BigDecimal getTotalBudget() { return totalBudget; }
 public String getStatus() { return status; }
 public String getCreatedAt() { return createdAt; }

 // ─── Setters ─────────────────────────────────────────────────────────────────
 public void setPlanId(String planId) { this.planId = planId; }
 public void setCustomerId(String customerId) { this.customerId = customerId; }
 public void setPlanName(String planName) { this.planName = planName; }
 public void setWeddingDate(String weddingDate) { this.weddingDate = weddingDate; }
 public void setVenue(String venue) { this.venue = venue; }
 public void setTheme(String theme) { this.theme = theme; }
 public void setDescription(String description) { this.description = description; }
 public void setTotalBudget(BigDecimal budget) { this.totalBudget = budget; }
 public void setStatus(String status) { this.status = status; }
 public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

 // ─── toString ────────────────────────────────────────────────────────────────
 @Override
 public String toString() {
 return "WeddingPlan{" +
 "planId='" + planId + '\'' +
 ", customerId='" + customerId + '\'' +
 ", planName='" + planName + '\'' +
 ", weddingDate='"+ weddingDate+ '\'' +
 ", venue='" + venue + '\'' +
 ", theme='" + theme + '\'' +
 ", totalBudget="+ totalBudget +
 ", status='" + status + '\'' +
 ", createdAt='" + createdAt + '\'' +
 '}';
 }
}
