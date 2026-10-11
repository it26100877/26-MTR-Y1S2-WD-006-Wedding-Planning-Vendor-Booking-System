<!DOCTYPE html>

<html>
<head>
    <title>Customer Management</title>

```
<style>
    * {
        box-sizing: border-box;
    }

    body {
        margin: 0;
        font-family: Arial, sans-serif;
        background-color: #fff0f5;
        color: #49313b;
    }

    header {
        background-color: #c2185b;
        color: white;
        padding: 25px 5%;
    }

    header h1 {
        margin: 0 0 8px;
    }

    main {
        max-width: 1400px;
        margin: 25px auto;
        padding: 0 20px;
    }

    .total-card {
        background-color: white;
        padding: 20px;
        border-radius: 10px;
        margin-bottom: 20px;
        box-shadow: 0 3px 10px #00000010;
    }

    .total-card p {
        margin: 0 0 8px;
        color: #777;
    }

    .total-card h2 {
        margin: 0;
        color: #c2185b;
        font-size: 30px;
    }

    .layout {
        display: grid;
        grid-template-columns: 320px minmax(0, 1fr);
        gap: 20px;
        align-items: start;
    }

    .panel {
        background-color: white;
        padding: 22px;
        border-radius: 10px;
        box-shadow: 0 3px 10px #00000010;
    }

    h2 {
        color: #c2185b;
        font-size: 20px;
    }

    label {
        display: block;
        margin: 13px 0 6px;
        font-weight: bold;
        font-size: 14px;
    }

    input {
        width: 100%;
        padding: 10px;
        border: 1px solid #e5c6d3;
        border-radius: 6px;
    }

    button {
        padding: 10px 14px;
        background-color: #c2185b;
        color: white;
        border: none;
        border-radius: 6px;
        cursor: pointer;
    }

    button:hover {
        background-color: #a3154c;
    }

    .toolbar {
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 12px;
        margin-bottom: 15px;
    }

    .toolbar h2 {
        margin: 0;
    }

    .toolbar input {
        max-width: 280px;
    }

    .table-wrap {
        overflow-x: auto;
    }

    table {
        width: 100%;
        border-collapse: collapse;
        background-color: white;
        font-size: 14px;
    }

    th, td {
        border-bottom: 1px solid #f0dce4;
        padding: 12px 10px;
        text-align: left;
    }

    th {
        background-color: #fce4ec;
        color: #9c1749;
    }

    .edit-button {
        background-color: #7b4b8a;
        margin-right: 4px;
    }

    .delete-button {
        background-color: #d32f2f;
    }

    .empty {
        text-align: center;
        padding: 25px;
        color: #777;
    }

    .success-message {
        background-color: #e2f6e9;
        color: #207443;
        padding: 12px;
        margin-bottom: 15px;
        border-radius: 6px;
    }

    @media (max-width: 850px) {
        .layout {
            grid-template-columns: 1fr;
        }

        .toolbar {
            flex-direction: column;
            align-items: stretch;
        }

        .toolbar input {
            max-width: none;
        }
    }
</style>
```

</head>

<body>

<header>
    <h1>Customer Management</h1>
    <p>Manage your customer information in one place.</p>
</header>

<main>

