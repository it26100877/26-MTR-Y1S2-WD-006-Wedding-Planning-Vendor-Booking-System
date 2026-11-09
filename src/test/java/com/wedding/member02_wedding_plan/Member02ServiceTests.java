package com.wedding.member02_wedding_plan;

import com.wedding.common.exceptions.BusinessRuleException;
import com.wedding.common.exceptions.ValidationException;
import com.wedding.common.utils.FileUtil;
import com.wedding.member02_wedding_plan.model.*;
import com.wedding.member02_wedding_plan.repository.BudgetItemRepository;
import com.wedding.member02_wedding_plan.repository.ChecklistTaskRepository;
import com.wedding.member02_wedding_plan.repository.GuestRepository;
import com.wedding.member02_wedding_plan.repository.WeddingPlanRepository;
import com.wedding.member02_wedding_plan.service.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Member 02 layers (model, file repositories, services) without Spring.
 * Every test uses its own temporary data files, which are deleted afterwards, so the real
 * CSV files and the other tests are never touched.
 *
 * @author Member 02
 */
public class Member02ServiceTests {

    private final List<String> files = new ArrayList<>();
    private String planFile;
    private String taskFile;
    private String budgetFile;
    private String guestFile;

    private WeddingPlanService planService;
    private ChecklistService checklistService;
    private BudgetService budgetService;
    private GuestService guestService;
    private PlanReportService reportService;

    @BeforeEach
    void setUp() {
        String id = UUID.randomUUID().toString().substring(0, 8);
        planFile = "test_m02_" + id + "_plans.csv";
        taskFile = "test_m02_" + id + "_tasks.csv";
        budgetFile = "test_m02_" + id + "_budget.csv";
        guestFile = "test_m02_" + id + "_guests.csv";
        files.add(planFile);
        files.add(taskFile);
        files.add(budgetFile);
        files.add(guestFile);
        buildServices();
    }

    private void buildServices() {
        planService = new WeddingPlanService(new WeddingPlanRepository(planFile));
        checklistService = new ChecklistService(new ChecklistTaskRepository(taskFile));
        budgetService = new BudgetService(new BudgetItemRepository(budgetFile));
        guestService = new GuestService(new GuestRepository(guestFile));
        reportService = new PlanReportService(planService, checklistService, budgetService, guestService);
    }

    @AfterEach
    void cleanUp() {
        for (String f : files) {
            new File(FileUtil.getFilePath(f)).delete();
        }
        files.clear();
    }

    private static String inDays(int days) {
        return LocalDate.now().plusDays(days).toString();
    }

    private WeddingPlan newPlan(String customer, String name, int days, String type, int guests) {
        return planService.createPlan(customer, name, inDays(days), "Colombo", "Classic", "notes",
                "2000000", guests, type);
    }

    // ─── Member 02 v7: reminder buckets and consistency protection ──────────────

    @Test
    @DisplayName("Reminder buckets: overdue, today, days 1-3, 4-7; no reminder after day 7")
    void reminderBoundaries() {
        LocalDate today = LocalDate.of(2030, 6, 10);
        int[] offsets = {-1, 0, 1, 3, 4, 7, 8};
        String[] expected = {"OVERDUE", "TODAY", "THREE_DAYS", "THREE_DAYS",
                "SEVEN_DAYS", "SEVEN_DAYS", "NONE"};
        for (int i = 0; i < offsets.length; i++) {
            ChecklistTask task = new ChecklistTask("TSK-"+i, "P1", "Test", "",
                    today.plusDays(offsets[i]).toString(), "PENDING", "HIGH", "now");
            assertEquals(expected[i], task.getReminderCategory(today));
        }
        ChecklistTask done = new ChecklistTask("TSK-D", "P1", "Done", "",
                today.minusDays(2).toString(), "COMPLETED", "HIGH", "now");
        assertEquals("NONE", done.getReminderCategory(today));
        ChecklistTask malformed = new ChecklistTask("TSK-X", "P1", "Malformed", "",
                "invalid", "PENDING", "HIGH", "now");
        assertEquals("NONE", malformed.getReminderCategory(today));
    }

