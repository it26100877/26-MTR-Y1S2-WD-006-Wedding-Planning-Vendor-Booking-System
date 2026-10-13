<!DOCTYPE html>

<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Wedding Planning and Management System</title>

<style>
    * {
        box-sizing: border-box;
    }

    body {
        margin: 0;
        font-family: Georgia, serif;
        background: #fff7f5;
        color: #49343a;
        text-align: center;
    }

    header {
        background: #c47785;
        color: white;
        padding: 25px;
    }

    header h1 {
        font-size: 30px;
        margin: 0;
    }

    .hero {
        min-height: 480px;
        padding: 80px 20px;
        background:
            linear-gradient(#fff0f0b8, #fff0f0d9),
            url("https://images.unsplash.com/photo-1519741497674-611481863552");
        background-size: cover;
        background-position: center;
    }

    .hero h2 {
        font-size: 38px;
        color: #713f4a;
        margin-top: 30px;
    }

    .hero p {
        font-size: 18px;
        line-height: 1.7;
    }

    .button {
        display: inline-block;
        background: #c47785;
        color: white;
        padding: 14px 25px;
        text-decoration: none;
        border-radius: 8px;
        margin: 10px;
        transition: background 0.3s;
    }

    .button:hover {
        background: #9e5364;
    }

    footer {
        padding: 20px;
        background: #f5e6e8;
        font-size: 14px;
    }

    @media (max-width: 600px) {
        .hero h2 {
            font-size: 28px;
        }

        header h1 {
            font-size: 23px;
        }
    }
</style>

</head>

<body>

<header>
    <h1>Wedding Planning and Management System</h1>
    <p>Customer Management Portal</p>
</header>

<main class="hero">
    <h2>Welcome to the Wedding Planning System</h2>

<p>
    A centralized platform for managing customer information
    and supporting efficient wedding planning operations.
</p>

<a href="customers" class="button">Manage Customers</a>
<a href="customers" class="button">Register New Customer</a>

</main>

<footer>
    <p>Wedding Planning and Management System | Customer Management Module</p>
</footer>

</body>
</html>
