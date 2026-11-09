package com.wedding.member01_customer_admin.service;

import com.wedding.common.exceptions.BusinessRuleException;
import com.wedding.common.exceptions.EntityNotFoundException;
import com.wedding.common.exceptions.ValidationException;
import com.wedding.common.model.User;
import com.wedding.common.service.BaseService;
import com.wedding.common.utils.FileUtil;
import com.wedding.common.utils.ValidationUtil;
import com.wedding.member01_customer_admin.model.Admin;
import com.wedding.member01_customer_admin.repository.AdminRepository;

import java.util.List;

/**
 * Business logic layer for Admin operations.
 * Extends BaseService demonstrating Inheritance.
 * Uses AdminRepository for persistence.
 *
 * @author Member 01
 */
public class AdminService extends BaseService {

 private final AdminRepository adminRepository;

 public AdminService(AdminRepository adminRepository) {
 this.adminRepository = adminRepository;
 }

 // ─── Methods ─────────────────────────────────────────────────────────────────

 /**
 * Authenticates an admin by email and password.
 *
 * @param email Email address
 * @param password Plain-text password
 * @return Authenticated Admin
 * @throws BusinessRuleException if credentials are invalid
 */
 public Admin login(String email, String password) {
 requireNonEmpty(email, "Email");
 requireNonEmpty(password, "Password");

 Admin admin = adminRepository.findByEmail(email)
 .orElseThrow(() -> new BusinessRuleException("Invalid credentials"));

 if (!admin.checkPassword(password)) {
 throw new BusinessRuleException("Invalid credentials");
 }
 if (!admin.isActive()) {
 throw new BusinessRuleException("Admin account is inactive.");
 }
 if (admin.needsPasswordHashUpgrade()) { admin.setPasswordHash(User.hashPassword(password)); adminRepository.save(admin); }
 log("Admin logged in: " + admin.getUserId());
 return admin;
 }

 /**
 * Returns all admins.
 *
 * @return List of all admins
 */
 public List<Admin> getAllAdmins() {
 return adminRepository.findAll();
 }

 /**
 * Creates a new admin account.
 *
 * @param name Full name
 * @param email Email address
 * @param phone Phone number
 * @param password Plain-text password
 * @param level "SUPER" or "STANDARD"
 * @return The created Admin
 * @throws ValidationException if any field is invalid
 * @throws BusinessRuleException if email already exists
 */
 public Admin createAdmin(String name, String email, String phone,
 String password, String level) {
 requireNonEmpty(name, "Full Name");
 requireNonEmpty(email, "Email");
 requireNonEmpty(phone, "Phone");
 requireNonEmpty(password, "Password");
 requireNonEmpty(level, "Admin Level");

 if (!ValidationUtil.isValidEmail(email)) {
 throw new ValidationException("Email address is not valid.");
 }
 if (!ValidationUtil.isValidPhone(phone)) {
 throw new ValidationException("Phone number is not valid (9-15 digits).");
 }
 if (!ValidationUtil.isValidPassword(password)) {
 throw new ValidationException("Password must be at least 12 characters.");
 }
 if (!level.equals("SUPER") && !level.equals("STANDARD")) {
 throw new ValidationException("Admin level must be SUPER or STANDARD.");
 }
 if (adminRepository.findByEmail(email).isPresent()) {
 throw new BusinessRuleException("An admin with this email already exists.");
 }

 String id = FileUtil.generateId("ADM");
 Admin admin = new Admin(
 id,
 name.trim(),
 email.trim().toLowerCase(),
 phone.trim(),
 User.hashPassword(password),
 ValidationUtil.nowDateTime(),
 true,
 level.trim()
 );
 adminRepository.save(admin);
 log("Created admin: " + admin.getUserId());
 return admin;
 }

 /**
 * Retrieves an admin by their ID.
 *
 * @param id Admin ID
 * @return The Admin
 * @throws EntityNotFoundException if not found
 */
 public Admin getById(String id) {
 return adminRepository.findById(id)
 .orElseThrow(() -> new EntityNotFoundException("Admin not found: " + id));
 }
}
