package com.wedding;

import com.wedding.member01_customer_admin.repository.AdminRepository;
import com.wedding.member01_customer_admin.repository.CustomerRepository;
import com.wedding.member01_customer_admin.service.AdminService;
import com.wedding.member01_customer_admin.service.CustomerService;
import com.wedding.member01_customer_admin.service.OtpService;
import com.wedding.member01_customer_admin.service.SmsService;
import com.wedding.member02_wedding_plan.repository.BudgetItemRepository;
import com.wedding.member02_wedding_plan.repository.ChecklistTaskRepository;
import com.wedding.member02_wedding_plan.repository.GuestRepository;
import com.wedding.member02_wedding_plan.repository.WeddingPlanRepository;
import com.wedding.member02_wedding_plan.service.BudgetService;
import com.wedding.member02_wedding_plan.service.ChecklistService;
import com.wedding.member02_wedding_plan.service.GuestService;
import com.wedding.member02_wedding_plan.service.PlanReportService;
import com.wedding.member02_wedding_plan.service.WeddingPlanService;
import com.wedding.member03_vendor.repository.VendorRepository;
import com.wedding.member03_vendor.service.VendorService;
import com.wedding.member04_booking.repository.BookingNotificationRepository;
import com.wedding.member04_booking.repository.BookingRepository;
import com.wedding.member04_booking.service.BookingNotificationService;
import com.wedding.member04_booking.service.BookingService;
import com.wedding.member05_contract_payment.repository.ContractRepository;
import com.wedding.member05_contract_payment.repository.PaymentRepository;
import com.wedding.member05_contract_payment.service.ContractService;
import com.wedding.member05_contract_payment.service.PaymentService;
import com.wedding.member06_review_feedback.repository.ReviewRepository;
import com.wedding.member06_review_feedback.service.ReviewService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Central dependency-injection configuration for all six integrated modules.
 */
@Configuration
public class AppConfig {

    // Member 01 — Customer, Admin and OTP
    @Bean public CustomerRepository customerRepository() { return new CustomerRepository(); }
    @Bean public AdminRepository adminRepository() { return new AdminRepository(); }
    @Bean public CustomerService customerService(CustomerRepository repository) { return new CustomerService(repository); }
    @Bean public AdminService adminService(AdminRepository repository) { return new AdminService(repository); }
    @Bean public OtpService otpService(SmsService smsService) { return new OtpService(smsService); }

    // Member 02 — Wedding Plan, Checklist, Budget and Guests
    @Bean public WeddingPlanRepository weddingPlanRepository() { return new WeddingPlanRepository(); }
    @Bean public ChecklistTaskRepository checklistTaskRepository() { return new ChecklistTaskRepository(); }
    @Bean public BudgetItemRepository budgetItemRepository() { return new BudgetItemRepository(); }
    @Bean public GuestRepository guestRepository() { return new GuestRepository(); }
    @Bean public WeddingPlanService weddingPlanService(WeddingPlanRepository repository) {
        return new WeddingPlanService(repository);
    }
    @Bean public ChecklistService checklistService(ChecklistTaskRepository repository) {
        return new ChecklistService(repository);
    }
    @Bean public BudgetService budgetService(BudgetItemRepository repository) { return new BudgetService(repository); }
    @Bean public GuestService guestService(GuestRepository repository) { return new GuestService(repository); }
    @Bean public PlanReportService planReportService(WeddingPlanService plans, ChecklistService checklist,
                                                     BudgetService budget, GuestService guests) {
        return new PlanReportService(plans, checklist, budget, guests);
    }

    // Member 03 — Vendor Management
    @Bean public VendorRepository vendorRepository() { return new VendorRepository(); }
    @Bean public VendorService vendorService(VendorRepository repository) { return new VendorService(repository); }

    // Member 04 — Booking and notifications
    @Bean public BookingRepository bookingRepository() { return new BookingRepository(); }
    @Bean public BookingNotificationRepository bookingNotificationRepository() {
        return new BookingNotificationRepository();
    }
    @Bean public BookingNotificationService bookingNotificationService(BookingNotificationRepository repository) {
        return new BookingNotificationService(repository);
    }
    @Bean public BookingService bookingService(BookingRepository repository, VendorService vendors,
                                               BookingNotificationService notifications) {
        return new BookingService(repository, vendors, notifications);
    }

    // Member 05 — Contracts and Payments
    @Bean public ContractRepository contractRepository() { return new ContractRepository(); }
    @Bean public PaymentRepository paymentRepository() { return new PaymentRepository(); }
    @Bean public PaymentService paymentService(PaymentRepository repository) { return new PaymentService(repository); }
    @Bean public ContractService contractService(ContractRepository contracts, BookingRepository bookings,
                                                 PaymentService payments) {
        return new ContractService(contracts, bookings, payments);
    }

    // Member 06 — Reviews and Feedback
    @Bean public ReviewRepository reviewRepository() { return new ReviewRepository(); }
    @Bean public ReviewService reviewService(ReviewRepository reviews, BookingRepository bookings) {
        return new ReviewService(reviews, bookings);
    }

}
