package com.wedding.service;

import com.wedding.model.Customer;
import com.wedding.repository.CustomerRepository;

import java.time.LocalDate;
import java.util.List;

public class CustomerService {

    private final CustomerRepository customerRepository;

    // Constructor
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // Get all customers
    public List<Customer> getAllCustomers() {
        return customerRepository.getAllCustomers();
    }

    // Generate sequential customer ID
    private String generateCustomerId() {

        int maxNumber = 0;

        for (Customer customer : getAllCustomers()) {

            String id = customer.getCustomerId();

            if (id != null && id.matches("^C\\d+$")) {

                try {
                    int number = Integer.parseInt(id.substring(1));

                    if (number > maxNumber) {
                        maxNumber = number;
                    }

                } catch (NumberFormatException e) {
                    // Ignore IDs that cannot be converted
                }
            }
        }

        return String.format("C%04d", maxNumber + 1);
    }

    // Find customer by ID
    public Customer findCustomerById(String customerId) {

        if (customerId == null) {
            return null;
        }

        for (Customer customer : getAllCustomers()) {

            if (customer.getCustomerId().equals(customerId)) {
                return customer;
            }
        }

        return null;
    }

    // Add a new customer
    public void addCustomer(String brideName,
                            String groomName,
                            String phoneNumber,
                            String email,
                            String address,
                            LocalDate weddingDate,
                            double budget) {

        // Remove unnecessary spaces
        brideName = trimValue(brideName);
        groomName = trimValue(groomName);
        phoneNumber = trimValue(phoneNumber);
        email = trimValue(email);
        address = trimValue(address);

        // Validate customer information
        validateCustomer(
                brideName,
                groomName,
                phoneNumber,
                email,
                address,
                weddingDate,
                budget
        );

        // Generate customer ID
        String customerId = generateCustomerId();

        Customer customer = new Customer(
                customerId,
                brideName,
                groomName,
                phoneNumber,
                email,
                address,
                weddingDate,
                budget
        );

        customerRepository.addCustomer(customer);
    }

    // Update customer details
    // Blank fields will keep their previous values
    public void updateCustomer(String customerId,
                               String brideName,
                               String groomName,
                               String phoneNumber,
                               String email,
                               String address,
                               LocalDate weddingDate,
                               Double budget) {

        Customer existingCustomer = findCustomerById(customerId);

        if (existingCustomer == null) {
            throw new IllegalArgumentException(
                    "Customer not found.");
        }

        // Keep old values when fields are blank
        String newBrideName = keepOldValue(
                brideName, existingCustomer.getBrideName());

        String newGroomName = keepOldValue(
                groomName, existingCustomer.getGroomName());

        String newPhoneNumber = keepOldValue(
                phoneNumber, existingCustomer.getPhoneNumber());

        String newEmail = keepOldValue(
                email, existingCustomer.getEmail());

        String newAddress = keepOldValue(
                address, existingCustomer.getAddress());

        LocalDate newWeddingDate =
                weddingDate != null
                        ? weddingDate
                        : existingCustomer.getWeddingDate();

        double newBudget =
                budget != null
                        ? budget
                        : existingCustomer.getBudget();

        // Validate updated information
        validateCustomer(
                newBrideName,
                newGroomName,
                newPhoneNumber,
                newEmail,
                newAddress,
                newWeddingDate,
                newBudget
        );

        Customer updatedCustomer = new Customer(
                customerId,
                newBrideName,
                newGroomName,
                newPhoneNumber,
                newEmail,
                newAddress,
                newWeddingDate,
                newBudget
        );

        customerRepository.updateCustomer(updatedCustomer);
    }

    // Remove unnecessary spaces
    private String trimValue(String value) {

        return value == null ? null : value.trim();
    }

    // Keep old value when new value is blank
    private String keepOldValue(String newValue, String oldValue) {

        if (newValue == null || newValue.trim().isEmpty()) {
            return oldValue;
        }

        return newValue.trim();
    }

    // Delete a customer
    public void deleteCustomer(String customerId) {

        Customer customer = findCustomerById(customerId);

        if (customer == null) {
            throw new IllegalArgumentException(
                    "Customer not found.");
        }

        customerRepository.deleteCustomer(customerId);
    }

    // Search customers
    public List<Customer> searchCustomers(String keyword) {

        List<Customer> customers = getAllCustomers();

        if (keyword == null || keyword.trim().isEmpty()) {
            return customers;
        }

        String search = keyword.trim().toLowerCase();

        return customers.stream()
                .filter(customer ->
                        customer.getCustomerId().toLowerCase().contains(search) ||
                                customer.getBrideName().toLowerCase().contains(search) ||
                                customer.getGroomName().toLowerCase().contains(search) ||
                                customer.getPhoneNumber().toLowerCase().contains(search) ||
                                customer.getEmail().toLowerCase().contains(search)
                )
                .toList();
    }

    // Validate customer information
    private void validateCustomer(String brideName,
                                  String groomName,
                                  String phoneNumber,
                                  String email,
                                  String address,
                                  LocalDate weddingDate,
                                  double budget) {

        // Bride name validation
        if (brideName == null || brideName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Bride name is required.");
        }

        if (!brideName.matches("^[\\p{L}\\p{M} .'-]+$")) {
            throw new IllegalArgumentException(
                    "Bride name must contain letters only.");
        }

        if (brideName.length() > 100) {
            throw new IllegalArgumentException(
                    "Bride name cannot exceed 100 characters.");
        }

        // Groom name validation
        if (groomName == null || groomName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Groom name is required.");
        }

        if (!groomName.matches("^[\\p{L}\\p{M} .'-]+$")) {
            throw new IllegalArgumentException(
                    "Groom name must contain letters only.");
        }

        if (groomName.length() > 100) {
            throw new IllegalArgumentException(
                    "Groom name cannot exceed 100 characters.");
        }

        // Phone number validation
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Phone number is required.");
        }

        if (!phoneNumber.matches("^[0-9]{10}$")) {
            throw new IllegalArgumentException(
                    "Phone number must contain exactly 10 digits.");
        }

        // Email validation
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Email address is required.");
        }

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException(
                    "Enter a valid email address.");
        }

        if (email.length() > 150) {
            throw new IllegalArgumentException(
                    "Email cannot exceed 150 characters.");
        }

        // Address validation
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Address is required.");
        }

        if (address.trim().length() < 5) {
            throw new IllegalArgumentException(
                    "Address must contain at least 5 characters.");
        }

        if (address.length() > 500) {
            throw new IllegalArgumentException(
                    "Address cannot exceed 500 characters.");
        }

        // Wedding date validation
        if (weddingDate == null) {
            throw new IllegalArgumentException(
                    "Wedding date is required.");
        }

        // Budget validation
        if (!Double.isFinite(budget) || budget < 0) {
            throw new IllegalArgumentException(
                    "Budget must be a valid non-negative number.");
        }
    }
}

