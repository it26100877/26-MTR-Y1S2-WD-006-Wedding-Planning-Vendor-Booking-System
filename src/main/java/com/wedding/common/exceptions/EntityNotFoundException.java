package com.wedding.common.exceptions;

/** Thrown when a requested entity is not found. */
public class EntityNotFoundException extends AppException {
 public EntityNotFoundException(String message) {
 super("NOT_FOUND", message);
 }

 public EntityNotFoundException(String entity, String id) {
 super("NOT_FOUND", entity + " not found with ID: " + id);
 }
}
