package servlet;

import model.Review;
import util.FileHandler;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

// This annotation maps the URL so your JSP form knows where to send data
@WebServlet("/ReviewServlet")
public class ReviewServlet extends HttpServlet {

    // ==========================================
    // GET: Handle Reading, Deleting, and Loading Edit Form
    // ==========================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Read the "action" parameter from the URL (e.g., ?action=delete)
        String action = request.getParameter("action");
        if (action == null) {
            action = "list"; // Default behavior
        }

        switch (action) {
            case "delete":
                // Get the ID from the URL and delete the file record
                String deleteId = request.getParameter("id");
                FileHandler.deleteReview(deleteId);
                response.sendRedirect("ReviewServlet"); // Reload the updated list
                break;

            case "edit":
                // Get the specific review and send it to an edit page
                String editId = request.getParameter("id");
                Review reviewToEdit = FileHandler.getReviewById(editId);
                request.setAttribute("review", reviewToEdit);
                request.getRequestDispatcher("jsp/edit-review.jsp").forward(request, response);
                break;

            default: // "list"
                // Load all reviews and send them to the main UI
                List<Review> reviewList = FileHandler.getAllReviews();
                request.setAttribute("reviews", reviewList);
                request.getRequestDispatcher("jsp/review-list.jsp").forward(request, response);
                break;
        }
    }

    // ==========================================
    // POST: Catch form data for Creating or Updating
    // ==========================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        // Extract common data typed into the form
        String vendorName = request.getParameter("vendorName");
        String customerName = request.getParameter("customerName");
        int rating = Integer.parseInt(request.getParameter("rating"));
        String comment = request.getParameter("comment");

        if ("update".equals(action)) {
            // --- UPDATE EXISTING REVIEW ---
            // The ID comes from a hidden field in the edit form
            String reviewId = request.getParameter("reviewId");
            Review updatedReview = new Review(reviewId, vendorName, customerName, rating, comment);
            FileHandler.updateReview(updatedReview);

        } else {
            // --- CREATE NEW REVIEW ---
            String reviewId = UUID.randomUUID().toString().substring(0, 8);
            Review newReview = new Review(reviewId, vendorName, customerName, rating, comment);
            FileHandler.saveReview(newReview);
        }

        // Redirect back to the main list after saving
        response.sendRedirect("ReviewServlet");
    }
}