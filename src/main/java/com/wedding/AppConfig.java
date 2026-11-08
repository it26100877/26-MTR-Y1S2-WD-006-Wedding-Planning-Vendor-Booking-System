package com.wedding;

import com.wedding.member01_customer_admin.repository.AdminRepository;
import com.wedding.member01_customer_admin.repository.CustomerRepository;
import com.wedding.member01_customer_admin.service.AdminService;
import com.wedding.member01_customer_admin.service.CustomerService;
import com.wedding.member02_wedding_plan.repository.ChecklistTaskRepository;
import com.wedding.member02_wedding_plan.repository.WeddingPlanRepository;
import com.wedding.member02_wedding_plan.service.ChecklistService;
import com.wedding.member02_wedding_plan.service.WeddingPlanService;
import com.wedding.member03_vendor.repository.VendorRepository;
import com.wedding.member03_vendor.service.VendorService;
import com.wedding.member04_booking.repository.BookingRepository;
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
 * Spring Boot Configuration class wiring all repositories and services
 * across all six member modules. Demonstrates Dependency Injection (IoC).
 *
 * @author Wedding Planning System Development Team (SE1020)
 */
@Configuration
public class AppConfig {

 // ── Member 01: Customer & Admin ───────────────────────────────────────────
 @Bean
 public CustomerRepository customerRepository() {
 return new CustomerRepository();
 }

 @Bean
 public AdminRepository adminRepository() {
 return new AdminRepository();
 }

 @Bean
 public CustomerService customerService(CustomerRepository customerRepository) {
 return new CustomerService(customerRepository);
 }

 @Bean
 public AdminService adminService(AdminRepository adminRepository) {
 return new AdminService(adminRepository);
 }

 // ── Member 02: Wedding Plan & Checklist ───────────────────────────────────
 @Bean
 public WeddingPlanRepository weddingPlanRepository() {
 return new WeddingPlanRepository();
 }

 @Bean
 public ChecklistTaskRepository checklistTaskRepository() {
 return new ChecklistTaskRepository();
 }

 @Bean
 public WeddingPlanService weddingPlanService(WeddingPlanRepository weddingPlanRepository) {
 return new WeddingPlanService(weddingPlanRepository);
 }

 @Bean
 public ChecklistService checklistService(ChecklistTaskRepository checklistTaskRepository) {
 return new ChecklistService(checklistTaskRepository);
 }

 // ── Member 03: Vendor Management ─────────────────────────────────────────
 @Bean
 public VendorRepository vendorRepository() {
 return new VendorRepository();
 }

 @Bean
 public VendorService vendorService(VendorRepository vendorRepository) {
 return new VendorService(vendorRepository);
 }

 // ── Member 04: Booking Management ────────────────────────────────────────
 @Bean
 public BookingRepository bookingRepository() {
 return new BookingRepository();
 }

 @Bean
 public BookingService bookingService(BookingRepository bookingRepository, VendorService vendorService) {
 return new BookingService(bookingRepository, vendorService);
 }

 // ── Member 05: Contract & Payment Management ──────────────────────────────
 @Bean
 public ContractRepository contractRepository() {
 return new ContractRepository();
 }

 @Bean
 public PaymentRepository paymentRepository() {
 return new PaymentRepository();
 }

 @Bean
 public PaymentService paymentService(PaymentRepository paymentRepository) {
 return new PaymentService(paymentRepository);
 }

 @Bean
 public ContractService contractService(ContractRepository contractRepository,
 BookingRepository bookingRepository,
 PaymentService paymentService) {
 return new ContractService(contractRepository, bookingRepository, paymentService);
 }

 // ── Member 06: Review & Feedback Management ──────────────────────────────
 @Bean
 public ReviewRepository reviewRepository() {
 return new ReviewRepository();
 }

 @Bean
 public ReviewService reviewService(ReviewRepository reviewRepository, BookingRepository bookingRepository) {
 return new ReviewService(reviewRepository, bookingRepository);
 }
}

