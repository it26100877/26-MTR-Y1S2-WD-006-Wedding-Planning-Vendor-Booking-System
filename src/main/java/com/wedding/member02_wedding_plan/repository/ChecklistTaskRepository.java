package com.wedding.member02_wedding_plan.repository;

import com.wedding.common.repository.AbstractFileRepository;
import com.wedding.member02_wedding_plan.model.ChecklistTask;

import java.util.List;
import java.util.stream.Collectors;

/**
 * File-based repository for ChecklistTask entities.
 * Extends AbstractFileRepository to gain CRUD and file-persistence behaviour.
 * Demonstrates Inheritance and Polymorphism (overrides serialize/deserialize).
 *
 * Fields are separated by the pipe '|' character.
 * Order: taskId|planId|taskName|description|deadline|status|priority|createdAt
 *
 * @author Member 02
 */
public class ChecklistTaskRepository extends AbstractFileRepository<ChecklistTask> {

 private static final String FILE_NAME = "checklist_tasks.csv";
 private static final String SEPARATOR = "|";
 private static final String SAFE_SEP = "\\|"; // regex split

 public ChecklistTaskRepository() {
 super(FILE_NAME);
 }

 // ─── AbstractFileRepository Contract ────────────────────────────────────────

 @Override
 protected String getId(ChecklistTask task) {
 return task.getTaskId();
 }

 /**
 * Serializes a ChecklistTask to a pipe-delimited line.
 */
 @Override
 protected String serialize(ChecklistTask task) {
 return String.join(SEPARATOR,
 safe(task.getTaskId()),
 safe(task.getPlanId()),
 safe(task.getTaskName()),
 safe(task.getDescription()),
 safe(task.getDeadline()),
 safe(task.getStatus()),
 safe(task.getPriority()),
 safe(task.getCreatedAt())
 );
 }

 /**
 * Deserializes a pipe-delimited line back to a ChecklistTask.
 */
 @Override
 protected ChecklistTask deserialize(String line) {
 if (line == null || line.trim().isEmpty()) return null;
 String[] p = line.split(SAFE_SEP, -1);
 if (p.length < 8) return null;
 return new ChecklistTask(
 p[0].trim(), // taskId
 p[1].trim(), // planId
 p[2].trim(), // taskName
 p[3].trim(), // description
 p[4].trim(), // deadline
 p[5].trim(), // status
 p[6].trim(), // priority
 p[7].trim() // createdAt
 );
 }

 // ─── Additional Query ────────────────────────────────────────────────────────

 /**
 * Returns all tasks belonging to the given plan.
 *
 * @param planId the plan's ID
 * @return list of matching ChecklistTasks
 */
 public List<ChecklistTask> findByPlanId(String planId) {
 return findAll().stream()
 .filter(t -> t.getPlanId().equals(planId))
 .collect(Collectors.toList());
 }

 // ─── Helpers ─────────────────────────────────────────────────────────────────

 private String safe(String value) {
 return value == null ? "" : value.replace("|", " ").replace("\n", " ").replace("\r", " ");
 }
}
