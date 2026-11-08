package com.wedding.member02_wedding_plan.controller;

import com.wedding.common.exceptions.AppException;
import com.wedding.common.model.User;
import com.wedding.member02_wedding_plan.model.ChecklistTask;
import com.wedding.member02_wedding_plan.model.WeddingPlan;
import com.wedding.member02_wedding_plan.service.ChecklistService;
import com.wedding.member02_wedding_plan.service.WeddingPlanService;
import com.wedding.member04_booking.service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller managing Wedding Plans and Pre-Wedding Checklists.
 *
 * @author Member 02 – Wedding Plan & Checklist Management
 */
@Controller
public class WeddingPlanController {

 private final WeddingPlanService planService;
 private final ChecklistService checklistService;
 private final BookingService bookingService;

 public WeddingPlanController(WeddingPlanService planService, ChecklistService checklistService,
                              BookingService bookingService) {
 this.planService = planService;
 this.checklistService = checklistService;
 this.bookingService = bookingService;
 }

 // ─── List Plans ──────────────────────────────────────────────────────────────
 @GetMapping("/plans")
 public String listPlans(HttpSession httpSession, Model model, RedirectAttributes redirectAttributes) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null) return "redirect:/login";

 List<WeddingPlan> plans;
 if ("ADMIN".equalsIgnoreCase(user.getRole())) {
 plans = planService.getAllPlans();
 } else {
 plans = planService.getByCustomerId(user.getUserId());
 }

 model.addAttribute("plans", plans);
 model.addAttribute("pageTitle", "Wedding Plans - Éclat");
 model.addAttribute("activePage", "plans");
 return "member02/plans";
 }

 // ─── Create Plan ─────────────────────────────────────────────────────────────
 @GetMapping("/plans/new")
 public String showCreateForm(HttpSession httpSession, Model model) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null || !"CUSTOMER".equalsIgnoreCase(user.getRole())) return "redirect:/login";

 model.addAttribute("isEdit", false);
 model.addAttribute("pageTitle", "New Wedding Plan - Éclat");
 model.addAttribute("activePage", "plans");
 return "member02/plan-form";
 }

 @PostMapping("/plans/new")
 public String createPlan(@RequestParam("planName") String planName,
 @RequestParam("weddingDate") String weddingDate,
 @RequestParam("venue") String venue,
 @RequestParam("theme") String theme,
 @RequestParam("description") String description,
 @RequestParam("totalBudget") String totalBudget,
 HttpSession httpSession,
 RedirectAttributes redirectAttributes) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null || !"CUSTOMER".equalsIgnoreCase(user.getRole())) return "redirect:/login";

 try {
 WeddingPlan plan = planService.createPlan(
 user.getUserId(), planName, weddingDate, venue, theme, description, totalBudget);
 redirectAttributes.addFlashAttribute("successMessage", "Wedding Plan '" + plan.getPlanName() + "' created successfully!");
 return "redirect:/plans";
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 return "redirect:/plans/new";
 }
 }

 // ─── Edit Plan ───────────────────────────────────────────────────────────────
 @GetMapping("/plans/edit/{id}")
 public String showEditForm(@PathVariable("id") String id, HttpSession httpSession, Model model) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null) return "redirect:/login";

 WeddingPlan plan = planService.getById(id);
 if (!canAccessPlan(user, plan)) return "redirect:/plans";
 model.addAttribute("plan", plan);
 model.addAttribute("isEdit", true);
 model.addAttribute("pageTitle", "Edit Wedding Plan - Éclat");
 model.addAttribute("activePage", "plans");
 return "member02/plan-form";
 }

 @PostMapping("/plans/edit/{id}")
 public String updatePlan(@PathVariable("id") String id,
 @RequestParam("planName") String planName,
 @RequestParam("weddingDate") String weddingDate,
 @RequestParam("venue") String venue,
 @RequestParam("theme") String theme,
 @RequestParam("description") String description,
 @RequestParam("totalBudget") String totalBudget,
 HttpSession httpSession, RedirectAttributes redirectAttributes) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null) return "redirect:/login";
 try {
 WeddingPlan existing = planService.getById(id);
 if (!canAccessPlan(user, existing)) {
  redirectAttributes.addFlashAttribute("errorMessage", "You cannot edit another customer's plan.");
  return "redirect:/plans";
 }
 planService.updatePlan(id, planName, weddingDate, venue, theme, description, totalBudget);
 redirectAttributes.addFlashAttribute("successMessage", "Wedding Plan updated successfully!");
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 }
 return "redirect:/plans";
 }

 // ─── Cancel / Delete Plan ────────────────────────────────────────────────────
 @PostMapping("/plans/cancel/{id}")
 public String cancelPlan(@PathVariable("id") String id, HttpSession httpSession, RedirectAttributes redirectAttributes) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null) return "redirect:/login";
 try {
 WeddingPlan existing = planService.getById(id);
 if (!canAccessPlan(user, existing)) {
  redirectAttributes.addFlashAttribute("errorMessage", "You cannot cancel another customer's plan.");
  return "redirect:/plans";
 }
 planService.cancelPlan(id);
 redirectAttributes.addFlashAttribute("warningMessage", "Wedding Plan has been cancelled.");
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 }
 return "redirect:/plans";
 }

 @PostMapping("/plans/delete/{id}")
 public String deletePlan(@PathVariable("id") String id, HttpSession httpSession, RedirectAttributes redirectAttributes) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null) return "redirect:/login";
 try {
 WeddingPlan existing = planService.getById(id);
 if (!canAccessPlan(user, existing)) {
  redirectAttributes.addFlashAttribute("errorMessage", "You cannot delete another customer's plan.");
  return "redirect:/plans";
 }
 planService.deletePlan(id);
 redirectAttributes.addFlashAttribute("successMessage", "Wedding Plan removed.");
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 }
 return "redirect:/plans";
 }

 // ─── Checklist Management ────────────────────────────────────────────────────
 @GetMapping("/plans/{id}/checklist")
 public String viewChecklist(@PathVariable("id") String planId, Model model, HttpSession httpSession) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null) return "redirect:/login";

 WeddingPlan plan = planService.getById(planId);
 if (!canAccessPlan(user, plan)) return "redirect:/plans";
 List<ChecklistTask> tasks = checklistService.getTasksByPlan(planId);
 int progress = checklistService.getCompletionPercent(planId);

 model.addAttribute("plan", plan);
 model.addAttribute("tasks", tasks);
 model.addAttribute("progress", progress);
 model.addAttribute("pageTitle", "Checklist - " + plan.getPlanName());
 model.addAttribute("activePage", "plans");
 return "member02/checklist";
 }

 @PostMapping("/plans/{id}/checklist/add")
 public String addTask(@PathVariable("id") String planId,
 @RequestParam("taskName") String taskName,
 @RequestParam("description") String description,
 @RequestParam("deadline") String deadline,
 @RequestParam("priority") String priority,
 HttpSession httpSession, RedirectAttributes redirectAttributes) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null) return "redirect:/login";
 try {
 WeddingPlan plan = planService.getById(planId);
 if (!canAccessPlan(user, plan)) {
  redirectAttributes.addFlashAttribute("errorMessage", "You cannot change another customer's checklist.");
  return "redirect:/plans";
 }
 checklistService.addTask(planId, taskName, description, deadline, priority);
 redirectAttributes.addFlashAttribute("successMessage", "Task added to checklist!");
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 }
 return "redirect:/plans/" + planId + "/checklist";
 }

 @PostMapping("/plans/{id}/checklist/status")
 public String updateTaskStatus(@PathVariable("id") String planId,
 @RequestParam("taskId") String taskId,
 @RequestParam("status") String status,
 HttpSession httpSession, RedirectAttributes redirectAttributes) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null) return "redirect:/login";
 try {
 WeddingPlan plan = planService.getById(planId);
 if (!canAccessPlan(user, plan) || checklistService.getTasksByPlan(planId).stream().noneMatch(t -> t.getTaskId().equals(taskId))) {
  redirectAttributes.addFlashAttribute("errorMessage", "You cannot update this checklist task.");
  return "redirect:/plans";
 }
 if ("COMPLETED".equalsIgnoreCase(status)) {
 checklistService.markComplete(taskId);
 } else if ("IN_PROGRESS".equalsIgnoreCase(status)) {
 checklistService.markInProgress(taskId);
 } else {
  throw new com.wedding.common.exceptions.ValidationException("Invalid checklist status.");
 }
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 }
 return "redirect:/plans/" + planId + "/checklist";
 }

 @PostMapping("/plans/{id}/checklist/delete")
 public String deleteTask(@PathVariable("id") String planId,
 @RequestParam("taskId") String taskId,
 HttpSession httpSession, RedirectAttributes redirectAttributes) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null) return "redirect:/login";
 try {
 WeddingPlan plan = planService.getById(planId);
 if (!canAccessPlan(user, plan) || checklistService.getTasksByPlan(planId).stream().noneMatch(t -> t.getTaskId().equals(taskId))) {
  redirectAttributes.addFlashAttribute("errorMessage", "You cannot delete this checklist task.");
  return "redirect:/plans";
 }
 checklistService.deleteTask(taskId);
 redirectAttributes.addFlashAttribute("successMessage", "Task removed from checklist.");
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 }
 return "redirect:/plans/" + planId + "/checklist";
 }

 private boolean canAccessPlan(User user, WeddingPlan plan) {
  return user != null && plan != null
      && ("ADMIN".equalsIgnoreCase(user.getRole())
          || ("CUSTOMER".equalsIgnoreCase(user.getRole())
              && user.getUserId().equalsIgnoreCase(plan.getCustomerId())));
 }
}
