package com.wedding.common.exceptions;

/**
 * Base application exception for the Wedding Planning System.
 * All custom exceptions extend this class (Inheritance).
 *
 * @author Common Module
 */
public class AppException extends RuntimeException {

 private final String errorCode;

 public AppException(String message) {
 super(message);
 this.errorCode = "APP_ERROR";
 }

 public AppException(String errorCode, String message) {
 super(message);
 this.errorCode = errorCode;
 }

 public AppException(String message, Throwable cause) {
 super(message, cause);
 this.errorCode = "APP_ERROR";
 }

 public String getErrorCode() {
 return errorCode;
 }

 @Override
 public String toString() {
 return "[" + errorCode + "] " + getMessage();
 }
}

