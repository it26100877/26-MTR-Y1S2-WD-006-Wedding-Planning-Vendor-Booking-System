package com.wedding.member02_wedding_plan.service;

import com.wedding.common.exceptions.BusinessRuleException;
import com.wedding.common.exceptions.EntityNotFoundException;
import com.wedding.common.exceptions.ValidationException;
import com.wedding.common.service.BaseService;
import com.wedding.common.utils.FileUtil;
import com.wedding.common.utils.ValidationUtil;
import com.wedding.member02_wedding_plan.model.ChecklistTask;
import com.wedding.member02_wedding_plan.repository.ChecklistTaskRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Business-logic service for Checklist Task management.
 * Extends BaseService for shared validation helpers and logging.
 * Demonstrates Inheritance (extends BaseService) and Encapsulation (private repo).
 *
 * @author Member 02
 */
public class ChecklistService extends BaseService {

 private final ChecklistTaskRepository repository;

 public ChecklistService(ChecklistTaskRepository repository) {
 this.repository = repository;
 }

 // ─── Add Task ────────────────────────────────────────────────────────────────

 /**
 * Creates and persists a new ChecklistTask for a given plan.
 *
 * @throws ValidationException if any required field is missing or invalid
 */
 public ChecklistTask addTask(String planId, String taskName, String description,
 String deadline, String priority) {
 requireNonEmpty(planId, "Plan ID");
 requireNonEmpty(taskName, "Task Name");
 requireNonEmpty(deadline, "Deadline");

 if (!ValidationUtil.isValidDate(deadline)) {
 throw new ValidationException("Deadline must be in yyyy-MM-dd format.");
 }

 String safePriority = validatePriority(priority);

 String taskId = FileUtil.generateId("TSK");
 ChecklistTask task = new ChecklistTask(
 taskId,
 planId,
 taskName.trim(),
 description == null ? "" : description.trim(),
 deadline.trim(),
 ChecklistTask.STATUS_PENDING,
 safePriority,
 ValidationUtil.nowDateTime()
 );
 repository.save(task);
 log("Added task: " + taskId + " to plan: " + planId);
 return task;
 }

 // ─── Update Task ─────────────────────────────────────────────────────────────

 /**
 * Updates an existing task's editable fields.
 *
 * @throws EntityNotFoundException if the task does not exist
 * @throws ValidationException if any field is invalid
 */
 public ChecklistTask updateTask(String taskId, String taskName, String description,
 String deadline, String priority) {
 ChecklistTask task = findOrThrow(taskId);

 requireNonEmpty(taskName, "Task Name");
 requireNonEmpty(deadline, "Deadline");

 if (!ValidationUtil.isValidDate(deadline)) {
 throw new ValidationException("Deadline must be in yyyy-MM-dd format.");
 }

 task.setTaskName(taskName.trim());
 task.setDescription(description == null ? "" : description.trim());
 task.setDeadline(deadline.trim());
 task.setPriority(validatePriority(priority));

 repository.save(task);
 log("Updated task: " + taskId);
 return task;
 }

 // ─── Status Transitions ──────────────────────────────────────────────────────

 /**
 * Marks a task as COMPLETED.
 *
 * @throws EntityNotFoundException if not found
 * @throws BusinessRuleException if already completed
 */
 public ChecklistTask markComplete(String taskId) {
 ChecklistTask task = findOrThrow(taskId);
 if (ChecklistTask.STATUS_COMPLETED.equals(task.getStatus())) {
 throw new BusinessRuleException("Task is already completed.");
 }
 task.setStatus(ChecklistTask.STATUS_COMPLETED);
 repository.save(task);
 log("Marked complete: " + taskId);
 return task;
 }

 /**
 * Marks a task as IN_PROGRESS.
 *
 * @throws EntityNotFoundException if not found
 * @throws BusinessRuleException if already completed
 */
 public ChecklistTask markInProgress(String taskId) {
 ChecklistTask task = findOrThrow(taskId);
 if (ChecklistTask.STATUS_COMPLETED.equals(task.getStatus())) {
 throw new BusinessRuleException("Cannot move a completed task back to In-Progress.");
 }
 task.setStatus(ChecklistTask.STATUS_IN_PROGRESS);
 repository.save(task);
 log("Marked in-progress: " + taskId);
 return task;
 }

 // ─── Delete ──────────────────────────────────────────────────────────────────

 /**
 * Deletes a task permanently.
 *
 * @return true if deleted, false if task did not exist
 */
 public boolean deleteTask(String taskId) {
 boolean removed = repository.deleteById(taskId);
 if (removed) log("Deleted task: " + taskId);
 return removed;
 }

 // ─── Queries ─────────────────────────────────────────────────────────────────

 /**
 * Returns all tasks belonging to a plan, refreshing OVERDUE status automatically.
 */
 public List<ChecklistTask> getTasksByPlan(String planId) {
 List<ChecklistTask> tasks = repository.findByPlanId(planId);
 LocalDate today = LocalDate.now();
 for (ChecklistTask t : tasks) {
 if (ChecklistTask.STATUS_PENDING.equals(t.getStatus()) ||
 ChecklistTask.STATUS_IN_PROGRESS.equals(t.getStatus())) {
 try {
 LocalDate dl = LocalDate.parse(t.getDeadline(), ValidationUtil.DATE_FMT);
 if (dl.isBefore(today)) {
 t.setStatus(ChecklistTask.STATUS_OVERDUE);
 repository.save(t);
 }
 } catch (Exception ignored) {}
 }
 }
 return repository.findByPlanId(planId);
 }

 /**
 * Calculates the completion percentage (0–100) for a plan's checklist.
 *
 * @param planId the plan's ID
 * @return integer percentage of COMPLETED tasks
 */
 public int getCompletionPercent(String planId) {
 List<ChecklistTask> tasks = repository.findByPlanId(planId);
 if (tasks.isEmpty()) return 0;
 long completed = tasks.stream()
 .filter(t -> ChecklistTask.STATUS_COMPLETED.equals(t.getStatus()))
 .count();
 return (int) Math.round((completed * 100.0) / tasks.size());
 }

 // ─── Private Helpers ─────────────────────────────────────────────────────────

 private ChecklistTask findOrThrow(String taskId) {
 return repository.findById(taskId)
 .orElseThrow(() -> new EntityNotFoundException("Checklist Task not found: " + taskId));
 }

 private String validatePriority(String priority) {
 if (priority == null || priority.trim().isEmpty()) return ChecklistTask.PRIORITY_MEDIUM;
 switch (priority.trim().toUpperCase()) {
 case "HIGH": return ChecklistTask.PRIORITY_HIGH;
 case "LOW": return ChecklistTask.PRIORITY_LOW;
 default: return ChecklistTask.PRIORITY_MEDIUM;
 }
 }
}
