package com.wedding.member05_contract_payment.controller;

import com.wedding.common.exceptions.AppException;
import com.wedding.common.model.User;
import com.wedding.member01_customer_admin.model.Customer;
import com.wedding.member01_customer_admin.service.CustomerService;
import com.wedding.member04_booking.model.Booking;
import com.wedding.member04_booking.model.BookingStatus;
import com.wedding.member04_booking.service.BookingService;
import com.wedding.member03_vendor.model.Vendor;
import com.wedding.member03_vendor.service.VendorService;
import com.wedding.member02_wedding_plan.model.WeddingPlan;
import com.wedding.member02_wedding_plan.service.WeddingPlanService;
import com.wedding.member05_contract_payment.model.Payment;
import com.wedding.member05_contract_payment.model.VendorContract;
import com.wedding.member05_contract_payment.service.ContractService;
import com.wedding.member05_contract_payment.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Controller managing Vendor Contracts and financial Payment installments.
 * Supports recording AdvancePayment, PartialPayment, FinalPayment, payment reversal,
 * and automatic outstanding balance calculations.
 *
 * @author Member 05 – Vendor Contract & Payment Management
 */
@Controller
public class ContractPaymentController {

 private final ContractService contractService;
 private final PaymentService paymentService;
 private final BookingService bookingService;
 private final CustomerService customerService;
 private final VendorService vendorService;
 private final WeddingPlanService weddingPlanService;

 public ContractPaymentController(ContractService contractService,
 PaymentService paymentService,
 BookingService bookingService,
 CustomerService customerService,
 VendorService vendorService,
 WeddingPlanService weddingPlanService) {
 this.contractService = contractService;
 this.paymentService = paymentService;
 this.bookingService = bookingService;
 this.customerService = customerService;
 this.vendorService = vendorService;
 this.weddingPlanService = weddingPlanService;
 }

