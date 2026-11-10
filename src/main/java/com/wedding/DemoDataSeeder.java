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
import com.wedding.member04_booking.model.BookingStatus;
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
 * Seeds a clearly fictional Sri Lankan-style demonstration catalogue and a linked
 * ten-record customer journey for local/demo use. No real vendor contact details
 * or real transactions are fabricated. Idempotent by stable demo emails and names.
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
        seedTenLinkedJourneys();
    }

    /**
     * The first 30 records preserve the original demo catalogue. The next 25
     * broaden Sri Lankan district/category coverage. All are synthetic examples,
     * visibly described as demo listings and use reserved example.test contacts.
     */
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
            {"Bloom Room Florals","FLORISTS","LKR 58,000"},

            {"Kandy Hill Country Ballroom","VENUES","LKR 385000"},
            {"Negombo Lagoon Reception Hall","VENUES","LKR 295000"},
            {"Galle Fortside Wedding Pavilion","VENUES","LKR 425000"},
            {"Jaffna Heritage Celebration Hall","VENUES","LKR 225000"},
            {"Nuwara Eliya Garden Reception","VENUES","LKR 340000"},
            {"Colombo City Wedding Studio","PHOTOGRAPHY","LKR 125000"},
            {"Southern Light Wedding Films","VIDEOGRAPHY","LKR 145000"},
            {"Kandy Kandyan Floral Creations","FLORISTS","LKR 65000"},
            {"Negombo Seaside Catering Co","CATERING","LKR 285000"},
            {"Gampaha Poruwa & Decor House","DECORATION","LKR 115000"},
            {"Matara Rhythm Wedding DJs","MUSIC_DJ","LKR 75000"},
            {"Kurunegala Bridal Couture","WEDDING_DRESSES","LKR 98000"},
            {"Anuradhapura Sweet Vows Cakes","WEDDING_CAKES","LKR 42000"},
            {"Galle Bridal Beauty Lounge","MAKEUP_ARTISTS","LKR 55000"},
            {"Kegalle Wedding Hair Studio","HAIR_STYLISTS","LKR 28000"},
            {"Batticaloa Pearl Bridal Salon","BRIDAL_SALONS","LKR 72000"},
            {"Ratnapura Wedding Moments Photo Booth","PHOTO_BOOTH","LKR 35000"},
            {"Trincomalee Event Story Creators","CONTENT_CREATOR","LKR 48000"},
            {"Badulla Everlasting Wedding Planners","WEDDING_PLANNERS","LKR 165000"},
            {"Polonnaruwa Traditional Floral Decor","FLORISTS","LKR 58000"},
            {"Kalutara Grand Table Catering","CATERING","LKR 310000"},
            {"Matale Ivory Stage & Lighting","DECORATION","LKR 135000"},
            {"Hambantota Coastal Wedding Music","MUSIC_DJ","LKR 82000"},
            {"Vavuniya Bridal Cake Atelier","WEDDING_CAKES","LKR 39000"},
            {"Ampara Wedding Car & Event Support","OTHER","LKR 45000"}
        };

        List<Vendor> existing = vendors.getAllVendors();
        for (int i = 0; i < records.length; i++) {
            String email = String.format("demo.vendor%02d@example.test", i + 1);
            if (existing.stream().anyMatch(v -> email.equalsIgnoreCase(v.getEmail()))) continue;
            String district = districtFor(i);
            String category = records[i][1];
            String description = "Demonstration listing inspired by Sri Lankan wedding-market service categories. "
                    + "Sample packages and prices are illustrative only; contact details are reserved demo values.";
            Vendor vendor = vendors.addVendor(records[i][0],
                    VendorCategory.fromStringStrict(category),
                    "Demo Vendor Team", email, String.format("077000%04d", i + 1),
                    district + ", Sri Lanka", description,
                    new BigDecimal(records[i][2].replace("LKR ", "").replace(",", "")));
            vendors.approveVendor(vendor.getVendorId());
        }
        System.out.println("[DemoData] Vendor catalogue contains " + vendors.getAllVendors().size()
                + " records (55 seeded demo listings).");
    }

    private String districtFor(int index) {
        String[] districts = {"Colombo", "Gampaha", "Kandy", "Galle", "Matara", "Negombo",
                "Kurunegala", "Jaffna", "Nuwara Eliya", "Kalutara", "Batticaloa", "Anuradhapura",
                "Ratnapura", "Badulla", "Trincomalee", "Hambantota", "Kegalle", "Matale",
                "Vavuniya", "Ampara"};
        return districts[index % districts.length];
    }

    /**
     * Creates ten plans, ten bookings, ten contracts, ten advance-payment records
     * and ten verified-booking review submissions for the demo customer. Each
     * record is linked through the actual service layer and is safe to re-run.
     */
    private void seedTenLinkedJourneys() {
        Customer customer = customers.getAllCustomers().stream()
                .filter(c -> DEMO_EMAIL.equalsIgnoreCase(c.getEmail()))
                .findFirst().orElseGet(() -> customers.register(
                        "Demo Couple", DEMO_EMAIL, "+94770002001", DEMO_PASSWORD,
                        "Colombo, Sri Lanka", LocalDate.now().plusDays(180).toString()));

        String[] planNames = {
                "Demo Garden Wedding", "Demo Kandyan Celebration", "Demo Beach Wedding",
                "Demo Heritage Reception", "Demo Hill Country Wedding", "Demo Modern Colombo Wedding",
                "Demo Southern Garden Wedding", "Demo Northern Family Wedding",
                "Demo Classic Ballroom Wedding", "Demo Intimate Celebration"
        };
        String[] venues = {
                "The Garden Pavilion, Colombo", "Kandy Hill Country Ballroom, Kandy",
                "Negombo Lagoon Reception Hall, Negombo", "Galle Fortside Wedding Pavilion, Galle",
                "Nuwara Eliya Garden Reception, Nuwara Eliya", "Colombo City Wedding Studio, Colombo",
                "Southern Garden Venue, Matara", "Jaffna Heritage Celebration Hall, Jaffna",
                "Kurunegala Grand Ballroom, Kurunegala", "Kalutara Riverside Hall, Kalutara"
        };
        String[] themes = {"Ivory & Sage", "Kandyan Gold", "Coastal Blue", "Heritage Ivory",
                "Highland Romance", "Modern Minimal", "Tropical Garden", "Traditional Maroon",
                "Champagne Classic", "Blush Intimate"};
        int[] guestCounts = {120, 180, 90, 160, 100, 220, 140, 130, 200, 60};
        String[] budgets = {"1500000", "2200000", "1250000", "1900000", "1650000",
                "2800000", "1750000", "1450000", "2400000", "850000"};
        int[] vendorNumbers = {1, 31, 32, 33, 34, 35, 36, 37, 38, 39};

        for (int i = 0; i < planNames.length; i++) {
            final String planName = planNames[i];
            LocalDate eventDate = LocalDate.now().plusDays(180L + (i * 7L));
            WeddingPlan plan = plans.getByCustomerId(customer.getUserId()).stream()
                    .filter(p -> planName.equalsIgnoreCase(p.getPlanName()))
                    .findFirst().orElseGet(() -> plans.createPlan(
                            customer.getUserId(), planName, eventDate.toString(), venues[i],
                            themes[i], "Fictional Sri Lankan wedding demo record for the "
                                    + themes[i] + " celebration.", budgets[i], guestCounts[i]));

            if (checklist.getTasksByPlan(plan.getPlanId()).isEmpty()) {
                checklist.generateFromTemplate(plan);
            }
            if (budget.getItemsByPlan(plan.getPlanId()).isEmpty()) {
                budget.addItem(plan.getPlanId(), "VENUE", "Venue and reception package",
                        "350000", "50000", "Illustrative demo allocation");
                budget.addItem(plan.getPlanId(), "CATERING", "Reception catering",
                        "240000", "0", "Illustrative demo allocation");
                budget.addItem(plan.getPlanId(), "PHOTOGRAPHY", "Photography and video",
                        "125000", "0", "Illustrative demo allocation");
            }
            if (guests.getGuestsByPlan(plan.getPlanId()).isEmpty()) {
                guests.addGuest(plan.getPlanId(), "Amara Perera", "FAMILY", "amara@example.test", "3", "CONFIRMED", "Demo guest");
                guests.addGuest(plan.getPlanId(), "Kavish Fernando", "FRIENDS", "kavish@example.test", "2", "INVITED", "Demo guest");
                guests.addGuest(plan.getPlanId(), "Nimali Silva", "COLLEAGUES", "nimali@example.test", "1", "DECLINED", "Demo guest");
            }

            String vendorEmail = String.format("demo.vendor%02d@example.test", vendorNumbers[i]);
            Vendor vendor = vendors.getAllVendors().stream()
                    .filter(v -> vendorEmail.equalsIgnoreCase(v.getEmail()))
                    .findFirst().orElse(null);
            if (vendor == null) continue;

            Booking booking = bookings.getByCustomer(customer.getUserId()).stream()
                    .filter(b -> plan.getPlanId().equals(b.getPlanId())
                            && vendor.getVendorId().equals(b.getVendorId()))
                    .findFirst().orElse(null);
            if (booking == null) {
                booking = bookings.createBooking(customer.getUserId(), vendor.getVendorId(),
                        plan.getPlanId(), plan.getWeddingDate(),
                        "Demonstration package for " + plan.getPlanName() + ".",
                        vendor.getBasePrice(), "Fictional demo booking; no real reservation.");
            }
            if (booking.getStatus() == BookingStatus.PENDING) {
                booking = bookings.confirmBooking(booking.getBookingId());
            }

            VendorContract contract = contracts.getAllContracts().stream()
                    .filter(c -> booking.getBookingId().equals(c.getBookingId()))
                    .findFirst().orElse(null);
            if (contract == null) {
                contract = contracts.createContract(booking.getBookingId(), customer.getUserId(),
                        vendor.getVendorId(), plan.getWeddingDate(), plan.getWeddingDate(),
                        booking.getAgreedAmount(),
                        "Demo contract only. Scope, event schedule, delivery timeline and cancellation terms are illustrative.");
            }
            if (payments.getAllPayments(contract.getContractId()).isEmpty()) {
                BigDecimal advance = contract.getContractAmount()
                        .multiply(new BigDecimal("0.25")).setScale(2, java.math.RoundingMode.HALF_UP);
                payments.recordAdvancePayment(contract, advance, LocalDate.now().toString(),
                        "CASH", "", "Demo payment only; not a real transaction.", 25.0);
            }
            if (booking.getStatus() == BookingStatus.CONFIRMED) {
                booking = bookings.completeBooking(booking.getBookingId());
            }
            final String bookingId = booking.getBookingId();
            if (reviews.getByCustomer(customer.getUserId()).stream()
                    .noneMatch(r -> bookingId.equals(r.getBookingId()))) {
                reviews.submitReview(customer.getUserId(), vendor.getVendorId(), bookingId,
                        5, "Demo wedding service experience",
                        "This is a fictional sample review to demonstrate the verified booking feedback workflow.");
            }
        }
        System.out.println("[DemoData] Seeded ten linked demo journeys for customer " + customer.getUserId());
    }
}
