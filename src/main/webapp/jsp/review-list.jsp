<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Review" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reviews & Ratings | Weddingly</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <style>
        /* Full-screen background image with a dark gradient overlay for text readability */
        body {
            background: linear-gradient(rgba(30, 15, 20, 0.6), rgba(30, 15, 20, 0.7)),
                        url('${pageContext.request.contextPath}/images/wedding.jpg') no-repeat center center fixed;
            background-size: cover;
            min-height: 100vh;
            margin: 0;
            padding-bottom: 50px;
        }

        /* White text with shadow for the main header */
        .page-title {
            color: #ffffff !important;
            font-family: Georgia, serif;
            text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.5);
        }

        /* Glassmorphism effect for the review cards */
        .glass-card {
            background: rgba(255, 255, 255, 0.75);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.2);
            transition: transform 0.3s ease, box-shadow 0.3s ease;
        }

        /* Hover animation to make the cards feel interactive */
        .glass-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2) !important;
        }

        /* Styling for the empty state box */
        .empty-state-box {
            background: rgba(255, 255, 255, 0.9);
            backdrop-filter: blur(10px);
        }
    </style>
</head>
<body>

<div class="container mt-5 pt-4">

    <!-- Header Section -->
    <div class="d-flex justify-content-between align-items-center mb-5 border-bottom border-light pb-3">
        <div>
            <a href="index.jsp" class="text-white-50 text-decoration-none small mb-1 d-block">
                <i class="fa-solid fa-arrow-left"></i> Back to Dashboard
            </a>
            <h2 class="page-title mb-0">Vendor Reviews</h2>
        </div>
        <a href="${pageContext.request.contextPath}/jsp/add-review.jsp" class="btn text-white fw-bold px-4 py-2 shadow" style="background-color: #c4517e; border-radius: 8px;">
            <i class="fa-solid fa-plus me-1"></i> Add New Review
        </a>
    </div>

    <div class="row">
        <%
            // Retrieve the list of reviews sent by the ReviewServlet
            List<Review> reviewList = (List<Review>) request.getAttribute("reviews");

            if (reviewList == null || reviewList.isEmpty()) {
        %>
            <!-- Modern Empty State -->
            <div class="col-12 mt-2">
                <div class="card empty-state-box border-0 shadow-lg rounded-4 text-center py-5">
                    <div class="card-body py-5">
                        <i class="fa-regular fa-star-half-stroke mb-4" style="font-size: 4rem; color: #e8ccd5;"></i>
                        <h3 class="fw-bold mb-3" style="color: #4a2c3a;">No Reviews Yet</h3>
                        <p class="text-muted mx-auto mb-0" style="max-width: 450px; font-size: 1.1rem; line-height: 1.6;">
                            It looks a little quiet here. Share your experience with a vendor to help other couples plan their perfect day!
                        </p>
                    </div>
                </div>
            </div>
        <%
            } else {
                // Loop through each review and generate a card
                for (Review r : reviewList) {
        %>
            <!-- Review Card -->
            <div class="col-md-6 col-lg-4 mb-4">
                <div class="card h-100 shadow glass-card rounded-4">
                    <div class="card-body p-4">

                        <div class="d-flex justify-content-between align-items-start mb-2">
                            <h5 class="card-title fw-bold mb-0" style="color: #442431; font-size: 1.25rem;"><%= r.getVendorName() %></h5>

                            <!-- Display Stars based on integer rating -->
                            <div class="text-warning" style="font-size: 0.9rem;">
                                <% for(int i=0; i < r.getRating(); i++) { %>
                                    <i class="fa-solid fa-star"></i>
                                <% } %>
                            </div>
                        </div>

                        <h6 class="card-subtitle mb-3 text-muted" style="font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.5px;">
                            <i class="fa-regular fa-user me-1"></i> <%= r.getCustomerName() %>
                        </h6>

                        <p class="card-text text-secondary mt-3" style="font-style: italic; line-height: 1.6;">
                            "<%= r.getComment() %>"
                        </p>

                    </div>

                    <!-- Action buttons for Update/Delete -->
                    <div class="card-footer bg-transparent border-top border-light d-flex justify-content-end gap-2 p-3">
                        <a href="ReviewServlet?action=edit&id=<%= r.getReviewId() %>" class="btn btn-sm px-3 rounded-pill" style="color: #c4517e; border-color: #c4517e;">Edit</a>
                        <a href="ReviewServlet?action=delete&id=<%= r.getReviewId() %>" class="btn btn-sm btn-outline-danger px-3 rounded-pill" onclick="return confirm('Are you sure you want to delete this review?');">Delete</a>
                    </div>
                </div>
            </div>
        <%
                }
            }
        %>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>