```
<%
    String registeredCustomerId =
            (String) request.getAttribute("registeredCustomerId");

    if (registeredCustomerId != null) {
%>

<div class="success-message">
    Registration successful! Your Customer ID is:
    <strong><%= registeredCustomerId %></strong>
    <br>
    Please save your Customer ID for future reference.
</div>

<%
    }
%>

<div class="total-card">
    <p>Total Customers</p>
    <h2 id="totalCustomers">0</h2>
</div>

<div class="layout">

    <div class="panel">

        <h2>Add Customer</h2>

        <form action="customers" method="post">

            <label>Name</label>
            <input type="text" name="name" required>

            <label>Phone</label>
            <input type="text" name="phone"
                   pattern="[0-9]{10}"
                   title="Phone number must contain exactly 10 digits"
                   required>

            <label>Email</label>
            <input type="email" name="email" required>

            <br><br>

            <button type="submit">Add Customer</button>

        </form>

    </div>

    <div class="panel">

        <div class="toolbar">
            <h2>Customer List</h2>

            <input type="search"
                   id="searchInput"
                   placeholder="Search by ID, name or phone...">
        </div>

        <div class="table-wrap">

            <table id="customerTable">

                <thead>
                <tr>
                    <th>Customer ID</th>
                    <th>Name</th>
                    <th>Phone</th>
                    <th>Email</th>
                    <th>Actions</th>
                </tr>
                </thead>

                <tbody>

                <%
                    java.util.List<com.wedding.model.Customer> customers =
                            (java.util.List<com.wedding.model.Customer>)
                            request.getAttribute("customers");

                    if (customers != null && !customers.isEmpty()) {
                        for (com.wedding.model.Customer customer : customers) {
                %>

                <tr>
                    <td><%= customer.getCustomerId() %></td>
                    <td><%= customer.getName() %></td>
                    <td><%= customer.getPhone() %></td>
                    <td><%= customer.getEmail() %></td>

                    <td>
                        <button type="button" class="edit-button"
                                onclick="editCustomer(this)">
                            Edit
                        </button>

                        <form action="customers" method="post"
                              style="display:inline"
                              onsubmit="return confirm('Delete this customer?')">

                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="customerId"
                                   value="<%= customer.getCustomerId() %>">

                            <button type="submit" class="delete-button">
                                Delete
                            </button>
                        </form>
                    </td>
                </tr>

                <%
                        }
                    } else {
                %>

                <tr>
                    <td colspan="5" class="empty">No customers found.</td>
                </tr>

                <%
                    }
                %>

                </tbody>

            </table>

        </div>

    </div>

</div>
```

</main>

<script>
    const searchInput = document.getElementById("searchInput");
    const tableRows = document.querySelectorAll("#customerTable tbody tr");

    document.getElementById("totalCustomers").textContent =
        Array.from(tableRows).filter(row =>
            row.cells.length === 5 && !row.querySelector(".empty")
        ).length;

    searchInput.addEventListener("input", function () {
        const searchText = this.value.toLowerCase();

        tableRows.forEach(function (row) {
            const rowText = row.innerText.toLowerCase();
            row.style.display = rowText.includes(searchText) ? "" : "none";
        });
    });

    function editCustomer(button) {
        const row = button.closest("tr");
        const cells = row.cells;

        const customerId = cells[0].innerText;
        const name = cells[1].innerText;
        const phone = cells[2].innerText;
        const email = cells[3].innerText;

        const form = document.createElement("form");
        form.action = "customers";
        form.method = "post";

        const fields = {
            action: "update",
            customerId: customerId,
            name: name,
            phone: phone,
            email: email
        };

        for (const key in fields) {
            const input = document.createElement("input");
            input.type = "hidden";
            input.name = key;
            input.value = fields[key];
            form.appendChild(input);
        }

        const newName = prompt("Enter new name:", name);
        if (newName === null) return;
        if (!newName.trim()) {
            alert("Name cannot be empty.");
            return;
        }

        const newPhone = prompt("Enter new phone number:", phone);
        if (newPhone === null) return;
        if (!/^[0-9]{10}$/.test(newPhone)) {
            alert("Phone number must contain exactly 10 digits.");
            return;
        }

        const newEmail = prompt("Enter new email:", email);
        if (newEmail === null) return;
        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(newEmail)) {
            alert("Please enter a valid email.");
            return;
        }

        form.elements.action.value = "update";
        form.elements.name.value = newName.trim();
        form.elements.phone.value = newPhone;
        form.elements.email.value = newEmail.trim();

        document.body.appendChild(form);
        form.submit();
    }
</script>

</body>
</html>