    @Test
    @DisplayName("Reminder dashboard includes unfinished tasks and excludes completed ones")
    void reminderSummaryCounts() {
        WeddingPlan plan = newPlan("C1", "Reminder Wedding", 40, "STANDARD", 100);
        ChecklistTask late = checklistService.addTask(plan.getPlanId(), "Late", "", inDays(-1), "HIGH", plan.getWeddingDate());
        checklistService.addTask(plan.getPlanId(), "Today", "", inDays(0), "HIGH", plan.getWeddingDate());
        checklistService.addTask(plan.getPlanId(), "In 3 days", "", inDays(3), "MEDIUM", plan.getWeddingDate());
        checklistService.addTask(plan.getPlanId(), "In 6 days", "", inDays(6), "LOW", plan.getWeddingDate());
        checklistService.addTask(plan.getPlanId(), "Later", "", inDays(8), "MEDIUM", plan.getWeddingDate());
        assertEquals(4, reportService.buildSummary(plan.getPlanId()).getReminderCount());
        assertEquals(1, reportService.buildSummary(plan.getPlanId()).getOverdueTasks());
        assertEquals(1, reportService.buildSummary(plan.getPlanId()).getDueTodayTasks());
        assertEquals(1, reportService.buildSummary(plan.getPlanId()).getDueInThreeDaysTasks());
        assertEquals(1, reportService.buildSummary(plan.getPlanId()).getDueInSevenDaysTasks());
        checklistService.markComplete(late.getTaskId());
        assertEquals(3, reportService.buildSummary(plan.getPlanId()).getReminderCount());
    }

    @Test
    @DisplayName("Changing wedding date past existing task deadlines is blocked without modifying data")
    void dateChangeRespectsChecklistDeadlines() {
        WeddingPlan plan = newPlan("C1", "Checklist Dates", 100, "STANDARD", 70);
        ChecklistTask task = checklistService.addTask(plan.getPlanId(), "Book venue", "", inDays(80), "HIGH", plan.getWeddingDate());
        assertThrows(BusinessRuleException.class, () -> planService.updatePlanWithChecklistValidation(
                plan.getPlanId(), plan.getPlanName(), inDays(60), plan.getVenue(), "", "",
                "2000000", 70, "STANDARD", checklistService));
        assertEquals(inDays(100), planService.getById(plan.getPlanId()).getWeddingDate());
        assertEquals(inDays(80), checklistService.getTask(task.getTaskId()).getDeadline());
        checklistService.updateTask(task.getTaskId(), "Book venue", "", inDays(50), "HIGH", inDays(100));
        assertEquals(inDays(60), planService.updatePlanWithChecklistValidation(
                plan.getPlanId(), plan.getPlanName(), inDays(60), plan.getVenue(), "", "",
                "2000000", 70, "STANDARD", checklistService).getWeddingDate());
    }

    @Test
    @DisplayName("Linked vendor bookings block cascade deletion and preserve child data")
    void bookingLinksProtectPlanDeletion() {
        WeddingPlan plan = newPlan("C1", "Booked Wedding", 100, "STANDARD", 70);
        checklistService.addTask(plan.getPlanId(), "Venue", "", inDays(40), "MEDIUM", plan.getWeddingDate());
        PlanReportService blocked = new PlanReportService(planService, checklistService,
                budgetService, guestService, id -> id.equals(plan.getPlanId()));
        assertThrows(BusinessRuleException.class, () -> blocked.deletePlanCascade(plan.getPlanId()));
        assertEquals(1, planService.count());
        assertEquals(1, checklistService.getTasksByPlan(plan.getPlanId()).size());
        PlanReportService allowed = new PlanReportService(planService, checklistService,
                budgetService, guestService, id -> false);
        assertTrue(allowed.deletePlanCascade(plan.getPlanId()));
        assertEquals(0, planService.count());
    }

    @Test
    @DisplayName("Invalid checklist priority is rejected; previous task remains intact")
    void invalidPriorityRejected() {
        WeddingPlan plan = newPlan("C1", "Priority Wedding", 90, "STANDARD", 70);
        ChecklistTask task = checklistService.addTask(plan.getPlanId(), "Choose flowers", "", inDays(12), "HIGH", plan.getWeddingDate());
        assertThrows(ValidationException.class, () -> checklistService.addTask(
                plan.getPlanId(), "Invalid", "", inDays(12), "URGENT", plan.getWeddingDate()));
        assertThrows(ValidationException.class, () -> checklistService.updateTask(
                task.getTaskId(), "Modified", "", inDays(12), "OTHER", plan.getWeddingDate()));
        assertEquals("Choose flowers", checklistService.getTask(task.getTaskId()).getTaskName());
        assertEquals("HIGH", checklistService.getTask(task.getTaskId()).getPriority());
    }

