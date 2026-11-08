package com.wedding.member01_customer_admin.repository;

import com.wedding.common.model.User;
import com.wedding.common.repository.AbstractFileRepository;
import com.wedding.common.utils.FileUtil;
import com.wedding.member01_customer_admin.model.Admin;

import java.util.Optional;

/**
 * File-based repository for {@link Admin} entities.
 * Extends AbstractFileRepository demonstrating Inheritance.
 * Data is persisted in admins.csv with 8 fields.
 * Seeds a default SUPER admin if the file is empty.
 *
 * @author Member 01
 */
public class AdminRepository extends AbstractFileRepository<Admin> {

 private static final String FILE_NAME = "admins.csv";

 public AdminRepository() {
 super(FILE_NAME);
 seedDefaultAdmin();
 }

 // ─── Serialization ───────────────────────────────────────────────────────────

 /**
 * Serializes an Admin to a CSV line.
 * Format: userId,name,email,phone,passwordHash,createdAt,active,adminLevel
 */
 @Override
 protected String serialize(Admin a) {
 return String.join(",",
 FileUtil.escape(a.getUserId()),
 FileUtil.escape(a.getName()),
 FileUtil.escape(a.getEmail()),
 FileUtil.escape(a.getPhone()),
 FileUtil.escape(a.getPasswordHash()),
 FileUtil.escape(a.getCreatedAt()),
 String.valueOf(a.isActive()),
 FileUtil.escape(a.getAdminLevel())
 );
 }

 /**
 * Deserializes a CSV line back to an Admin.
 * Expects 8 fields.
 */
 @Override
 protected Admin deserialize(String line) {
 String[] p = FileUtil.splitCSV(line);
 if (p.length < 8) return null;
 return new Admin(
 p[0].trim(), // userId
 p[1].trim(), // name
 p[2].trim(), // email
 p[3].trim(), // phone
 p[4].trim(), // passwordHash
 p[5].trim(), // createdAt
 Boolean.parseBoolean(p[6].trim()), // active
 p[7].trim() // adminLevel
 );
 }

 @Override
 protected String getId(Admin a) {
 return a.getUserId();
 }

 // ─── Additional Query Methods ─────────────────────────────────────────────────

 /**
 * Finds an admin by their email address.
 *
 * @param email The email to search for
 * @return Optional containing the admin if found
 */
 public Optional<Admin> findByEmail(String email) {
 return cache.values().stream()
 .filter(a -> a.getEmail().equalsIgnoreCase(email))
 .findFirst();
 }

 // ─── Seeding ─────────────────────────────────────────────────────────────────

 /**
 * Seeds a default SUPER admin if no admins exist.
 * Default credentials: admin@wedding.com / admin123
 */
 private void seedDefaultAdmin() {
 if (cache.isEmpty()) {
 Admin defaultAdmin = new Admin(
 "ADM-001",
 "System Admin",
 "admin@wedding.com",
 "0000000000",
 User.hashPassword("admin123"),
 com.wedding.common.utils.ValidationUtil.nowDateTime(),
 true,
 "SUPER"
 );
 save(defaultAdmin);
 System.out.println("[AdminRepository] Default SUPER admin seeded.");
 }
 }
}
