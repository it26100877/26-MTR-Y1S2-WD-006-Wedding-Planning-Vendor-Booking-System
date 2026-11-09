package com.wedding.member02_wedding_plan.service;

import com.wedding.member01_customer_admin.model.Customer;
import com.wedding.member01_customer_admin.service.CustomerService;
import com.wedding.member02_wedding_plan.model.AdminWeddingPlanRow;
import com.wedding.member02_wedding_plan.model.ChecklistTask;
import com.wedding.member02_wedding_plan.model.WeddingPlan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Member 02: read-only aggregate service for admin wedding overview.
 * It reuses existing services and file repositories; it never stores duplicate data.
 */
public final class WeddingPlanAdminService {
    private final WeddingPlanService planService;
    private final ChecklistService checklistService;
    private final CustomerService customerService;

    public WeddingPlanAdminService(WeddingPlanService planService, ChecklistService checklistService,
                                   CustomerService customerService) {
        this.planService = planService;
        this.checklistService = checklistService;
        this.customerService = customerService;
    }

    public DashboardSnapshot getDashboard(String query, String status, String type, String sort) {
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        String statusFilter = status == null ? "ALL" : status.trim().toUpperCase(Locale.ROOT);
        String typeFilter = type == null ? "ALL" : type.trim().toUpperCase(Locale.ROOT);
        Map<String, Customer> customers = new HashMap<>();
        for (Customer c : customerService.getAllCustomers()) {
            customers.put(c.getUserId(), c);
        }

        List<AdminWeddingPlanRow> allRows = new ArrayList<>();
        long active = 0, completed = 0, cancelled = 0;
        int allTasks = 0, doneTasks = 0;
        BigDecimal plannedBudget = BigDecimal.ZERO;
        Set<String> uniqueCustomers = new HashSet<>();
        for (WeddingPlan plan : planService.getAllPlans()) {
            if (WeddingPlan.STATUS_ACTIVE.equals(plan.getStatus())) active++;
            if (WeddingPlan.STATUS_COMPLETED.equals(plan.getStatus())) completed++;
            if (WeddingPlan.STATUS_CANCELLED.equals(plan.getStatus())) cancelled++;
            if (!WeddingPlan.STATUS_CANCELLED.equals(plan.getStatus()) && plan.getTotalBudget() != null) {
                plannedBudget = plannedBudget.add(plan.getTotalBudget());
            }
            uniqueCustomers.add(plan.getCustomerId());
            List<ChecklistTask> tasks = checklistService.getTasksByPlan(plan.getPlanId());
            int done = (int) tasks.stream().filter(ChecklistTask::isCompleted).count();
            allTasks += tasks.size();
            doneTasks += done;
            Customer owner = customers.get(plan.getCustomerId());
            allRows.add(new AdminWeddingPlanRow(plan,
                    owner == null ? "Unknown customer" : owner.getName(),
                    owner == null ? "Not available" : owner.getEmail(), tasks.size(), done));
        }

        List<AdminWeddingPlanRow> rows = allRows.stream()
                .filter(r -> "ALL".equals(statusFilter) || r.getPlan().getStatus().equalsIgnoreCase(statusFilter))
                .filter(r -> "ALL".equals(typeFilter) || r.getPlan().getType().getCode().equalsIgnoreCase(typeFilter))
                .filter(r -> matches(r, q))
                .collect(Collectors.toCollection(ArrayList::new));
        Comparator<AdminWeddingPlanRow> byDate = Comparator.comparing(r -> r.getPlan().getWeddingDate());
        if ("latest".equals(sort)) rows.sort(byDate.reversed());
        else if ("name".equals(sort)) rows.sort(Comparator.comparing(r -> r.getPlan().getPlanName().toLowerCase(Locale.ROOT)));
        else if ("budget".equals(sort)) rows.sort(Comparator.comparing(
                (AdminWeddingPlanRow r) -> r.getPlan().getTotalBudget() == null ? BigDecimal.ZERO : r.getPlan().getTotalBudget()).reversed());
        else rows.sort(byDate);

        List<AdminWeddingPlanRow> upcoming = allRows.stream()
                .filter(r -> WeddingPlan.STATUS_ACTIVE.equals(r.getPlan().getStatus()))
                .filter(r -> !r.getPlan().getWeddingDate().isEmpty() &&
                        r.getPlan().getWeddingDate().compareTo(LocalDate.now().toString()) >= 0)
                .sorted(byDate).limit(3).collect(Collectors.toList());

        return new DashboardSnapshot(rows, upcoming, allRows.size(), active, completed, cancelled,
                uniqueCustomers.size(), plannedBudget, allTasks, doneTasks);
    }

    private boolean matches(AdminWeddingPlanRow row, String q) {
        if (q.isEmpty()) return true;
        WeddingPlan p = row.getPlan();
        return contains(p.getPlanName(), q) || contains(p.getPlanId(), q)
                || contains(p.getVenue(), q) || contains(p.getTheme(), q)
                || contains(p.getCustomerId(), q) || contains(row.getCustomerName(), q)
                || contains(row.getCustomerEmail(), q);
    }

    private boolean contains(String source, String query) {
        return source != null && source.toLowerCase(Locale.ROOT).contains(query);
    }

    /** Immutable-ish snapshot with JavaBean getters for JSTL access. */
    public static final class DashboardSnapshot {
        private final List<AdminWeddingPlanRow> rows;
        private final List<AdminWeddingPlanRow> upcoming;
        private final int totalCount;
        private final long activeCount, completedCount, cancelledCount;
        private final int customerCount;
        private final BigDecimal totalBudget;
        private final int taskCount, completedTaskCount;

        private DashboardSnapshot(List<AdminWeddingPlanRow> rows, List<AdminWeddingPlanRow> upcoming,
                                  int totalCount, long activeCount, long completedCount, long cancelledCount,
                                  int customerCount, BigDecimal totalBudget, int taskCount, int completedTaskCount) {
            this.rows = List.copyOf(rows);
            this.upcoming = List.copyOf(upcoming);
            this.totalCount = totalCount;
            this.activeCount = activeCount;
            this.completedCount = completedCount;
            this.cancelledCount = cancelledCount;
            this.customerCount = customerCount;
            this.totalBudget = totalBudget;
            this.taskCount = taskCount;
            this.completedTaskCount = completedTaskCount;
        }
        public List<AdminWeddingPlanRow> getRows() { return rows; }
        public List<AdminWeddingPlanRow> getUpcoming() { return upcoming; }
        public int getTotalCount() { return totalCount; }
        public long getActiveCount() { return activeCount; }
        public long getCompletedCount() { return completedCount; }
        public long getCancelledCount() { return cancelledCount; }
        public int getCustomerCount() { return customerCount; }
        public BigDecimal getTotalBudget() { return totalBudget; }
        public int getTaskCount() { return taskCount; }
        public int getCompletedTaskCount() { return completedTaskCount; }
        public int getOverallProgress() {
            return taskCount == 0 ? 0 : (int) Math.round(completedTaskCount * 100.0 / taskCount);
        }
    }
}
