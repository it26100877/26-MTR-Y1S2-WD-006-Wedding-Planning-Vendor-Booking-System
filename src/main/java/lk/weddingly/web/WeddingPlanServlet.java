package lk.weddingly.web;

import lk.weddingly.model.WeddingPlan;
import lk.weddingly.model.WeddingRecord;
import lk.weddingly.repository.WeddingPlanRepository;

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

@WebServlet("/weddings")
public class WeddingPlanServlet extends HttpServlet {
    private WeddingPlanRepository repository;

    @Override public void init() { repository = new WeddingPlanRepository(getServletContext()); }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if ("new".equals(action) || "edit".equals(action)) {
            WeddingPlan plan = "edit".equals(action) ? repository.findById(request.getParameter("id")) : null;
            if ("edit".equals(action) && plan == null) { response.sendRedirect(request.getContextPath() + "/weddings?missing=1"); return; }
            request.setAttribute("plan", plan);
            request.getRequestDispatcher("/WEB-INF/views/plan-form.jsp").forward(request, response);
            return;
        }
        List<WeddingPlan> plans = repository.findAll();
        String query = request.getParameter("q");
        if (query != null && !query.trim().isEmpty()) {
            String term = query.trim().toLowerCase();
            List<WeddingPlan> filtered = new ArrayList<>();
            for (WeddingPlan p : plans) {
                if (p.getCoupleNames().toLowerCase().contains(term) || p.getVenue().toLowerCase().contains(term)
                        || p.getTheme().toLowerCase().contains(term)) filtered.add(p);
            }
            plans = filtered;
        }
        // Demonstrate polymorphism through the abstract parent type.
        List<String> labels = new ArrayList<>();
        for (WeddingRecord record : plans) labels.add(record.getPlanLabel());
        request.setAttribute("plans", plans);
        request.setAttribute("labels", labels);
        request.getRequestDispatcher("/WEB-INF/views/plans.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if ("delete".equals(action)) {
            repository.delete(request.getParameter("id"));
            response.sendRedirect(request.getContextPath() + "/weddings?deleted=1");
            return;
        }
        List<String> errors = new ArrayList<>();
        String id = value(request, "id");
        String names = value(request, "coupleNames");
        String date = value(request, "weddingDate");
        String venue = value(request, "venue");
        String guests = value(request, "guestCount");
        String budget = value(request, "budget");
        String theme = value(request, "theme");
        String notes = value(request, "notes");
        LocalDate parsedDate = null;
        int parsedGuests = 0;
        BigDecimal parsedBudget = BigDecimal.ZERO;
        if (names.length() < 3) errors.add("Enter both partners' names (at least 3 characters).");
        if (venue.isEmpty()) errors.add("Enter a venue.");
        if (theme.isEmpty()) errors.add("Choose or enter a wedding style.");
        try { parsedDate = LocalDate.parse(date); if (parsedDate.isBefore(LocalDate.now())) errors.add("Choose today or a future wedding date."); }
        catch (Exception e) { errors.add("Enter a valid wedding date."); }
        try { parsedGuests = Integer.parseInt(guests); if (parsedGuests < 1) errors.add("Guest count must be at least 1."); }
        catch (Exception e) { errors.add("Enter a valid guest count."); }
        try { parsedBudget = new BigDecimal(budget); if (parsedBudget.signum() < 0) errors.add("Budget cannot be negative."); }
        catch (Exception e) { errors.add("Enter a valid budget."); }
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("plan", new WeddingPlan(id, names, parsedDate, venue, parsedGuests, parsedBudget, theme, notes));
            request.getRequestDispatcher("/WEB-INF/views/plan-form.jsp").forward(request, response);
            return;
        }
        repository.save(new WeddingPlan(id, names, parsedDate, venue, parsedGuests, parsedBudget, theme, notes));
        response.sendRedirect(request.getContextPath() + "/weddings?saved=1");
    }

    private String value(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value.trim();
    }
}
