package com.wedding.member02_wedding_plan.controller;

import com.wedding.common.exceptions.AppException;
import com.wedding.common.model.User;
import com.wedding.member01_customer_admin.service.CustomerService;
import com.wedding.member02_wedding_plan.model.WeddingType;
import com.wedding.member02_wedding_plan.service.ChecklistService;
import com.wedding.member02_wedding_plan.service.PlanReportService;
import com.wedding.member02_wedding_plan.service.WeddingPlanAdminService;
import com.wedding.member02_wedding_plan.service.WeddingPlanService;
import com.wedding.member04_booking.repository.BookingRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** A dedicated admin-only wedding workspace, owned entirely by Member 02. */
@Controller
public class WeddingPlanAdminController extends PlanControllerSupport {
    private final WeddingPlanAdminService dashboardService;
    private final PlanReportService reportService;
    private final BookingRepository bookingRepository;

    public WeddingPlanAdminController(WeddingPlanService planService,
                                      ChecklistService checklistService,
                                      CustomerService customerService,
                                      PlanReportService reportService,
                                      BookingRepository bookingRepository) {
        super(planService);
        this.dashboardService = new WeddingPlanAdminService(planService, checklistService, customerService);
        this.reportService = reportService;
        this.bookingRepository = bookingRepository;
    }

    @GetMapping("/admin/wedding-plans")
    public String dashboard(@RequestParam(value = "q", required = false) String query,
                            @RequestParam(value = "status", defaultValue = "ALL") String status,
                            @RequestParam(value = "type", defaultValue = "ALL") String type,
                            @RequestParam(value = "sort", defaultValue = "soonest") String sort,
                            HttpSession session, Model model) {
        User user = currentUser(session);
        if (user == null) return "redirect:/login";
        if (!isAdmin(user)) return "redirect:/dashboard";
        model.addAttribute("stats", dashboardService.getDashboard(query, status, type, sort));
        model.addAttribute("query", query == null ? "" : query);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedSort", sort);
        model.addAttribute("types", WeddingType.values());
        model.addAttribute("pageTitle", "Wedding Administration - Éclat");
        model.addAttribute("activePage", "admin");
        return "member02/admin-wedding-dashboard";
    }

    @PostMapping("/admin/wedding-plans/{id}/complete")
    public String complete(@PathVariable String id, HttpSession session, RedirectAttributes flash) {
        User user = currentUser(session);
        if (user == null) return "redirect:/login";
        if (!isAdmin(user)) return "redirect:/dashboard";
        if (findAccessiblePlan(user, id).isEmpty()) {
            flash.addFlashAttribute("errorMessage", "Wedding plan not found.");
            return "redirect:/admin/wedding-plans";
        }
        try {
            planService.completePlan(id);
            flash.addFlashAttribute("successMessage", "Wedding plan marked as completed.");
        } catch (AppException ex) {
            flash.addFlashAttribute("errorMessage", esc(ex.getMessage()));
        }
        return "redirect:/admin/wedding-plans";
    }

    @PostMapping("/admin/wedding-plans/{id}/cancel")
    public String cancel(@PathVariable String id, HttpSession session, RedirectAttributes flash) {
        User user = currentUser(session);
        if (user == null) return "redirect:/login";
        if (!isAdmin(user)) return "redirect:/dashboard";
        if (findAccessiblePlan(user, id).isEmpty()) {
            flash.addFlashAttribute("errorMessage", "Wedding plan not found.");
            return "redirect:/admin/wedding-plans";
        }
        try {
            planService.cancelPlan(id);
            flash.addFlashAttribute("warningMessage", "Wedding plan cancelled.");
        } catch (AppException ex) {
            flash.addFlashAttribute("errorMessage", esc(ex.getMessage()));
        }
        return "redirect:/admin/wedding-plans";
    }

    @PostMapping("/admin/wedding-plans/{id}/delete")
    public String delete(@PathVariable String id, HttpSession session, RedirectAttributes flash) {
        User user = currentUser(session);
        if (user == null) return "redirect:/login";
        if (!isAdmin(user)) return "redirect:/dashboard";
        if (findAccessiblePlan(user, id).isEmpty()) {
            flash.addFlashAttribute("errorMessage", "Wedding plan not found.");
            return "redirect:/admin/wedding-plans";
        }
        // Prevent dangling cross-member Booking records; do not mutate the Booking module.
        if (!bookingRepository.findByPlanId(id).isEmpty()) {
            flash.addFlashAttribute("errorMessage",
                    "Cannot delete this plan because vendor bookings are linked to it. Review its bookings first.");
            return "redirect:/admin/wedding-plans";
        }
        try {
            reportService.deletePlanCascade(id);
            flash.addFlashAttribute("successMessage", "Wedding plan and related checklist, budget and guest records deleted.");
        } catch (AppException ex) {
            flash.addFlashAttribute("errorMessage", esc(ex.getMessage()));
        }
        return "redirect:/admin/wedding-plans";
    }
}
