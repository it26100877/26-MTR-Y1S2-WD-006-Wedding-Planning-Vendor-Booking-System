package com.wedding.member02_wedding_plan.service;

import com.wedding.common.exceptions.BusinessRuleException;
import com.wedding.common.exceptions.EntityNotFoundException;
import com.wedding.common.exceptions.ValidationException;
import com.wedding.common.service.BaseService;
import com.wedding.common.utils.FileUtil;
import com.wedding.common.utils.ValidationUtil;
import com.wedding.member02_wedding_plan.model.ChecklistTask;
import com.wedding.member02_wedding_plan.model.RecommendedTask;
import com.wedding.member02_wedding_plan.model.WeddingPlan;
import com.wedding.member02_wedding_plan.repository.ChecklistTaskRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Business-logic service for Checklist Task management.
 * Extends BaseService for shared validation helpers and logging.
 * Demonstrates Inheritance (extends BaseService) and Encapsulation (private repo).
 *
 * Overdue is calculated from the deadline (see ChecklistTask#getDisplayStatus); it is
 * never written to the file.
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
        return addTask(planId, taskName, description, deadline, priority, null);
    }

    /**
     * Same as the 5-argument version but also rejects a deadline after the wedding date.
     *
     * @param weddingDate the plan's wedding date (yyyy-MM-dd), or null to skip that check
     */
    public ChecklistTask addTask(String planId, String taskName, String description,
                                 String deadline, String priority, String weddingDate) {
        requireNonEmpty(planId, "Plan ID");
        validateTaskFields(taskName, description, deadline, weddingDate);

        ChecklistTask task = new ChecklistTask(
                FileUtil.generateId("TSK"),
                planId,
                taskName.trim(),
                description == null ? "" : description.trim(),
                deadline.trim(),
                ChecklistTask.STATUS_PENDING,
                validatePriority(priority),
                ValidationUtil.nowDateTime()
        );
        repository.save(task);
        log("Added task: " + task.getTaskId() + " to plan: " + planId);
        return task;
    }

    // ─── Template ────────────────────────────────────────────────────────────────

    /**
     * Adds the checklist template of the plan's wedding type (polymorphic call to
     * {@link WeddingPlan#getRecommendedChecklist()}). Tasks whose name already exists in the
     * plan are skipped, so the template can be applied again safely. If the wedding is closer
     * than the template's longest lead time, due dates are compressed to fit between today
     * and the wedding date.
     *
     * @return the number of tasks added
     */
    public int generateFromTemplate(WeddingPlan plan) {
        LocalDate wedding;
        try {
            wedding = LocalDate.parse(plan.getWeddingDate());
        } catch (Exception e) {
            throw new ValidationException("The wedding date of this plan is not valid.");
        }
        List<RecommendedTask> template = plan.getRecommendedChecklist();
        int longestLead = template.stream().mapToInt(RecommendedTask::getDaysBeforeWedding).max().orElse(0);
        long daysAvailable = Math.max(0, java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), wedding));
        double factor = longestLead == 0 ? 1.0 : Math.min(1.0, (double) daysAvailable / longestLead);

        Set<String> existing = new HashSet<>();
        for (ChecklistTask t : repository.findByPlanId(plan.getPlanId())) {
            existing.add(t.getTaskName().trim().toLowerCase());
        }

        List<ChecklistTask> created = new ArrayList<>();
        for (RecommendedTask rt : template) {
            if (existing.contains(rt.getName().trim().toLowerCase())) continue;
            int lead = (int) Math.round(rt.getDaysBeforeWedding() * factor);
            created.add(new ChecklistTask(
                    FileUtil.generateId("TSK"),
                    plan.getPlanId(),
                    rt.getName(),
                    rt.getDescription(),
                    wedding.minusDays(lead).toString(),
                    ChecklistTask.STATUS_PENDING,
                    validatePriority(rt.getPriority()),
                    ValidationUtil.nowDateTime()));
        }
        if (!created.isEmpty()) {
            repository.saveAll(created);
        }
        log("Template added " + created.size() + " task(s) to plan: " + plan.getPlanId());
        return created.size();
    }

    // ─── Update Task ─────────────────────────────────────────────────────────────

    /**
     * Updates an existing task's editable fields.
     *
     * @throws EntityNotFoundException if the task does not exist
     * @throws ValidationException     if any field is invalid
     */
    public ChecklistTask updateTask(String taskId, String taskName, String description,
                                    String deadline, String priority) {
        return updateTask(taskId, taskName, description, deadline, priority, null);
    }

    public ChecklistTask updateTask(String taskId, String taskName, String description,
                                    String deadline, String priority, String weddingDate) {
        ChecklistTask task = findOrThrow(taskId);
        validateTaskFields(taskName, description, deadline, weddingDate);
        // Validate before editing the cached entity. A rejected update must be atomic.
        String checkedPriority = validatePriority(priority);

        task.setTaskName(taskName.trim());
        task.setDescription(description == null ? "" : description.trim());
        task.setDeadline(deadline.trim());
        task.setPriority(checkedPriority);

        repository.save(task);
        log("Updated task: " + taskId);
        return task;
    }

    // ─── Status Transitions ──────────────────────────────────────────────────────

    /**
     * Marks a task as COMPLETED.
     *
     * @throws EntityNotFoundException if not found
     * @throws BusinessRuleException   if already completed
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
     * @throws BusinessRuleException   if already completed
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

    /**
     * Re-opens a completed task (sets it back to PENDING).
     *
     * @throws BusinessRuleException if the task is not completed
     */
    public ChecklistTask reopen(String taskId) {
        ChecklistTask task = findOrThrow(taskId);
        if (!ChecklistTask.STATUS_COMPLETED.equals(task.getStatus())) {
            throw new BusinessRuleException("Only a completed task can be re-opened.");
        }
        task.setStatus(ChecklistTask.STATUS_PENDING);
        repository.save(task);
        log("Re-opened: " + taskId);
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

    /** Deletes every task of a plan; returns how many were removed. */
    public int deleteByPlan(String planId) {
        int removed = repository.deleteByPlanId(planId);
        if (removed > 0) log("Deleted " + removed + " task(s) of plan: " + planId);
        return removed;
    }

    // ─── Queries ─────────────────────────────────────────────────────────────────

    /** Returns one task, or throws if it does not exist. */
    public ChecklistTask getTask(String taskId) {
        return findOrThrow(taskId);
    }

    /**
     * Returns all tasks of a plan: open tasks first by deadline then priority, completed
     * tasks last. OVERDUE is calculated, nothing is written to the file.
     */
    public List<ChecklistTask> getTasksByPlan(String planId) {
        List<ChecklistTask> tasks = repository.findByPlanId(planId);
        Comparator<ChecklistTask> order = Comparator
                .comparing(ChecklistTask::isCompleted)
                .thenComparing(ChecklistTask::getDeadline)
                .thenComparingInt(t -> priorityRank(t.getPriority()));
        tasks.sort(order);
        return tasks;
    }

    /**
     * Calculates the completion percentage (0-100) for a plan's checklist.
     *
     * @return integer percentage of COMPLETED tasks, 0 when there are no tasks
     */
    public int getCompletionPercent(String planId) {
        List<ChecklistTask> tasks = repository.findByPlanId(planId);
        if (tasks.isEmpty()) return 0;
        long completed = tasks.stream().filter(ChecklistTask::isCompleted).count();
        return (int) Math.round((completed * 100.0) / tasks.size());
    }

    /**
     * Stops rescheduling a wedding to a date before any linked checklist deadline.
     * No task or wedding plan is changed when a conflict is found.
     */
    public void validateDeadlinesForWeddingDate(String planId, String proposedWeddingDate) {
        requireNonEmpty(planId, "Plan ID");
        if (!isStrictDate(proposedWeddingDate)) {
            throw new ValidationException("Wedding date must be a valid yyyy-MM-dd date.");
        }
        LocalDate wedding = LocalDate.parse(proposedWeddingDate.trim());
        for (ChecklistTask task : repository.findByPlanId(planId)) {
            if (!isStrictDate(task.getDeadline())) {
                throw new ValidationException("Task '" + task.getTaskName()
                        + "' has an invalid deadline. Correct the task before changing the wedding date.");
            }
            if (LocalDate.parse(task.getDeadline()).isAfter(wedding)) {
                throw new BusinessRuleException("Cannot move the wedding to " + proposedWeddingDate
                        + ": checklist task '" + task.getTaskName() + "' is due on " + task.getDeadline()
                        + ". Edit its deadline first.");
            }
        }
    }

    // ─── Private Helpers ─────────────────────────────────────────────────────────

    private ChecklistTask findOrThrow(String taskId) {
        return repository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Checklist Task not found: " + taskId));
    }

    private void validateTaskFields(String taskName, String description, String deadline, String weddingDate) {
        requireNonEmpty(taskName, "Task Name");
        requireNonEmpty(deadline, "Deadline");
        if (taskName.trim().length() > 120) {
            throw new ValidationException("Task Name must be 120 characters or fewer.");
        }
        if (description != null && description.trim().length() > 800) {
            throw new ValidationException("Description must be 800 characters or fewer.");
        }
        if (!isStrictDate(deadline.trim())) {
            throw new ValidationException("Deadline must be in yyyy-MM-dd format.");
        }
        if (weddingDate != null && isStrictDate(weddingDate)
                && LocalDate.parse(deadline.trim()).isAfter(LocalDate.parse(weddingDate))) {
            throw new BusinessRuleException("A task deadline cannot be after the wedding date (" + weddingDate + ").");
        }
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

    private int priorityRank(String priority) {
        if (ChecklistTask.PRIORITY_HIGH.equals(priority)) return 0;
        if (ChecklistTask.PRIORITY_LOW.equals(priority)) return 2;
        return 1;
    }

    private String validatePriority(String priority) {
        if (priority == null || priority.trim().isEmpty()) return ChecklistTask.PRIORITY_MEDIUM;
        switch (priority.trim().toUpperCase()) {
            case "HIGH": return ChecklistTask.PRIORITY_HIGH;
            case "LOW": return ChecklistTask.PRIORITY_LOW;
            case "MEDIUM": return ChecklistTask.PRIORITY_MEDIUM;
            default: throw new ValidationException("Priority must be HIGH, MEDIUM, or LOW.");
        }
    }
}
