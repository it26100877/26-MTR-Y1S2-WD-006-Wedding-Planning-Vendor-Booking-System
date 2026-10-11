<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Review | Weddingly</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <style>
        /* Shared full-screen background from the reviews list page */
        body {
            background: linear-gradient(rgba(30, 15, 20, 0.6), rgba(30, 15, 20, 0.7)),
                        url('${pageContext.request.contextPath}/images/wedding.jpg') no-repeat center center fixed;
            background-size: cover;
            min-height: 100vh;
            margin: 0;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        /* Frosted glass effect for the form container */
        .glass-form-card {
            background: rgba(255, 255, 255, 0.85); /* Slightly less transparent for readability */
            backdrop-filter: blur(12px);
            border: 1px solid rgba(255, 255, 255, 0.3);
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.3);
            width: 100%;
            max-width: 600px;
        }

        /* Dark maroon header to match your original design */
        .form-header {
            background-color: #442431;
            color: #ffffff;
            padding: 20px;
            text-align: center;
        }

        /* Custom pink submit button */
        .btn-submit {
            background-color: #c4517e;
            color: white;
            border: none;
            transition: background-color 0.3s ease, transform 0.1s ease;
        }

        .btn-submit:hover {
            background-color: #a8426a;
            color: white;
        }

        .btn-submit:active {
            transform: scale(0.98);
        }

        .form-label {
            color: #442431;
            font-weight: 600;
        }
    </style>
</head>
<body>

    <div class="container d-flex justify-content-center align-items-center" style="padding: 40px 15px;">

        <div class="glass-form-card">

            <div class="form-header">
                <h3 class="mb-0 fw-bold"><i class="fa-solid fa-star text-warning me-2"></i> Submit a Vendor Review</h3>
            </div>

            <div class="card-body p-4 p-md-5">

                <!-- Updated action with context path to prevent 404 error on submit -->
                <form action="${pageContext.request.contextPath}/ReviewServlet" method="post">

                    <!-- Hidden field in case your servlet uses an 'action' parameter to distinguish form submissions -->
                    <input type="hidden" name="action" value="add">

                    <div class="mb-3">
                        <label for="customerName" class="form-label">Your Name</label>
                        <input type="text" class="form-control bg-light" id="customerName" name="customerName" placeholder="Enter your name" required>
                    </div>

                    <div class="mb-3">
                        <label for="vendorName" class="form-label">Vendor Name</label>
                        <input type="text" class="form-control bg-light" id="vendorName" name="vendorName" placeholder="Which vendor are you reviewing?" required>
                    </div>

                    <div class="mb-3">
                        <label for="rating" class="form-label">Rating (1 to 5)</label>
                        <select class="form-select bg-light" id="rating" name="rating" required>
                            <option value="" disabled selected>Select a rating...</option>
                            <option value="5">5 Stars</option>
                            <option value="4">4 Stars</option>
                            <option value="3">3 Stars</option>
                            <option value="2">2 Stars</option>
                            <option value="1">1 Star</option>
                        </select>
                    </div>

                    <div class="mb-4">
                        <label for="comment" class="form-label">Review Comment</label>
                        <textarea class="form-control bg-light" id="comment" name="comment" rows="4" placeholder="Share your experience with this vendor..." required></textarea>
                    </div>

                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-submit fw-bold py-2 text-uppercase letter-spacing-1">Submit Review</button>

                        <!-- Updated href with context path to prevent 404 error on cancel -->
                        <a href="${pageContext.request.contextPath}/ReviewServlet" class="btn btn-outline-secondary py-2 mt-2 fw-bold">Cancel / Back to Reviews</a>
                    </div>

                </form>
            </div>
        </div>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>