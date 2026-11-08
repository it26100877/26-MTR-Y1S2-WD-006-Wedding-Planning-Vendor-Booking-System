package com.wedding.member02_wedding_plan.controller;

import com.wedding.common.exceptions.AppException;
import com.wedding.common.model.User;
import com.wedding.member02_wedding_plan.model.ChecklistTask;
import com.wedding.member02_wedding_plan.model.PlanSearchCriteria;
import com.wedding.member02_wedding_plan.model.PlanSummary;
import com.wedding.member02_wedding_plan.model.WeddingPlan;
import com.wedding.member02_wedding_plan.model.WeddingPlanFactory;
import com.wedding.member02_wedding_plan.model.WeddingType;
import com.wedding.member02_wedding_plan.service.ChecklistService;
import com.wedding.member02_wedding_plan.service.PlanReportService;
import com.wedding.member02_wedding_plan.service.WeddingPlanService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller managing Wedding Plans, the pre-wedding checklist, the planning timeline,
 * the printable summary and the CSV export. Budget and guests are handled by
 * PlanBudgetController and PlanGuestController.
 *
 * @author Member 02 - Wedding Plan and Checklist Management
 */
@Controller
public class WeddingPlanController extends PlanControllerSupport {

    private final ChecklistService checklistService;
    private final PlanReportService reportService;

    public WeddingPlanController(WeddingPlanService planService, ChecklistService checklistService,
                                 PlanReportService reportService) {
        super(planService);
        this.checklistService = checklistService;
        this.reportService = reportService;
    }

    // ─── List Plans (search, filter, sort) ───────────────────────────────────────
    @GetMapping("/plans")
    public String listPlans(@RequestParam(value = "q", required = false) String q,
                            @RequestParam(value = "status", required = false) String status,
                            @RequestParam(value = "type", required = false) String type,
                            @RequestParam(value = "from", required = false) String from,
                            @RequestParam(value = "to", required = false) String to,
                            @RequestParam(value = "sort", required = false) String sort,
                            HttpSession httpSession, Model model) {
        User user = currentUser(httpSession);
        if (user == null) return "redirect:/login";

        List<WeddingPlan> allPlans = isAdmin(user)
                ? planService.getAllPlans()
                : planService.getByCustomerId(user.getUserId());

        PlanSearchCriteria criteria = new PlanSearchCriteria();
        criteria.setKeyword(q);
        criteria.setStatus(status);
        criteria.setType(type);
        criteria.setFromDate(from);
        criteria.setToDate(to);
        criteria.setSortBy(sort);
        List<WeddingPlan> visible = planService.searchPlans(allPlans, criteria);

        // Summary data for the dashboard (always based on all of the user's plans).
        long activePlans = allPlans.stream().filter(p -> WeddingPlan.STATUS_ACTIVE.equals(p.getStatus())).count();
        long completedPlans = allPlans.stream().filter(p -> WeddingPlan.STATUS_COMPLETED.equals(p.getStatus())).count();
        BigDecimal plannedBudget = allPlans.stream().filter(p -> !WeddingPlan.STATUS_CANCELLED.equals(p.getStatus()))
                .map(WeddingPlan::getTotalBudget).filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Integer> completion = new HashMap<>();
        int allTasks = 0, completedTasks = 0;
        int attentionTaskCount = 0, lateTaskCount = 0;
        String firstAttentionPlanId = null;
        for (WeddingPlan item : allPlans) {
            List<ChecklistTask> planTasks = checklistService.getTasksByPlan(item.getPlanId());
            allTasks += planTasks.size();
            completedTasks += (int) planTasks.stream().filter(ChecklistTask::isCompleted).count();
            completion.put(item.getPlanId(), checklistService.getCompletionPercent(item.getPlanId()));
            if (WeddingPlan.STATUS_ACTIVE.equals(item.getStatus())) {
                for (ChecklistTask task : planTasks) {
                    if (task.isReminderDue()) {
                        attentionTaskCount++;
                        if (ChecklistTask.REMINDER_OVERDUE.equals(task.getReminderCategory())) lateTaskCount++;
                        if (firstAttentionPlanId == null) firstAttentionPlanId = item.getPlanId();
                    }
                }
            }
        }

        // The nearest upcoming active wedding, shown as a countdown banner.
        WeddingPlan next = allPlans.stream()
                .filter(p -> WeddingPlan.STATUS_ACTIVE.equals(p.getStatus()) && p.getDaysUntilWedding() >= 0)
                .min((a, b) -> a.getWeddingDate().compareTo(b.getWeddingDate()))
                .orElse(null);

        model.addAttribute("activeCount", activePlans);
        model.addAttribute("completedCount", completedPlans);
        model.addAttribute("budgetTotal", plannedBudget);
        model.addAttribute("taskCount", allTasks);
        model.addAttribute("completedTaskCount", completedTasks);
        model.addAttribute("overallProgress", allTasks == 0 ? 0 : (int) Math.round(completedTasks * 100.0 / allTasks));
        model.addAttribute("attentionTaskCount", attentionTaskCount);
        model.addAttribute("lateTaskCount", lateTaskCount);
        model.addAttribute("firstAttentionPlanId", firstAttentionPlanId);
        model.addAttribute("completionByPlan", completion);
        model.addAttribute("nextPlan", next);
        model.addAttribute("plans", visible);
        model.addAttribute("allPlanCount", allPlans.size());
        model.addAttribute("criteria", criteria);
        model.addAttribute("types", WeddingType.values());
        model.addAttribute("pageTitle", "Wedding Plans - Éclat");
        model.addAttribute("activePage", "plans");
        return "member02/plans";
    }

