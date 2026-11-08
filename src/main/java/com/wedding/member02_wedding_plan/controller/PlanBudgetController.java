package com.wedding.member02_wedding_plan.controller;

import com.wedding.common.exceptions.AppException;
import com.wedding.common.model.User;
import com.wedding.member02_wedding_plan.model.BudgetCategory;
import com.wedding.member02_wedding_plan.model.BudgetSummary;
import com.wedding.member02_wedding_plan.model.PlanSummary;
import com.wedding.member02_wedding_plan.model.WeddingPlan;
import com.wedding.member02_wedding_plan.service.BudgetService;
import com.wedding.member02_wedding_plan.service.PlanReportService;
import com.wedding.member02_wedding_plan.service.WeddingPlanService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Budget tracker: add, view, edit and delete budget items for a wedding plan.
 *
 * @author Member 02
 */
@Controller
public class PlanBudgetController extends PlanControllerSupport {

    private final BudgetService budgetService;
    private final PlanReportService reportService;

    public PlanBudgetController(WeddingPlanService planService, BudgetService budgetService,
                                PlanReportService reportService) {
        super(planService);
        this.budgetService = budgetService;
        this.reportService = reportService;
    }

    @GetMapping("/plans/{id}/budget")
    public String viewBudget(@PathVariable("id") String planId, Model model, HttpSession httpSession) {
        User user = currentUser(httpSession);
        if (user == null) return "redirect:/login";
        if (findAccessiblePlan(user, planId).isEmpty()) return "redirect:/plans";

        PlanSummary summary = reportService.buildSummary(planId);
        addWorkspaceModel(model, summary, "Budget");
        model.addAttribute("items", summary.getBudgetItems());
        model.addAttribute("budget", summary.getBudget());
        model.addAttribute("categories", BudgetCategory.values());

        // Data for the category donut chart (labels are fixed enum text, not user input).
        StringBuilder values = new StringBuilder();
        StringBuilder labels = new StringBuilder();
        StringBuilder colors = new StringBuilder();
        for (BudgetSummary.CategoryTotal c : summary.getBudget().getCategories()) {
            if (c.getPlanned().signum() <= 0) continue;
            if (values.length() > 0) { values.append(','); labels.append('|'); colors.append(','); }
            values.append(c.getPlanned().toPlainString());
            labels.append(c.getCategory().getLabel());
            colors.append(c.getCategory().getColor());
        }
        model.addAttribute("chartValues", values.toString());
        model.addAttribute("chartLabels", labels.toString());
        model.addAttribute("chartColors", colors.toString());
        return "member02/budget";
    }

    @PostMapping("/plans/{id}/budget/add")
    public String addItem(@PathVariable("id") String planId,
                          @RequestParam(value = "category", defaultValue = "OTHER") String category,
                          @RequestParam("itemName") String itemName,
                          @RequestParam(value = "plannedAmount", defaultValue = "") String plannedAmount,
                          @RequestParam(value = "spentAmount", defaultValue = "") String spentAmount,
                          @RequestParam(value = "note", defaultValue = "") String note,
                          HttpSession httpSession, RedirectAttributes redirectAttributes) {
        User user = currentUser(httpSession);
        if (user == null) return "redirect:/login";
        Optional<WeddingPlan> plan = findAccessiblePlan(user, planId);
        if (plan.isEmpty() || !canModify(user, plan.get())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot change this budget.");
            return "redirect:/plans";
        }
        try {
            budgetService.addItem(planId, category, itemName, plannedAmount, spentAmount, note);
            redirectAttributes.addFlashAttribute("successMessage", "Budget item added.");
        } catch (AppException e) {
            redirectAttributes.addFlashAttribute("errorMessage", esc(e.getMessage()));
        }
        return "redirect:/plans/" + planId + "/budget";
    }

    @PostMapping("/plans/{id}/budget/edit")
    public String editItem(@PathVariable("id") String planId,
                           @RequestParam("itemId") String itemId,
                           @RequestParam(value = "category", defaultValue = "OTHER") String category,
                           @RequestParam("itemName") String itemName,
                           @RequestParam(value = "plannedAmount", defaultValue = "") String plannedAmount,
                           @RequestParam(value = "spentAmount", defaultValue = "") String spentAmount,
                           @RequestParam(value = "note", defaultValue = "") String note,
                           HttpSession httpSession, RedirectAttributes redirectAttributes) {
        User user = currentUser(httpSession);
        if (user == null) return "redirect:/login";
        Optional<WeddingPlan> plan = findAccessiblePlan(user, planId);
        if (plan.isEmpty() || !canModify(user, plan.get())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot change this budget.");
            return "redirect:/plans";
        }
        try {
            budgetService.updateItem(planId, itemId, category, itemName, plannedAmount, spentAmount, note);
            redirectAttributes.addFlashAttribute("successMessage", "Budget item updated.");
        } catch (AppException e) {
            redirectAttributes.addFlashAttribute("errorMessage", esc(e.getMessage()));
        }
        return "redirect:/plans/" + planId + "/budget";
    }

    @PostMapping("/plans/{id}/budget/delete")
    public String deleteItem(@PathVariable("id") String planId,
                             @RequestParam("itemId") String itemId,
                             HttpSession httpSession, RedirectAttributes redirectAttributes) {
        User user = currentUser(httpSession);
        if (user == null) return "redirect:/login";
        Optional<WeddingPlan> plan = findAccessiblePlan(user, planId);
        if (plan.isEmpty() || !canModify(user, plan.get())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot change this budget.");
            return "redirect:/plans";
        }
        try {
            budgetService.deleteItem(planId, itemId);
            redirectAttributes.addFlashAttribute("successMessage", "Budget item removed.");
        } catch (AppException e) {
            redirectAttributes.addFlashAttribute("errorMessage", esc(e.getMessage()));
        }
        return "redirect:/plans/" + planId + "/budget";
    }
}
