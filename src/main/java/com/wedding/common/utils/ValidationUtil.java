package com.wedding.common.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class for date and validation operations.
 *
 * @author Common Module
 */
public class ValidationUtil {

 public static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
 public static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

 public static boolean isNullOrEmpty(String s) {
 return s == null || s.trim().isEmpty();
 }

 public static boolean isValidEmail(String email) {
 if (isNullOrEmpty(email)) return false;
 return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
 }

 /**
  * Accept Sri Lankan mobile numbers in local (07XXXXXXXX) or international
  * (+947XXXXXXXX) format. Spaces and hyphens are intentionally not accepted.
  */
 public static boolean isValidPhone(String phone) {
 if (isNullOrEmpty(phone)) return false;
 return phone.matches("^(?:07[0-9]{8}|\\+947[0-9]{8})$");
 }

 public static boolean isValidDate(String date) {
 if (isNullOrEmpty(date)) return false;
 try {
 LocalDate.parse(date, DATE_FMT);
 return true;
 } catch (DateTimeParseException e) {
 return false;
 }
 }

 public static boolean isFutureDate(String date) {
 if (!isValidDate(date)) return false;
 return LocalDate.parse(date, DATE_FMT).isAfter(LocalDate.now());
 }

 public static String today() {
 return LocalDate.now().format(DATE_FMT);
 }

 public static String nowDateTime() {
 return LocalDateTime.now().format(DATETIME_FMT);
 }

 public static boolean isPositiveDecimal(String value) {
 try {
 return new java.math.BigDecimal(value).compareTo(java.math.BigDecimal.ZERO) > 0;
 } catch (NumberFormatException e) {
 return false;
 }
 }

 public static boolean isValidPassword(String password) {
 return password != null && password.length() >= 8;
 }
}