    // ─── Inheritance and polymorphism ────────────────────────────────────────────

    @Test
    @DisplayName("Factory returns the right subclass for every wedding type")
    void factoryBuildsSubclasses() {
        assertTrue(WeddingPlanFactory.createPreview(WeddingType.BEACH, 10) instanceof BeachWedding);
        assertTrue(WeddingPlanFactory.createPreview(WeddingType.TRADITIONAL, 10) instanceof TraditionalWedding);
        assertTrue(WeddingPlanFactory.createPreview(WeddingType.GARDEN, 10) instanceof GardenWedding);
        assertTrue(WeddingPlanFactory.createPreview(WeddingType.HOTEL, 10) instanceof HotelWedding);
        assertTrue(WeddingPlanFactory.createPreview(WeddingType.STANDARD, 10) instanceof StandardWedding);
        for (WeddingType t : WeddingType.values()) {
            assertEquals(t, WeddingPlanFactory.createPreview(t, 1).getType());
        }
    }

    @Test
    @DisplayName("Each subclass calculates the estimated cost differently")
    void estimatesFollowEachTypesOwnRules() {
        assertEquals(0, new BigDecimal("1974000").compareTo(WeddingPlanFactory.createPreview(WeddingType.STANDARD, 200).calculateEstimatedCost()));
        assertEquals(0, new BigDecimal("189000").compareTo(WeddingPlanFactory.createPreview(WeddingType.STANDARD, 0).calculateEstimatedCost()));
        assertEquals(0, new BigDecimal("2921400").compareTo(WeddingPlanFactory.createPreview(WeddingType.BEACH, 250).calculateEstimatedCost()));
        assertEquals(0, new BigDecimal("1415100").compareTo(WeddingPlanFactory.createPreview(WeddingType.GARDEN, 150).calculateEstimatedCost()));
        // Hotel banquet discount above 300 guests.
        assertEquals(0, new BigDecimal("5181000").compareTo(WeddingPlanFactory.createPreview(WeddingType.HOTEL, 400).calculateEstimatedCost()));
        // Traditional caterers bill at least 100 guests.
        assertEquals(0, new BigDecimal("1155000").compareTo(WeddingPlanFactory.createPreview(WeddingType.TRADITIONAL, 20).calculateEstimatedCost()));

        Set<BigDecimal> distinct = new HashSet<>();
        for (WeddingType t : WeddingType.values()) {
            distinct.add(WeddingPlanFactory.createPreview(t, 200).calculateEstimatedCost());
        }
        assertEquals(5, distinct.size());
    }

    @Test
    @DisplayName("Recommended checklist = common tasks + type-specific tasks, earliest first")
    void recommendedChecklist() {
        WeddingPlan standard = WeddingPlanFactory.createPreview(WeddingType.STANDARD, 10);
        WeddingPlan beach = WeddingPlanFactory.createPreview(WeddingType.BEACH, 10);
        assertEquals(10, standard.getRecommendedChecklist().size());
        assertEquals(15, beach.getRecommendedChecklist().size());
        boolean hasPermit = beach.getRecommendedChecklist().stream().anyMatch(t -> t.getName().startsWith("Obtain beach permit"));
        boolean hasInvites = beach.getRecommendedChecklist().stream().anyMatch(t -> t.getName().equals("Send invitations"));
        assertTrue(hasPermit);
        assertTrue(hasInvites);
        List<RecommendedTask> list = beach.getRecommendedChecklist();
        for (int i = 1; i < list.size(); i++) {
            assertTrue(list.get(i - 1).getDaysBeforeWedding() >= list.get(i).getDaysBeforeWedding());
        }
    }

    @Test
    @DisplayName("Countdown label and urgency")
    void countdown() {
        WeddingPlan plan = WeddingPlanFactory.createPreview(WeddingType.STANDARD, 1);
        plan.setWeddingDate("2030-06-15");
        assertEquals("In 10 days", plan.getCountdownLabel(LocalDate.parse("2030-06-05")));
        assertEquals("Tomorrow", plan.getCountdownLabel(LocalDate.parse("2030-06-14")));
        assertEquals("Today - the big day!", plan.getCountdownLabel(LocalDate.parse("2030-06-15")));
        assertEquals("3 days ago", plan.getCountdownLabel(LocalDate.parse("2030-06-18")));
        plan.setStatus(WeddingPlan.STATUS_CANCELLED);
        assertEquals("Cancelled", plan.getCountdownLabel(LocalDate.parse("2030-06-05")));
    }

