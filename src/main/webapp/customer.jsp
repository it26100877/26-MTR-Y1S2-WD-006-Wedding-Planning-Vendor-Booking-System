<%@ page import="java.util.List" %>
<%@ page import="com.wedding.model.Customer" %>

<%
    String formMode = (String) request.getAttribute("formMode");

    if (formMode == null) {
        formMode = "list";
    }

    List<Customer> customers =
            (List<Customer>) request.getAttribute("customers");

    Customer customer =
            (Customer) request.getAttribute("customer");

    String errorMessage =
            (String) request.getAttribute("errorMessage");

    String contextPath = request.getContextPath();

    int totalCustomers = customers == null ? 0 : customers.size();

    boolean isEdit = "edit".equals(formMode);
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Customer Management | Wedding Planner</title>

    <link rel="stylesheet"
          href="<%= contextPath %>/css/customer.css">

</head>

<body>

<% if ("list".equals(formMode)) { %>

<div class="container">

    <!-- Dashboard Header -->
    <div class="header">

        <h1>Wedding Customer Management</h1>

        <p>Manage your wedding couples and their special day.</p>

    </div>

    <!-- Dashboard Summary -->
    <div class="summary">

        <div class="summary-card">

            <h3>Total Customers</h3>

            <p><%= totalCustomers %></p>

        </div>

        <div class="summary-card">

            <h3>Customer Records</h3>

            <p>Manage your wedding customers</p>

        </div>

    </div>

    <!-- Customer Section -->
    <div class="section-heading">

        <h2>Customer Details</h2>

        <a href="<%= contextPath %>/customers?action=new"
           class="btn btn-primary">

            + Add New Customer

        </a>

    </div>

    <!-- Search Form -->
    <form action="<%= contextPath %>/customers"
          method="get"
          class="search-box">

        <input type="hidden"
               name="action"
               value="search">

        <input type="text"
               name="keyword"
               placeholder="Search by ID, bride, groom, phone or email..."
               value="<%= request.getAttribute("searchKeyword") == null
                       ? "" : request.getAttribute("searchKeyword") %>">

        <button type="submit" class="btn btn-primary">
            Search
        </button>

        <a href="<%= contextPath %>/customers"
           class="btn btn-edit">

            Clear

        </a>

    </form>

    <!-- Customer Table -->
    <div class="table-container">

        <table>

            <thead>

            <tr>
                <th>Customer ID</th>
                <th>Bride Name</th>
                <th>Groom Name</th>
                <th>Phone</th>
                <th>Email</th>
                <th>Address</th>
                <th>Wedding Date</th>
                <th>Budget</th>
                <th>Actions</th>
            </tr>

            </thead>

            <tbody>

            <% if (customers != null && !customers.isEmpty()) { %>

                <% for (Customer c : customers) { %>

                <tr>

                    <td><%= c.getCustomerId() %></td>

                    <td><%= c.getBrideName() %></td>

                    <td><%= c.getGroomName() %></td>

                    <td><%= c.getPhoneNumber() %></td>

                    <td><%= c.getEmail() %></td>

                    <td><%= c.getAddress() %></td>

                    <td><%= c.getWeddingDate() %></td>

                    <td>
                        Rs. <%= String.format("%.2f", c.getBudget()) %>
                    </td>

                    <td>

                        <!-- Edit Button -->
                        <a class="btn btn-edit"
                           href="<%= contextPath %>/customers?action=edit&id=<%= c.getCustomerId() %>">

                            Edit

                        </a>

                        <!-- Delete Form -->
                        <form action="<%= contextPath %>/customers"
                              method="post"
                              style="display:inline;"
                              onsubmit="return confirmDelete();">

                            <input type="hidden"
                                   name="action"
                                   value="delete">

                            <input type="hidden"
                                   name="customerId"
                                   value="<%= c.getCustomerId() %>">

                            <button type="submit"
                                    class="btn btn-delete">

                                Delete

                            </button>

                        </form>

                    </td>

                </tr>

                <% } %>

            <% } else { %>

                <tr>

                    <td colspan="9" style="text-align:center;">

                        No customers found.

                    </td>

                </tr>

            <% } %>

            </tbody>

        </table>

    </div>

</div>

<% } else { %>

<!-- Add / Edit Customer Form -->

<div class="form-container">

    <h2>
        <%= isEdit ? "Edit Customer Details" : "Add New Customer" %>
    </h2>

    <% if (errorMessage != null) { %>

        <div class="error-message">

            <%= errorMessage %>

        </div>

    <% } %>

    <form action="<%= contextPath %>/customers"
          method="post"
          onsubmit="return validateCustomerForm(<%= isEdit %>);">

        <!-- Form Action -->
        <input type="hidden"
               name="action"
               value="<%= isEdit ? "update" : "add" %>">

        <% if (isEdit && customer != null) { %>

            <input type="hidden"
                   name="customerId"
                   value="<%= customer.getCustomerId() %>">

        <% } %>

        <div class="form-grid">

            <!-- Bride Name -->
            <div class="form-group">

                <label for="brideName">Bride Name</label>

                <input type="text"
                       id="brideName"
                       name="brideName"
                       value="<%= isEdit && customer != null ? customer.getBrideName() : "" %>"
                       placeholder="Enter bride name"
                       maxlength="100"
                       <%= isEdit ? "" : "required" %>>

            </div>

            <!-- Groom Name -->
            <div class="form-group">

                <label for="groomName">Groom Name</label>

                <input type="text"
                       id="groomName"
                       name="groomName"
                       value="<%= isEdit && customer != null ? customer.getGroomName() : "" %>"
                       placeholder="Enter groom name"
                       maxlength="100"
                       <%= isEdit ? "" : "required" %>>

            </div>

            <!-- Phone Number -->
            <div class="form-group">

                <label for="phoneNumber">Phone Number</label>

                <input type="tel"
                       id="phoneNumber"
                       name="phoneNumber"
                       value="<%= isEdit && customer != null ? customer.getPhoneNumber() : "" %>"
                       placeholder="Enter 10-digit phone number"
                       maxlength="10"
                       pattern="[0-9]{10}"
                       title="Please enter exactly 10 digits"
                       <%= isEdit ? "" : "required" %>>

            </div>

            <!-- Email -->
            <div class="form-group">

                <label for="email">Email Address</label>

                <input type="email"
                       id="email"
                       name="email"
                       value="<%= isEdit && customer != null ? customer.getEmail() : "" %>"
                       placeholder="Enter email address"
                       maxlength="150"
                       <%= isEdit ? "" : "required" %>>

            </div>

            <!-- Address -->
            <div class="form-group full-width">

                <label for="address">Address</label>

                <textarea id="address"
                          name="address"
                          placeholder="Enter address"
                          maxlength="500"
                          <%= isEdit ? "" : "required" %>><%= isEdit && customer != null ? customer.getAddress() : "" %></textarea>

            </div>

            <!-- Wedding Date -->
            <div class="form-group">

                <label for="weddingDate">Wedding Date</label>

                <input type="date"
                       id="weddingDate"
                       name="weddingDate"
                       value="<%= isEdit && customer != null ? customer.getWeddingDate() : "" %>"
                       <%= isEdit ? "" : "required" %>>

            </div>

            <!-- Budget -->
            <div class="form-group">

                <label for="budget">Wedding Budget (Rs.)</label>

                <input type="number"
                       id="budget"
                       name="budget"
                       step="0.01"
                       min="0"
                       value="<%= isEdit && customer != null ? customer.getBudget() : "" %>"
                       placeholder="Enter budget"
                       <%= isEdit ? "" : "required" %>>

            </div>

        </div>

        <!-- Form Buttons -->
        <div class="form-actions">

            <button type="submit" class="btn btn-primary">

                <%= isEdit ? "Update Customer" : "Save Customer" %>

            </button>

            <a href="<%= contextPath %>/customers"
               class="btn btn-edit">

                Cancel

            </a>

        </div>

        <% if (isEdit) { %>

            <p style="margin-top:15px; color:#777; font-size:13px;">

                Leave a field blank to keep its existing value.

            </p>

        <% } %>

    </form>

</div>

<% } %>

<!-- Connect JavaScript -->
<script src="<%= contextPath %>/js/customer.js"></script>

</body>

</html>

