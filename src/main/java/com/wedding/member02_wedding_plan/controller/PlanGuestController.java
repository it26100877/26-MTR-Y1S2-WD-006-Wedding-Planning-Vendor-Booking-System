package com.wedding.member02_wedding_plan.controller;

import com.wedding.common.exceptions.AppException;
import com.wedding.common.model.User;
import com.wedding.member02_wedding_plan.model.GuestGroup;
import com.wedding.member02_wedding_plan.model.PlanSummary;
import com.wedding.member02_wedding_plan.model.WeddingPlan;
import com.wedding.member02_wedding_plan.service.GuestService;
import com.wedding.member02_wedding_plan.service.PlanReportService;
import com.wedding.member02_wedding_plan.service.WeddingPlanService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Guest list and RSVP tracking: add, view, edit, change RSVP and delete guests,
 * and copy the attending headcount into the plan's expected guest count.
 *
 * @author Member 02
 */
@Controller
public class PlanGuestController extends PlanControllerSupport {

    private final GuestService guestService;
    private final PlanReportService reportService;

    public PlanGuestController(WeddingPlanService planService, GuestService guestService,
                               PlanReportService reportService) {
        super(planService);
        this.guestService = guestService;
        this.reportService = reportService;
    }

    @GetMapping("/plans/{id}/guests")
    public String viewGuests(@PathVariable("id") String planId, Model model, HttpSession httpSession) {
        User user = currentUser(httpSession);
        if (user == null) return "redirect:/login";
        if (findAccessiblePlan(user, planId).isEmpty()) return "redirect:/plans";

        PlanSummary summary = reportService.buildSummary(planId);
        addWorkspaceModel(model, summary, "Guests");
        model.addAttribute("guests", summary.getGuests());
        model.addAttribute("stats", summary.getGuestStats());
        model.addAttribute("groups", GuestGroup.values());
        return "member02/guests";
    }

    @PostMapping("/plans/{id}/guests/add")
    public String addGuest(@PathVariable("id") String planId,
                           @RequestParam("guestName") String guestName,
                           @RequestParam(value = "group", defaultValue = "OTHER") String group,
                           @RequestParam(value = "contact", defaultValue = "") String contact,
                           @RequestParam(value = "partySize", defaultValue = "1") String partySize,
                           @RequestParam(value = "rsvpStatus", defaultValue = "INVITED") String rsvpStatus,
                           @RequestParam(value = "note", defaultValue = "") String note,
                           HttpSession httpSession, RedirectAttributes redirectAttributes) {
        User user = currentUser(httpSession);
        if (user == null) return "redirect:/login";
        Optional<WeddingPlan> plan = findAccessiblePlan(user, planId);
        if (plan.isEmpty() || !canModify(user, plan.get())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot change this guest list.");
            return "redirect:/plans";
        }
        try {
            guestService.addGuest(planId, guestName, group, contact, partySize, rsvpStatus, note);
            redirectAttributes.addFlashAttribute("successMessage", "Guest added.");
        } catch (AppException e) {
            redirectAttributes.addFlashAttribute("errorMessage", esc(e.getMessage()));
        }
        return "redirect:/plans/" + planId + "/guests";
    }

    @PostMapping("/plans/{id}/guests/edit")
    public String editGuest(@PathVariable("id") String planId,
                            @RequestParam("guestId") String guestId,
                            @RequestParam("guestName") String guestName,
                            @RequestParam(value = "group", defaultValue = "OTHER") String group,
                            @RequestParam(value = "contact", defaultValue = "") String contact,
                            @RequestParam(value = "partySize", defaultValue = "1") String partySize,
                            @RequestParam(value = "rsvpStatus", defaultValue = "INVITED") String rsvpStatus,
                            @RequestParam(value = "note", defaultValue = "") String note,
                            HttpSession httpSession, RedirectAttributes redirectAttributes) {
        User user = currentUser(httpSession);
        if (user == null) return "redirect:/login";
        Optional<WeddingPlan> plan = findAccessiblePlan(user, planId);
        if (plan.isEmpty() || !canModify(user, plan.get())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot change this guest list.");
            return "redirect:/plans";
        }
        try {
            guestService.updateGuest(planId, guestId, guestName, group, contact, partySize, rsvpStatus, note);
            redirectAttributes.addFlashAttribute("successMessage", "Guest updated.");
        } catch (AppException e) {
            redirectAttributes.addFlashAttribute("errorMessage", esc(e.getMessage()));
        }
        return "redirect:/plans/" + planId + "/guests";
    }

    @PostMapping("/plans/{id}/guests/rsvp")
    public String changeRsvp(@PathVariable("id") String planId,
                             @RequestParam("guestId") String guestId,
                             @RequestParam("rsvpStatus") String rsvpStatus,
                             HttpSession httpSession, RedirectAttributes redirectAttributes) {
        User user = currentUser(httpSession);
        if (user == null) return "redirect:/login";
        Optional<WeddingPlan> plan = findAccessiblePlan(user, planId);
        if (plan.isEmpty() || !canModify(user, plan.get())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot change this guest list.");
            return "redirect:/plans";
        }
        try {
            guestService.setRsvp(planId, guestId, rsvpStatus);
        } catch (AppException e) {
            redirectAttributes.addFlashAttribute("errorMessage", esc(e.getMessage()));
        }
        return "redirect:/plans/" + planId + "/guests";
    }

    @PostMapping("/plans/{id}/guests/delete")
    public String deleteGuest(@PathVariable("id") String planId,
                              @RequestParam("guestId") String guestId,
                              HttpSession httpSession, RedirectAttributes redirectAttributes) {
        User user = currentUser(httpSession);
        if (user == null) return "redirect:/login";
        Optional<WeddingPlan> plan = findAccessiblePlan(user, planId);
        if (plan.isEmpty() || !canModify(user, plan.get())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot change this guest list.");
            return "redirect:/plans";
        }
        try {
            guestService.deleteGuest(planId, guestId);
            redirectAttributes.addFlashAttribute("successMessage", "Guest removed.");
        } catch (AppException e) {
            redirectAttributes.addFlashAttribute("errorMessage", esc(e.getMessage()));
        }
        return "redirect:/plans/" + planId + "/guests";
    }

    /** Copies the attending headcount (confirmed + awaiting reply) into the plan's guest count. */
    @PostMapping("/plans/{id}/guests/sync")
    public String syncGuestCount(@PathVariable("id") String planId, HttpSession httpSession,
                                 RedirectAttributes redirectAttributes) {
        User user = currentUser(httpSession);
        if (user == null) return "redirect:/login";
        Optional<WeddingPlan> plan = findAccessiblePlan(user, planId);
        if (plan.isEmpty() || !canModify(user, plan.get())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot change this plan.");
            return "redirect:/plans";
        }
        try {
            PlanSummary summary = reportService.buildSummary(planId);
            int attending = summary.getGuestStats().getAttendingEstimate();
            planService.updateGuestCount(planId, attending);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Expected guest count updated to " + attending + ".");
        } catch (AppException e) {
            redirectAttributes.addFlashAttribute("errorMessage", esc(e.getMessage()));
        }
        return "redirect:/plans/" + planId + "/guests";
    }
}
