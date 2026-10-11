<%@ page import="java.util.List" %>
<%@ page import="com.wedding.model.Customer" %>

<!DOCTYPE html>
<html>
<head>
    <title>Customer Management</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            font-family: Arial, sans-serif;
            margin: 30px;
            background-color: #fff0f5;
        }

        h1, h2 {
            color: #c2185b;
        }

        .panel {
            background-color: white;
            padding: 20px;
            margin-bottom: 25px;
            border-radius: 10px;
            box-shadow: 0 2px 8px #e0b6c6;
        }

        form {
            display: flex;
            flex-direction: column;
            gap: 10px;
            max-width: 450px;
        }

        label {
            font-weight: bold;
        }

        input {
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 5px;
        }

        button {
            padding: 10px;
            background-color: #c2185b;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }

        button:hover {
            background-color: #880e4f;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            background-color: white;
        }

        th, td {
            padding: 12px;
            border: 1px solid #ddd;
            text-align: left;
        }

        th {
            background-color: #c2185b;
            color: white;
        }

        .success-message {
            background-color: #e8f5e9;
            color: #2e7d32;
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
        }
    </style>
</head>

<body>

<h1>Customer Management</h1>

<%
    String registeredCustomerId =
            (String) request.getAttribute("registeredCustomerId");

    if (registeredCustomerId != null) {
%>

<div class="success-message">
    Customer added successfully! Customer ID:
    <strong><%= registeredCustomerId %></strong>
</div>

<%
    }
%>

<div class="panel">

    <h2>Add Customer</h2>

    <form action="customers" method="post">

        <input type="hidden" name="action" value="add">

        <label>Name</label>
        <input type="text" name="name" required>

        <label>Phone</label>
        <input type="tel" name="phone" required>

        <label>Email</label>
        <input type="email" name="email" required>

        <label>Password</label>
        <input type="password"
               name="password"
               minlength="8"
               title="Password must contain at least 8 characters"
               required>

        <button type="submit">Add Customer</button>

    </form>

</div>

<div class="panel">

    <h2>Update Customer</h2>

    <form action="customers" method="post">

        <input type="hidden" name="action" value="update">

        <label>Customer ID</label>
        <input type="text" name="customerId" required>

        <label>New Name</label>
        <input type="text" name="name" required>

        <label>New Phone</label>
        <input type="tel" name="phone" required>

        <label>New Email</label>
        <input type="email" name="email" required>

        <label>New Password (leave empty to keep current password)</label>
        <input type="password"
               name="password"
               minlength="8"
               title="Password must contain at least 8 characters">

        <button type="submit">Update Customer</button>

    </form>

</div>

<div class="panel">

    <h2>Delete Customer</h2>

    <form action="customers" method="post">

        <input type="hidden" name="action" value="delete">

        <label>Customer ID</label>
        <input type="text" name="customerId" required>

        <button type="submit">Delete Customer</button>

    </form>

</div>

<div class="panel">

    <h2>Customer List</h2>

    <table>

        <tr>
            <th>Customer ID</th>
            <th>Name</th>
            <th>Phone</th>
            <th>Email</th>
        </tr>

        <%
            List<Customer> customers =
                    (List<Customer>) request.getAttribute("customers");

            if (customers != null) {
                for (Customer customer : customers) {
        %>

        <tr>
            <td><%= customer.getCustomerId() %></td>
            <td><%= customer.getName() %></td>
            <td><%= customer.getPhone() %></td>
            <td><%= customer.getEmail() %></td>
        </tr>

        <%
                }
            }
        %>

    </table>

</div>

</body>
</html>


