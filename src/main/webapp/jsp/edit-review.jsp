<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Review" %>
<%
    // Retrieve the specific review object passed from the Servlet
    Review review = (Review) request.getAttribute("review");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Review | Weddingly</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<div class="container mt-5" style="max-width: 600px;">
    <div class="card shadow-sm border-0">
        <div class="card-header bg-white border-bottom-0 pt-4 pb-0">
            <h4 class="mb-0" style="color: #6c2d45;">Edit Review</h4>
        </div>
        <div class="card-body p-4">

            <!-- Form submits to ReviewServlet using POST -->
            <form action="ReviewServlet" method="post">

                <!-- Hidden fields: Tells the Servlet this is an update, and which ID to modify -->
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="reviewId" value="<%= review.getReviewId() %>">

                <div class="mb-3">
                    <label class="form-label fw-bold text-secondary">Vendor Name</label>
                    <input type="text" name="vendorName" class="form-control" value="<%= review.getVendorName() %>" required>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-bold text-secondary">Your Name</label>
                    <input type="text" name="customerName" class="form-control" value="<%= review.getCustomerName() %>" required>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-bold text-secondary">Rating (1-5)</label>
                    <input type="number" name="rating" class="form-control" min="1" max="5" value="<%= review.getRating() %>" required>
                </div>

                <div class="mb-4">
                    <label class="form-label fw-bold text-secondary">Review Comment</label>
                    <textarea name="comment" class="form-control" rows="4" required><%= review.getComment() %></textarea>
                </div>

                <div class="d-flex justify-content-between">
                    <a href="ReviewServlet" class="btn btn-outline-secondary px-4">Cancel</a>
                    <button type="submit" class="btn text-white px-4" style="background-color: #d16b8a; border-color: #d16b8a;">Update Review</button>
                </div>
            </form>

        </div>
    </div>
</div>

</body>
</html>