    // ─── Plan validation ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Plan validation: date, budget, guests, name, type")
    void planValidation() {
        assertThrows(BusinessRuleException.class, () -> newPlan("C1", "Past Wedding", -3, "BEACH", 10));
        assertThrows(BusinessRuleException.class, () -> newPlan("C1", "Far Wedding", 366 * 11, "BEACH", 10));
        assertThrows(ValidationException.class, () ->
                planService.createPlan("C1", "Bad Date", "2027-02-30", "Colombo", "", "", "1000", 5, "BEACH"));
        assertThrows(ValidationException.class, () ->
                planService.createPlan("C1", "Negative Budget", inDays(50), "Colombo", "", "", "-5", 5, "BEACH"));
        assertThrows(ValidationException.class, () ->
                planService.createPlan("C1", "Text Budget", inDays(50), "Colombo", "", "", "lots", 5, "BEACH"));
        assertThrows(ValidationException.class, () -> newPlan("C1", "Too Many Guests", 50, "BEACH", 5001));
        assertThrows(ValidationException.class, () -> newPlan("C1", "Negative Guests", 50, "BEACH", -1));
        assertThrows(ValidationException.class, () -> newPlan("C1", "AB", 50, "BEACH", 10));
        assertThrows(ValidationException.class, () -> newPlan("C1", "  ", 50, "BEACH", 10));
        assertThrows(ValidationException.class, () -> newPlan("C1", "Unknown Type", 50, "SPACESHIP", 10));
        assertEquals(0, planService.count());
        assertNotNull(newPlan("C1", "Valid Wedding", 50, "BEACH", 5000));
    }

    @Test
    @DisplayName("Duplicate plan names are rejected per customer (cancelled plans excluded)")
    void duplicateNames() {
        WeddingPlan first = newPlan("C1", "Our Wedding", 100, "STANDARD", 50);
        assertThrows(BusinessRuleException.class, () -> newPlan("C1", "  our wedding ", 120, "BEACH", 50));
        assertNotNull(newPlan("C2", "Our Wedding", 100, "STANDARD", 50));
        planService.cancelPlan(first.getPlanId());
        assertNotNull(newPlan("C1", "Our Wedding", 130, "GARDEN", 50));
    }

    @Test
    @DisplayName("Type is stored in the file and changing it replaces the subclass")
    void typePersistsAndCanChange() {
        WeddingPlan plan = newPlan("C1", "Seaside Vows", 200, "BEACH", 120);
        String id = plan.getPlanId();
        assertTrue(new WeddingPlanRepository(planFile).findById(id).get() instanceof BeachWedding);

        WeddingPlan updated = planService.updatePlan(id, "Seaside Vows", inDays(200), "Colombo", "Classic",
                "notes", "2000000", 120, "HOTEL");
        assertTrue(updated instanceof HotelWedding);
        assertEquals(id, updated.getPlanId());
        assertEquals("C1", updated.getCustomerId());
        assertTrue(new WeddingPlanRepository(planFile).findById(id).get() instanceof HotelWedding);
        assertEquals(1, planService.count());
    }

    @Test
    @DisplayName("A failed update changes nothing")
    void failedUpdateDoesNotChangePlan() {
        WeddingPlan plan = newPlan("C1", "Original Name", 100, "STANDARD", 50);
        assertThrows(ValidationException.class, () -> planService.updatePlan(plan.getPlanId(), "New Name",
                inDays(101), "Kandy", "T", "D", "5000", 9999, "BEACH"));
        WeddingPlan stored = planService.getById(plan.getPlanId());
        assertEquals("Original Name", stored.getPlanName());
        assertEquals(50, stored.getGuestCount());
        assertEquals(WeddingType.STANDARD, stored.getType());
    }

    @Test
    @DisplayName("An unchanged wedding date can still be edited after it has passed")
    void unchangedPastDateIsAllowed() {
        WeddingPlan past = WeddingPlanFactory.create(WeddingType.STANDARD, "PLN-OLD", "C1", "Old Plan",
                inDays(-5), "Galle", "", "", new BigDecimal("1000"), WeddingPlan.STATUS_ACTIVE, "2026-01-01 10:00");
        new WeddingPlanRepository(planFile).save(past);
        buildServices();
        assertNotNull(planService.updatePlan("PLN-OLD", "Old Plan Renamed", inDays(-5), "Galle", "", "", "1000", 10, "STANDARD"));
        assertThrows(BusinessRuleException.class, () ->
                planService.updatePlan("PLN-OLD", "Old Plan Renamed", inDays(-2), "Galle", "", "", "1000", 10, "STANDARD"));
    }