    // ─── Create Plan ─────────────────────────────────────────────────────────────
    @GetMapping("/plans/new")
    public String showCreateForm(HttpSession httpSession, Model model) {
        User user = currentUser(httpSession);
        if (!isCustomer(user)) return "redirect:/login";

        model.addAttribute("isEdit", false);
        model.addAttribute("minDate", java.time.LocalDate.now().plusDays(1).toString());
        model.addAttribute("types", WeddingType.values());
        model.addAttribute("pageTitle", "New Wedding Plan - Éclat");
        model.addAttribute("activePage", "plans");
        return "member02/plan-form";
    }

    @PostMapping("/plans/new")
    public String createPlan(@RequestParam("planName") String planName,
                             @RequestParam("weddingDate") String weddingDate,
                             @RequestParam("venue") String venue,
                             @RequestParam(value = "theme", defaultValue = "") String theme,
                             @RequestParam(value = "description", defaultValue = "") String description,
                             @RequestParam(value = "totalBudget", defaultValue = "0") String totalBudget,
                             @RequestParam(value = "guestCount", defaultValue = "0") String guestCount,
                             @RequestParam(value = "planType", defaultValue = "STANDARD") String planType,
                             @RequestParam(value = "autoChecklist", required = false) String autoChecklist,
                             HttpSession httpSession,
                             RedirectAttributes redirectAttributes) {
        User user = currentUser(httpSession);
        if (!isCustomer(user)) return "redirect:/login";

        try {
            WeddingPlan plan = planService.createPlan(user.getUserId(), planName, weddingDate, venue, theme,
                    description, totalBudget, parseInt(guestCount, "Guest count", 0), planType);
            int added = autoChecklist != null ? checklistService.generateFromTemplate(plan) : 0;
            String message = "Wedding Plan '" + esc(plan.getPlanName()) + "' created successfully!";
            if (added > 0) message += " " + added + " recommended tasks were added to your checklist.";
            redirectAttributes.addFlashAttribute("successMessage", message);
            return "redirect:/plans";
        } catch (AppException e) {
            redirectAttributes.addFlashAttribute("errorMessage", esc(e.getMessage()));
            redirectAttributes.addFlashAttribute("form", formValues(planName, weddingDate, venue, theme,
                    description, totalBudget, guestCount, planType, autoChecklist));
            return "redirect:/plans/new";
        }
    }

    // ─── Edit Plan ───────────────────────────────────────────────────────────────
    @GetMapping("/plans/edit/{id}")
    public String showEditForm(@PathVariable("id") String id, HttpSession httpSession,
                               Model model, RedirectAttributes redirectAttributes) {
        User user = currentUser(httpSession);
        if (user == null) return "redirect:/login";

        Optional<WeddingPlan> plan = findAccessiblePlan(user, id);
        if (plan.isEmpty()) return "redirect:/plans";
        if (!WeddingPlan.STATUS_ACTIVE.equals(plan.get().getStatus())) {
            redirectAttributes.addFlashAttribute("warningMessage", "Only active wedding plans can be edited.");
            return "redirect:/plans";
        }
        model.addAttribute("plan", plan.get());
        model.addAttribute("isEdit", true);
        model.addAttribute("types", WeddingType.values());
        model.addAttribute("pageTitle", "Edit Wedding Plan - Éclat");
        model.addAttribute("activePage", "plans");
        return "member02/plan-form";
    }

