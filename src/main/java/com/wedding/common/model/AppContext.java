package com.wedding.common.model;

/**
 * Central service registry (Application Context).
 * All module services can be accessed through this singleton or injected via Spring.
 * Demonstrates Service Locator and Singleton patterns.
 *
 * @author Common Module
 */
public class AppContext {

 private static AppContext instance;

 // ─── Repositories ────────────────────────────────────────────────────────────
 private com.wedding.member01_customer_admin.repository.CustomerRepository customerRepository;
 private com.wedding.member01_customer_admin.repository.AdminRepository adminRepository;
 private com.wedding.member02_wedding_plan.repository.WeddingPlanRepository planRepository;
 private com.wedding.member02_wedding_plan.repository.ChecklistTaskRepository checklistRepository;
 private com.wedding.member03_vendor.repository.VendorRepository vendorRepository;
 private com.wedding.member04_booking.repository.BookingRepository bookingRepository;
 private com.wedding.member05_contract_payment.repository.ContractRepository contractRepository;
 private com.wedding.member05_contract_payment.repository.PaymentRepository paymentRepository;
 private com.wedding.member06_review_feedback.repository.ReviewRepository reviewRepository;

 // ─── Services ────────────────────────────────────────────────────────────────
 private com.wedding.member01_customer_admin.service.CustomerService customerService;
 private com.wedding.member01_customer_admin.service.AdminService adminService;
 private com.wedding.member02_wedding_plan.service.WeddingPlanService planService;
 private com.wedding.member02_wedding_plan.service.ChecklistService checklistService;
 private com.wedding.member03_vendor.service.VendorService vendorService;
 private com.wedding.member04_booking.service.BookingService bookingService;
 private com.wedding.member05_contract_payment.service.ContractService contractService;
 private com.wedding.member05_contract_payment.service.PaymentService paymentService;
 private com.wedding.member06_review_feedback.service.ReviewService reviewService;

 private AppContext() {
 init();
 }

 public static synchronized AppContext getInstance() {
 if (instance == null) {
 instance = new AppContext();
 }
 return instance;
 }

 private void init() {
 customerRepository = new com.wedding.member01_customer_admin.repository.CustomerRepository();
 adminRepository = new com.wedding.member01_customer_admin.repository.AdminRepository();
 planRepository = new com.wedding.member02_wedding_plan.repository.WeddingPlanRepository();
 checklistRepository= new com.wedding.member02_wedding_plan.repository.ChecklistTaskRepository();
 vendorRepository = new com.wedding.member03_vendor.repository.VendorRepository();
 bookingRepository = new com.wedding.member04_booking.repository.BookingRepository();
 contractRepository = new com.wedding.member05_contract_payment.repository.ContractRepository();
 paymentRepository = new com.wedding.member05_contract_payment.repository.PaymentRepository();
 reviewRepository = new com.wedding.member06_review_feedback.repository.ReviewRepository();

 customerService = new com.wedding.member01_customer_admin.service.CustomerService(customerRepository);
 adminService = new com.wedding.member01_customer_admin.service.AdminService(adminRepository);
 vendorService = new com.wedding.member03_vendor.service.VendorService(vendorRepository);
 planService = new com.wedding.member02_wedding_plan.service.WeddingPlanService(planRepository);
 checklistService= new com.wedding.member02_wedding_plan.service.ChecklistService(checklistRepository);
 bookingService = new com.wedding.member04_booking.service.BookingService(bookingRepository, vendorService);
 paymentService = new com.wedding.member05_contract_payment.service.PaymentService(paymentRepository);
 contractService = new com.wedding.member05_contract_payment.service.ContractService(contractRepository, bookingRepository, paymentService);
 reviewService = new com.wedding.member06_review_feedback.service.ReviewService(reviewRepository, bookingRepository);
 }

 // ─── Getters ─────────────────────────────────────────────────────────────────
 public com.wedding.member01_customer_admin.service.CustomerService getCustomerService() { return customerService; }
 public com.wedding.member01_customer_admin.service.AdminService getAdminService() { return adminService; }
 public com.wedding.member02_wedding_plan.service.WeddingPlanService getPlanService() { return planService; }
 public com.wedding.member02_wedding_plan.service.ChecklistService getChecklistService(){ return checklistService; }
 public com.wedding.member03_vendor.service.VendorService getVendorService() { return vendorService; }
 public com.wedding.member04_booking.service.BookingService getBookingService() { return bookingService; }
 public com.wedding.member05_contract_payment.service.ContractService getContractService(){ return contractService; }
 public com.wedding.member05_contract_payment.service.PaymentService getPaymentService() { return paymentService; }
 public com.wedding.member06_review_feedback.service.ReviewService getReviewService() { return reviewService; }

 public com.wedding.member01_customer_admin.repository.CustomerRepository getCustomerRepository() { return customerRepository; }
 public com.wedding.member01_customer_admin.repository.AdminRepository getAdminRepository() { return adminRepository; }
 public com.wedding.member02_wedding_plan.repository.WeddingPlanRepository getPlanRepository() { return planRepository; }
 public com.wedding.member02_wedding_plan.repository.ChecklistTaskRepository getChecklistRepository() { return checklistRepository; }
 public com.wedding.member03_vendor.repository.VendorRepository getVendorRepository() { return vendorRepository; }
 public com.wedding.member04_booking.repository.BookingRepository getBookingRepository() { return bookingRepository; }
 public com.wedding.member05_contract_payment.repository.ContractRepository getContractRepository() { return contractRepository; }
 public com.wedding.member05_contract_payment.repository.PaymentRepository getPaymentRepository() { return paymentRepository; }
 public com.wedding.member06_review_feedback.repository.ReviewRepository getReviewRepository() { return reviewRepository; }
}
