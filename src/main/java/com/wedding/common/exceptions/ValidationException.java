package com.wedding.common.exceptions;

/** Thrown when input validation fails. */
public class ValidationException extends AppException {
 public ValidationException(String message) {
 super("VALIDATION_ERROR", message);
 }
}