    @Test
    @DisplayName("Old files without guest count / plan type still load")
    void legacyFilesLoad() {
        FileUtil.writeLines(planFile, List.of(
                "PLN-A|C1|Ten Columns|2030-01-01|V|T|D|1000|ACTIVE|2026-01-01 10:00",
                "PLN-B|C1|Eleven Columns|2030-01-02|V|T|D|1000|ACTIVE|2026-01-01 10:00|40",
                "PLN-C|C1|Twelve Columns|2030-01-03|V|T|D|1000|ACTIVE|2026-01-01 10:00|60|BEACH",
                "PLN-D|C1|Unknown Type|2030-01-04|V|T|D|1000|ACTIVE|2026-01-01 10:00|60|MARS"));
        WeddingPlanRepository repo = new WeddingPlanRepository(planFile);
        assertEquals(WeddingType.STANDARD, repo.findById("PLN-A").get().getType());
        assertEquals(0, repo.findById("PLN-A").get().getGuestCount());
        assertEquals(40, repo.findById("PLN-B").get().getGuestCount());
        assertEquals(WeddingType.STANDARD, repo.findById("PLN-B").get().getType());
        assertEquals(WeddingType.BEACH, repo.findById("PLN-C").get().getType());
        assertEquals(WeddingType.STANDARD, repo.findById("PLN-D").get().getType());
    }

    @Test
    @DisplayName("Search, filter and sort plans")
    void searchPlans() {
        WeddingPlan a = planService.createPlan("C1", "Alpha Beach", inDays(30), "Mirissa", "Boho", "sunset", "900000", 80, "BEACH");
        WeddingPlan b = planService.createPlan("C1", "Beta Garden", inDays(90), "Nuwara Eliya", "Floral", "", "3000000", 150, "GARDEN");
        WeddingPlan c = planService.createPlan("C1", "Gamma Hall", inDays(60), "Colombo", "Classic", "", "1500000", 200, "HOTEL");
        planService.cancelPlan(c.getPlanId());
        List<WeddingPlan> all = planService.getByCustomerId("C1");

        PlanSearchCriteria byWord = new PlanSearchCriteria();
        byWord.setKeyword("MIRISSA");
        assertEquals(1, planService.searchPlans(all, byWord).size());

        PlanSearchCriteria byStatus = new PlanSearchCriteria();
        byStatus.setStatus("cancelled");
        assertEquals(c.getPlanId(), planService.searchPlans(all, byStatus).get(0).getPlanId());

        PlanSearchCriteria byType = new PlanSearchCriteria();
        byType.setType("GARDEN");
        assertEquals(1, planService.searchPlans(all, byType).size());

        PlanSearchCriteria range = new PlanSearchCriteria();
        range.setFromDate(inDays(50));
        range.setToDate(inDays(100));
        assertEquals(2, planService.searchPlans(all, range).size());

        PlanSearchCriteria badDates = new PlanSearchCriteria();
        badDates.setFromDate("not-a-date");
        assertEquals(3, planService.searchPlans(all, badDates).size());

        PlanSearchCriteria soonest = new PlanSearchCriteria();
        assertEquals(a.getPlanId(), planService.searchPlans(all, soonest).get(0).getPlanId());
        PlanSearchCriteria latest = new PlanSearchCriteria();
        latest.setSortBy("date_desc");
        assertEquals(b.getPlanId(), planService.searchPlans(all, latest).get(0).getPlanId());
        PlanSearchCriteria budget = new PlanSearchCriteria();
        budget.setSortBy("budget_desc");
        assertEquals(b.getPlanId(), planService.searchPlans(all, budget).get(0).getPlanId());
        PlanSearchCriteria name = new PlanSearchCriteria();
        name.setSortBy("name_asc");
        assertEquals(a.getPlanId(), planService.searchPlans(all, name).get(0).getPlanId());
    }

