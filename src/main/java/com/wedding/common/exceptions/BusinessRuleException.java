package com.wedding.common.exceptions;

/** Thrown when a business rule is violated. */
public class BusinessRuleException extends AppException {
 public BusinessRuleException(String message) {
 super("BUSINESS_RULE", message);
 }
}

