package com.wedding.service;

import com.wedding.model.Customer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerService {

    private String filePath = "customers.txt";

    public void addCustomer(Customer customer) throws IOException {

        FileWriter writer = new FileWriter(filePath, true);

        writer.write(
                customer.getCustomerId().trim() + "," +
                        customer.getName().trim() + "," +
                        customer.getPhone().trim() + "," +
                        customer.getEmail().trim() + "\n"
        );

        writer.close();
    }

    public List<Customer> getCustomers() throws IOException {

        List<Customer> customers = new ArrayList<>();

        File file = new File(filePath);

        if (!file.exists()) {
            file.createNewFile();
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line;

        while ((line = reader.readLine()) != null) {

            if (line.trim().isEmpty()) {
                continue;
            }

            String[] data = line.split(",", -1);

            if (data.length >= 4) {

                Customer customer = new Customer(
                        data[0].trim(),
                        data[1].trim(),
                        data[2].trim(),
                        data[3].trim()
                );

                customers.add(customer);
            }
        }

        reader.close();

        return customers;
    }

    public void updateCustomer(Customer updatedCustomer) throws IOException {

        String updateId = updatedCustomer.getCustomerId().trim();

        List<Customer> customers = getCustomers();

        FileWriter writer = new FileWriter(filePath);

        for (Customer customer : customers) {

            String existingId = customer.getCustomerId().trim();

            if (existingId.equalsIgnoreCase(updateId)) {

                String name = updatedCustomer.getName().trim();
                String phone = updatedCustomer.getPhone().trim();
                String email = updatedCustomer.getEmail().trim();

                if (name.isEmpty()) {
                    name = customer.getName();
                }

                if (phone.isEmpty()) {
                    phone = customer.getPhone();
                }

                if (email.isEmpty()) {
                    email = customer.getEmail();
                }

                writer.write(
                        existingId + "," +
                                name + "," +
                                phone + "," +
                                email + "\n"
                );

            } else {

                writer.write(
                        existingId + "," +
                                customer.getName().trim() + "," +
                                customer.getPhone().trim() + "," +
                                customer.getEmail().trim() + "\n"
                );
            }
        }

        writer.close();
    }

    public void deleteCustomer(String customerId) throws IOException {

        String deleteId = customerId.trim();

        List<Customer> customers = getCustomers();

        FileWriter writer = new FileWriter(filePath);

        for (Customer customer : customers) {

            String existingId = customer.getCustomerId().trim();

            if (!existingId.equalsIgnoreCase(deleteId)) {

                writer.write(
                        existingId + "," +
                                customer.getName().trim() + "," +
                                customer.getPhone().trim() + "," +
                                customer.getEmail().trim() + "\n"
                );
            }
        }

        writer.close();
    }
}