    // ─── Checklist ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Overdue is calculated from the deadline and never saved")
    void overdueIsCalculated() {
        ChecklistTask late = checklistService.addTask("P1", "Late task", "", inDays(-4), "HIGH");
        ChecklistTask fine = checklistService.addTask("P1", "Future task", "", inDays(10), "LOW");
        assertEquals("OVERDUE", late.getDisplayStatus());
        assertEquals("PENDING", late.getStatus());
        assertEquals("PENDING", fine.getDisplayStatus());
        assertEquals("Overdue by 4 days", late.getDueLabel());

        List<ChecklistTask> listed = checklistService.getTasksByPlan("P1");
        assertEquals("Late task", listed.get(0).getTaskName());
        for (String line : FileUtil.readLines(taskFile)) {
            assertFalse(line.contains("|OVERDUE|"));
        }

        checklistService.markComplete(late.getTaskId());
        assertEquals("COMPLETED", checklistService.getTask(late.getTaskId()).getDisplayStatus());
        checklistService.reopen(late.getTaskId());
        assertEquals("OVERDUE", checklistService.getTask(late.getTaskId()).getDisplayStatus());
        assertThrows(BusinessRuleException.class, () -> checklistService.reopen(fine.getTaskId()));
    }

    @Test
    @DisplayName("A stored OVERDUE status from an older file is read back as PENDING")
    void legacyOverdueStatus() {
        FileUtil.writeLines(taskFile, List.of("TSK-X|P1|Old task|d|2020-01-01|OVERDUE|HIGH|2020-01-01 10:00"));
        ChecklistTask t = new ChecklistTaskRepository(taskFile).findById("TSK-X").get();
        assertEquals("PENDING", t.getStatus());
        assertEquals("OVERDUE", t.getDisplayStatus());
    }

    @Test
    @DisplayName("Task deadline cannot be after the wedding date; completion percent")
    void deadlineRuleAndProgress() {
        String wedding = inDays(60);
        assertThrows(BusinessRuleException.class, () ->
                checklistService.addTask("P1", "Too late", "", inDays(61), "HIGH", wedding));
        assertThrows(ValidationException.class, () ->
                checklistService.addTask("P1", "Bad date", "", "2027-02-30", "HIGH", wedding));
        ChecklistTask a = checklistService.addTask("P1", "On time", "", inDays(60), "HIGH", wedding);
        checklistService.addTask("P1", "Another", "", inDays(20), "LOW", wedding);
        assertEquals(0, checklistService.getCompletionPercent("P1"));
        checklistService.markComplete(a.getTaskId());
        assertEquals(50, checklistService.getCompletionPercent("P1"));
    }

    @Test
    @DisplayName("Template adds the type's tasks once and fits due dates before the wedding")
    void templateGeneration() {
        WeddingPlan beach = newPlan("C1", "Template Beach", 400, "BEACH", 100);
        int added = checklistService.generateFromTemplate(beach);
        assertEquals(beach.getRecommendedTaskCount(), added);
        assertEquals(added, checklistService.getTasksByPlan(beach.getPlanId()).size());
        assertEquals(0, checklistService.generateFromTemplate(beach));

        // A wedding only 30 days away: due dates are compressed between today and the wedding.
        WeddingPlan soon = newPlan("C2", "Template Soon", 30, "TRADITIONAL", 100);
        checklistService.generateFromTemplate(soon);
        LocalDate today = LocalDate.now();
        LocalDate weddingDate = LocalDate.parse(soon.getWeddingDate());
        for (ChecklistTask t : checklistService.getTasksByPlan(soon.getPlanId())) {
            LocalDate due = LocalDate.parse(t.getDeadline());
            assertFalse(due.isBefore(today));
            assertFalse(due.isAfter(weddingDate));
        }
        // Different types produce different task lists.
        assertTrue(checklistService.getTasksByPlan(soon.getPlanId()).stream()
                .anyMatch(t -> t.getTaskName().contains("nekath")));
    }

    // ─── Budget ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Budget totals, category totals and over-budget flags")
    void budgetTotals() {
        budgetService.addItem("P1", "VENUE", "Hall deposit", "500000", "200000", "");
        budgetService.addItem("P1", "VENUE", "Hall balance", "300000", "0", "");
        budgetService.addItem("P1", "CATERING", "Dinner", "400000", "450000", "over");
        List<BudgetItem> items = budgetService.getItemsByPlan("P1");
        BudgetSummary s = budgetService.buildSummary(new BigDecimal("1000000"), items);

        assertEquals(0, new BigDecimal("1200000").compareTo(s.getTotalPlanned()));
        assertEquals(0, new BigDecimal("650000").compareTo(s.getTotalSpent()));
        assertEquals(2, s.getCategories().size());
        assertEquals(0, new BigDecimal("-200000").compareTo(s.getUnallocated()));
        assertEquals(0, new BigDecimal("350000").compareTo(s.getRemaining()));
        assertTrue(s.isAllocationOverBudget());
        assertFalse(s.isSpendingOverBudget());
        assertEquals(65, s.getSpentPercent());
        assertTrue(s.getCategories().get(1).isOverBudget());
        assertTrue(items.stream().anyMatch(BudgetItem::isOverBudget));
    }