    @PostMapping("/plans/edit/{id}")
    public String updatePlan(@PathVariable("id") String id,
                             @RequestParam("planName") String planName,
                             @RequestParam("weddingDate") String weddingDate,
                             @RequestParam("venue") String venue,
                             @RequestParam(value = "theme", defaultValue = "") String theme,
                             @RequestParam(value = "description", defaultValue = "") String description,
                             @RequestParam(value = "totalBudget", defaultValue = "0") String totalBudget,
                             @RequestParam(value = "guestCount", defaultValue = "0") String guestCount,
                             @RequestParam(value = "planType", defaultValue = "STANDARD") String planType,
                             HttpSession httpSession, RedirectAttributes redirectAttributes) {
        User user = currentUser(httpSession);
        if (user == null) return "redirect:/login";
        if (findAccessiblePlan(user, id).isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot edit this wedding plan.");
            return "redirect:/plans";
        }
        try {
            planService.updatePlanWithChecklistValidation(id, planName, weddingDate, venue, theme,
                    description, totalBudget, parseInt(guestCount, "Guest count", 0),
                    planType, checklistService);
            redirectAttributes.addFlashAttribute("successMessage", "Wedding Plan updated successfully!");
            return "redirect:/plans";
        } catch (AppException e) {
            redirectAttributes.addFlashAttribute("errorMessage", esc(e.getMessage()));
            redirectAttributes.addFlashAttribute("form", formValues(planName, weddingDate, venue, theme,
                    description, totalBudget, guestCount, planType, null));
            return "redirect:/plans/edit/" + id;
        }
    }

    // ─── Cancel / Complete / Delete Plan ─────────────────────────────────────────
    @PostMapping("/plans/cancel/{id}")
    public String cancelPlan(@PathVariable("id") String id, HttpSession httpSession,
                             RedirectAttributes redirectAttributes) {
        User user = currentUser(httpSession);
        if (user == null) return "redirect:/login";
        if (findAccessiblePlan(user, id).isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot cancel this wedding plan.");
            return "redirect:/plans";
        }
        try {
            planService.cancelPlan(id);
            redirectAttributes.addFlashAttribute("warningMessage", "Wedding Plan has been cancelled.");
        } catch (AppException e) {
            redirectAttributes.addFlashAttribute("errorMessage", esc(e.getMessage()));
        }
        return "redirect:/plans";
    }

    @PostMapping("/plans/complete/{id}")
    public String completePlan(@PathVariable("id") String id, HttpSession httpSession,
                               RedirectAttributes redirectAttributes) {
        User user = currentUser(httpSession);
        if (user == null) return "redirect:/login";
        if (findAccessiblePlan(user, id).isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot update this wedding plan.");
            return "redirect:/plans";
        }
        try {
            planService.completePlan(id);
            redirectAttributes.addFlashAttribute("successMessage", "Wedding Plan marked as completed. Congratulations!");
        } catch (AppException e) {
            redirectAttributes.addFlashAttribute("errorMessage", esc(e.getMessage()));
        }
        return "redirect:/plans";
    }

