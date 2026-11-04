package com.wedding.repository;

import com.wedding.model.Customer;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepository {

    private final String filePath;

    // Constructor
    public CustomerRepository(String filePath) {
        this.filePath = filePath;
    }

    // Read all customers from the text file
    public List<Customer> getAllCustomers() {

        List<Customer> customers = new ArrayList<>();

        File file = new File(filePath);

        if (!file.exists()) {
            return customers;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split("\\|", -1);

                if (data.length == 8) {

                    Customer customer = new Customer(
                            data[0],
                            data[1],
                            data[2],
                            data[3],
                            data[4],
                            data[5],
                            LocalDate.parse(data[6]),
                            Double.parseDouble(data[7])
                    );

                    customers.add(customer);
                }
            }

        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }

        return customers;
    }

    // Add a new customer
    public void addCustomer(Customer customer) {

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(filePath, true))) {

            writer.write(convertToText(customer));
            writer.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Update an existing customer
    public void updateCustomer(Customer updatedCustomer) {

        List<Customer> customers = getAllCustomers();

        for (int i = 0; i < customers.size(); i++) {

            if (customers.get(i).getCustomerId()
                    .equals(updatedCustomer.getCustomerId())) {

                customers.set(i, updatedCustomer);
                break;
            }
        }

        saveAllCustomers(customers);
    }

    // Delete a customer
    public void deleteCustomer(String customerId) {

        List<Customer> customers = getAllCustomers();

        customers.removeIf(customer ->
                customer.getCustomerId().equals(customerId));

        saveAllCustomers(customers);
    }

    // Save all customers to the text file
    private void saveAllCustomers(List<Customer> customers) {

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(filePath))) {

            for (Customer customer : customers) {

                writer.write(convertToText(customer));
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Convert customer information into a text-file record
    private String convertToText(Customer customer) {

        return customer.getCustomerId() + "|" +
                customer.getBrideName() + "|" +
                customer.getGroomName() + "|" +
                customer.getPhoneNumber() + "|" +
                customer.getEmail() + "|" +
                customer.getAddress() + "|" +
                customer.getWeddingDate() + "|" +
                customer.getBudget();
    }
}