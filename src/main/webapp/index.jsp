<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard | Weddingly</title>

    <!-- Bootstrap & FontAwesome -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <style>




        /* Matches the dark, elegant vibe of your login page */

body {
            /* Explicitly separate the image/gradient from the positioning rules */
            background-image: linear-gradient(rgba(0, 0, 0, 0.6), rgba(0, 0, 0, 0.7)), url('/images/wedding.jpg');
            background-size: cover;
            background-position: center center;
            background-repeat: no-repeat;
            background-attachment: fixed;

            font-family: sans-serif;
            min-height: 100vh;
        }
        .navbar-brand {
            font-family: Georgia, serif;
            font-size: 1.5rem;
            letter-spacing: 2px;
        }

        .dashboard-card {
            background: rgba(255, 255, 255, 0.95); /* Semi-transparent white like the login box */
            border: none;
            border-radius: 15px;
            transition: all 0.3s ease;
            text-decoration: none;
            color: #442431;
            display: block;
        }

        .dashboard-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 20px rgba(0,0,0,0.2);
            color: #c4517e;
        }

        .icon-circle {
            background-color: #d16b8a; /* Your maroon/pink accent color */
            color: white;
            width: 70px;
            height: 70px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.8rem;
            margin: 0 auto 15px;
        }
    </style>
</head>
<body>

    <!-- Top Navigation -->
    <nav class="navbar navbar-dark bg-transparent pt-4 px-5">
        <a class="navbar-brand fw-bold text-white" href="#">
            <i class="fa-solid fa-rings me-2"></i> WEDDINGLY
        </a>
        <div class="d-flex gap-3">
            <span class="text-white align-self-center">Welcome Back!</span>
            <a href="login.html" class="btn btn-outline-light rounded-pill px-4">Logout</a>
        </div>
    </nav>

    <!-- Main Dashboard Grid -->
    <div class="container mt-5 pt-4">
        <div class="text-center text-white mb-5">
            <h1 style="font-family: Georgia, serif;">Plan Your Perfect Wedding</h1>
            <p class="lead">Manage your wedding, book vendors, and share your experience.</p>
        </div>

        <div class="row g-4 justify-content-center">

            <!-- Member 1: Customer Management -->
            <div class="col-md-4 col-lg-3">
                <a href="jsp/customer.jsp" class="dashboard-card p-4 text-center h-100 shadow">
                    <div class="icon-circle"><i class="fa-solid fa-users"></i></div>
                    <h5 class="fw-bold">Customers</h5>
                    <small class="text-muted">Manage user profiles</small>
                </a>
            </div>

            <!-- Member 2: Vendor Management -->
            <div class="col-md-4 col-lg-3">
                <a href="jsp/vendor.jsp" class="dashboard-card p-4 text-center h-100 shadow">
                    <div class="icon-circle"><i class="fa-solid fa-store"></i></div>
                    <h5 class="fw-bold">Vendors</h5>
                    <small class="text-muted">Browse service providers</small>
                </a>
            </div>

            <!-- Member 3: Booking Management -->
            <div class="col-md-4 col-lg-3">
                <a href="jsp/booking.jsp" class="dashboard-card p-4 text-center h-100 shadow">
                    <div class="icon-circle"><i class="fa-solid fa-calendar-check"></i></div>
                    <h5 class="fw-bold">Bookings</h5>
                    <small class="text-muted">Schedule and reservations</small>
                </a>
            </div>

            <!-- Member 4: Wedding Planning -->
            <div class="col-md-4 col-lg-3">
                <a href="jsp/wedding.jsp" class="dashboard-card p-4 text-center h-100 shadow">
                    <div class="icon-circle"><i class="fa-solid fa-church"></i></div>
                    <h5 class="fw-bold">Wedding Plans</h5>
                    <small class="text-muted">Event timelines & details</small>
                </a>
            </div>

            <!-- Member 5: Budget Management -->
            <div class="col-md-4 col-lg-3">
                <a href="jsp/budget.jsp" class="dashboard-card p-4 text-center h-100 shadow">
                    <div class="icon-circle"><i class="fa-solid fa-wallet"></i></div>
                    <h5 class="fw-bold">Budget</h5>
                    <small class="text-muted">Track expenses & payments</small>
                </a>
            </div>

            <!-- Member 6: Reviews & Ratings (YOUR PART - WIRED UP!) -->
            <div class="col-md-4 col-lg-3">
                <a href="ReviewServlet" class="dashboard-card p-4 text-center h-100 shadow">
                    <div class="icon-circle"><i class="fa-solid fa-star"></i></div>
                    <h5 class="fw-bold">Reviews</h5>
                    <small class="text-muted">Read & write feedback</small>
                </a>
            </div>

        </div>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>