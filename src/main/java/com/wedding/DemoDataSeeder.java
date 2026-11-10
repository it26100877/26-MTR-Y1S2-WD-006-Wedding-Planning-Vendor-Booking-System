package com.wedding;

import com.wedding.member01_customer_admin.model.Customer;
import com.wedding.member01_customer_admin.service.CustomerService;
import com.wedding.member02_wedding_plan.model.WeddingPlan;
import com.wedding.member02_wedding_plan.service.BudgetService;
import com.wedding.member02_wedding_plan.service.ChecklistService;
import com.wedding.member02_wedding_plan.service.GuestService;
import com.wedding.member02_wedding_plan.service.WeddingPlanService;
import com.wedding.member03_vendor.model.Vendor;
import com.wedding.member03_vendor.model.VendorCategory;
import com.wedding.member03_vendor.service.VendorService;
import com.wedding.member04_booking.model.Booking;
import com.wedding.member04_booking.service.BookingService;
import com.wedding.member05_contract_payment.model.VendorContract;
import com.wedding.member05_contract_payment.service.ContractService;
import com.wedding.member05_contract_payment.service.PaymentService;
import com.wedding.member06_review_feedback.service.ReviewService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Creates realistic, linked demonstration records for local/demo environments.
 * The seeder is idempotent: it checks stable demo emails before inserting records.
 * It is excluded from the production profile and can be disabled with
 * app.demo-data.enabled=false.
 */
@Component
@Profile("!prod")
public class DemoDataSeeder implements ApplicationRunner {

    private static final String DEMO_EMAIL = "demo.customer@example.test";
    private static final String DEMO_PASSWORD = "DemoWedding#2026";

    private final CustomerService customers;
    private final VendorService vendors;
    private final WeddingPlanService plans;
    private final ChecklistService checklist;
    private final BudgetService budget;
    private final GuestService guests;
    private final BookingService bookings;
    private final ContractService contracts;
    private final PaymentService payments;
    private final ReviewService reviews;
    private final boolean enabled;

