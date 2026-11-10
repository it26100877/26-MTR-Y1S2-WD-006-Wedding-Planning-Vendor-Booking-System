package com.wedding.member04_booking.controller;

import com.wedding.common.exceptions.AppException;
import com.wedding.common.model.User;
import com.wedding.member01_customer_admin.service.CustomerService;
import com.wedding.member03_vendor.model.Vendor;
import com.wedding.member03_vendor.service.VendorService;
import com.wedding.member04_booking.model.Booking;
import com.wedding.member04_booking.model.BookingStatus;
import com.wedding.member04_booking.service.BookingNotificationService;
import com.wedding.member04_booking.service.BookingPdfReceipt;
import com.wedding.member04_booking.service.BookingService;
import com.wedding.member02_wedding_plan.service.WeddingPlanService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/** Member 04: availability, notifications, reporting and downloadable receipt routes. */
@Controller
public class BookingInsightsController {
    private final BookingService bookings;
    private final VendorService vendors;
    private final CustomerService customers;
    private final WeddingPlanService plans;
    private final BookingNotificationService notifications;
    public BookingInsightsController(BookingService bookings, VendorService vendors,
             CustomerService customers, WeddingPlanService plans, BookingNotificationService notifications) {
        this.bookings = bookings; this.vendors = vendors; this.customers = customers;
        this.plans = plans; this.notifications = notifications;
    }
    private User user(HttpSession session) { return (User) session.getAttribute("currentUser"); }
    private boolean admin(User user) { return user != null && "ADMIN".equalsIgnoreCase(user.getRole()); }
    private String recipient(User u) { return admin(u) ? "ADMIN" : u.getUserId(); }
    private String vendorName(String id) { try { return vendors.getById(id).getVendorName(); } catch (Exception e) { return id; } }
    private String customerName(String id) { try { return customers.getById(id).getName(); } catch (Exception e) { return id; } }
    private String planName(String id) { try { return plans.getById(id).getPlanName(); } catch (Exception e) { return id; } }

    /** Calendar metadata exposed as simple JavaBean properties for JSP EL. */
    public static class CalendarDay {
        private final int dayOfMonth;
        private final String date;
        private final boolean currentMonth;
        private final String state;
        private final int requests;
        public CalendarDay(int n, String date, boolean currentMonth, String state, int requests) {
            this.dayOfMonth=n; this.date=date; this.currentMonth=currentMonth;
            this.state=state; this.requests=requests;
        }
        public int getDayOfMonth(){return dayOfMonth;}
        public String getDate(){return date;}
        public boolean isCurrentMonth(){return currentMonth;}
        public String getState(){return state;}
        public int getRequests(){return requests;}
    }
    @GetMapping("/bookings/calendar")
    public String calendar(@RequestParam(value="month", required=false) String month,
            @RequestParam(value="vendorId", required=false) String vendorId,
            HttpSession session, Model model) {
        User actor=user(session); if(actor==null) return "redirect:/login";
        YearMonth ym;
        try { ym = month == null || month.isBlank() ? YearMonth.now() : YearMonth.parse(month); }
        catch (DateTimeParseException e) { ym=YearMonth.now(); }
        if (ym.isBefore(YearMonth.now().minusYears(1)) || ym.isAfter(YearMonth.now().plusYears(3))) ym=YearMonth.now();
        List<Vendor> choices=vendors.getApprovedVendors().stream().filter(Vendor::isActive).toList();
        Vendor selected=null;
        if (vendorId != null && !vendorId.isBlank()) selected=choices.stream()
            .filter(v -> v.getVendorId().equals(vendorId)).findFirst().orElse(null);
        if (selected == null && !choices.isEmpty()) selected=choices.get(0);
        Map<String,List<Booking>> byDate = selected == null ? Collections.emptyMap() :
            bookings.getByVendor(selected.getVendorId()).stream()
              .collect(Collectors.groupingBy(Booking::getBookingDate));
        LocalDate first=ym.atDay(1);
        int pad=first.getDayOfWeek().getValue()-1;
        List<CalendarDay> days=new ArrayList<>();
        for(int i=0;i<42;i++) {
            LocalDate date=first.plusDays(i-pad);
            List<Booking> matches=byDate.getOrDefault(date.toString(), List.of());
            boolean occupied=matches.stream().anyMatch(b -> b.getStatus()==BookingStatus.CONFIRMED || b.getStatus()==BookingStatus.COMPLETED);
            int pending=(int) matches.stream().filter(b -> b.getStatus()==BookingStatus.PENDING).count();
            String state=date.isBefore(LocalDate.now().plusDays(1)) ? "past" : occupied ? "booked" : pending>0 ? "requested" : "available";
            days.add(new CalendarDay(date.getDayOfMonth(),date.toString(), YearMonth.from(date).equals(ym),state,pending));
        }
        model.addAttribute("calendarDays",days); model.addAttribute("vendors",choices);
        model.addAttribute("selectedVendor",selected); model.addAttribute("month",ym.toString());
        model.addAttribute("monthLabel",ym.getMonth().getDisplayName(java.time.format.TextStyle.FULL,Locale.ENGLISH)+" "+ym.getYear());
        model.addAttribute("previousMonth",ym.minusMonths(1).toString());
        model.addAttribute("nextMonth",ym.plusMonths(1).toString());
        model.addAttribute("isBookingAdmin",admin(actor));
        model.addAttribute("pageTitle","Availability Calendar - Eclat"); model.addAttribute("activePage","bookings");
        return "member04/booking-calendar";
    }

