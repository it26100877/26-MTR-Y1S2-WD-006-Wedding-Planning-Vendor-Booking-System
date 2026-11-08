package com.wedding.member02_wedding_plan.model;

/**
 * Represents a checklist task belonging to a WeddingPlan.
 * Demonstrates Encapsulation: all fields are private with public getters/setters.
 *
 * @author Member 02
 */
public class ChecklistTask {

 // ─── Status Constants ────────────────────────────────────────────────────────
 public static final String STATUS_PENDING = "PENDING";
 public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
 public static final String STATUS_COMPLETED = "COMPLETED";
 public static final String STATUS_OVERDUE = "OVERDUE";

 // ─── Priority Constants ──────────────────────────────────────────────────────
 public static final String PRIORITY_HIGH = "HIGH";
 public static final String PRIORITY_MEDIUM = "MEDIUM";
 public static final String PRIORITY_LOW = "LOW";

 // ─── Private Fields ──────────────────────────────────────────────────────────
 private String taskId;
 private String planId;
 private String taskName;
 private String description;
 private String deadline; // yyyy-MM-dd
 private String status; // PENDING | IN_PROGRESS | COMPLETED | OVERDUE
 private String priority; // HIGH | MEDIUM | LOW
 private String createdAt;

 // ─── Full Constructor ────────────────────────────────────────────────────────
 public ChecklistTask(String taskId, String planId, String taskName,
 String description, String deadline,
 String status, String priority, String createdAt) {
 this.taskId = taskId;
 this.planId = planId;
 this.taskName = taskName;
 this.description = description;
 this.deadline = deadline;
 this.status = status;
 this.priority = priority;
 this.createdAt = createdAt;
 }

 // ─── Getters ─────────────────────────────────────────────────────────────────
 public String getTaskId() { return taskId; }
 public String getPlanId() { return planId; }
 public String getTaskName() { return taskName; }
 public String getDescription() { return description; }
 public String getDeadline() { return deadline; }
 public String getStatus() { return status; }
 public String getPriority() { return priority; }
 public String getCreatedAt() { return createdAt; }

 // ─── Setters ─────────────────────────────────────────────────────────────────
 public void setTaskId(String taskId) { this.taskId = taskId; }
 public void setPlanId(String planId) { this.planId = planId; }
 public void setTaskName(String taskName) { this.taskName = taskName; }
 public void setDescription(String description) { this.description = description; }
 public void setDeadline(String deadline) { this.deadline = deadline; }
 public void setStatus(String status) { this.status = status; }
 public void setPriority(String priority) { this.priority = priority; }
 public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

 // ─── toString ────────────────────────────────────────────────────────────────
 @Override
 public String toString() {
 return "ChecklistTask{" +
 "taskId='" + taskId + '\'' +
 ", planId='" + planId + '\'' +
 ", taskName='"+ taskName + '\'' +
 ", deadline='"+ deadline + '\'' +
 ", status='" + status + '\'' +
 ", priority='"+ priority + '\'' +
 '}';
 }
}
