package com.wedding.member02_wedding_plan.repository;

import com.wedding.member02_wedding_plan.model.ChecklistTask;

/**
 * File-based repository for ChecklistTask entities (checklist_tasks.csv).
 * Inherits CRUD, getId() and the per-plan queries from PlanItemRepository.
 * Demonstrates Inheritance and Polymorphism (overrides serialize/deserialize).
 *
 * Fields are separated by the pipe '|' character.
 * Order: taskId|planId|taskName|description|deadline|status|priority|createdAt
 *
 * Older files may contain the status OVERDUE. It is turned back into PENDING when
 * loaded, because overdue is calculated from the deadline and never stored.
 *
 * @author Member 02
 */
public class ChecklistTaskRepository extends PlanItemRepository<ChecklistTask> {

    private static final String FILE_NAME = "checklist_tasks.csv";

    public ChecklistTaskRepository() {
        super(FILE_NAME);
    }

    /** Uses a different file name (for unit tests). */
    public ChecklistTaskRepository(String filename) {
        super(filename);
    }

    @Override
    protected String serialize(ChecklistTask task) {
        return String.join(SEPARATOR,
                safe(task.getTaskId()),
                safe(task.getPlanId()),
                safe(task.getTaskName()),
                safe(task.getDescription()),
                safe(task.getDeadline()),
                safe(storedStatus(task.getStatus())),
                safe(task.getPriority()),
                safe(task.getCreatedAt())
        );
    }

    @Override
    protected ChecklistTask deserialize(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] p = line.split(SAFE_SEP, -1);
        if (p.length < 8) return null;
        return new ChecklistTask(
                p[0].trim(),               // taskId
                p[1].trim(),               // planId
                p[2].trim(),               // taskName
                p[3].trim(),               // description
                p[4].trim(),               // deadline
                storedStatus(p[5].trim()), // status
                p[6].trim(),               // priority
                p[7].trim()                // createdAt
        );
    }

    private String storedStatus(String status) {
        return ChecklistTask.STATUS_OVERDUE.equals(status) ? ChecklistTask.STATUS_PENDING : status;
    }
}
