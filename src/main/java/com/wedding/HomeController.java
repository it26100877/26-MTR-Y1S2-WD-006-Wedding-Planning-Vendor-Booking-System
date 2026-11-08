package com.wedding;

import com.wedding.common.model.User;
import com.wedding.member01_customer_admin.service.CustomerService;
import com.wedding.member02_wedding_plan.service.WeddingPlanService;
import com.wedding.member03_vendor.model.VendorCategory;
import com.wedding.member03_vendor.service.VendorService;
import com.wedding.member04_booking.service.BookingService;
import com.wedding.member05_contract_payment.service.ContractService;
import com.wedding.member06_review_feedback.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Public homepage and authenticated dashboard routes.
 * Keeps the public marketplace accessible without requiring login.
 */
@Controller
public class HomeController {

    private final CustomerService customerService;
    private final WeddingPlanService planService;
    private final VendorService vendorService;
    private final BookingService bookingService;
    private final ContractService contractService;
    private final ReviewService reviewService;

    public HomeController(CustomerService customerService,
                          WeddingPlanService planService,
                          VendorService vendorService,
                          BookingService bookingService,
                          ContractService contractService,
                          ReviewService reviewService) {
        this.customerService = customerService;
        this.planService = planService;
        this.vendorService = vendorService;
        this.bookingService = bookingService;
        this.contractService = contractService;
        this.reviewService = reviewService;
    }

    /**
     * Public landing page. Visitors can browse vendors before signing in.
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("categories", VendorCategory.values());
        model.addAttribute("featuredVendors", vendorService.getApprovedVendors().stream().limit(3).toList());
        model.addAttribute("pageTitle", "Éclat | Discover Your Perfect Wedding");
        return "public-home";
    }

    /**
     * Private dashboard. Authentication is still required.
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession httpSession, Model model) {
        User user = (User) httpSession.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            model.addAttribute("totalCustomers", customerService.count());
            model.addAttribute("totalPlans", planService.count());
            model.addAttribute("totalVendors", vendorService.count());
            model.addAttribute("totalBookings", bookingService.count());
            model.addAttribute("totalContracts", contractService.count());
            model.addAttribute("totalReviews", reviewService.count());
        } else {
            model.addAttribute("totalCustomers", 1);
            model.addAttribute("totalPlans", planService.getByCustomerId(user.getUserId()).size());
            model.addAttribute("totalVendors", vendorService.getApprovedVendors().size());
            model.addAttribute("totalBookings", bookingService.getByCustomer(user.getUserId()).size());
            model.addAttribute("totalContracts", contractService.getByCustomer(user.getUserId()).size());
            model.addAttribute("totalReviews", reviewService.getByCustomer(user.getUserId()).size());
        }

        model.addAttribute("pageTitle", "Dashboard - Éclat");
        model.addAttribute("activePage", "dashboard");
        return "dashboard";
    }
}
