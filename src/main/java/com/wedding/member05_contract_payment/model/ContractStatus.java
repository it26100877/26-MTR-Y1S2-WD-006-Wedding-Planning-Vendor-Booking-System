package com.wedding.member05_contract_payment.model;

/**
 * Enum representing the lifecycle status of a vendor contract.
 * Demonstrates use of enum with associated metadata.
 *
 * @author Member 05
 */
public enum ContractStatus {

 DRAFT("Draft"),
 ACTIVE("Active"),
 COMPLETED("Completed"),
 CANCELLED("Cancelled");

 private final String displayName;

 ContractStatus(String displayName) {
 this.displayName = displayName;
 }

 public String getDisplayName() {
 return displayName;
 }

 public static ContractStatus fromString(String name) {
 if (name == null || name.trim().isEmpty()) return DRAFT;
 try {
 return ContractStatus.valueOf(name.trim().toUpperCase());
 } catch (IllegalArgumentException e) {
 return DRAFT;
 }
 }

 @Override
 public String toString() {
 return displayName;
 }
}