    @GetMapping("/bookings/notifications")
    public String notificationPage(HttpSession session,Model model) {
        User actor=user(session); if(actor==null) return "redirect:/login";
        model.addAttribute("notifications", notifications.forRecipient(recipient(actor)));
        model.addAttribute("unreadNotifications", notifications.unreadCount(recipient(actor)));
        model.addAttribute("pageTitle","Booking Notifications - Eclat"); model.addAttribute("activePage","bookings");
        return "member04/booking-notifications";
    }
    @PostMapping("/bookings/notifications/read/{id}")
    public String markRead(@PathVariable("id") String id,HttpSession session) {
        User actor=user(session); if(actor==null) return "redirect:/login";
        notifications.markRead(recipient(actor),id);
        return "redirect:/bookings/notifications";
    }
    @PostMapping("/bookings/notifications/read-all")
    public String markAllRead(HttpSession session) {
        User actor=user(session); if(actor==null) return "redirect:/login";
        notifications.markAllRead(recipient(actor));
        return "redirect:/bookings/notifications";
    }
    private List<Booking> reportBookings(String month,String vendorId) {
        List<Booking> result=bookings.getAllBookings();
        if(month!=null && !month.isBlank()) result=result.stream().filter(b -> b.getBookingDate().startsWith(month)).toList();
        if(vendorId!=null && !vendorId.isBlank()) result=result.stream().filter(b -> b.getVendorId().equals(vendorId)).toList();
        return result;
    }
    @GetMapping("/bookings/reports")
    public String reports(@RequestParam(value="month",required=false) String month,
             @RequestParam(value="vendorId",required=false) String vendorId,
             HttpSession session,Model model) {
        User actor=user(session); if(actor==null) return "redirect:/login";
        if(!admin(actor)) return "redirect:/bookings";
        String selectedMonth=validMonth(month);
        List<Booking> selected=reportBookings(selectedMonth,vendorId);
        Map<String,String> vnames=new HashMap<>(),cnames=new HashMap<>();
        for(Booking b:selected) { vnames.putIfAbsent(b.getVendorId(),vendorName(b.getVendorId()));
             cnames.putIfAbsent(b.getCustomerId(),customerName(b.getCustomerId())); }
        BigDecimal value=selected.stream().filter(b -> b.getStatus()==BookingStatus.CONFIRMED || b.getStatus()==BookingStatus.COMPLETED)
            .map(Booking::getAgreedAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add);
        Map<BookingStatus,Long> counts=new EnumMap<>(BookingStatus.class);
        for(BookingStatus st:BookingStatus.values()) counts.put(st,selected.stream().filter(b -> b.getStatus()==st).count());
        model.addAttribute("bookings",selected);model.addAttribute("counts",counts);
        model.addAttribute("confirmedReportCount",counts.get(BookingStatus.CONFIRMED)+counts.get(BookingStatus.COMPLETED));
        model.addAttribute("totalValue",value);model.addAttribute("vendorNames",vnames);
        model.addAttribute("customerNames",cnames);model.addAttribute("statuses",BookingStatus.values());
        model.addAttribute("vendors",vendors.getApprovedVendors());model.addAttribute("selectedMonth",selectedMonth);
        model.addAttribute("selectedVendorId",vendorId==null?"":vendorId);
        model.addAttribute("pageTitle","Booking Reports - Eclat");model.addAttribute("activePage","bookings");
        return "member04/booking-reports";
    }
    private String validMonth(String month) {
        if (month==null || month.isBlank()) return YearMonth.now().toString();
        try { return YearMonth.parse(month).toString(); }
        catch (DateTimeParseException e) { return YearMonth.now().toString(); }
    }
    private String csv(String text) {
        String safe=text==null?"":text.replaceAll("^[=+@\\-\\t\\r]+", "'");
        return "\""+safe.replace("\"","\"\"").replace("\r"," ").replace("\n"," ")+"\"";
    }
    @GetMapping("/bookings/reports/export")
    public void export(@RequestParam(value="month",required=false) String month,
              @RequestParam(value="vendorId",required=false) String vendorId,
              HttpSession session,HttpServletResponse response) throws IOException {
        User actor=user(session);
        if(actor==null || !admin(actor)) { response.sendError(403);return; }
        String selected=validMonth(month);
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=booking-report-"+selected+".csv");
        StringBuilder csv=new StringBuilder("\uFEFFBooking ID,Customer,Vendor,Event Date,Status,Amount (LKR),Requested At\r\n");
        for(Booking b:reportBookings(selected,vendorId))
            csv.append(csv(b.getBookingId())).append(',').append(csv(customerName(b.getCustomerId()))).append(',')
               .append(csv(vendorName(b.getVendorId()))).append(',').append(csv(b.getBookingDate())).append(',')
               .append(csv(b.getStatus().name())).append(',').append(b.getAgreedAmount()).append(',')
               .append(csv(b.getRequestedAt())).append("\r\n");
        response.getOutputStream().write(csv.toString().getBytes(StandardCharsets.UTF_8));
    }
    @GetMapping("/bookings/receipt/{id}")
    public String receipt(@PathVariable("id") String id, HttpSession session, Model model,
                          RedirectAttributes redirectAttributes) {
        User actor = user(session);
        if (actor == null) return "redirect:/login";
        Booking booking;
        try {
            booking = bookings.getById(id);
        } catch (AppException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Booking not found.");
            return "redirect:/bookings";
        }
        if (!admin(actor) && !actor.getUserId().equalsIgnoreCase(booking.getCustomerId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You are not allowed to view this receipt.");
            return "redirect:/bookings";
        }
        if (booking.getStatus() != BookingStatus.CONFIRMED && booking.getStatus() != BookingStatus.COMPLETED) {
            redirectAttributes.addFlashAttribute("errorMessage", "Only confirmed or completed bookings have receipts.");
            return "redirect:/bookings/view/" + id;
        }
        model.addAttribute("booking", booking);
        model.addAttribute("customerName", customerName(booking.getCustomerId()));
        model.addAttribute("vendor", vendors.getById(booking.getVendorId()));
        model.addAttribute("plan", booking.getPlanId() == null || booking.getPlanId().isBlank()
                ? null : plans.getById(booking.getPlanId()));
        model.addAttribute("pageTitle", "Booking Receipt - " + booking.getBookingId());
        return "member04/booking-receipt";
    }
}