    @PostMapping("/plans/delete/{id}")
    public String deletePlan(@PathVariable("id") String id, HttpSession httpSession,
                             RedirectAttributes redirectAttributes) {
        User user = currentUser(httpSession);
        if (user == null) return "redirect:/login";
        if (findAccessiblePlan(user, id).isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot delete this wedding plan.");
            return "redirect:/plans";
        }
        try {
            reportService.deletePlanCascade(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Wedding Plan and its checklist, budget and guest list were removed.");
        } catch (AppException e) {
            redirectAttributes.addFlashAttribute("errorMessage", esc(e.getMessage()));
        }
        return "redirect:/plans";
    }

    // ─── Live estimate used by the plan form (JSON) ──────────────────────────────
    @GetMapping(value = "/plans/estimate", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String estimate(@RequestParam(value = "type", defaultValue = "STANDARD") String type,
                           @RequestParam(value = "guests", defaultValue = "0") String guests,
                           HttpSession httpSession) {
        if (currentUser(httpSession) == null) return "{}";
        int count;
        try {
            count = Math.max(0, Math.min(WeddingPlanService.MAX_GUESTS, Integer.parseInt(guests.trim())));
        } catch (NumberFormatException e) {
            count = 0;
        }
        // Polymorphism: the preview object is the subclass for the chosen type.
        WeddingPlan preview = WeddingPlanFactory.createPreview(WeddingType.fromCode(type), count);
        return "{\"label\":\"" + json(preview.getTypeLabel()) + "\","
                + "\"estimate\":" + preview.calculateEstimatedCost().toPlainString() + ","
                + "\"tasks\":" + preview.getRecommendedTaskCount() + ","
                + "\"advice\":\"" + json(preview.getSpecialConsiderations()) + "\"}";
    }

    // ─── Checklist ───────────────────────────────────────────────────────────────
    @GetMapping("/plans/{id}/checklist")
    public String viewChecklist(@PathVariable("id") String planId, Model model, HttpSession httpSession) {
        User user = currentUser(httpSession);
        if (user == null) return "redirect:/login";
        if (findAccessiblePlan(user, planId).isEmpty()) return "redirect:/plans";

        PlanSummary summary = reportService.buildSummary(planId);
        addWorkspaceModel(model, summary, "Checklist");
        model.addAttribute("tasks", summary.getTasks());
        return "member02/checklist";
    }

    @PostMapping("/plans/{id}/checklist/add")
    public String addTask(@PathVariable("id") String planId,
                          @RequestParam("taskName") String taskName,
                          @RequestParam(value = "description", defaultValue = "") String description,
                          @RequestParam("deadline") String deadline,
                          @RequestParam(value = "priority", defaultValue = "MEDIUM") String priority,
                          HttpSession httpSession, RedirectAttributes redirectAttributes) {
        User user = currentUser(httpSession);
        if (user == null) return "redirect:/login";
        Optional<WeddingPlan> plan = findAccessiblePlan(user, planId);
        if (plan.isEmpty() || !canModify(user, plan.get())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot change this checklist.");
            return "redirect:/plans";
        }
        try {
            checklistService.addTask(planId, taskName, description, deadline, priority, plan.get().getWeddingDate());
            redirectAttributes.addFlashAttribute("successMessage", "Task added to checklist!");
        } catch (AppException e) {
            redirectAttributes.addFlashAttribute("errorMessage", esc(e.getMessage()));
        }
        return "redirect:/plans/" + planId + "/checklist";
    }

    @PostMapping("/plans/{id}/checklist/template")
    public String addRecommendedTasks(@PathVariable("id") String planId, HttpSession httpSession,
                                      RedirectAttributes redirectAttributes) {
        User user = currentUser(httpSession);
        if (user == null) return "redirect:/login";
        Optional<WeddingPlan> plan = findAccessiblePlan(user, planId);
        if (plan.isEmpty() || !canModify(user, plan.get())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot change this checklist.");
            return "redirect:/plans";
        }
        try {
            int added = checklistService.generateFromTemplate(plan.get());
            if (added == 0) {
                redirectAttributes.addFlashAttribute("warningMessage",
                        "All recommended tasks are already in your checklist.");
            } else {
                redirectAttributes.addFlashAttribute("successMessage",
                        added + " recommended tasks were added for your " + esc(plan.get().getTypeLabel()) + ".");
            }
        } catch (AppException e) {
            redirectAttributes.addFlashAttribute("errorMessage", esc(e.getMessage()));
        }
        return "redirect:/plans/" + planId + "/checklist";
    }

    @PostMapping("/plans/{id}/checklist/status")
    public String updateTaskStatus(@PathVariable("id") String planId,
                                   @RequestParam("taskId") String taskId,
                                   @RequestParam("status") String status,
                                   HttpSession httpSession, RedirectAttributes redirectAttributes) {
        User user = currentUser(httpSession);
        if (user == null) return "redirect:/login";
        Optional<WeddingPlan> plan = findAccessiblePlan(user, planId);
        if (plan.isEmpty() || !canModify(user, plan.get()) || !taskBelongsToPlan(planId, taskId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot update this checklist task.");
            return "redirect:/plans";
        }
        try {
            if ("COMPLETED".equalsIgnoreCase(status)) {
                checklistService.markComplete(taskId);
            } else if ("IN_PROGRESS".equalsIgnoreCase(status)) {
                checklistService.markInProgress(taskId);
            } else if ("PENDING".equalsIgnoreCase(status)) {
                checklistService.reopen(taskId);
            } else {
                throw new com.wedding.common.exceptions.ValidationException("Invalid checklist status.");
            }
            redirectAttributes.addFlashAttribute("successMessage",
                    "COMPLETED".equalsIgnoreCase(status) ? "Task completed — great progress!" :
                    "IN_PROGRESS".equalsIgnoreCase(status) ? "Task marked in progress." :
                    "Task re-opened successfully.");
        } catch (AppException e) {
            redirectAttributes.addFlashAttribute("errorMessage", esc(e.getMessage()));
        }
        return "redirect:/plans/" + planId + "/checklist";
    }

    @PostMapping("/plans/{id}/checklist/edit")
    public String editTask(@PathVariable("id") String planId,
                           @RequestParam("taskId") String taskId,
                           @RequestParam("taskName") String taskName,
                           @RequestParam(value = "description", defaultValue = "") String description,
                           @RequestParam("deadline") String deadline,
                           @RequestParam(value = "priority", defaultValue = "MEDIUM") String priority,
                           HttpSession httpSession, RedirectAttributes redirectAttributes) {
        User user = currentUser(httpSession);
        if (user == null) return "redirect:/login";
        Optional<WeddingPlan> plan = findAccessiblePlan(user, planId);
        if (plan.isEmpty() || !canModify(user, plan.get()) || !taskBelongsToPlan(planId, taskId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot edit this checklist task.");
            return "redirect:/plans";
        }
        try {
            checklistService.updateTask(taskId, taskName, description, deadline, priority,
                    plan.get().getWeddingDate());
            redirectAttributes.addFlashAttribute("successMessage", "Checklist task updated.");
        } catch (AppException e) {
            redirectAttributes.addFlashAttribute("errorMessage", esc(e.getMessage()));
        }
        return "redirect:/plans/" + planId + "/checklist";
    }

    @PostMapping("/plans/{id}/checklist/delete")
    public String deleteTask(@PathVariable("id") String planId,
                             @RequestParam("taskId") String taskId,
                             HttpSession httpSession, RedirectAttributes redirectAttributes) {
        User user = currentUser(httpSession);
        if (user == null) return "redirect:/login";
        Optional<WeddingPlan> plan = findAccessiblePlan(user, planId);
        if (plan.isEmpty() || !canModify(user, plan.get()) || !taskBelongsToPlan(planId, taskId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot delete this checklist task.");
            return "redirect:/plans";
        }
        try {
            checklistService.deleteTask(taskId);
            redirectAttributes.addFlashAttribute("successMessage", "Task removed from checklist.");
        } catch (AppException e) {
            redirectAttributes.addFlashAttribute("errorMessage", esc(e.getMessage()));
        }
        return "redirect:/plans/" + planId + "/checklist";
    }

    // ─── Timeline, printable summary and CSV export ──────────────────────────────
    @GetMapping("/plans/{id}/timeline")
    public String viewTimeline(@PathVariable("id") String planId, Model model, HttpSession httpSession) {
        User user = currentUser(httpSession);
        if (user == null) return "redirect:/login";
        if (findAccessiblePlan(user, planId).isEmpty()) return "redirect:/plans";

        PlanSummary summary = reportService.buildSummary(planId);
        addWorkspaceModel(model, summary, "Timeline");
        model.addAttribute("timeline", reportService.buildTimeline(planId));
        return "member02/timeline";
    }

    @GetMapping("/plans/{id}/summary")
    public String viewSummary(@PathVariable("id") String planId, Model model, HttpSession httpSession) {
        User user = currentUser(httpSession);
        if (user == null) return "redirect:/login";
        if (findAccessiblePlan(user, planId).isEmpty()) return "redirect:/plans";

        addWorkspaceModel(model, reportService.buildSummary(planId), "Summary");
        return "member02/summary";
    }

    @GetMapping("/plans/{id}/export.csv")
    public void exportCsv(@PathVariable("id") String planId, HttpSession httpSession,
                          HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = currentUser(httpSession);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (findAccessiblePlan(user, planId).isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        byte[] bytes = reportService.exportCsv(planId).getBytes(StandardCharsets.UTF_8);
        String safeName = planId.replaceAll("[^A-Za-z0-9._-]", "_");
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"wedding-plan-" + safeName + ".csv\"");
        response.setContentLength(bytes.length);
        response.getOutputStream().write(bytes);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────────

    private boolean taskBelongsToPlan(String planId, String taskId) {
        return checklistService.getTasksByPlan(planId).stream().anyMatch(t -> t.getTaskId().equals(taskId));
    }

    /** The values the user typed, handed back to the form after a validation error. */
    private Map<String, String> formValues(String planName, String weddingDate, String venue, String theme,
                                           String description, String totalBudget, String guestCount,
                                           String planType, String autoChecklist) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("planName", planName);
        form.put("weddingDate", weddingDate);
        form.put("venue", venue);
        form.put("theme", theme);
        form.put("description", description);
        form.put("totalBudget", totalBudget);
        form.put("guestCount", guestCount);
        form.put("planType", planType);
        form.put("autoChecklist", autoChecklist == null ? "" : "on");
        return form;
    }

    private String json(String text) {
        return text == null ? "" : text.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", " ").replace("\r", " ");
    }
}
