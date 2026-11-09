package com.wedding.member01_customer_admin.service;

import com.wedding.common.exceptions.BusinessRuleException;
import com.wedding.common.exceptions.EntityNotFoundException;
import com.wedding.common.exceptions.ValidationException;
import com.wedding.common.model.User;
import com.wedding.common.service.BaseService;
import com.wedding.common.utils.FileUtil;
import com.wedding.common.utils.ValidationUtil;
import com.wedding.member01_customer_admin.model.Customer;
import com.wedding.member01_customer_admin.repository.CustomerRepository;

import java.util.List;

/**
 * Business logic layer for Customer operations.
 * Extends BaseService demonstrating Inheritance.
 * Uses CustomerRepository for persistence.
 *
 * @author Member 01
 */
public class CustomerService extends BaseService {

 private final CustomerRepository customerRepository;

 public CustomerService(CustomerRepository customerRepository) {
 this.customerRepository = customerRepository;
 }

 // ─── Methods ─────────────────────────────────────────────────────────────────

 /**
 * Registers a new customer account.
 *
 * @param name Full name
 * @param email Email address
 * @param phone Phone number
 * @param password Plain-text password (min 6 chars)
 * @param address Physical address
 * @param weddingDate Planned wedding date (yyyy-MM-dd)
 * @return The created Customer
 * @throws ValidationException if any field is invalid
 * @throws BusinessRuleException if email is already registered
 */
 public Customer register(String name, String email, String phone,
 String password, String address, String weddingDate) {
 requireNonEmpty(name, "Full Name");
 requireNonEmpty(email, "Email");
 requireNonEmpty(phone, "Phone");
 requireNonEmpty(password, "Password");
 

 if (!ValidationUtil.isValidEmail(email)) {
 throw new ValidationException("Email address is not valid.");
 }
 if (!ValidationUtil.isValidPhone(phone)) {
 throw new ValidationException("Phone number is not valid (9-15 digits).");
 }
 if (!ValidationUtil.isValidPassword(password)) {
 throw new ValidationException("Password must be at least 12 characters.");
 }
 if (!ValidationUtil.isNullOrEmpty(weddingDate) && !ValidationUtil.isValidDate(weddingDate)) {
 throw new ValidationException("Wedding date must be in yyyy-MM-dd format.");
 }
 if (customerRepository.findByEmail(email).isPresent()) {
 throw new BusinessRuleException("An account with this email already exists.");
 }

 String id = FileUtil.generateId("CST");
 Customer customer = new Customer(
 id,
 name.trim(),
 email.trim().toLowerCase(),
 phone.trim(),
 User.hashPassword(password),
 ValidationUtil.nowDateTime(),
 true,
 address == null ? "" : address.trim(),
 weddingDate == null ? "" : weddingDate.trim()
 );
 customerRepository.save(customer);
 log("Registered customer: " + customer.getUserId());
 return customer;
 }

 /**
 * Authenticates a customer by email and password.
 *
 * @param email Email address
 * @param password Plain-text password
 * @return Authenticated Customer
 * @throws ValidationException if inputs are blank
 * @throws BusinessRuleException if credentials are invalid or account inactive
 */
 public Customer login(String email, String password) {
 requireNonEmpty(email, "Email");
 requireNonEmpty(password, "Password");

 Customer customer = customerRepository.findByEmail(email)
 .orElseThrow(() -> new BusinessRuleException("Invalid email or password."));

 if (!customer.checkPassword(password)) {
 throw new BusinessRuleException("Invalid email or password.");
 }
 if (!customer.isActive()) {
 throw new BusinessRuleException("Your account has been deactivated. Please contact support.");
 }
 if (customer.needsPasswordHashUpgrade()) { customer.setPasswordHash(User.hashPassword(password)); customerRepository.save(customer); }
 log("Customer logged in: " + customer.getUserId());
 return customer;
 }

 /**
 * Updates an existing customer's profile information.
 *
 * @param customerId ID of the customer to update
 * @param name New full name
 * @param phone New phone
 * @param address New address
 * @param weddingDate New wedding date (yyyy-MM-dd), may be empty
 * @throws EntityNotFoundException if customer not found
 * @throws ValidationException if any field is invalid
 */
 public void updateProfile(String customerId, String name, String phone,
 String address, String weddingDate) {
 Customer customer = getById(customerId);

 requireNonEmpty(name, "Full Name");
 requireNonEmpty(phone, "Phone");
 requireNonEmpty(address, "Address");

 if (!ValidationUtil.isValidPhone(phone)) {
 throw new ValidationException("Phone number is not valid (9-15 digits).");
 }
 if (!ValidationUtil.isNullOrEmpty(weddingDate) && !ValidationUtil.isValidDate(weddingDate)) {
 throw new ValidationException("Wedding date must be in yyyy-MM-dd format.");
 }

 customer.setName(name.trim());
 customer.setPhone(phone.trim());
 customer.setAddress(address.trim());
 customer.setWeddingDate(weddingDate == null ? "" : weddingDate.trim());
 customerRepository.save(customer);
 log("Updated profile for: " + customerId);
 }

 /**
 * Returns all registered customers.
 *
 * @return List of all customers
 */
 public List<Customer> getAllCustomers() {
 return customerRepository.findAll();
 }

 /**
 * Retrieves a customer by their ID.
 *
 * @param id Customer ID
 * @return The Customer
 * @throws EntityNotFoundException if not found
 */
 public Customer getById(String id) {
 return customerRepository.findById(id)
 .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + id));
 }

 /**
 * Deactivates a customer account.
 *
 * @param id Customer ID
 * @throws EntityNotFoundException if not found
 */
 public void deactivate(String id) {
 Customer customer = getById(id);
 customer.setActive(false);
 customerRepository.save(customer);
 log("Deactivated customer: " + id);
 }

 /**
 * Activates a customer account.
 *
 * @param id Customer ID
 * @throws EntityNotFoundException if not found
 */
 public void activate(String id) {
 Customer customer = getById(id);
 customer.setActive(true);
 customerRepository.save(customer);
 log("Activated customer: " + id);
 }

 /**
 * Returns the total number of registered customers.
 *
 * @return Customer count
 */
 public int count() {
 return customerRepository.count();
 }
}
