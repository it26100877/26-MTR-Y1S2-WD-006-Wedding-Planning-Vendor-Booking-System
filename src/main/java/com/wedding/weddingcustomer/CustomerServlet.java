package com.wedding.weddingcustomer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

@WebServlet("/customers")
public class CustomerServlet extends HttpServlet {

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[\\p{L}][\\p{L} .'-]{1,99}$");

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[0-9][0-9 ()-]{7,18}[0-9]$");

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile(
                    "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)"
                            + "(?=.*[^A-Za-z0-9\\s])\\S{8,64}$");

    private static final int MAX_ADDRESS_LENGTH = 250;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String search = request.getParameter("search");

        List<Customer> customers;

        if (search != null && !search.isBlank()) {
            customers = CustomerFileHandler.searchCustomers(search.trim());
        } else {
            customers = CustomerFileHandler.getAllCustomers();
        }

        request.setAttribute("customers", customers);
        request.setAttribute("search", search == null ? "" : search);
        request.setAttribute("message", request.getParameter("message"));

        request.getRequestDispatcher("/customers.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String action = value(request, "action");

        try {
            String message;

            switch (action) {
                case "add":
                    message = addCustomer(request);
                    break;

                case "update":
                    message = updateCustomer(request);
                    break;

                case "delete":
                    message = deleteCustomer(request);
                    break;

                default:
                    throw new IllegalArgumentException(
                            "Invalid customer operation.");
            }

            response.sendRedirect(
                    request.getContextPath()
                            + "/customers?message="
                            + URLEncoder.encode(
                            message, StandardCharsets.UTF_8));

        } catch (IllegalArgumentException ex) {

            request.setAttribute("error", ex.getMessage());
            showCustomers(request, response);

        } catch (Exception ex) {

            getServletContext().log(
                    "Customer operation failed.", ex);

            request.setAttribute(
                    "error",
                    "The operation failed. Please check the customer details "
                            + "and try again.");

            showCustomers(request, response);
        }
    }

    private String addCustomer(HttpServletRequest request)
            throws Exception {

        String name = value(request, "name");
        String email = value(request, "email").toLowerCase();
        String password = request.getParameter("password");
        String phone = value(request, "phone");
        String address = value(request, "address");

        validateCustomer(name, email, password, phone, address, true);

        Customer customer = CustomerFileHandler.addCustomer(
                name, email, password, phone, address);

        return "Customer registered successfully! Customer ID: "
                + customer.getCustomerId();
    }

    private String updateCustomer(HttpServletRequest request)
            throws Exception {

        String id = value(request, "customerId");

        if (!id.matches("CUS\\d+")) {
            throw new IllegalArgumentException(
                    "Please select a valid customer using the Edit button.");
        }

        Customer existing = CustomerFileHandler.findById(id).orElse(null);

        if (existing == null) {
            throw new IllegalArgumentException(
                    "Customer not found. Refresh the customer list.");
        }

        /*
         * A blank field means keep the existing value.
         * Only nonblank fields are changed.
         */

        String name = value(request, "name");
        if (name.isBlank()) {
            name = existing.getName();
        }

        String email = value(request, "email");
        if (email.isBlank()) {
            email = existing.getEmail();
        } else {
            email = email.toLowerCase();
        }

        String phone = value(request, "phone");
        if (phone.isBlank()) {
            phone = existing.getPhone();
        }

        String address = value(request, "address");
        if (address.isBlank()) {
            address = existing.getAddress();
        }

        String password = request.getParameter("password");

        if (password == null) {
            password = "";
        }

        validateCustomer(
                name, email, password, phone, address, false);

        boolean updated = CustomerFileHandler.updateCustomer(
                id, name, email, password, phone, address);

        if (!updated) {
            throw new IllegalArgumentException(
                    "Customer could not be updated.");
        }

        return "Customer " + id + " updated successfully!";
    }

    private String deleteCustomer(HttpServletRequest request)
            throws Exception {

        String id = value(request, "customerId");

        if (!id.matches("CUS\\d+")) {
            throw new IllegalArgumentException(
                    "Invalid customer selected.");
        }

        boolean deleted = CustomerFileHandler.deleteCustomer(id);

        if (!deleted) {
            throw new IllegalArgumentException(
                    "Customer not found. Refresh the customer list.");
        }

        return "Customer " + id + " removed successfully.";
    }

    private void validateCustomer(
            String name,
            String email,
            String password,
            String phone,
            String address,
            boolean passwordRequired) {

        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException(
                    "Name must contain 2–100 valid name characters.");
        }

        if (email.length() > 254
                || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException(
                    "Enter a valid email address.");
        }

        if (passwordRequired
                && (password == null || password.isBlank())) {
            throw new IllegalArgumentException(
                    "Password is required when registering.");
        }

        if (password != null && !password.isBlank()
                && !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException(
                    "Password must be 8–64 characters and include "
                            + "uppercase, lowercase, a number and a special character.");
        }

        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new IllegalArgumentException(
                    "Enter a valid phone number.");
        }

        if (address.isBlank() || address.length() > MAX_ADDRESS_LENGTH) {
            throw new IllegalArgumentException(
                    "Address is required and must be at most 250 characters.");
        }
    }

    private String value(HttpServletRequest request, String field) {
        String result = request.getParameter(field);
        return result == null ? "" : result.trim();
    }

    private void showCustomers(HttpServletRequest request,
                               HttpServletResponse response)
            throws ServletException, IOException {

        String search = request.getParameter("search");

        List<Customer> customers;

        if (search != null && !search.isBlank()) {
            customers = CustomerFileHandler.searchCustomers(search.trim());
        } else {
            customers = CustomerFileHandler.getAllCustomers();
        }

        request.setAttribute("customers", customers);
        request.setAttribute("search", search == null ? "" : search);

        request.getRequestDispatcher("/customers.jsp")
                .forward(request, response);
    }
}