    @Test
    @DisplayName("Budget validation and plan ownership")
    void budgetValidation() {
        assertThrows(ValidationException.class, () -> budgetService.addItem("P1", "VENUE", "", "100", "0", ""));
        assertThrows(ValidationException.class, () -> budgetService.addItem("P1", "VENUE", "X", "", "0", ""));
        assertThrows(ValidationException.class, () -> budgetService.addItem("P1", "VENUE", "X", "-1", "0", ""));
        assertThrows(ValidationException.class, () -> budgetService.addItem("P1", "VENUE", "X", "abc", "0", ""));
        assertThrows(ValidationException.class, () -> budgetService.addItem("P1", "VENUE", "X", "100", "-5", ""));
        assertThrows(ValidationException.class, () -> budgetService.addItem("P1", "NOPE", "X", "100", "0", ""));
        BudgetItem item = budgetService.addItem("P1", "VENUE", "X", "100", "", "");
        assertEquals(0, BigDecimal.ZERO.compareTo(item.getSpentAmount()));
        assertThrows(BusinessRuleException.class, () -> budgetService.updateItem("OTHER", item.getItemId(), "VENUE", "Y", "5", "0", ""));
        assertThrows(BusinessRuleException.class, () -> budgetService.deleteItem("OTHER", item.getItemId()));
        budgetService.updateItem("P1", item.getItemId(), "DECOR", "Flowers", "250", "50", "n");
        assertEquals(BudgetCategory.DECOR, new BudgetItemRepository(budgetFile).findById(item.getItemId()).get().getCategory());
        assertTrue(budgetService.deleteItem("P1", item.getItemId()));
    }

    // ─── Guests ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Guest headcounts, RSVP changes and validation")
    void guestStatsAndRsvp() {
        Guest g1 = guestService.addGuest("P1", "Perera family", "FAMILY", "0771234567", "5", "CONFIRMED", "");
        guestService.addGuest("P1", "Nimal", "FRIENDS", "", "2", "INVITED", "");
        Guest g3 = guestService.addGuest("P1", "Dilani", "FRIENDS", "", "1", "DECLINED", "");
        GuestStats stats = guestService.buildStats(guestService.getGuestsByPlan("P1"), 6);

        assertEquals(3, stats.getEntries());
        assertEquals(5, stats.getConfirmedHeadcount());
        assertEquals(2, stats.getPendingHeadcount());
        assertEquals(1, stats.getDeclinedHeadcount());
        assertEquals(8, stats.getTotalHeadcount());
        assertEquals(7, stats.getAttendingEstimate());
        assertEquals(75, stats.getResponseRate());
        assertTrue(stats.isOverExpected());

        guestService.setRsvp("P1", g3.getGuestId(), "confirmed");
        assertEquals(6, guestService.buildStats(guestService.getGuestsByPlan("P1"), 0).getConfirmedHeadcount());
        assertFalse(guestService.buildStats(guestService.getGuestsByPlan("P1"), 0).isOverExpected());

        assertThrows(ValidationException.class, () -> guestService.addGuest("P1", "A", "FAMILY", "", "1", "INVITED", ""));
        assertThrows(ValidationException.class, () -> guestService.addGuest("P1", "Valid", "FAMILY", "", "11", "INVITED", ""));
        assertThrows(ValidationException.class, () -> guestService.addGuest("P1", "Valid", "FAMILY", "", "0", "INVITED", ""));
        assertThrows(ValidationException.class, () -> guestService.addGuest("P1", "Valid", "FAMILY", "", "x", "INVITED", ""));
        assertThrows(ValidationException.class, () -> guestService.addGuest("P1", "Valid", "FAMILY", "", "1", "MAYBE", ""));
        assertThrows(ValidationException.class, () -> guestService.addGuest("P1", "Valid", "ALIENS", "", "1", "INVITED", ""));
        assertThrows(BusinessRuleException.class, () -> guestService.setRsvp("OTHER", g1.getGuestId(), "DECLINED"));
        assertThrows(BusinessRuleException.class, () -> guestService.deleteGuest("OTHER", g1.getGuestId()));
        assertTrue(guestService.deleteGuest("P1", g1.getGuestId()));
    }

