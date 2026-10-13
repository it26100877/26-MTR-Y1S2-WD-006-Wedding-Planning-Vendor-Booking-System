<%@ page import="java.util.List" %>
<%@ page import="com.wedding.weddingcustomer.Customer" %>
<%@ page import="org.apache.commons.text.StringEscapeUtils" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%!
    private String esc(Object value) {
        return StringEscapeUtils.escapeHtml4(
                value == null ? "" : value.toString());
    }
%>

<%
    List<Customer> customers =
            (List<Customer>) request.getAttribute("customers");

    String search =
            (String) request.getAttribute("search");

    String message =
            (String) request.getAttribute("message");

    String error =
            (String) request.getAttribute("error");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Manage Customers | Wedding Planning</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: "Segoe UI", Arial, sans-serif;
            background: #fff8fa;
            color: #49343c;
        }

        header {
            background: linear-gradient(135deg, #803d59, #b66c86);
            color: white;
            padding: 25px 6%;
        }

        header h1 {
            margin: 0 0 8px;
        }

        header p {
            margin: 0;
        }

        .container {
            max-width: 1250px;
            margin: 30px auto;
            padding: 0 20px;
        }

        .back-link {
            display: inline-block;
            margin-bottom: 20px;
            color: #8d4563;
            text-decoration: none;
            font-weight: bold;
        }

        .card {
            background: white;
            border-radius: 14px;
            padding: 24px;
            margin-bottom: 25px;
            box-shadow: 0 5px 22px rgba(100, 50, 70, 0.08);
        }

        h2 {
            margin-top: 0;
            color: #803d59;
        }

        .search-row {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
        }

        input {
            width: 100%;
            padding: 11px 12px;
            border: 1px solid #e3cbd5;
            border-radius: 7px;
            font-size: 14px;
        }

        .search-row input {
            flex: 1;
            min-width: 220px;
        }

        .form-grid {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 16px;
        }

        label {
            display: block;
            margin-bottom: 7px;
            font-weight: 600;
        }

        .field {
            min-width: 0;
        }

        .hint {
            display: block;
            margin-top: 6px;
            font-size: 12px;
            color: #806c74;
        }

        button, .button {
            border: none;
            border-radius: 7px;
            padding: 11px 17px;
            font-weight: 600;
            font-size: 14px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            text-align: center;
        }

        .primary {
            background: #914b68;
            color: white;
        }

        .secondary {
            background: #f0e3e9;
            color: #693349;
        }

        .edit {
            background: #f4dce6;
            color: #783b54;
        }

        .remove {
            background: #b94154;
            color: white;
        }

        .actions {
            display: flex;
            gap: 7px;
            flex-wrap: wrap;
        }

        .notice {
            padding: 13px 16px;
            border-radius: 8px;
            margin-bottom: 20px;
        }

        .success {
            background: #e4f5e9;
            color: #216438;
        }

        .error {
            background: #fde7e9;
            color: #9b2335;
        }

        .table-wrap {
            overflow-x: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            min-width: 850px;
        }

        th, td {
            padding: 13px 12px;
            text-align: left;
            border-bottom: 1px solid #f0e3e9;
            vertical-align: middle;
        }

        th {
            background: #fbf0f4;
            color: #773d56;
        }

        tr:hover td {
            background: #fffafb;
        }

        .empty {
            text-align: center;
            padding: 25px;
            color: #806c74;
        }

        footer {
            text-align: center;
            color: #806c74;
            padding: 25px;
            font-size: 13px;
        }

        @media (max-width: 650px) {
            .form-grid {
                grid-template-columns: 1fr;
            }

            .card {
                padding: 18px;
            }
        }
    </style>
</head>

<body>

<header>
    <h1>Wedding Planning & Management</h1>
    <p>Customer Management Portal</p>
</header>

<div class="container">

    <a class="back-link"
       href="<%= request.getContextPath() %>/index.jsp">
        &larr; Back to Dashboard
    </a>

    <% if (message != null && !message.isBlank()) { %>
        <div class="notice success">
            <%= esc(message) %>
        </div>
    <% } %>

    <% if (error != null && !error.isBlank()) { %>
        <div class="notice error">
            <%= esc(error) %>
        </div>
    <% } %>

    <!-- SEARCH CUSTOMERS -->

    <section class="card">
        <h2>Search Customers</h2>

        <form method="get"
              action="<%= request.getContextPath() %>/customers"
              class="search-row">

            <input type="search"
                   id="customerSearch"
                   name="search"
                   value="<%= esc(search) %>"
                   placeholder="Search by ID, name, email or phone">

            <button type="submit" class="primary">
                Search
            </button>

            <a class="button secondary"
               href="<%= request.getContextPath() %>/customers">
                Clear
            </a>
        </form>
    </section>

    <!-- REGISTER CUSTOMER -->

    <section class="card">
        <h2>Register New Customer</h2>

        <form method="post"
              action="<%= request.getContextPath() %>/customers">

            <input type="hidden" name="action" value="add">

            <div class="form-grid">

                <div class="field">
                    <label for="name">Full Name *</label>
                    <input id="name"
                           name="name"
                           required
                           minlength="2"
                           maxlength="100"
                           pattern="[\p{L}][\p{L} .'-]{1,99}"
                           title="Enter a valid name."
                           placeholder="Enter full name">
                </div>

                <div class="field">
                    <label for="email">Email *</label>
                    <input id="email"
                           type="email"
                           name="email"
                           required
                           maxlength="254"
                           placeholder="example@email.com">
                </div>

                <div class="field">
                    <label for="password">Password *</label>
                    <input id="password"
                           type="password"
                           name="password"
                           required
                           minlength="8"
                           maxlength="64"
                           pattern="(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9\s])\S{8,64}"
                           title="Use uppercase, lowercase, a number and a special character."
                           placeholder="Enter password">

                    <span class="hint">
                        Use at least 8 characters, including uppercase,
                        lowercase, a number and a special character.
                    </span>
                </div>

                <div class="field">
                    <label for="phone">Phone Number *</label>
                    <input id="phone"
                           name="phone"
                           required
                           minlength="10"
                           maxlength="20"
                           pattern="\+?[0-9][0-9 ()-]{7,18}[0-9]"
                           placeholder="Enter phone number">
                </div>

                <div class="field" style="grid-column: 1 / -1;">
                    <label for="address">Address *</label>
                    <input id="address"
                           name="address"
                           required
                           maxlength="250"
                           placeholder="Enter full address">
                </div>

            </div>

            <br>

            <button type="submit" class="primary">
                Register Customer
            </button>

        </form>
    </section>

    <!-- UPDATE CUSTOMER -->

    <section class="card" id="editCustomer">
        <h2>Update Customer Details</h2>

        <p>
            Select Edit beside a customer below. Enter only the
            details you want to change.
        </p>

        <form method="post"
              action="<%= request.getContextPath() %>/customers"
              id="updateCustomerForm">

            <input type="hidden" name="action" value="update">

            <div class="form-grid">

                <div class="field">
                    <label for="editCustomerId">Customer ID *</label>
                    <input id="editCustomerId"
                           name="customerId"
                           required
                           pattern="CUS[0-9]+"
                           placeholder="Select a customer using Edit">
                </div>

                <div class="field">
                    <label for="editName">New Name</label>
                    <input id="editName"
                           name="name"
                           minlength="2"
                           maxlength="100"
                           pattern="[\p{L}][\p{L} .'-]{1,99}"
                           placeholder="New name">
                </div>

                <div class="field">
                    <label for="editEmail">New Email</label>
                    <input id="editEmail"
                           type="email"
                           name="email"
                           maxlength="254"
                           placeholder="New email">
                </div>

                <div class="field">
                    <label for="editPhone">New Phone Number</label>
                    <input id="editPhone"
                           name="phone"
                           maxlength="20"
                           pattern="\+?[0-9][0-9 ()-]{7,18}[0-9]"
                           placeholder="New phone number">
                </div>

                <div class="field">
                    <label for="editAddress">New Address</label>
                    <input id="editAddress"
                           name="address"
                           maxlength="250"
                           placeholder="New address">
                </div>

                <div class="field">
                    <label for="editPassword">New Password</label>
                    <input id="editPassword"
                           type="password"
                           name="password"
                           minlength="8"
                           maxlength="64"
                           pattern="(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9\s])\S{8,64}"
                           title="Use uppercase, lowercase, a number and a special character."
                           placeholder="New password">
                </div>

            </div>

            <br>

            <div class="actions">
                <button type="submit" class="primary">
                    Save Changes
                </button>

                <button type="button"
                        class="secondary"
                        onclick="clearEditForm()">
                    Clear Form
                </button>
            </div>

        </form>
    </section>

    <!-- CUSTOMER LIST -->

    <section class="card">
        <h2>Customer List</h2>

        <p>
            Customers found:
            <strong><%= customers == null ? 0 : customers.size() %></strong>
        </p>

        <div class="table-wrap">

            <table>
                <thead>
                <tr>
                    <th>Customer ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Address</th>
                    <th>Actions</th>
                </tr>
                </thead>

                <tbody>

                <% if (customers != null && !customers.isEmpty()) {
                    for (Customer c : customers) { %>

                    <tr>
                        <td><%= esc(c.getCustomerId()) %></td>
                        <td><%= esc(c.getName()) %></td>
                        <td><%= esc(c.getEmail()) %></td>
                        <td><%= esc(c.getPhone()) %></td>
                        <td><%= esc(c.getAddress()) %></td>

                        <td>
                            <div class="actions">

                                <button type="button"
                                        class="edit"
                                        data-customer-id="<%= esc(c.getCustomerId()) %>"
                                        onclick="selectCustomer(this.dataset.customerId)">
                                    Edit
                                </button>

                                <form method="post"
                                      action="<%= request.getContextPath() %>/customers"
                                      onsubmit="return confirm('Are you sure you want to remove this customer?');">

                                    <input type="hidden"
                                           name="action"
                                           value="delete">

                                    <input type="hidden"
                                           name="customerId"
                                           value="<%= esc(c.getCustomerId()) %>">

                                    <button type="submit" class="remove">
                                        Remove
                                    </button>

                                </form>

                            </div>
                        </td>
                    </tr>

                <%  }
                   } else { %>

                    <tr>
                        <td colspan="6" class="empty">
                            No customers found. Try another search
                            or register a new customer.
                        </td>
                    </tr>

                <% } %>

                </tbody>
            </table>

        </div>
    </section>

</div>

<footer>
    Wedding Planning and Management System |
    Customer Management Module
</footer>

<script>
    function selectCustomer(customerId) {
        const idField = document.getElementById("editCustomerId");

        idField.value = customerId;

        document.getElementById("editName").value = "";
        document.getElementById("editEmail").value = "";
        document.getElementById("editPhone").value = "";
        document.getElementById("editAddress").value = "";
        document.getElementById("editPassword").value = "";

        document.getElementById("editCustomer").scrollIntoView({
            behavior: "smooth",
            block: "start"
        });

        document.getElementById("editName").focus();
    }

    function clearEditForm() {
        document.getElementById("updateCustomerForm").reset();
    }
</script>

</body>
</html>