 // ─── List Contracts ──────────────────────────────────────────────────────────
 @GetMapping("/contracts")
 public String listContracts(HttpSession httpSession, Model model) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null) return "redirect:/login";

 List<VendorContract> contracts;
 if ("ADMIN".equalsIgnoreCase(user.getRole())) {
 contracts = contractService.getAllContracts();
 } else {
 contracts = contractService.getByCustomer(user.getUserId());
 }

 // Refresh payment totals before building the dashboard summary.
 contracts.forEach(c -> {
     contractService.refreshTotals(c.getContractId());
     c.setTotalPaid(paymentService.getTotalPaid(c.getContractId()));
     c.updatePaymentStatus();
 });

 BigDecimal totalContractValue = contracts.stream()
         .map(VendorContract::getContractAmount)
         .filter(amount -> amount != null)
         .reduce(BigDecimal.ZERO, BigDecimal::add);
 BigDecimal totalCollected = contracts.stream()
         .map(VendorContract::getTotalPaid)
         .filter(amount -> amount != null)
         .reduce(BigDecimal.ZERO, BigDecimal::add);
 BigDecimal totalOutstanding = contracts.stream()
         .map(VendorContract::getOutstandingBalance)
         .reduce(BigDecimal.ZERO, BigDecimal::add);
 long activeContracts = contracts.stream()
         .filter(c -> c.getStatus() == com.wedding.member05_contract_payment.model.ContractStatus.ACTIVE)
         .count();
 long completedContracts = contracts.stream()
         .filter(c -> c.getStatus() == com.wedding.member05_contract_payment.model.ContractStatus.COMPLETED)
         .count();

 model.addAttribute("contracts", contracts);
 model.addAttribute("totalContracts", contracts.size());
 model.addAttribute("activeContracts", activeContracts);
 model.addAttribute("completedContracts", completedContracts);
 model.addAttribute("totalContractValue", totalContractValue);
 model.addAttribute("totalCollected", totalCollected);
 model.addAttribute("totalOutstanding", totalOutstanding);
 model.addAttribute("pageTitle", "Contracts & Payments - Éclat");
 model.addAttribute("activePage", "contracts");
 return "member05/contracts";
 }

 // ─── Create Contract Form ────────────────────────────────────────────────────
 @GetMapping("/contracts/new")
 public String showCreateContractForm(@RequestParam(value = "bookingId", required = false) String bookingId,
 HttpSession httpSession, Model model, RedirectAttributes redirectAttributes) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null) return "redirect:/login";
 if (!isAdmin(user)) {
  redirectAttributes.addFlashAttribute("errorMessage", "Only an administrator can generate contracts.");
  return "redirect:/bookings";
 }

 if (bookingId == null || bookingId.isEmpty()) {
 redirectAttributes.addFlashAttribute("warningMessage", "Please select a confirmed booking to generate a contract.");
 return "redirect:/bookings";
 }

 try {
 Booking booking = bookingService.getById(bookingId);
 if (booking.getStatus() != BookingStatus.CONFIRMED && booking.getStatus() != BookingStatus.COMPLETED) {
 redirectAttributes.addFlashAttribute("errorMessage", "Contracts can only be created for CONFIRMED bookings.");
 return "redirect:/bookings/view/" + bookingId;
 }

 model.addAttribute("booking", booking);
 model.addAttribute("vendor", vendorService.getById(booking.getVendorId()));
 model.addAttribute("weddingPlan", weddingPlanService.getById(booking.getPlanId()));
 model.addAttribute("pageTitle", "Generate Contract - Booking #" + bookingId);
 model.addAttribute("activePage", "contracts");
 return "member05/contract-form";
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 return "redirect:/bookings";
 }
 }

 @PostMapping("/contracts/new")
 public String processCreateContract(@RequestParam("bookingId") String bookingId,
 @RequestParam("customerId") String customerId,
 @RequestParam("vendorId") String vendorId,
 @RequestParam("startDate") String startDate,
 @RequestParam("endDate") String endDate,
 @RequestParam("contractAmount") BigDecimal contractAmount,
 @RequestParam(value = "terms", required = false) String terms,
 HttpSession httpSession, RedirectAttributes redirectAttributes) {
 User actor = (User) httpSession.getAttribute("currentUser");
 if (!isAdmin(actor)) {
  redirectAttributes.addFlashAttribute("errorMessage", "Only an administrator can generate contracts.");
  return "redirect:/login";
 }
 try {
 VendorContract contract = contractService.createContract(
 bookingId, customerId, vendorId, startDate, endDate, contractAmount, terms);
 redirectAttributes.addFlashAttribute("successMessage",
 "Contract " + contract.getContractId() + " generated successfully! You may now record payments.");
 return "redirect:/contracts/view/" + contract.getContractId();
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 return "redirect:/contracts/new?bookingId=" + bookingId;
 }
 }

 // ─── Contract & Payment Details ──────────────────────────────────────────────
 @GetMapping("/contracts/view/{id}")
 public String viewContractAndPayments(@PathVariable("id") String id, HttpSession httpSession,
                                       Model model, RedirectAttributes redirectAttributes) {
 User viewer = (User) httpSession.getAttribute("currentUser");
 if (viewer == null) return "redirect:/login";
 VendorContract contract;
 try {
  contract = contractService.getById(id);
  if (!canAccessContract(viewer, contract)) {
   redirectAttributes.addFlashAttribute("errorMessage", "You are not allowed to view this contract.");
   return "redirect:/contracts";
  }
  contractService.refreshTotals(id);
  contract = contractService.getById(id);
 } catch (AppException e) {
  redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
  return "redirect:/contracts";
 }
 List<Payment> payments = paymentService.getAllPayments(id);
 BigDecimal outstanding = paymentService.getOutstandingBalance(contract);

 model.addAttribute("contract", contract);
 model.addAttribute("payments", payments);
 model.addAttribute("outstanding", outstanding);
 model.addAttribute("paymentStatus", contract.getPaymentStatus());
 model.addAttribute("serverPaymentDate", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
 model.addAttribute("isAdmin", isAdmin(viewer));
 model.addAttribute("auditEntries", buildAuditEntries(contract, payments));
 model.addAttribute("pageTitle", "Contract " + contract.getContractId() + " - Payments");
 model.addAttribute("activePage", "contracts");
 return "member05/payment-detail";
 }


 @GetMapping("/contracts/{id}/print")
 public String printContract(@PathVariable("id") String id, HttpSession session,
                             Model model, RedirectAttributes redirectAttributes) {
  User viewer = (User) session.getAttribute("currentUser");
  if (viewer == null) return "redirect:/login";
  try {
   VendorContract contract = contractService.getById(id);
   if (!canAccessContract(viewer, contract)) {
    redirectAttributes.addFlashAttribute("errorMessage", "You are not allowed to view this contract.");
    return "redirect:/contracts";
   }
   model.addAttribute("contract", contract);
   model.addAttribute("customer", customerService.getById(contract.getCustomerId()));
   model.addAttribute("payments", paymentService.getAllPayments(id));
   model.addAttribute("pageTitle", "Contract " + id + " - Printable Copy");
   return "member05/contract-print";
  } catch (AppException ex) {
   redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
   return "redirect:/contracts";
  }
 }

 @GetMapping("/contracts/{id}/export.csv")
 public org.springframework.http.ResponseEntity<String> exportPaymentsCsv(
         @PathVariable("id") String id, HttpSession session) {
  User viewer = (User) session.getAttribute("currentUser");
  if (viewer == null) return org.springframework.http.ResponseEntity.status(401).build();
  VendorContract contract = contractService.getById(id);
  if (!canAccessContract(viewer, contract)) return org.springframework.http.ResponseEntity.status(403).build();
  StringBuilder csv = new StringBuilder("Payment ID,Type,Amount,Payment Date,Method,Transaction Reference,Status,Recorded At,Notes\r\n");
  for (Payment p : paymentService.getAllPayments(id)) {
   csv.append(csvCell(p.getPaymentId())).append(',')
      .append(csvCell(p.getPaymentType())).append(',')
      .append(csvCell(p.getAmount().toPlainString())).append(',')
      .append(csvCell(p.getPaymentDate())).append(',')
      .append(csvCell(p.getPaymentMethod())).append(',')
      .append(csvCell(p.getTransactionRef())).append(',')
      .append(csvCell(p.isVoided() ? "VOIDED" : "VALID")).append(',')
      .append(csvCell(p.getRecordedAt())).append(',')
      .append(csvCell(p.getNotes())).append("\r\n");
  }
  return org.springframework.http.ResponseEntity.ok()
      .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
          "attachment; filename=contract-" + id + "-payments.csv")
      .contentType(new org.springframework.http.MediaType("text", "csv", java.nio.charset.StandardCharsets.UTF_8))
      .body(csv.toString());
 }

 @GetMapping("/payments/{paymentId}/receipt")
 public String printPaymentReceipt(@PathVariable("paymentId") String paymentId,
                                   HttpSession session, Model model,
                                   RedirectAttributes redirectAttributes) {
  User viewer = (User) session.getAttribute("currentUser");
  if (viewer == null) return "redirect:/login";
  try {
   Payment payment = paymentService.getAllPaymentsForReceipt(paymentId);
   VendorContract contract = contractService.getById(payment.getContractId());
   if (!canAccessContract(viewer, contract)) {
    redirectAttributes.addFlashAttribute("errorMessage", "You are not allowed to view this receipt.");
    return "redirect:/contracts";
   }
   model.addAttribute("payment", payment);
   model.addAttribute("contract", contract);
   model.addAttribute("customer", customerService.getById(contract.getCustomerId()));
   model.addAttribute("pageTitle", "Payment Receipt - " + paymentId);
   return "member05/payment-receipt";
  } catch (AppException ex) {
   redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
   return "redirect:/contracts";
  }
 }

 private String csvCell(String value) {
  String safe = value == null ? "" : value;
  if (safe.matches("^[=+@-].*")) safe = "'" + safe;
  return "\"" + safe.replace("\"", "\"\"") + "\"";
 }

 @GetMapping("/admin/payment-proofs")
 public String paymentProofQueue(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
  User actor = (User) session.getAttribute("currentUser");
  if (!isAdmin(actor)) return actor == null ? "redirect:/login" : "redirect:/dashboard";
  try {
   java.nio.file.Path path = java.nio.file.Paths.get("data", "payment-proofs", "payment-proofs.csv");
   java.util.List<String[]> rows = new java.util.ArrayList<>();
   if (java.nio.file.Files.exists(path)) {
    for (String line : java.nio.file.Files.readAllLines(path)) {
     String[] parts = line.split("\\|", -1);
     if (parts.length >= 8) rows.add(parts);
    }
   }
   model.addAttribute("paymentProofs", rows);
   model.addAttribute("pageTitle", "Payment Proof Verification");
   model.addAttribute("activePage", "admin");
   return "member05/payment-proofs";
  } catch (java.io.IOException ex) {
   redirectAttributes.addFlashAttribute("errorMessage", "Unable to read payment proof queue.");
   return "redirect:/dashboard";
  }
 }

 @PostMapping("/admin/payment-proofs/decision")
 public String decidePaymentProof(@RequestParam("proofId") String proofId,
                                  @RequestParam("decision") String decision,
                                  @RequestParam(value = "reason", required = false) String reason,
                                  HttpSession session, RedirectAttributes redirectAttributes) {
  User actor = (User) session.getAttribute("currentUser");
  if (!isAdmin(actor)) return actor == null ? "redirect:/login" : "redirect:/dashboard";
  java.nio.file.Path path = java.nio.file.Paths.get("data", "payment-proofs", "payment-proofs.csv");
  try {
   if (!java.nio.file.Files.exists(path)) throw new java.io.IOException("Queue does not exist");
   java.util.List<String> updated = new java.util.ArrayList<>();
   boolean found = false;
   for (String line : java.nio.file.Files.readAllLines(path)) {
    String[] parts = line.split("\\|", -1);
    if (parts.length >= 8 && parts[0].equals(proofId)) {
     if (!"PENDING".equals(parts[7])) throw new com.wedding.common.exceptions.BusinessRuleException("This proof was already processed.");
     if ("APPROVE".equalsIgnoreCase(decision)) {
      VendorContract contract = contractService.getById(parts[1]);
      if (contract.getStatus() != com.wedding.member05_contract_payment.model.ContractStatus.ACTIVE) {
       throw new com.wedding.common.exceptions.BusinessRuleException("Only active contracts can accept verified payments.");
      }
      boolean alreadyRecorded = paymentService.getAllPayments(parts[1]).stream()
          .anyMatch(payment -> parts[5].equalsIgnoreCase(payment.getTransactionRef()));
      if (!alreadyRecorded) {
       paymentService.recordPartialPayment(contract, new BigDecimal(parts[3]),
           LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), parts[4], parts[5],
           "Customer proof " + proofId + " approved by " + actor.getUserId(), nextInstallmentNumber(parts[1]));
       contractService.refreshTotals(parts[1]);
      }
            parts[7] = "APPROVED";
     } else if ("REJECT".equalsIgnoreCase(decision)) {
      parts[7] = "REJECTED";
      parts = java.util.Arrays.copyOf(parts, Math.max(parts.length, 9));
      parts[8] = (reason == null ? "Rejected by Admin" : reason.replace("|", "/").replace("\\n", " "));
     } else {
      throw new com.wedding.common.exceptions.ValidationException("Choose APPROVE or REJECT.");
     }
     found = true;
     line = String.join("|", parts);
    }
    updated.add(line);
   }
   if (!found) throw new com.wedding.common.exceptions.EntityNotFoundException("Payment Proof", proofId);
   java.nio.file.Path temp = path.resolveSibling("payment-proofs.csv.tmp");
   java.nio.file.Files.write(temp, updated);
   try { java.nio.file.Files.move(temp, path, java.nio.file.StandardCopyOption.REPLACE_EXISTING, java.nio.file.StandardCopyOption.ATOMIC_MOVE); }
   catch (java.nio.file.AtomicMoveNotSupportedException ex) { java.nio.file.Files.move(temp, path, java.nio.file.StandardCopyOption.REPLACE_EXISTING); }
   redirectAttributes.addFlashAttribute("successMessage", "Payment proof " + decision.toLowerCase() + "d successfully.");
  } catch (AppException | java.io.IOException ex) {
   redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
  }
  return "redirect:/admin/payment-proofs";
 }

 private int nextInstallmentNumber(String contractId) {
  return paymentService.getAllPayments(contractId).stream()
      .filter(p -> p instanceof com.wedding.member05_contract_payment.model.PartialPayment)
      .map(p -> (com.wedding.member05_contract_payment.model.PartialPayment) p)
      .mapToInt(com.wedding.member05_contract_payment.model.PartialPayment::getInstallmentNumber)
      .max().orElse(0) + 1;
 }

 @PostMapping("/contracts/{id}/payments/proof")
 @GetMapping("/admin/payment-proofs")
 public String paymentProofQueue(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
  User actor = (User) session.getAttribute("currentUser");
  if (!isAdmin(actor)) return actor == null ? "redirect:/login" : "redirect:/dashboard";
  try {
   java.nio.file.Path path = java.nio.file.Paths.get("data", "payment-proofs", "payment-proofs.csv");
   java.util.List<String[]> rows = new java.util.ArrayList<>();
   if (java.nio.file.Files.exists(path)) {
    for (String line : java.nio.file.Files.readAllLines(path)) {
     String[] parts = line.split("\\|", -1);
     if (parts.length >= 8) rows.add(parts);
    }
   }
   model.addAttribute("paymentProofs", rows);
   model.addAttribute("pageTitle", "Payment Proof Verification");
   model.addAttribute("activePage", "admin");
   return "member05/payment-proofs";
  } catch (java.io.IOException ex) {
   redirectAttributes.addFlashAttribute("errorMessage", "Unable to read payment proof queue.");
   return "redirect:/dashboard";
  }
 }

 @PostMapping("/admin/payment-proofs/decision")
 public String decidePaymentProof(@RequestParam("proofId") String proofId,
                                  @RequestParam("decision") String decision,
                                  @RequestParam(value = "reason", required = false) String reason,
                                  HttpSession session, RedirectAttributes redirectAttributes) {
  User actor = (User) session.getAttribute("currentUser");
  if (!isAdmin(actor)) return actor == null ? "redirect:/login" : "redirect:/dashboard";
  java.nio.file.Path path = java.nio.file.Paths.get("data", "payment-proofs", "payment-proofs.csv");
  try {
   if (!java.nio.file.Files.exists(path)) throw new java.io.IOException("Queue does not exist");
   java.util.List<String> updated = new java.util.ArrayList<>();
   boolean found = false;
   for (String line : java.nio.file.Files.readAllLines(path)) {
    String[] parts = line.split("\\|", -1);
    if (parts.length >= 8 && parts[0].equals(proofId)) {
     if (!"PENDING".equals(parts[7])) throw new com.wedding.common.exceptions.BusinessRuleException("This proof was already processed.");
     if ("APPROVE".equalsIgnoreCase(decision)) {
      VendorContract contract = contractService.getById(parts[1]);
      if (contract.getStatus() != com.wedding.member05_contract_payment.model.ContractStatus.ACTIVE) {
       throw new com.wedding.common.exceptions.BusinessRuleException("Only active contracts can accept verified payments.");
      }
      paymentService.recordPartialPayment(contract, new BigDecimal(parts[3]),
          LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), parts[4], parts[5],
          "Customer proof " + proofId + " approved by " + actor.getUserId(), nextInstallmentNumber(parts[1]));
      contractService.refreshTotals(parts[1]);
      parts[7] = "APPROVED";
     } else if ("REJECT".equalsIgnoreCase(decision)) {
      parts[7] = "REJECTED";
      parts = java.util.Arrays.copyOf(parts, Math.max(parts.length, 9));
      parts[8] = (reason == null ? "Rejected by Admin" : reason.replace("|", "/").replace("\\n", " "));
     } else {
      throw new com.wedding.common.exceptions.ValidationException("Choose APPROVE or REJECT.");
     }
     found = true;
     line = String.join("|", parts);
    }
    updated.add(line);
   }
   if (!found) throw new com.wedding.common.exceptions.EntityNotFoundException("Payment Proof", proofId);
   java.nio.file.Path temp = path.resolveSibling("payment-proofs.csv.tmp");
   java.nio.file.Files.write(temp, updated);
   try { java.nio.file.Files.move(temp, path, java.nio.file.StandardCopyOption.REPLACE_EXISTING, java.nio.file.StandardCopyOption.ATOMIC_MOVE); }
   catch (java.nio.file.AtomicMoveNotSupportedException ex) { java.nio.file.Files.move(temp, path, java.nio.file.StandardCopyOption.REPLACE_EXISTING); }
   redirectAttributes.addFlashAttribute("successMessage", "Payment proof " + decision.toLowerCase() + "d successfully.");
  } catch (AppException | java.io.IOException ex) {
   redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
  }
  return "redirect:/admin/payment-proofs";
 }

 private int nextInstallmentNumber(String contractId) {
  return paymentService.getAllPayments(contractId).stream()
      .filter(p -> p instanceof com.wedding.member05_contract_payment.model.PartialPayment)
      .map(p -> (com.wedding.member05_contract_payment.model.PartialPayment) p)
      .mapToInt(com.wedding.member05_contract_payment.model.PartialPayment::getInstallmentNumber)
      .max().orElse(0) + 1;
 }

 @PostMapping("/contracts/{id}/payments/proof")
 public String submitPaymentProof(@PathVariable("id") String id,
                                  @RequestParam("amount") BigDecimal amount,
                                  @RequestParam("method") String method,
                                  @RequestParam("transactionRef") String transactionRef,
                                  @RequestParam(value = "notes", required = false) String notes,
                                  HttpSession session, RedirectAttributes redirectAttributes) {
  User actor = (User) session.getAttribute("currentUser");
  if (actor == null) return "redirect:/login";
  try {
   VendorContract contract = contractService.getById(id);
   if (!"CUSTOMER".equalsIgnoreCase(actor.getRole())
           || !actor.getUserId().equalsIgnoreCase(contract.getCustomerId())) {
    redirectAttributes.addFlashAttribute("errorMessage", "Only the owner customer may submit payment proof.");
    return "redirect:/contracts";
   }
   if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0
           || amount.compareTo(paymentService.getOutstandingBalance(contract)) > 0) {
    throw new com.wedding.common.exceptions.ValidationException("Enter a positive amount within the outstanding balance.");
   }
   if (transactionRef == null || transactionRef.trim().length() < 4) {
    throw new com.wedding.common.exceptions.ValidationException("Enter a valid transaction reference.");
   }
   if (!java.util.List.of("BANK_TRANSFER", "CARD", "CASH").contains(method == null ? "" : method.trim().toUpperCase())) {
    throw new com.wedding.common.exceptions.ValidationException("Select a supported payment method.");
   }
   String normalizedRef = transactionRef.trim();
   boolean duplicate = paymentService.getAllPayments(id).stream()
       .anyMatch(p -> normalizedRef.equalsIgnoreCase(p.getTransactionRef()));
   if (duplicate) throw new com.wedding.common.exceptions.BusinessRuleException("This transaction reference already exists.");
   // Proof is recorded as a pending audit note and is not added to paid totals until Admin verification.
   java.nio.file.Path folder = java.nio.file.Paths.get("data", "payment-proofs");
   java.nio.file.Files.createDirectories(folder);
   String proofId = com.wedding.common.utils.FileUtil.generateId("PRF");
   java.nio.file.Path queue = folder.resolve("payment-proofs.csv");
   java.util.List<String> proofLines = java.nio.file.Files.exists(queue)
       ? java.nio.file.Files.readAllLines(queue) : new java.util.ArrayList<>();
   boolean duplicateRef = proofLines.stream().map(existing -> existing.split("\\|", -1))
       .anyMatch(parts -> parts.length >= 8 && normalizedRef.equalsIgnoreCase(parts[5]));
   if (duplicateRef) throw new com.wedding.common.exceptions.BusinessRuleException("This transaction reference has already been submitted.");
   String line = proofId + "|" + id + "|" + actor.getUserId() + "|" + amount.toPlainString()
       + "|" + method.trim().toUpperCase() + "|" + normalizedRef.replace("|", "/")
       + "|" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
       + "|PENDING|" + (notes == null ? "" : notes.replace("|", "/").replace("\\n", " "));
   proofLines.add(line);
   java.nio.file.Path tempQueue = queue.resolveSibling("payment-proofs.csv.tmp");
   java.nio.file.Files.write(tempQueue, proofLines);
   try { java.nio.file.Files.move(tempQueue, queue, java.nio.file.StandardCopyOption.REPLACE_EXISTING, java.nio.file.StandardCopyOption.ATOMIC_MOVE); }
   catch (java.nio.file.AtomicMoveNotSupportedException moveEx) { java.nio.file.Files.move(tempQueue, queue, java.nio.file.StandardCopyOption.REPLACE_EXISTING); }
   redirectAttributes.addFlashAttribute("successMessage", "Payment proof submitted for Admin verification. Your paid balance will update after verification.");
  } catch (AppException ex) {
   redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
  } catch (java.io.IOException ex) {
   redirectAttributes.addFlashAttribute("errorMessage", "Unable to save payment proof right now.");
  }
  return "redirect:/contracts/view/" + id;
 }

 @GetMapping("/contracts/edit/{id}")
 public String showEditContractForm(@PathVariable("id") String id, HttpSession httpSession,
                                    Model model, RedirectAttributes redirectAttributes) {
  User actor = (User) httpSession.getAttribute("currentUser");
  if (!isAdmin(actor)) { redirectAttributes.addFlashAttribute("errorMessage", "Only an administrator can manage contracts."); return actor == null ? "redirect:/login" : "redirect:/contracts"; }
  try {
   VendorContract contract = contractService.getById(id);
   if (contract.getStatus() == com.wedding.member05_contract_payment.model.ContractStatus.COMPLETED
           || contract.getStatus() == com.wedding.member05_contract_payment.model.ContractStatus.CANCELLED) {
    redirectAttributes.addFlashAttribute("errorMessage", "Completed or cancelled contracts cannot be edited.");
    return "redirect:/contracts/view/" + id;
   }
   model.addAttribute("contract", contract);
   model.addAttribute("pageTitle", "Edit Contract - Éclat");
   model.addAttribute("activePage", "contracts");
   return "member05/contract-edit-form";
  } catch (AppException e) {
   redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
   return "redirect:/contracts";
  }
 }

 @PostMapping("/contracts/edit/{id}")
 public String updateContract(@PathVariable("id") String id,
                              @RequestParam("startDate") String startDate,
                              @RequestParam("endDate") String endDate,
                              @RequestParam("contractAmount") BigDecimal contractAmount,
                              @RequestParam(value = "terms", required = false) String terms,
                              HttpSession httpSession, RedirectAttributes redirectAttributes) {
  User actor = (User) httpSession.getAttribute("currentUser");
  if (!isAdmin(actor)) { redirectAttributes.addFlashAttribute("errorMessage", "Only an administrator can manage contracts."); return actor == null ? "redirect:/login" : "redirect:/contracts"; }
  try {
   contractService.updateContract(id, startDate, endDate, contractAmount, terms);
   redirectAttributes.addFlashAttribute("successMessage", "Contract details updated successfully.");
  } catch (AppException e) {
   redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
  }
  return "redirect:/contracts/view/" + id;
 }


 @PostMapping("/contracts/cancel/{id}")
 public String cancelContract(@PathVariable("id") String id, HttpSession httpSession,
                              RedirectAttributes redirectAttributes) {
  User actor = (User) httpSession.getAttribute("currentUser");
  if (!isAdmin(actor)) {
   redirectAttributes.addFlashAttribute("errorMessage", "Only an administrator can cancel contracts.");
   return actor == null ? "redirect:/login" : "redirect:/contracts";
  }
  try {
   contractService.cancelContract(id);
   redirectAttributes.addFlashAttribute("warningMessage", "Contract cancelled. Its payment history has been retained.");
  } catch (AppException e) {
   redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
  }
  return "redirect:/contracts/view/" + id;
 }

 // ─── Record Advance Payment ──────────────────────────────────────────────────
 @PostMapping("/contracts/{id}/payments/advance")
 public String recordAdvancePayment(@PathVariable("id") String contractId,
 @RequestParam("amount") BigDecimal amount,
 @RequestParam(value = "paymentDate", required = false) String paymentDate,
 @RequestParam("method") String method,
 @RequestParam(value = "transactionRef", required = false) String transactionRef,
 @RequestParam(value = "notes", required = false) String notes,
 @RequestParam(value = "advancePct", defaultValue = "0.0") double advancePct,
 HttpSession httpSession, RedirectAttributes redirectAttributes) {
  User actor = (User) httpSession.getAttribute("currentUser");
  if (!isAdmin(actor)) {
   redirectAttributes.addFlashAttribute("errorMessage", "Only an administrator can record payments.");
   return actor == null ? "redirect:/login" : "redirect:/contracts";
  }
  try {
   VendorContract accessCheck = contractService.getById(contractId);
   if (!isAdmin(actor) || !canAccessContract(actor, accessCheck)) {
    redirectAttributes.addFlashAttribute("errorMessage", "Only an administrator can record payments for this contract.");
    return "redirect:/contracts";
   }
  } catch (AppException ex) {
   redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
   return "redirect:/contracts";
  }
 try {
 VendorContract contract = contractService.getById(contractId);
 paymentService.recordAdvancePayment(contract, amount, LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), method, transactionRef, notes, advancePct);
 contractService.refreshTotals(contractId);
 redirectAttributes.addFlashAttribute("successMessage", "Advance payment of LKR " + amount + " recorded successfully!");
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 }
 return "redirect:/contracts/view/" + contractId;
 }

 // ─── Record Partial Payment ──────────────────────────────────────────────────
 @PostMapping("/contracts/{id}/payments/partial")
 public String recordPartialPayment(@PathVariable("id") String contractId,
 @RequestParam("amount") BigDecimal amount,
 @RequestParam(value = "paymentDate", required = false) String paymentDate,
 @RequestParam("method") String method,
 @RequestParam(value = "transactionRef", required = false) String transactionRef,
 @RequestParam(value = "notes", required = false) String notes,
 @RequestParam(value = "installmentNumber", defaultValue = "1") int installmentNumber,
 HttpSession httpSession, RedirectAttributes redirectAttributes) {
  User actor = (User) httpSession.getAttribute("currentUser");
  if (!isAdmin(actor)) {
   redirectAttributes.addFlashAttribute("errorMessage", "Only an administrator can record payments.");
   return actor == null ? "redirect:/login" : "redirect:/contracts";
  }
  try {
   VendorContract accessCheck = contractService.getById(contractId);
   if (!canAccessContract(actor, accessCheck)) {
    redirectAttributes.addFlashAttribute("errorMessage", "You are not allowed to record payments for this contract.");
    return "redirect:/contracts";
   }
  } catch (AppException ex) {
   redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
   return "redirect:/contracts";
  }
 try {
 VendorContract contract = contractService.getById(contractId);
 paymentService.recordPartialPayment(contract, amount, LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), method, transactionRef, notes, installmentNumber);
 contractService.refreshTotals(contractId);
 redirectAttributes.addFlashAttribute("successMessage", "Partial installment #" + installmentNumber + " of LKR " + amount + " recorded!");
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 }
 return "redirect:/contracts/view/" + contractId;
 }

 // ─── Record Final Settlement Payment ─────────────────────────────────────────
 @PostMapping("/contracts/{id}/payments/final")
 public String recordFinalPayment(@PathVariable("id") String contractId,
 @RequestParam("amount") BigDecimal amount,
 @RequestParam(value = "paymentDate", required = false) String paymentDate,
 @RequestParam("method") String method,
 @RequestParam(value = "transactionRef", required = false) String transactionRef,
 @RequestParam(value = "notes", required = false) String notes,
 @RequestParam(value = "receiptNumber", required = false) String receiptNumber,
 HttpSession httpSession, RedirectAttributes redirectAttributes) {
  User actor = (User) httpSession.getAttribute("currentUser");
  if (!isAdmin(actor)) {
   redirectAttributes.addFlashAttribute("errorMessage", "Only an administrator can record payments.");
   return actor == null ? "redirect:/login" : "redirect:/contracts";
  }
  try {
   VendorContract accessCheck = contractService.getById(contractId);
   if (!canAccessContract(actor, accessCheck)) {
    redirectAttributes.addFlashAttribute("errorMessage", "You are not allowed to record payments for this contract.");
    return "redirect:/contracts";
   }
  } catch (AppException ex) {
   redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
   return "redirect:/contracts";
  }
 try {
 VendorContract contract = contractService.getById(contractId);
 paymentService.recordFinalPayment(contract, amount, LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), method, transactionRef, notes, receiptNumber);
 contractService.refreshTotals(contractId);
 redirectAttributes.addFlashAttribute("successMessage", "Final settlement payment recorded! Contract is now FULLY PAID.");
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 }
 return "redirect:/contracts/view/" + contractId;
 }

 // ─── Void / Reverse Payment ──────────────────────────────────────────────────
 @PostMapping("/payments/void")
 public String voidPayment(@RequestParam("paymentId") String paymentId,
 @RequestParam("contractId") String contractId,
 @RequestParam("reason") String reason,
 HttpSession httpSession, RedirectAttributes redirectAttributes) {
 User actor = (User) httpSession.getAttribute("currentUser");
 if (!isAdmin(actor)) {
  redirectAttributes.addFlashAttribute("errorMessage", "Only an administrator can reverse a payment.");
  return "redirect:/login";
 }
 try {
  boolean belongs = paymentService.getAllPayments(contractId).stream()
          .anyMatch(p -> p.getPaymentId().equals(paymentId));
  if (!belongs) throw new com.wedding.common.exceptions.BusinessRuleException("Payment does not belong to this contract.");
  VendorContract contract = contractService.getById(contractId);
  if (contract.getStatus() == com.wedding.member05_contract_payment.model.ContractStatus.COMPLETED) {
   throw new com.wedding.common.exceptions.BusinessRuleException("Payments on a completed contract cannot be reversed.");
  }
 paymentService.voidPayment(paymentId, reason);
 contractService.refreshTotals(contractId);
 redirectAttributes.addFlashAttribute("warningMessage", "Payment reversed and marked VOID.");
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 }
 return "redirect:/contracts/view/" + contractId;
 }

 // ─── Complete Contract ───────────────────────────────────────────────────────
 @PostMapping("/contracts/complete/{id}")
 public String completeContract(@PathVariable("id") String id, HttpSession httpSession,
                                RedirectAttributes redirectAttributes) {
 User actor = (User) httpSession.getAttribute("currentUser");
 if (!isAdmin(actor)) {
  redirectAttributes.addFlashAttribute("errorMessage", "Only an administrator can complete a contract.");
  return actor == null ? "redirect:/login" : "redirect:/contracts";
 }
 try {
 contractService.completeContract(id);
 redirectAttributes.addFlashAttribute("successMessage", "Contract marked COMPLETED.");
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 }
 return "redirect:/contracts/view/" + id;
 }

 private List<String> buildAuditEntries(VendorContract contract, List<Payment> payments) {
  java.util.ArrayList<String> entries = new java.util.ArrayList<>();
  entries.add("Contract " + contract.getContractId() + " created on " + contract.getCreatedAt()
          + " | Status: " + contract.getStatus());
  for (Payment payment : payments) {
   entries.add(payment.getRecordedAt() + " | " + payment.getPaymentType() + " | "
           + payment.getPaymentId() + " | LKR " + payment.getAmount()
           + (payment.isVoided() ? " | VOIDED" : " | VALID"));
  }
  return entries;
 }

 private boolean isAdmin(User user) {
  return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
 }

 private boolean canAccessContract(User user, VendorContract contract) {
  return isAdmin(user) || ("CUSTOMER".equalsIgnoreCase(user.getRole())
          && user.getUserId().equalsIgnoreCase(contract.getCustomerId()));
 }
}