    // ─── Report, export, timeline, cascade ───────────────────────────────────────

    @Test
    @DisplayName("Summary, CSV export (formula guard), polymorphic descriptions and cascade delete")
    void reportExportAndCascade() {
        WeddingPlan plan = newPlan("C1", "Report Wedding", 120, "GARDEN", 100);
        String id = plan.getPlanId();
        checklistService.addTask(id, "Task one", "", inDays(10), "HIGH", plan.getWeddingDate());
        ChecklistTask done = checklistService.addTask(id, "Task two", "", inDays(20), "LOW", plan.getWeddingDate());
        checklistService.markComplete(done.getTaskId());
        budgetService.addItem(id, "DECOR", "Flowers", "1000", "200", "");
        guestService.addGuest(id, "=HYPERLINK(\"http://evil\")", "OTHER", "", "1", "INVITED", "");

        PlanSummary summary = reportService.buildSummary(id);
        assertEquals(2, summary.getTotalTasks());
        assertEquals(1, summary.getCompletedTasks());
        assertEquals(50, summary.getProgressPercent());
        assertEquals(1, summary.getBudgetItems().size());
        assertEquals(1, summary.getGuests().size());

        String csv = reportService.exportCsv(id);
        assertTrue(csv.startsWith("\uFEFF"));
        assertTrue(csv.contains("WEDDING PLAN SUMMARY"));
        assertTrue(csv.contains("CHECKLIST"));
        assertTrue(csv.contains("BUDGET"));
        assertTrue(csv.contains("GUESTS"));
        assertTrue(csv.contains("Garden Wedding"));
        assertTrue(csv.contains("'=HYPERLINK"));
        assertFalse(csv.contains(",=HYPERLINK"));

        List<String> lines = reportService.describeAllItems(id);
        assertEquals(4, lines.size());
        assertTrue(lines.stream().anyMatch(l -> l.startsWith("CHECKLIST_TASK: ")));
        assertTrue(lines.stream().anyMatch(l -> l.startsWith("BUDGET_ITEM: ")));
        assertTrue(lines.stream().anyMatch(l -> l.startsWith("GUEST: ")));

        assertTrue(reportService.deletePlanCascade(id));
        assertEquals(0, new ChecklistTaskRepository(taskFile).count());
        assertEquals(0, new BudgetItemRepository(budgetFile).count());
        assertEquals(0, new GuestRepository(guestFile).count());
        assertEquals(0, new WeddingPlanRepository(planFile).count());
    }

    @Test
    @DisplayName("Timeline is grouped by month and ends with the wedding day")
    void timeline() {
        WeddingPlan plan = newPlan("C1", "Timeline Wedding", 90, "STANDARD", 50);
        checklistService.addTask(plan.getPlanId(), "Early", "", inDays(5), "HIGH", plan.getWeddingDate());
        checklistService.addTask(plan.getPlanId(), "Later", "", inDays(60), "HIGH", plan.getWeddingDate());
        Map<String, List<TimelineEvent>> months = reportService.buildTimeline(plan.getPlanId());

        List<TimelineEvent> flat = new ArrayList<>();
        months.values().forEach(flat::addAll);
        assertEquals(3, flat.size());
        assertEquals("Early", flat.get(0).getTitle());
        assertTrue(flat.get(2).isWedding());
        assertTrue(months.size() >= 2);
    }

    @Test
    @DisplayName("Plan status changes and guest-count sync")
    void statusAndGuestSync() {
        WeddingPlan plan = newPlan("C1", "Status Wedding", 90, "STANDARD", 50);
        planService.updateGuestCount(plan.getPlanId(), 75);
        assertEquals(75, planService.getById(plan.getPlanId()).getGuestCount());
        assertThrows(ValidationException.class, () -> planService.updateGuestCount(plan.getPlanId(), 6000));
        planService.completePlan(plan.getPlanId());
        assertThrows(BusinessRuleException.class, () -> planService.cancelPlan(plan.getPlanId()));
        assertThrows(BusinessRuleException.class, () -> planService.updateGuestCount(plan.getPlanId(), 10));
    }
}
