package com.wedding;

import com.wedding.common.exceptions.BusinessRuleException;
import com.wedding.member01_customer_admin.model.Customer;
import com.wedding.member01_customer_admin.service.AdminService;
import com.wedding.member01_customer_admin.service.CustomerService;
import com.wedding.member02_wedding_plan.model.WeddingPlan;
import com.wedding.member02_wedding_plan.service.ChecklistService;
import com.wedding.member02_wedding_plan.service.WeddingPlanService;
import com.wedding.member03_vendor.model.Vendor;
import com.wedding.member03_vendor.model.VendorCategory;
import com.wedding.member03_vendor.service.VendorService;
import com.wedding.member04_booking.model.Booking;
import com.wedding.member04_booking.model.BookingStatus;
import com.wedding.member04_booking.service.BookingService;
import com.wedding.member05_contract_payment.model.*;
import com.wedding.member05_contract_payment.service.ContractService;
import com.wedding.member05_contract_payment.service.PaymentService;
import com.wedding.member06_review_feedback.model.Review;
import com.wedding.member06_review_feedback.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockHttpSession;

import java.math.BigDecimal;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Comprehensive integration tests covering all six member modules,
 * OOP concepts, CRUD operations, and cross-module business rules.
 *
 * @author Wedding Planning System Development Team (SE1020)
 */
@SpringBootTest
@AutoConfigureMockMvc
public class WeddingApplicationTests {

