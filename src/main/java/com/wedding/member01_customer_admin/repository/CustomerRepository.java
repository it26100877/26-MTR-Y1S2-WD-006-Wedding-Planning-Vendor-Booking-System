package com.wedding.member01_customer_admin.repository;

import com.wedding.common.repository.AbstractFileRepository;
import com.wedding.common.utils.FileUtil;
import com.wedding.member01_customer_admin.model.Customer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * File-based repository for {@link Customer} entities.
 * Extends AbstractFileRepository demonstrating Inheritance.
 * Data is persisted in customers.csv with 9 fields.
 *
 * @author Member 01
 */
public class CustomerRepository extends AbstractFileRepository<Customer> {

 private static final String FILE_NAME = "customers.csv";

 public CustomerRepository() {
 super(FILE_NAME);
 }

 // ─── Serialization ───────────────────────────────────────────────────────────

 /**
 * Serializes a Customer to a CSV line.
 * Format: userId,name,email,phone,passwordHash,createdAt,active,address,weddingDate
 */
 @Override
 protected String serialize(Customer c) {
 return String.join(",",
 FileUtil.escape(c.getUserId()),
 FileUtil.escape(c.getName()),
 FileUtil.escape(c.getEmail()),
 FileUtil.escape(c.getPhone()),
 FileUtil.escape(c.getPasswordHash()),
 FileUtil.escape(c.getCreatedAt()),
 String.valueOf(c.isActive()),
 FileUtil.escape(c.getAddress()),
 FileUtil.escape(c.getWeddingDate())
 );
 }

 /**
 * Deserializes a CSV line back to a Customer.
 * Expects 9 fields.
 */
 @Override
 protected Customer deserialize(String line) {
 String[] p = FileUtil.splitCSV(line);
 if (p.length < 9) return null;
 return new Customer(
 p[0].trim(), // userId
 p[1].trim(), // name
 p[2].trim(), // email
 p[3].trim(), // phone
 p[4].trim(), // passwordHash
 p[5].trim(), // createdAt
 Boolean.parseBoolean(p[6].trim()), // active
 p[7].trim(), // address
 p[8].trim() // weddingDate
 );
 }

 @Override
 protected String getId(Customer c) {
 return c.getUserId();
 }

 // ─── Additional Query Methods ─────────────────────────────────────────────────

 /**
 * Finds a customer by their email address.
 *
 * @param email The email to search for
 * @return Optional containing the customer if found
 */
 public Optional<Customer> findByEmail(String email) {
 return cache.values().stream()
 .filter(c -> c.getEmail().equalsIgnoreCase(email))
 .findFirst();
 }

 /**
 * Returns all currently active customers.
 *
 * @return List of active customers
 */
 public List<Customer> findByActive() {
 return cache.values().stream()
 .filter(Customer::isActive)
 .collect(Collectors.toList());
 }
}
