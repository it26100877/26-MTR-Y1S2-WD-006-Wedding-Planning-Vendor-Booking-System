package lk.weddingly.web;

import lk.weddingly.model.Booking;
import lk.weddingly.model.BookingRecord;
import lk.weddingly.repository.BookingRepository;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/bookings")
public class BookingServlet extends HttpServlet {
    private BookingRepository repository;
    @Override public void init() { repository = new BookingRepository(getServletContext()); }

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if ("new".equals(action) || "edit".equals(action)) {
            Booking booking = "edit".equals(action) ? repository.findById(request.getParameter("id")) : null;
            if ("edit".equals(action) && booking == null) { response.sendRedirect(request.getContextPath()+"/bookings?missing=1"); return; }
            request.setAttribute("booking", booking);
            request.getRequestDispatcher("/WEB-INF/views/booking-form.jsp").forward(request, response);
            return;
        }
        List<Booking> bookings = repository.findAll();
        String query = request.getParameter("q");
        if (query != null && !query.trim().isEmpty()) {
            String term = query.trim().toLowerCase();
            List<Booking> filtered = new ArrayList<>();
            for (Booking b : bookings) if (b.getCustomerName().toLowerCase().contains(term)
                    || b.getVendorName().toLowerCase().contains(term) || b.getServiceName().toLowerCase().contains(term)
                    || b.getCustomerId().toLowerCase().contains(term) || b.getVendorId().toLowerCase().contains(term)) filtered.add(b);
            bookings = filtered;
        }
        List<String> types = new ArrayList<>();
        for (BookingRecord record : bookings) types.add(record.getRecordType()); // polymorphic call
        request.setAttribute("bookings", bookings);
        request.setAttribute("recordTypes", types);
        request.getRequestDispatcher("/WEB-INF/views/bookings.jsp").forward(request, response);
    }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = val(request,"action");
        if ("delete".equals(action)) {
            repository.delete(val(request,"id"));
            response.sendRedirect(request.getContextPath()+"/bookings?deleted=1");
            return;
        }
        String id=val(request,"id"), customerId=val(request,"customerId"), customer=val(request,"customerName");
        String vendorId=val(request,"vendorId"), vendor=val(request,"vendorName"), service=val(request,"serviceName");
        String date=val(request,"eventDate"), status=val(request,"status"), amount=val(request,"amount"), notes=val(request,"notes");
        List<String> errors=new ArrayList<>(); LocalDate eventDate=null; BigDecimal parsedAmount=BigDecimal.ZERO;
        if(customerId.isEmpty()||customer.isEmpty()) errors.add("Enter the customer ID and name.");
        if(vendorId.isEmpty()||vendor.isEmpty()) errors.add("Enter the vendor ID and name.");
        if(service.isEmpty()) errors.add("Enter the booked service.");
        try { eventDate=LocalDate.parse(date); if(eventDate.isBefore(LocalDate.now())) errors.add("Choose today or a future event date."); }
        catch(Exception e){errors.add("Enter a valid event date.");}
        if(!List.of("Pending","Confirmed","Completed","Cancelled").contains(status)) errors.add("Choose a valid booking status.");
        try {parsedAmount=new BigDecimal(amount); if(parsedAmount.signum()<0) errors.add("Amount cannot be negative.");}
        catch(Exception e){errors.add("Enter a valid booking amount.");}
        Booking booking=new Booking(id,customerId,customer,vendorId,vendor,service,eventDate,status,parsedAmount,notes);
        if(!errors.isEmpty()){
            request.setAttribute("errors",errors); request.setAttribute("booking",booking);
            request.getRequestDispatcher("/WEB-INF/views/booking-form.jsp").forward(request,response); return;
        }
        repository.save(booking);
        response.sendRedirect(request.getContextPath()+"/bookings?saved=1");
    }

    private String val(HttpServletRequest request,String key){String v=request.getParameter(key);return v==null?"":v.trim();}
}