    // Use the isolated test-data directory configured by Maven Surefire.
    // Never copy or modify the real project data directory.
    static {
        try {
            Path testData = Paths.get(System.getProperty("eclat.data.dir", "data"));
            Files.createDirectories(testData);

            String vendors = String.join(System.lineSeparator(),
                    "VND-101|Studio One Photography|PHOTOGRAPHY|Alex|studio101@test.com|0771111101|Colombo|Wedding photography|75000.00|AVAILABLE|APPROVED|2026-01-01|true",
                    "VND-102|Catering House|CATERING|Ben|catering102@test.com|0771111102|Colombo|Wedding catering|90000.00|AVAILABLE|APPROVED|2026-01-01|true",
                    "VND-103|Floral Dreams|FLORISTS|Cara|floral103@test.com|0771111103|Colombo|Wedding flowers|45000.00|AVAILABLE|APPROVED|2026-01-01|true",
                    "VND-104|DJ Sound System|MUSIC_DJ|Dan|dj104@test.com|0771111104|Colombo|DJ setup|80000.00|AVAILABLE|APPROVED|2026-01-01|true",
                    "VND-105|Cake Studio|WEDDING_CAKES|Eve|cake105@test.com|0771111105|Colombo|Wedding cakes|60000.00|AVAILABLE|APPROVED|2026-01-01|true");
            Files.writeString(testData.resolve("vendors.csv"), vendors + System.lineSeparator());
        } catch (IOException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    @Autowired private MockMvc mockMvc;
    @Autowired private CustomerService customerService;
    @Autowired private AdminService adminService;
    @Autowired private WeddingPlanService planService;
    @Autowired private ChecklistService checklistService;
    @Autowired private VendorService vendorService;
    @Autowired private BookingService bookingService;
    @Autowired private ContractService contractService;
    @Autowired private PaymentService paymentService;
    @Autowired private ReviewService reviewService;

    @Test
    @DisplayName("Module 01: Customer Registration and Authentication")
    void testCustomerManagement() {
        String testEmail = "test_couple_" + System.currentTimeMillis() + "@wedding.com";
        Customer customer = customerService.register(
                "Saman & Kumari", testEmail, "+94770001122", "secret123", "Colombo", "2027-05-20");

        assertNotNull(customer.getUserId());
        assertEquals("CUSTOMER", customer.getRole());
        assertTrue(customer.isActive());

        // Verify login
        Customer loggedIn = customerService.login(testEmail, "secret123");
        assertEquals(customer.getUserId(), loggedIn.getUserId());

        // Duplicate email must be rejected
        assertThrows(BusinessRuleException.class, () ->
                customerService.register("Clone", testEmail, "+94770001122", "secret123", "Colombo", "2027-05-20"));
    }

    @Test
    @DisplayName("Module 02: Wedding Plan and Checklist Completion Calculation")
    void testWeddingPlanAndChecklist() {
        WeddingPlan plan = planService.createPlan(
                "CST-1001", "Botanical Garden Wedding", "2027-06-15",
                "Peradeniya Botanical Gardens", "Eco Green", "Outdoor morning wedding", "10000.00");

        assertNotNull(plan.getPlanId());
        assertEquals("ACTIVE", plan.getStatus());

        // Add Checklist Tasks
        checklistService.addTask(plan.getPlanId(), "Book Florist", "Fresh lilies", "2027-04-01", "HIGH");
        var task2 = checklistService.addTask(plan.getPlanId(), "Tasting", "Cake tasting", "2027-04-10", "LOW");

        checklistService.markComplete(task2.getTaskId());
        int percent = checklistService.getCompletionPercent(plan.getPlanId());
        assertEquals(50, percent);
    }

    @Test
    @DisplayName("Module 03: Vendor Category Lookup and Filtering")
    void testVendorManagement() {
        List<Vendor> photoVendors = vendorService.searchVendors("", VendorCategory.PHOTOGRAPHY, null);
        assertFalse(photoVendors.isEmpty());
        assertEquals(VendorCategory.PHOTOGRAPHY, photoVendors.get(0).getCategory());
    }

    @Test
    @DisplayName("Module 04: Booking Conflict Prevention on Same Vendor & Date")
    void testBookingConflict() {
        // Book VND-104 for a dynamic future date
        long ts = System.currentTimeMillis() % 10000;
        String date = java.time.LocalDate.now().plusDays(200 + ts).toString();
        Booking b1 = bookingService.createBooking(
                "CST-1001", "VND-104", "PLN-201", date, "DJ full setup", new BigDecimal("800.00"), "First booking");
        bookingService.confirmBooking(b1.getBookingId());

        // Second booking for the same vendor on the same date must be rejected
        assertThrows(BusinessRuleException.class, () ->
                bookingService.createBooking("CST-1002", "VND-104", "PLN-202", date, "DJ setup duplicate", new BigDecimal("800.00"), ""));
    }

    @Test
    @DisplayName("Module 05: Contract & Polymorphic Payments (Advance, Partial, Final)")
    void testContractAndPolymorphicPayments() {
        // Create a confirmed booking first with dynamic future date
        long ts = System.currentTimeMillis() % 10000;
        String date = java.time.LocalDate.now().plusDays(400 + ts).toString();
        Booking b = bookingService.createBooking(
                "CST-1001", "VND-105", "PLN-201", date, "Cake 5 tier", new BigDecimal("600.00"), "Test cake");
        bookingService.confirmBooking(b.getBookingId());

        // Create Contract
        VendorContract contract = contractService.createContract(
                b.getBookingId(), "CST-1001", "VND-105", date, date, new BigDecimal("600.00"), "Cake contract terms");

        assertEquals(new BigDecimal("600.00"), contract.getContractAmount());
        assertEquals("UNPAID", contract.getPaymentStatus());

        // 1. Advance Payment ($200) - Polymorphism test
        AdvancePayment adv = paymentService.recordAdvancePayment(
                contract, new BigDecimal("200.00"), "2027-01-01", "BANK_TRANSFER", "TXN-ADV", "20% Deposit", 33.3);
        assertEquals("ADVANCE", adv.getPaymentType());
        contractService.refreshTotals(contract.getContractId());
        assertEquals(new BigDecimal("400.00"), paymentService.getOutstandingBalance(contract));

        // 2. Partial Payment ($100)
        PartialPayment part = paymentService.recordPartialPayment(
                contract, new BigDecimal("100.00"), "2027-01-15", "CASH", "REC-01", "Milestone 2", 2);
        assertEquals("PARTIAL", part.getPaymentType());
        contractService.refreshTotals(contract.getContractId());
        assertEquals(new BigDecimal("300.00"), paymentService.getOutstandingBalance(contract));

        // Overpayment must be rejected
        assertThrows(BusinessRuleException.class, () ->
                paymentService.recordAdvancePayment(contract, new BigDecimal("500.00"), "2027-01-20", "CASH", "", "", 25.0));

        // 3. Final Settlement ($300)
        FinalPayment fin = paymentService.recordFinalPayment(
                contract, new BigDecimal("300.00"), "2027-02-01", "CARD", "TXN-FIN", "Settled", "REC-FINAL");
        assertEquals("FINAL", fin.getPaymentType());
        contractService.refreshTotals(contract.getContractId());

        assertEquals(0, paymentService.getOutstandingBalance(contract).compareTo(BigDecimal.ZERO));
        assertEquals("FULLY_PAID", contractService.getById(contract.getContractId()).getPaymentStatus());
    }

    @Test
    @DisplayName("Module 06: Verified Review Submission and Dynamic Rating")
    void testVerifiedReviewAndDynamicRating() {
        double initialRating = reviewService.getAverageRating("VND-101");
        assertTrue(initialRating >= 0.0);

        // Create a real PENDING booking; only COMPLETED bookings can be reviewed.
        Booking pendingBooking = bookingService.createBooking(
                "CST-1002",
                "VND-102",
                "PLN-201",
                java.time.LocalDate.now().plusDays(30).toString(),
                "Catering service for wedding",
                new BigDecimal("90000.00"),
                "Review validation test"
        );

        assertThrows(BusinessRuleException.class, () ->
                reviewService.submitReview(
                        "CST-1002",
                        "VND-102",
                        pendingBooking.getBookingId(),
                        5,
                        "Great",
                        "Catering was wonderful and tasty"
                ));
    }

    @Test
    @DisplayName("Web MVC: Controllers and JSP View Resolvers")
    void testWebControllersAndViews() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("member01/login"));

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("member01/register"));

