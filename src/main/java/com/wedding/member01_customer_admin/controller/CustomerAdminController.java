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

 public CustomerAdminController(CustomerService customerService, AdminService adminService,
                                   OtpService otpService) {
 this.customerService = customerService;
 this.adminService = adminService;
        this.otpService = otpService;
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
            if ("ADMIN".equalsIgnoreCase(role)) {
                User admin = adminService.login(email, password);
                request.changeSessionId();
                httpSession.setAttribute("currentUser", admin);
                redirectAttributes.addFlashAttribute("successMessage", "Welcome back, " + admin.getName() + "!");
                return redirectToCurrentHost(request, "/admin/dashboard");
            }
            if ("CUSTOMER".equalsIgnoreCase(role)) {
                // Step 1: password is correct -> text a code to the phone on file.
                // The customer is NOT logged in until the code is verified.
                Customer customer = customerService.login(email, password);
                otpService.start(httpSession, customer);
                redirectAttributes.addFlashAttribute("successMessage",
                        "We sent a 6-digit code to " + OtpService.maskPhone(customer.getPhone()) + ".");
                return redirectToCurrentHost(request, "/login/otp");
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
                    otpService.clear(httpSession);
                    request.changeSessionId();
                    httpSession.setAttribute("currentUser", customer);
                    redirectAttributes.addFlashAttribute("successMessage",
                            "Phone verified. Welcome back, " + customer.getName() + "!");
                    return redirectToCurrentHost(request, "/dashboard");
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
    public String resendOtp(HttpSession httpSession, RedirectAttributes redirectAttributes) {
        OtpChallenge challenge = otpService.current(httpSession);
        if (challenge == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your login session has expired. Please log in again.");
            return "redirect:/login";
        }
        try {
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

    @GetMapping("/login/otp/cancel")
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

    /**
     * Builds an absolute redirect using the incoming request host. This keeps
     * authentication redirects on the same GitHub Codespaces forwarded domain
     * instead of accidentally sending the browser to localhost.
     */
    private String redirectToCurrentHost(HttpServletRequest request, String path) {
        String contextPath = request.getContextPath() == null ? "" : request.getContextPath();
        String targetPath = contextPath + path;
        String forwardedHost = request.getHeader("X-Forwarded-Host");
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        String forwardedPort = request.getHeader("X-Forwarded-Port");

        if (forwardedHost != null && !forwardedHost.isBlank()) {
            String scheme = (forwardedProto == null || forwardedProto.isBlank())
                    ? request.getScheme() : forwardedProto.split(",")[0].trim();
            String host = forwardedHost.split(",")[0].trim();
            String port = forwardedPort == null ? "" : forwardedPort.split(",")[0].trim();
            boolean standardPort = ("https".equalsIgnoreCase(scheme) && "443".equals(port))
                    || ("http".equalsIgnoreCase(scheme) && "80".equals(port));
            String authority = host;
            if (!host.contains(":") && !port.isBlank() && !standardPort) {
                authority += ":" + port;
            }
            return "redirect:" + scheme + "://" + authority + targetPath;
        }

        return "redirect:" + request.getScheme() + "://" + request.getServerName()
                + (request.getServerPort() == 80 || request.getServerPort() == 443
                ? "" : ":" + request.getServerPort()) + targetPath;
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
