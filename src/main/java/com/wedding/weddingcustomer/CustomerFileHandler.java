package com.wedding.weddingcustomer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class CustomerFileHandler {

    // Stores customer data in the application's working directory.
    private static final Path FILE_PATH =
            Path.of(System.getProperty("user.dir"))
                    .resolve("customers.txt")
                    .toAbsolutePath()
                    .normalize();

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final int ITERATIONS = 600000;
    private static final int KEY_LENGTH = 256;

    private CustomerFileHandler() {
        // Prevent creating unnecessary objects.
    }

    // Encode values so special characters do not break file records.
    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(
                value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        return new String(
                Base64.getDecoder().decode(value),
                StandardCharsets.UTF_8);
    }

    // Hash passwords securely using PBKDF2 and a random salt.
    public static String hashPassword(String password) throws Exception {

        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);

        KeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                ITERATIONS,
                KEY_LENGTH);

        try {
            byte[] hash = SecretKeyFactory
                    .getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .getEncoded();

            return ITERATIONS + ":"
                    + Base64.getEncoder().encodeToString(salt) + ":"
                    + Base64.getEncoder().encodeToString(hash);

        } finally {
            if (spec instanceof PBEKeySpec passwordSpec) {
                passwordSpec.clearPassword();
            }
        }
    }

    // Read all customers from the file.
    public static synchronized List<Customer> getAllCustomers()
            throws IOException {

        List<Customer> customers = new ArrayList<>();

        if (!Files.exists(FILE_PATH)) {
            return customers;
        }

        List<String> lines =
                Files.readAllLines(FILE_PATH, StandardCharsets.UTF_8);

        for (String line : lines) {
            try {
                String[] data = line.split("\\|", -1);

                // Each customer must have six encoded fields.
                if (data.length != 6) {
                    continue;
                }

                Customer customer = new Customer(
                        decode(data[0]),
                        decode(data[1]),
                        decode(data[2]),
                        decode(data[3]),
                        decode(data[4]),
                        decode(data[5]));

                customers.add(customer);

            } catch (IllegalArgumentException ex) {
                // Skip malformed records.
            }
        }

        return customers;
    }

    // Generate the next customer ID.
    private static String generateCustomerId(List<Customer> customers) {

        int highest = 1000;

        for (Customer customer : customers) {

            String id = customer.getCustomerId();

            if (id != null && id.matches("CUS\\d+")) {
                try {
                    highest = Math.max(
                            highest,
                            Integer.parseInt(id.substring(3)));
                } catch (NumberFormatException ignored) {
                    // Ignore IDs outside the supported integer range.
                }
            }
        }

        return "CUS" + (highest + 1);
    }

    // Find a customer by ID.
    public static synchronized Optional<Customer> findById(String id)
            throws IOException {

        return getAllCustomers().stream()
                .filter(c -> c.getCustomerId() != null
                        && c.getCustomerId().equalsIgnoreCase(id))
                .findFirst();
    }

    // Search customers by ID, name, email, or phone number.
    public static synchronized List<Customer> searchCustomers(String query)
            throws IOException {

        String q = query == null
                ? ""
                : query.trim().toLowerCase(Locale.ROOT);

        List<Customer> results = new ArrayList<>();

        for (Customer customer : getAllCustomers()) {

            if (q.isEmpty()
                    || contains(customer.getCustomerId(), q)
                    || contains(customer.getName(), q)
                    || contains(customer.getEmail(), q)
                    || contains(customer.getPhone(), q)) {

                results.add(customer);
            }
        }

        return results;
    }

    private static boolean contains(String value, String query) {
        return value != null
                && value.toLowerCase(Locale.ROOT).contains(query);
    }

    // Add a new customer without removing existing customers.
    public static synchronized Customer addCustomer(
            String name,
            String email,
            String password,
            String phone,
            String address) throws Exception {

        List<Customer> customers = getAllCustomers();

        // Prevent duplicate email addresses.
        boolean emailExists = customers.stream()
                .anyMatch(c -> c.getEmail() != null
                        && c.getEmail().equalsIgnoreCase(email));

        if (emailExists) {
            throw new IllegalArgumentException(
                    "This email address is already registered.");
        }

        // Create a unique ID.
        String id = generateCustomerId(customers);

        // Hash the password before saving.
        String passwordHash = hashPassword(password);

        Customer newCustomer = new Customer(
                id,
                name,
                email,
                passwordHash,
                phone,
                address);

        // Keep every existing customer and append the new one.
        customers.add(newCustomer);

        // Save the complete customer list.
        saveAllCustomers(customers);

        return newCustomer;
    }

    // Update a customer without changing their ID.
    public static synchronized boolean updateCustomer(
            String id,
            String name,
            String email,
            String password,
            String phone,
            String address) throws Exception {

        List<Customer> customers = getAllCustomers();

        Customer selected = null;

        for (Customer customer : customers) {
            if (customer.getCustomerId() != null
                    && customer.getCustomerId().equalsIgnoreCase(id)) {
                selected = customer;
                break;
            }
        }

        if (selected == null) {
            return false;
        }

        // Do not allow another customer to use the same email.
        for (Customer customer : customers) {

            if (!customer.getCustomerId().equalsIgnoreCase(id)
                    && customer.getEmail() != null
                    && customer.getEmail().equalsIgnoreCase(email)) {

                throw new IllegalArgumentException(
                        "Another customer already uses this email.");
            }
        }

        selected.setName(name);
        selected.setEmail(email);
        selected.setPhone(phone);
        selected.setAddress(address);

        // Keep the old password if no new password was entered.
        if (password != null && !password.isBlank()) {
            selected.setPasswordHash(hashPassword(password));
        }

        saveAllCustomers(customers);

        return true;
    }

    // Delete only the selected customer.
    public static synchronized boolean deleteCustomer(String id)
            throws IOException {

        List<Customer> customers = getAllCustomers();

        boolean removed = customers.removeIf(
                customer -> customer.getCustomerId() != null
                        && customer.getCustomerId().equalsIgnoreCase(id));

        if (removed) {
            saveAllCustomers(customers);
        }

        return removed;
    }

    // Save every customer to customers.txt.
    private static void saveAllCustomers(List<Customer> customers)
            throws IOException {

        List<String> lines = new ArrayList<>();

        for (Customer customer : customers) {

            String line = encode(customer.getCustomerId()) + "|"
                    + encode(customer.getName()) + "|"
                    + encode(customer.getEmail()) + "|"
                    + encode(customer.getPasswordHash()) + "|"
                    + encode(customer.getPhone()) + "|"
                    + encode(customer.getAddress());

            lines.add(line);
        }

        Files.write(
                FILE_PATH,
                lines,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
    }

    // Display the exact file location in the terminal if needed.
    public static String getFileLocation() {
        return FILE_PATH.toString();
    }
}

