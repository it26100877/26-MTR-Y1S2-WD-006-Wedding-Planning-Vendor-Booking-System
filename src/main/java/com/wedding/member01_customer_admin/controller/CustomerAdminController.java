package com.wedding.member01_customer_admin.controller;

import com.wedding.common.exceptions.AppException;
import com.wedding.common.model.User;
import com.wedding.common.model.Session;
import com.wedding.member01_customer_admin.model.Admin;
import com.wedding.member01_customer_admin.model.Customer;
import com.wedding.member01_customer_admin.service.AdminService;
import com.wedding.member01_customer_admin.service.CustomerService;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller managing Customer and Admin authentication, profile management,
 * and user administration.
 *
 * @author Member 01 – Customer & Admin Management
 */
@Controller
public class CustomerAdminController {

 private final CustomerService customerService;
 private final AdminService adminService;

 public CustomerAdminController(CustomerService customerService, AdminService adminService) {
 this.customerService = customerService;
 this.adminService = adminService;
 }

 // ─── Login Page ──────────────────────────────────────────────────────────────
 @GetMapping("/login")
 public String showLoginPage(Model model) {
 model.addAttribute("pageTitle", "Login - Éclat");
 return "member01/login";
 }

 @PostMapping("/login")
 public String processLogin(@RequestParam("role") String role,
 @RequestParam("email") String email,
 @RequestParam("password") String password,
 HttpSession httpSession, HttpServletRequest request,
 RedirectAttributes redirectAttributes) {
 try {
 User user;
 if ("ADMIN".equalsIgnoreCase(role)) {
 user = adminService.login(email, password);
 } else if ("CUSTOMER".equalsIgnoreCase(role)) {
 user = customerService.login(email, password);
 } else {
  throw new com.wedding.common.exceptions.ValidationException("Select a valid account type.");
 }

 request.changeSessionId();
 httpSession.setAttribute("currentUser", user);

 redirectAttributes.addFlashAttribute("successMessage", "Welcome back, " + user.getName() + "!");
 if ("ADMIN".equalsIgnoreCase(role)) {
 return "redirect:/admin/dashboard";
 } else {
 return "redirect:/dashboard";
 }
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 return "redirect:/login";
 }
 }

 // ─── Registration Page ───────────────────────────────────────────────────────
 @GetMapping("/register")
 public String showRegisterPage(Model model) {
 model.addAttribute("pageTitle", "Register Customer - Éclat");
 return "member01/register";
 }

 @PostMapping("/register")
 public String processRegister(@RequestParam("name") String name,
 @RequestParam("email") String email,
 @RequestParam("phone") String phone,
 @RequestParam("password") String password,
 @RequestParam("confirmPassword") String confirmPassword,
 @RequestParam(value = "address", required = false) String address,
 @RequestParam(value = "weddingDate", required = false) String weddingDate,
 RedirectAttributes redirectAttributes) {
 if (!password.equals(confirmPassword)) {
 redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match.");
 return "redirect:/register";
 }
 try {
 Customer customer = customerService.register(name, email, phone, password, address, weddingDate);
 redirectAttributes.addFlashAttribute("successMessage",
 "Account registered successfully for " + customer.getName() + "! Please log in.");
 return "redirect:/login";
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 return "redirect:/register";
 }
 }

 // ─── Customer Profile ────────────────────────────────────────────────────────
 @GetMapping("/profile")
 public String viewProfile(HttpSession httpSession, Model model, RedirectAttributes redirectAttributes) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null || !"CUSTOMER".equalsIgnoreCase(user.getRole())) {
 redirectAttributes.addFlashAttribute("errorMessage", "Please log in as a customer to access your profile.");
 return "redirect:/login";
 }

 Customer customer = customerService.getById(user.getUserId());
 model.addAttribute("customer", customer);
 model.addAttribute("pageTitle", "My Profile - Éclat");
 model.addAttribute("activePage", "profile");
 return "member01/profile";
 }

 @PostMapping("/profile")
 public String updateProfile(@RequestParam("name") String name,
 @RequestParam("phone") String phone,
 @RequestParam(value = "address", required = false) String address,
 @RequestParam(value = "weddingDate", required = false) String weddingDate,
 HttpSession httpSession,
 RedirectAttributes redirectAttributes) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null || !"CUSTOMER".equalsIgnoreCase(user.getRole())) return "redirect:/login";

 try {
 customerService.updateProfile(user.getUserId(), name, phone, address, weddingDate);
 Customer updated = customerService.getById(user.getUserId());
 httpSession.setAttribute("currentUser", updated);
 redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 }
 return "redirect:/profile";
 }

 // ─── Admin Dashboard ─────────────────────────────────────────────────────────
 @GetMapping("/admin/dashboard")
 public String adminDashboard(HttpSession httpSession, Model model, RedirectAttributes redirectAttributes) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
 redirectAttributes.addFlashAttribute("errorMessage", "Access denied. Admin privileges required.");
 return "redirect:/login";
 }

 List<Customer> customers = customerService.getAllCustomers();
 List<Admin> admins = adminService.getAllAdmins();

 long activeCustomers = customers.stream().filter(Customer::isActive).count();

 model.addAttribute("customers", customers);
 model.addAttribute("admins", admins);
 model.addAttribute("totalCustomers", customers.size());
 model.addAttribute("activeCustomers", activeCustomers);
 model.addAttribute("totalAdmins", admins.size());
 model.addAttribute("pageTitle", "Admin Dashboard - User Management");
 model.addAttribute("activePage", "admin");
 return "member01/admin-dashboard";
 }

 @PostMapping("/admin/customer/toggle-active")
 public String toggleCustomerActive(@RequestParam("customerId") String customerId,
 @RequestParam("active") boolean active,
 HttpSession httpSession,
 RedirectAttributes redirectAttributes) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
 return "redirect:/login";
 }

 try {
 if (active) {
 customerService.deactivate(customerId);
 redirectAttributes.addFlashAttribute("warningMessage", "Customer account deactivated.");
 } else {
 customerService.activate(customerId);
 redirectAttributes.addFlashAttribute("successMessage", "Customer account activated.");
 }
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 }
 return "redirect:/admin/dashboard";
 }

 // ─── Logout ──────────────────────────────────────────────────────────────────
 @GetMapping("/logout")
 public String logout(HttpSession httpSession, RedirectAttributes redirectAttributes) {
 httpSession.invalidate();
 Session.getInstance().logout();
 redirectAttributes.addFlashAttribute("successMessage", "You have been logged out successfully.");
 return "redirect:/login";
 }
}
