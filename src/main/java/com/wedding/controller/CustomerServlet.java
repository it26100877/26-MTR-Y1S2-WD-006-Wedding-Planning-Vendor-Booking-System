package com.wedding.controller;

import com.wedding.model.Customer;
import com.wedding.service.CustomerService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/customers")
public class CustomerServlet extends HttpServlet {

    private CustomerService customerService = new CustomerService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String customerId = request.getParameter("customerId");
        String name = request.getParameter("name");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String action = request.getParameter("action");

        if (customerId != null) customerId = customerId.trim();
        if (name != null) name = name.trim();
        if (phone != null) phone = phone.trim();
        if (email != null) email = email.trim();

        if ("update".equals(action) || "delete".equals(action)) {

            if (customerId == null || customerId.isEmpty()) {
                response.getWriter().println("ERROR: Customer ID cannot be empty.");
                return;
            }
        }

        if ("update".equals(action)) {

            if ((name == null || name.isEmpty()) &&
                    (phone == null || phone.isEmpty()) &&
                    (email == null || email.isEmpty())) {

                response.getWriter().println(
                        "ERROR: Enter at least one field to update."
                );
                return;
            }

            if (name != null && !name.isEmpty()
                    && !name.matches("[A-Za-z ]+")) {

                response.getWriter().println(
                        "ERROR: Name can contain letters and spaces only."
                );
                return;
            }

            if (phone != null && !phone.isEmpty()
                    && !phone.matches("[0-9]{10}")) {

                response.getWriter().println(
                        "ERROR: Phone number must contain exactly 10 digits."
                );
                return;
            }

            if (email != null && !email.isEmpty()
                    && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

                response.getWriter().println(
                        "ERROR: Please enter a valid email."
                );
                return;
            }

            Customer customer = new Customer(
                    customerId,
                    name == null ? "" : name,
                    phone == null ? "" : phone,
                    email == null ? "" : email
            );

            customerService.updateCustomer(customer);

            response.sendRedirect("customers");
            return;

        } else if ("delete".equals(action)) {

            customerService.deleteCustomer(customerId);

            response.sendRedirect("customers");
            return;

        } else {

            if (name == null || !name.matches("[A-Za-z ]+")) {
                response.getWriter().println(
                        "ERROR: Name can contain letters and spaces only."
                );
                return;
            }

            if (phone == null || !phone.matches("[0-9]{10}")) {
                response.getWriter().println(
                        "ERROR: Phone number must contain exactly 10 digits."
                );
                return;
            }

            if (email == null ||
                    !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

                response.getWriter().println(
                        "ERROR: Please enter a valid email."
                );
                return;
            }

            List<Customer> customers = customerService.getCustomers();

            int nextNumber = 1;

            for (Customer customer : customers) {

                String id = customer.getCustomerId().trim();

                if (id.startsWith("CU")) {
                    try {
                        int number = Integer.parseInt(id.substring(2));

                        if (number >= nextNumber) {
                            nextNumber = number + 1;
                        }

                    } catch (NumberFormatException e) {
                        // Ignore invalid customer IDs.
                    }
                }
            }

            String newCustomerId = String.format("CU%03d", nextNumber);

            Customer customer = new Customer(
                    newCustomerId,
                    name,
                    phone,
                    email
            );

            customerService.addCustomer(customer);

            request.setAttribute("registeredCustomerId", newCustomerId);

            doGet(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Customer> customers = customerService.getCustomers();

        request.setAttribute("customers", customers);

        request.getRequestDispatcher("customers.jsp")
                .forward(request, response);
    }
}