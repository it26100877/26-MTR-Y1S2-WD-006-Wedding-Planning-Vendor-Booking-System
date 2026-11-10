package com.wedding.member01_customer_admin.controller;

import com.wedding.common.exceptions.AppException;
import com.wedding.common.model.User;
import com.wedding.common.model.Session;
import com.wedding.member01_customer_admin.model.Admin;
import com.wedding.member01_customer_admin.model.Customer;
import com.wedding.member01_customer_admin.model.OtpChallenge;
import com.wedding.member01_customer_admin.service.AdminService;
import com.wedding.member01_customer_admin.service.CustomerService;
import com.wedding.member01_customer_admin.service.OtpService;
import com.wedding.member01_customer_admin.service.LoginRateLimiter;
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
    private final OtpService otpService;
    private final LoginRateLimiter loginRateLimiter;

 public CustomerAdminController(CustomerService customerService, AdminService adminService,
                                   OtpService otpService, LoginRateLimiter loginRateLimiter) {
 this.customerService = customerService;
 this.adminService = adminService;
        this.otpService = otpService;
        this.loginRateLimiter = loginRateLimiter;
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
            loginRateLimiter.recordLoginAttempt(request, email);
            if ("ADMIN".equalsIgnoreCase(role)) {
                User admin = adminService.login(email, password);
                request.changeSessionId();
                httpSession.setAttribute("currentUser", admin);
                redirectAttributes.addFlashAttribute("successMessage", "Welcome back, " + admin.getName() + "!");
                return "redirect:/admin/dashboard";
            }
            if ("CUSTOMER".equalsIgnoreCase(role)) {
                Customer customer = customerService.login(email, password);
                request.changeSessionId();
                httpSession.setAttribute("currentUser", customer);
                redirectAttributes.addFlashAttribute("successMessage", "Welcome back, " + customer.getName() + "!");
                return "redirect:/dashboard";
            }
            throw new com.wedding.common.exceptions.ValidationException("Select a valid account type.");
        } catch (AppException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/login";
        }
    }

    // ─── Phone verification (OTP) ────────────────────────────────────────────────
    @GetMapping("/login/otp")
    public String showOtpPage(HttpSession httpSession, Model model, RedirectAttributes redirectAttributes) {
        OtpChallenge challenge = otpService.current(httpSession);
        if (challenge == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please log in first.");
            return "redirect:/login";
        }
        try {
            Customer customer = customerService.getById(challenge.getUserId());
            model.addAttribute("maskedPhone", OtpService.maskPhone(customer.getPhone()));
            model.addAttribute("registrationVerification", !customer.isPhoneVerified());
        } catch (AppException e) {
            otpService.clear(httpSession);
            return "redirect:/login";
        }
        model.addAttribute("resendIn", otpService.secondsUntilResend(challenge));
        model.addAttribute("pageTitle", "Verify your phone - Éclat");
        return "member01/otp";
    }

    @PostMapping("/login/otp")
    public String verifyOtp(@RequestParam("code") String code,
                            HttpSession httpSession, HttpServletRequest request,
                            RedirectAttributes redirectAttributes) {
        OtpChallenge challenge = otpService.current(httpSession);
        if (challenge == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your login session has expired. Please log in again.");
            return "redirect:/login";
        }

        OtpService.Result result = otpService.verify(httpSession, code);
        switch (result) {
            case OK -> {
                try {
                    Customer customer = customerService.getById(challenge.getUserId());
                    if (!customer.isActive()) {
                        otpService.clear(httpSession);
                        redirectAttributes.addFlashAttribute("errorMessage",
                                "Your account has been deactivated. Please contact support.");
                        return "redirect:/login";
                    }
                    if (!customer.isPhoneVerified()) {
                        customerService.markPhoneVerified(customer.getUserId());
                        customer = customerService.getById(customer.getUserId());
                    }
                    otpService.clear(httpSession);
                    request.changeSessionId();
                    httpSession.setAttribute("currentUser", customer);
                    redirectAttributes.addFlashAttribute("successMessage",
                            "Phone verified. Welcome back, " + customer.getName() + "!");
                    return "redirect:/dashboard";
                } catch (AppException e) {
                    otpService.clear(httpSession);
                    redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                    return "redirect:/login";
                }
            }
            case WRONG -> {
                OtpChallenge current = otpService.current(httpSession);
                int left = current == null ? 0 : otpService.attemptsLeft(current);
                redirectAttributes.addFlashAttribute("errorMessage",
                        "That code is not correct. " + left + (left == 1 ? " attempt" : " attempts") + " left.");
                return "redirect:/login/otp";
            }
            case EXPIRED -> {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "That code has expired. Request a new one below.");
                return "redirect:/login/otp";
            }
            default -> {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Too many wrong attempts. Please log in again.");
                return "redirect:/login";
            }
        }
    }

    @PostMapping("/login/otp/resend")
    public String resendOtp(HttpSession httpSession, HttpServletRequest request,
                            RedirectAttributes redirectAttributes) {
        OtpChallenge challenge = otpService.current(httpSession);
        if (challenge == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your login session has expired. Please log in again.");
            return "redirect:/login";
        }
        try {
            loginRateLimiter.recordOtpResend(request);
            Customer customer = customerService.getById(challenge.getUserId());
            otpService.resend(httpSession, customer);
            redirectAttributes.addFlashAttribute("successMessage",
                    "A new code was sent to " + OtpService.maskPhone(customer.getPhone()) + ".");
        } catch (AppException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            if (otpService.current(httpSession) == null) return "redirect:/login";
        }
        return "redirect:/login/otp";
    }

    @PostMapping("/login/otp/cancel")
    public String cancelOtp(HttpSession httpSession) {
        otpService.clear(httpSession);
        return "redirect:/login";
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
 HttpSession httpSession, RedirectAttributes redirectAttributes) {
 if (!password.equals(confirmPassword)) {
 redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match.");
 return "redirect:/register";
 }
 try {
 Customer customer = customerService.registerPendingPhoneVerification(name, email, phone, password, address, weddingDate);
 otpService.start(httpSession, customer);
 redirectAttributes.addFlashAttribute("successMessage",
 "Account created. Verify your phone to complete registration.");
 return "redirect:/login/otp";
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 return "redirect:/register";
 }
 }

 private Customer requireActiveCustomer(HttpSession session) {
  User actor = (User) session.getAttribute("currentUser");
  if (actor == null || !"CUSTOMER".equalsIgnoreCase(actor.getRole())) return null;
  try {
   Customer customer = customerService.getById(actor.getUserId());
   if (!customer.isActive()) {
    session.invalidate();
    return null;
   }
   return customer;
  } catch (AppException ex) {
   session.invalidate();
   return null;
  }
 }

 // ─── Customer Profile ────────────────────────────────────────────────────────
 @GetMapping("/profile")
 public String viewProfile(HttpSession httpSession, Model model, RedirectAttributes redirectAttributes) {
 Customer customer = requireActiveCustomer(httpSession);
 if (customer == null) {
 redirectAttributes.addFlashAttribute("errorMessage", "Please log in with an active customer account to access your profile.");
 return "redirect:/login";
 }
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
 Customer activeCustomer = requireActiveCustomer(httpSession);
 if (activeCustomer == null) {
  redirectAttributes.addFlashAttribute("errorMessage", "Please log in with an active customer account.");
  return "redirect:/login";
 }

 try {
 customerService.updateProfile(activeCustomer.getUserId(), name, phone, address, weddingDate);
 Customer updated = customerService.getById(activeCustomer.getUserId());
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
 @PostMapping("/logout")
 public String logout(HttpSession httpSession, RedirectAttributes redirectAttributes) {
 httpSession.invalidate();
 Session.getInstance().logout();
 redirectAttributes.addFlashAttribute("successMessage", "You have been logged out successfully.");
 return "redirect:/login";
 }
}
