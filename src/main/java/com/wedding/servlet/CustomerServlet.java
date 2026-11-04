package com.wedding.servlet;

import com.wedding.model.Customer;
import com.wedding.repository.CustomerRepository;
import com.wedding.service.CustomerService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/customers")
public class CustomerServlet extends HttpServlet {

    private CustomerService customerService;

    // Initialize the service and repository
    @Override
    public void init() throws ServletException {

        String filePath = Path.of(
                System.getProperty("user.home"),
                "wedding-planning-system",
                "customers.txt"
        ).toString();

        try {
            java.nio.file.Files.createDirectories(
                    Path.of(filePath).getParent()
            );
        } catch (IOException e) {
            throw new ServletException("Unable to create customer data folder.", e);
        }

        CustomerRepository repository =
                new CustomerRepository(filePath);

        customerService = new CustomerService(repository);
    }

    // Handle GET requests
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            action = "list";
        }

        switch (action) {

            case "new":
                request.setAttribute("formMode", "add");
                showPage(request, response);
                break;

            case "edit":
                showEditForm(request, response);
                break;

            case "search":
                searchCustomers(request, response);
                break;

            default:
                showCustomerList(request, response);
                break;
        }
    }

    // Handle POST requests
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        try {

            if ("add".equals(action)) {
                addCustomer(request);
            }

            else if ("update".equals(action)) {
                updateCustomer(request);
            }

            else if ("delete".equals(action)) {
                deleteCustomer(request);
            }

            response.sendRedirect(
                    request.getContextPath() + "/customers"
            );

        } catch (IllegalArgumentException e) {

            request.setAttribute("errorMessage", e.getMessage());

            if ("update".equals(action)) {

                Customer customer = customerService.findCustomerById(
                        request.getParameter("customerId")
                );

                request.setAttribute("customer", customer);
                request.setAttribute("formMode", "edit");

            } else {
                request.setAttribute("formMode", "add");
            }

            showPage(request, response);
        }
    }

    // Display all customers
    private void showCustomerList(HttpServletRequest request,
                                  HttpServletResponse response)
            throws ServletException, IOException {

        List<Customer> customers =
                customerService.getAllCustomers();

        request.setAttribute("customers", customers);
        request.setAttribute("formMode", "list");

        showPage(request, response);
    }

    // Display the add/edit page
    private void showPage(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/customer.jsp")
                .forward(request, response);
    }

    // Display edit form with existing customer information
    private void showEditForm(HttpServletRequest request,
                              HttpServletResponse response)
            throws ServletException, IOException {

        String customerId = request.getParameter("id");

        Customer customer =
                customerService.findCustomerById(customerId);

        if (customer == null) {
            response.sendRedirect(
                    request.getContextPath() + "/customers"
            );
            return;
        }

        request.setAttribute("customer", customer);
        request.setAttribute("formMode", "edit");

        showPage(request, response);
    }

    // Add a customer
    private void addCustomer(HttpServletRequest request) {

        String brideName = request.getParameter("brideName");
        String groomName = request.getParameter("groomName");
        String phoneNumber = request.getParameter("phoneNumber");
        String email = request.getParameter("email");
        String address = request.getParameter("address");

        LocalDate weddingDate = LocalDate.parse(
                request.getParameter("weddingDate")
        );

        double budget = Double.parseDouble(
                request.getParameter("budget")
        );

        customerService.addCustomer(
                brideName,
                groomName,
                phoneNumber,
                email,
                address,
                weddingDate,
                budget
        );
    }

    // Update a customer
    private void updateCustomer(HttpServletRequest request) {

        String customerId = request.getParameter("customerId");

        String brideName = request.getParameter("brideName");
        String groomName = request.getParameter("groomName");
        String phoneNumber = request.getParameter("phoneNumber");
        String email = request.getParameter("email");
        String address = request.getParameter("address");

        String dateValue = request.getParameter("weddingDate");
        LocalDate weddingDate = null;

        if (dateValue != null && !dateValue.trim().isEmpty()) {
            weddingDate = LocalDate.parse(dateValue);
        }

        String budgetValue = request.getParameter("budget");
        Double budget = null;

        if (budgetValue != null && !budgetValue.trim().isEmpty()) {
            budget = Double.parseDouble(budgetValue);
        }

        customerService.updateCustomer(
                customerId,
                brideName,
                groomName,
                phoneNumber,
                email,
                address,
                weddingDate,
                budget
        );
    }

    // Delete a customer
    private void deleteCustomer(HttpServletRequest request) {

        String customerId = request.getParameter("customerId");

        customerService.deleteCustomer(customerId);
    }

    // Search customers
    private void searchCustomers(HttpServletRequest request,
                                 HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = request.getParameter("keyword");

        List<Customer> customers =
                customerService.searchCustomers(keyword);

        request.setAttribute("customers", customers);
        request.setAttribute("searchKeyword", keyword);
        request.setAttribute("formMode", "list");

        showPage(request, response);
    }
}