    public DemoDataSeeder(CustomerService customers, VendorService vendors,
                          WeddingPlanService plans, ChecklistService checklist,
                          BudgetService budget, GuestService guests,
                          BookingService bookings, ContractService contracts,
                          PaymentService payments, ReviewService reviews,
                          @Value("${app.demo-data.enabled:false}") boolean enabled) {
        this.customers = customers;
        this.vendors = vendors;
        this.plans = plans;
        this.checklist = checklist;
        this.budget = budget;
        this.guests = guests;
        this.bookings = bookings;
        this.contracts = contracts;
        this.payments = payments;
        this.reviews = reviews;
        this.enabled = enabled;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) {
            System.out.println("[DemoData] Demo data seeding is disabled.");
            return;
        }
        seedVendors();
        seedLinkedCustomerJourney();
    }

    private void seedVendors() {
        String[][] records = {
            {"Amaya Wedding Photography","PHOTOGRAPHY","LKR 85,000"},
            {"Golden Hour Films","VIDEOGRAPHY","LKR 110,000"},
            {"Cinnamon Leaf Catering","CATERING","LKR 240,000"},
            {"Petal & Pearl Decor","DECORATION","LKR 95,000"},
            {"Velvet Sound DJs","MUSIC_DJ","LKR 65,000"},
            {"Ivory Bridal Studio","WEDDING_DRESSES","LKR 75,000"},
            {"Sugar Bloom Cakes","WEDDING_CAKES","LKR 28,000"},
            {"Blush Beauty Lounge","BEAUTY_SALON","LKR 32,000"},
            {"Ever After Planners","WEDDING_PLANNERS","LKR 125,000"},
            {"The Garden Pavilion","VENUES","LKR 350,000"},
            {"Nethmi Makeup Artistry","MAKEUP_ARTISTS","LKR 38,000"},
            {"Lace & Lily Bridal","BRIDAL_SALONS","LKR 55,000"},
            {"The Hair Atelier","HAIR_STYLISTS","LKR 22,000"},
            {"SnapNest Photo Booths","PHOTO_BOOTH","LKR 30,000"},
            {"Moments Social Studio","CONTENT_CREATOR","LKR 42,000"},
            {"Serene Vows Celebrants","WEDDING_OFFICIANTS","LKR 25,000"},
            {"Wildflower Collective","FLORISTS","LKR 48,000"},
            {"Lanka Wedding Essentials","OTHER","LKR 18,000"},
            {"Lightbox Wedding Stories","PHOTOGRAPHY","LKR 98,000"},
            {"Silver Frame Cinema","VIDEOGRAPHY","LKR 135,000"},
            {"Olive & Oak Banquets","CATERING","LKR 275,000"},
            {"Rosewood Event Styling","DECORATION","LKR 120,000"},
            {"The Rhythm Collective","MUSIC_DJ","LKR 78,000"},
            {"Maison de Mariée","WEDDING_DRESSES","LKR 92,000"},
            {"Cloud Nine Confections","WEDDING_CAKES","LKR 35,000"},
            {"Petal Skin & Beauty","BEAUTY_SALON","LKR 40,000"},
            {"White Aisle Coordination","WEDDING_PLANNERS","LKR 145,000"},
            {"Lakeside Grand Hall","VENUES","LKR 420,000"},
            {"Kavindi Bridal Makeup","MAKEUP_ARTISTS","LKR 45,000"},
            {"Bloom Room Florals","FLORISTS","LKR 58,000"}
        };
        List<Vendor> existing = vendors.getAllVendors();
        for (int i = 0; i < records.length; i++) {
            String email = String.format("demo.vendor%02d@example.test", i + 1);
            if (existing.stream().anyMatch(v -> email.equalsIgnoreCase(v.getEmail()))) continue;
            Vendor vendor = vendors.addVendor(records[i][0],
                    VendorCategory.fromStringStrict(records[i][1]),
                    "Demo Vendor Team", email, String.format("077000%04d", i + 1),
                    "Colombo, Sri Lanka",
                    records[i][0] + " provides professional wedding services with clear packages, "
                            + "experienced staff, and coordinated event-day support.",
                    new BigDecimal(records[i][2].replace("LKR ", "").replace(",", "")));
            vendors.approveVendor(vendor.getVendorId());
        }
        System.out.println("[DemoData] Vendor catalogue contains " + vendors.getAllVendors().size() + " records.");
    }

    private void seedLinkedCustomerJourney() {
        Customer customer = customers.getAllCustomers().stream()
                .filter(c -> DEMO_EMAIL.equalsIgnoreCase(c.getEmail()))
                .findFirst().orElseGet(() -> customers.register(
                        "Demo Couple", DEMO_EMAIL, "+94770002001", DEMO_PASSWORD,
                        "Colombo, Sri Lanka", LocalDate.now().plusDays(180).toString()));

        LocalDate eventDate = LocalDate.now().plusDays(180);
        WeddingPlan plan = plans.getByCustomerId(customer.getUserId()).stream()
                .filter(p -> "Demo Garden Wedding".equalsIgnoreCase(p.getPlanName()))
                .findFirst().orElseGet(() -> plans.createPlan(
                        customer.getUserId(), "Demo Garden Wedding", eventDate.toString(),
                        "The Garden Pavilion, Colombo", "Ivory & Sage",
                        "A sample wedding plan used to demonstrate the connected modules.",
                        "1500000", 120));

        if (checklist.getTasksByPlan(plan.getPlanId()).isEmpty()) {
            checklist.generateFromTemplate(plan);
        }
        if (budget.getItemsByPlan(plan.getPlanId()).isEmpty()) {
            budget.addItem(plan.getPlanId(), "VENUE", "Garden venue package", "350000", "50000", "Demo deposit allocation");
            budget.addItem(plan.getPlanId(), "CATERING", "Reception catering", "240000", "0", "Dinner for invited guests");
            budget.addItem(plan.getPlanId(), "PHOTOGRAPHY", "Photography package", "85000", "0", "Full-day coverage");
            budget.addItem(plan.getPlanId(), "DECOR", "Floral and table styling", "95000", "0", "Ivory and sage palette");
        }
        if (guests.getGuestsByPlan(plan.getPlanId()).isEmpty()) {
            guests.addGuest(plan.getPlanId(), "Amara Perera", "FAMILY", "amara@example.test", "3", "CONFIRMED", "Demo guest");
            guests.addGuest(plan.getPlanId(), "Kavish Fernando", "FRIENDS", "kavish@example.test", "2", "INVITED", "Demo guest");
            guests.addGuest(plan.getPlanId(), "Nimali Silva", "COLLEAGUES", "nimali@example.test", "1", "DECLINED", "Demo guest");
        }

        Vendor featured = vendors.getAllVendors().stream()
                .filter(v -> "demo.vendor01@example.test".equalsIgnoreCase(v.getEmail()))
                .findFirst().orElse(null);
        if (featured == null) return;

        Booking booking = bookings.getByCustomer(customer.getUserId()).stream()
                .filter(b -> plan.getPlanId().equals(b.getPlanId()) && featured.getVendorId().equals(b.getVendorId()))
                .findFirst().orElse(null);
        if (booking == null) {
            booking = bookings.createBooking(customer.getUserId(), featured.getVendorId(),
                    plan.getPlanId(), eventDate.toString(),
                    "Full-day wedding photography, couple portraits, ceremony and reception coverage.",
                    featured.getBasePrice(), "Linked demonstration booking.");
        }
        if (booking.getStatus() == com.wedding.member04_booking.model.BookingStatus.PENDING) {
            booking = bookings.confirmBooking(booking.getBookingId());
        }
        String linkedBookingId = booking.getBookingId();

        VendorContract contract = contracts.getAllContracts().stream()
                .filter(c -> linkedBookingId.equals(c.getBookingId()))
                .findFirst().orElse(null);
        if (contract == null) {
            contract = contracts.createContract(booking.getBookingId(), customer.getUserId(),
                    featured.getVendorId(), eventDate.toString(), eventDate.toString(),
                    booking.getAgreedAmount(),
                    "Demo agreement: service scope, event schedule, delivery timeline, and cancellation terms.");
        }
        if (payments.getAllPayments(contract.getContractId()).isEmpty()) {
            BigDecimal advance = contract.getContractAmount()
                    .multiply(new BigDecimal("0.25")).setScale(2, java.math.RoundingMode.HALF_UP);
            payments.recordAdvancePayment(contract, advance, LocalDate.now().toString(),
                    "CASH", "", "Demonstration advance payment; not a real transaction.", 25.0);
        }
        if (booking.getStatus() == com.wedding.member04_booking.model.BookingStatus.CONFIRMED) {
            bookings.completeBooking(booking.getBookingId());
        }
        if (reviews.getByVendor(featured.getVendorId()).stream()
                .noneMatch(r -> linkedBookingId.equals(r.getBookingId()))) {
            reviews.submitReview(customer.getUserId(), featured.getVendorId(), linkedBookingId,
                    5, "Beautiful demo experience",
                    "A sample verified review showing the completed booking feedback workflow.");
        }
    }
}