        mockMvc.perform(get("/vendors"))
                .andExpect(status().isOk())
                .andExpect(view().name("member03/vendors"));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("public-home"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.model().attributeExists("featuredVendors"));

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/contracts/view/CON-501"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/plans"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/bookings"))
                .andExpect(status().is3xxRedirection());
    }


    @Test
    @DisplayName("Vendor Directory: category-only filter returns matching vendors only")
    void testVendorCategoryOnlyFilter() throws Exception {
        var result = mockMvc.perform(get("/vendors").param("category", "PHOTOGRAPHY"))
                .andExpect(status().isOk())
                .andExpect(view().name("member03/vendors"))
                .andReturn();

        @SuppressWarnings("unchecked")
        List<Vendor> filtered = (List<Vendor>) result.getModelAndView().getModel().get("vendors");
        assertNotNull(filtered);
        assertFalse(filtered.isEmpty());
        assertTrue(filtered.stream().allMatch(v -> v.getCategory() == VendorCategory.PHOTOGRAPHY));

        var districtResult = mockMvc.perform(get("/vendors").param("location", "Colombo"))
                .andExpect(status().isOk())
                .andReturn();
        @SuppressWarnings("unchecked")
        List<Vendor> districtFiltered = (List<Vendor>) districtResult.getModelAndView().getModel().get("vendors");
        assertNotNull(districtFiltered);
        assertTrue(districtFiltered.stream().allMatch(v -> v.getAddress().toLowerCase().contains("colombo")));
    }

    @Test
    @DisplayName("Security: protected state-changing actions redirect anonymous visitors")
    void testProtectedActionsRequireLogin() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/bookings/confirm/BKG-401"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/contracts/complete/CON-501"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/payments/void")
                        .param("paymentId", "PAY-501").param("contractId", "CON-501").param("reason", "test"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/admin/vendors/delete/VND-101"))
                .andExpect(status().is3xxRedirection());
    }


    @Test
    @DisplayName("Customer UI: core pages render for an authenticated customer")
    void testCustomerPagesRender() throws Exception {
        String email = "ui_customer_" + System.currentTimeMillis() + "@eclat.test";
        Customer customer = customerService.register("UI Test Couple", email, "+94770009988",
                "testpass123", "Colombo", java.time.LocalDate.now().plusDays(300).toString());
        WeddingPlan plan = planService.createPlan(customer.getUserId(), "UI Test Wedding",
                java.time.LocalDate.now().plusDays(300).toString(), "Colombo", "Classic", "UI test", "250000.00");
        checklistService.addTask(plan.getPlanId(), "Book photographer", "Confirm package",
                java.time.LocalDate.now().plusDays(30).toString(), "HIGH");
        Booking booking = bookingService.createBooking(customer.getUserId(), "VND-101", plan.getPlanId(),
                java.time.LocalDate.now().plusDays(300).toString(), "Photography package",
                new BigDecimal("1500.00"), "UI test booking");
        Booking pendingEditBooking = bookingService.createBooking(customer.getUserId(), "VND-103", plan.getPlanId(),
                java.time.LocalDate.now().plusDays(301).toString(), "Floral decoration package",
                new BigDecimal("1800.00"), "Pending edit test");
        bookingService.confirmBooking(booking.getBookingId());
        bookingService.completeBooking(booking.getBookingId());
        VendorContract contract = contractService.createContract(booking.getBookingId(), customer.getUserId(),
                "VND-101", booking.getBookingDate(), booking.getBookingDate(),
                new BigDecimal("1500.00"), "UI test contract");
        paymentService.recordFinalPayment(contract, new BigDecimal("1500.00"), java.time.LocalDate.now().toString(),
                "CASH", "UI-TEST", "Test settlement", "UI-REC");
        contractService.refreshTotals(contract.getContractId());
        reviewService.submitReview(customer.getUserId(), "VND-101", booking.getBookingId(),
                5, "Excellent service", "The photographer delivered excellent photos and service.");

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("currentUser", customer);

        mockMvc.perform(get("/dashboard").session(session)).andExpect(status().isOk()).andExpect(view().name("dashboard"));
        mockMvc.perform(get("/profile").session(session)).andExpect(status().isOk()).andExpect(view().name("member01/profile"));
        mockMvc.perform(get("/plans").session(session)).andExpect(status().isOk()).andExpect(view().name("member02/plans"));
        mockMvc.perform(get("/plans/new").session(session)).andExpect(status().isOk()).andExpect(view().name("member02/plan-form"));
        mockMvc.perform(get("/plans/edit/" + plan.getPlanId()).session(session)).andExpect(status().isOk()).andExpect(view().name("member02/plan-form"));
        mockMvc.perform(get("/plans/" + plan.getPlanId() + "/checklist").session(session)).andExpect(status().isOk()).andExpect(view().name("member02/checklist"));
        mockMvc.perform(get("/vendors/new").session(session)).andExpect(status().isOk()).andExpect(view().name("member03/vendor-form"));
        mockMvc.perform(get("/vendors/view/VND-101").session(session)).andExpect(status().isOk()).andExpect(view().name("member03/vendor-detail"));
        mockMvc.perform(get("/bookings").session(session)).andExpect(status().isOk()).andExpect(view().name("member04/bookings"));
        mockMvc.perform(get("/bookings/new").session(session)).andExpect(status().isOk()).andExpect(view().name("member04/booking-form"));
        mockMvc.perform(get("/bookings/edit/" + pendingEditBooking.getBookingId()).session(session)).andExpect(status().isOk()).andExpect(view().name("member04/booking-edit-form"));
        mockMvc.perform(get("/bookings/view/" + booking.getBookingId()).session(session)).andExpect(status().isOk()).andExpect(view().name("member04/booking-detail"));
        mockMvc.perform(get("/contracts").session(session)).andExpect(status().isOk()).andExpect(view().name("member05/contracts"));
        mockMvc.perform(get("/contracts/view/" + contract.getContractId()).session(session)).andExpect(status().isOk()).andExpect(view().name("member05/payment-detail"));
        mockMvc.perform(get("/reviews").session(session)).andExpect(status().isOk()).andExpect(view().name("member06/reviews"));
        mockMvc.perform(get("/reviews/new").session(session)).andExpect(status().isOk()).andExpect(view().name("member06/review-form"));

        Customer otherCustomer = customerService.register("Other Test Couple",
                "other_ui_" + System.currentTimeMillis() + "@eclat.test", "+94770008877",
                "testpass123", "Kandy", java.time.LocalDate.now().plusDays(400).toString());
        MockHttpSession otherSession = new MockHttpSession();
        otherSession.setAttribute("currentUser", otherCustomer);
        mockMvc.perform(get("/plans/edit/" + plan.getPlanId()).session(otherSession))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/bookings/view/" + booking.getBookingId()).session(otherSession))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/contracts/view/" + contract.getContractId()).session(otherSession))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Admin UI: administration pages render and protected moderation is accessible")
    void testAdminPagesRender() throws Exception {
        String adminEmail = "test_admin_" + System.currentTimeMillis() + "@eclat.test";
        adminService.createAdmin("Test Administrator", adminEmail, "+94770001234",
                "SecureTestAdmin123", "SUPER");
        var admin = adminService.login(adminEmail, "SecureTestAdmin123");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("currentUser", admin);

        // Create real bookings and contracts instead of relying on hardcoded CSV IDs.
        String bookingDate1 = java.time.LocalDate.now().plusDays(500).toString();
        Booking booking1 = bookingService.createBooking(
                "CST-1001", "VND-101", "PLN-201", bookingDate1,
                "Photography service", new BigDecimal("75000.00"), "Admin UI test");
        bookingService.confirmBooking(booking1.getBookingId());

        VendorContract contract1 = contractService.createContract(
                booking1.getBookingId(), "CST-1001", "VND-101",
                bookingDate1, bookingDate1, new BigDecimal("75000.00"), "Admin UI test contract");

        String bookingDate2 = java.time.LocalDate.now().plusDays(501).toString();
        Booking booking2 = bookingService.createBooking(
                "CST-1002", "VND-102", "PLN-202", bookingDate2,
                "Catering service", new BigDecimal("90000.00"), "Admin UI test");
        bookingService.confirmBooking(booking2.getBookingId());

        VendorContract contract2 = contractService.createContract(
                booking2.getBookingId(), "CST-1002", "VND-102",
                bookingDate2, bookingDate2, new BigDecimal("90000.00"), "Admin UI edit test contract");

        mockMvc.perform(get("/admin/dashboard").session(session)).andExpect(status().isOk()).andExpect(view().name("member01/admin-dashboard"));
        mockMvc.perform(get("/vendors?filter=PENDING").session(session)).andExpect(status().isOk()).andExpect(view().name("member03/vendors"));
        mockMvc.perform(get("/vendors/edit/VND-101").session(session)).andExpect(status().isOk()).andExpect(view().name("member03/vendor-form"));
        mockMvc.perform(get("/admin/reviews/moderation").session(session)).andExpect(status().isOk()).andExpect(view().name("member06/review-moderation"));
        mockMvc.perform(get("/bookings/view/" + booking1.getBookingId()).session(session)).andExpect(status().isOk()).andExpect(view().name("member04/booking-detail"));
        mockMvc.perform(get("/contracts/view/" + contract1.getContractId()).session(session)).andExpect(status().isOk()).andExpect(view().name("member05/payment-detail"));
        mockMvc.perform(get("/contracts/edit/" + contract2.getContractId()).session(session)).andExpect(status().isOk()).andExpect(view().name("member05/contract-edit-form"));
    }
}
