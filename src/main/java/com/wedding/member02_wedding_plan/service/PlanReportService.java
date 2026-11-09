package com.wedding.member02_wedding_plan.service;

import com.wedding.common.utils.FileUtil;
import com.wedding.common.exceptions.BusinessRuleException;
import com.wedding.member04_booking.repository.BookingRepository;
import com.wedding.member02_wedding_plan.model.BudgetItem;
import com.wedding.member02_wedding_plan.model.BudgetSummary;
import com.wedding.member02_wedding_plan.model.ChecklistTask;
import com.wedding.member02_wedding_plan.model.Guest;
import com.wedding.member02_wedding_plan.model.GuestStats;
import com.wedding.member02_wedding_plan.model.PlanItem;
import com.wedding.member02_wedding_plan.model.PlanSummary;
import com.wedding.member02_wedding_plan.model.TimelineEvent;
import com.wedding.member02_wedding_plan.model.WeddingPlan;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Facade over the four Member 02 services. It builds the read-only views (summary,
 * timeline, CSV export) and removes a plan together with everything that belongs to it.
 *
 * @author Member 02
 */
public class PlanReportService {

    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);

    private final WeddingPlanService planService;
    private final ChecklistService checklistService;
    private final BudgetService budgetService;
    private final GuestService guestService;
    private final Predicate<String> hasLinkedBookings;

    public PlanReportService(WeddingPlanService planService, ChecklistService checklistService,
                             BudgetService budgetService, GuestService guestService) {
        this(planService, checklistService, budgetService, guestService,
                id -> !new BookingRepository().findByPlanId(id).isEmpty());
    }

    /** Additional constructor allows isolated tests without touching the real bookings.csv. */
    public PlanReportService(WeddingPlanService planService, ChecklistService checklistService,
                             BudgetService budgetService, GuestService guestService,
                             Predicate<String> hasLinkedBookings) {
        this.planService = planService;
        this.checklistService = checklistService;
        this.budgetService = budgetService;
        this.guestService = guestService;
        this.hasLinkedBookings = java.util.Objects.requireNonNull(hasLinkedBookings);
    }

    // ─── Summary ─────────────────────────────────────────────────────────────────

    /** Collects the plan, checklist, budget and guest data into one snapshot. */
    public PlanSummary buildSummary(String planId) {
        WeddingPlan plan = planService.getById(planId);
        List<ChecklistTask> tasks = checklistService.getTasksByPlan(planId);
        List<BudgetItem> items = budgetService.getItemsByPlan(planId);
        List<Guest> guests = guestService.getGuestsByPlan(planId);
        BudgetSummary budget = budgetService.buildSummary(plan.getTotalBudget(), items);
        GuestStats stats = guestService.buildStats(guests, plan.getGuestCount());
        return new PlanSummary(plan, tasks, items, budget, guests, stats);
    }

    // ─── Timeline ────────────────────────────────────────────────────────────────

    /** Checklist deadlines and the wedding day in date order, grouped by month. */
    public Map<String, List<TimelineEvent>> buildTimeline(String planId) {
        WeddingPlan plan = planService.getById(planId);
        List<TimelineEvent> events = new ArrayList<>();
        for (ChecklistTask task : checklistService.getTasksByPlan(planId)) {
            events.add(TimelineEvent.forTask(task));
        }
        events.add(TimelineEvent.forWedding(plan));
        events.sort(Comparator.comparing(TimelineEvent::getDate)
                .thenComparing(TimelineEvent::isWedding));

        Map<String, List<TimelineEvent>> byMonth = new LinkedHashMap<>();
        for (TimelineEvent event : events) {
            byMonth.computeIfAbsent(monthLabel(event.getDate()), k -> new ArrayList<>()).add(event);
        }
        return byMonth;
    }

    private String monthLabel(String date) {
        try {
            return LocalDate.parse(date).format(MONTH_FMT);
        } catch (Exception e) {
            return "No date";
        }
    }

    // ─── CSV export ──────────────────────────────────────────────────────────────

    /**
     * Builds a single CSV document with the plan details, checklist, budget and guest list.
     * Text cells that could be read as a spreadsheet formula are prefixed with an apostrophe.
     */
    public String exportCsv(String planId) {
        PlanSummary s = buildSummary(planId);
        WeddingPlan plan = s.getPlan();
        StringBuilder out = new StringBuilder("\uFEFF");

        row(out, "WEDDING PLAN SUMMARY");
        row(out, "Plan ID", plan.getPlanId());
        row(out, "Name", plan.getPlanName());
        row(out, "Type", plan.getTypeLabel());
        row(out, "Status", plan.getStatus());
        row(out, "Wedding date", plan.getWeddingDate());
        row(out, "Countdown", plan.getCountdownLabel());
        row(out, "Venue", plan.getVenue());
        row(out, "Theme", plan.getTheme());
        row(out, "Expected guests", plan.getGuestCount());
        row(out, "Total budget (LKR)", plan.getTotalBudget());
        row(out, "Estimated cost (LKR)", plan.getEstimatedCost());
        row(out, "Checklist progress (%)", s.getProgressPercent());
        row(out, "Notes", plan.getDescription());
        row(out);

        row(out, "CHECKLIST");
        row(out, "Task", "Description", "Deadline", "Priority", "Status");
        for (ChecklistTask t : s.getTasks()) {
            row(out, t.getTaskName(), t.getDescription(), t.getDeadline(), t.getPriority(), t.getDisplayStatus());
        }
        row(out);

        row(out, "BUDGET");
        row(out, "Category", "Item", "Planned (LKR)", "Spent (LKR)", "Note");
        for (BudgetItem b : s.getBudgetItems()) {
            row(out, b.getCategory().getLabel(), b.getItemName(), b.getPlannedAmount(), b.getSpentAmount(), b.getNote());
        }
        row(out, "TOTAL", "", s.getBudget().getTotalPlanned(), s.getBudget().getTotalSpent(), "");
        row(out);

        row(out, "GUESTS");
        row(out, "Name", "Group", "Contact", "Party size", "RSVP", "Note");
        for (Guest g : s.getGuests()) {
            row(out, g.getGuestName(), g.getGroup().getLabel(), g.getContact(), g.getPartySize(),
                    g.getRsvpStatus(), g.getNote());
        }
        row(out, "Confirmed headcount", s.getGuestStats().getConfirmedHeadcount());
        row(out, "Awaiting reply headcount", s.getGuestStats().getPendingHeadcount());
        row(out, "Declined headcount", s.getGuestStats().getDeclinedHeadcount());
        return out.toString();
    }

    /**
     * Polymorphism: every PlanItem describes itself through getItemType(), getDisplayTitle()
     * and getDetailLine(); this method needs no knowledge of the concrete subclass.
     */
    public List<String> describeAllItems(String planId) {
        List<PlanItem> all = new ArrayList<>();
        all.addAll(checklistService.getTasksByPlan(planId));
        all.addAll(budgetService.getItemsByPlan(planId));
        all.addAll(guestService.getGuestsByPlan(planId));
        List<String> lines = new ArrayList<>();
        for (PlanItem item : all) {
            lines.add(item.getItemType() + ": " + item.getDisplayTitle() + " (" + item.getDetailLine() + ")");
        }
        return lines;
    }

    private void row(StringBuilder out, Object... cells) {
        for (int i = 0; i < cells.length; i++) {
            if (i > 0) out.append(',');
            out.append(cell(cells[i]));
        }
        out.append("\r\n");
    }

    private String cell(Object value) {
        if (value == null) return "";
        if (value instanceof Number) return value.toString();
        String text = value.toString();
        if (!text.isEmpty() && "=+-@\t\r".indexOf(text.charAt(0)) >= 0) {
            text = "'" + text;
        }
        return FileUtil.escape(text);
    }

    // ─── Delete ──────────────────────────────────────────────────────────────────

    /**
     * Permanently deletes a plan together with its checklist, budget items and guests.
     *
     * @return true if the plan existed and was deleted
     */
    public boolean deletePlanCascade(String planId) {
        // Check before deleting children, so booking references never become orphaned.
        planService.getById(planId);
        if (hasLinkedBookings.test(planId)) {
            throw new BusinessRuleException("This wedding plan has linked vendor bookings. "
                    + "Please resolve its bookings before deleting the plan.");
        }
        checklistService.deleteByPlan(planId);
        budgetService.deleteByPlan(planId);
        guestService.deleteByPlan(planId);
        return planService.deletePlan(planId);
    }
}
