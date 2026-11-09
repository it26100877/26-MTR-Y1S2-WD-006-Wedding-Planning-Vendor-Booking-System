package com.wedding.member05_contract_payment.repository;

import com.wedding.common.repository.AbstractFileRepository;
import com.wedding.member05_contract_payment.model.ContractStatus;
import com.wedding.member05_contract_payment.model.VendorContract;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * File-based repository for {@link VendorContract} entities.
 * Stored in contracts.csv with pipe delimiter.
 *
 * @author Member 05 – Vendor Contract & Payment Management
 */
public class ContractRepository extends AbstractFileRepository<VendorContract> {

 private static final String FILE_NAME = "contracts.csv";
 private static final String DELIM = "|";

 public ContractRepository() {
 super(FILE_NAME);
 }

 @Override
 protected String getId(VendorContract c) {
 return c.getContractId();
 }

 @Override
 protected String serialize(VendorContract c) {
 return String.join(DELIM,
 safe(c.getContractId()),
 safe(c.getBookingId()),
 safe(c.getCustomerId()),
 safe(c.getVendorId()),
 safe(c.getContractDate()),
 safe(c.getStartDate()),
 safe(c.getEndDate()),
 c.getContractAmount() != null ? c.getContractAmount().toPlainString() : "0.00",
 safe(c.getTerms()),
 c.getStatus() != null ? c.getStatus().name() : ContractStatus.DRAFT.name(),
 safe(c.getCreatedAt()),
 c.getTotalPaid() != null ? c.getTotalPaid().toPlainString() : "0.00",
 safe(c.getPaymentStatus())
 );
 }

 @Override
 protected VendorContract deserialize(String line) {
 if (line == null || line.trim().isEmpty()) return null;
 String[] p = line.split(Pattern.quote(DELIM), -1);
 if (p.length < 13) return null;

 BigDecimal amount;
 try { amount = new BigDecimal(p[7]); } catch (Exception e) { amount = BigDecimal.ZERO; }

 BigDecimal totalPaid;
 try { totalPaid = new BigDecimal(p[11]); } catch (Exception e) { totalPaid = BigDecimal.ZERO; }

 return new VendorContract(
 p[0], p[1], p[2], p[3], p[4], p[5], p[6],
 amount, p[8], ContractStatus.fromString(p[9]), p[10],
 totalPaid, p[12]
 );
 }

 private String safe(String s) {
 return s == null ? "" : s.replace("|", "/").replace("\r", " ").replace("\n", " ");
 }

 public Optional<VendorContract> findByBookingId(String bookingId) {
 return findAll().stream()
 .filter(c -> bookingId != null && bookingId.equalsIgnoreCase(c.getBookingId()))
 .findFirst();
 }

 public List<VendorContract> findByCustomerId(String cid) {
 return findAll().stream()
 .filter(c -> cid != null && cid.equalsIgnoreCase(c.getCustomerId()))
 .collect(Collectors.toList());
 }

 public List<VendorContract> findByVendorId(String vid) {
 return findAll().stream()
 .filter(c -> vid != null && vid.equalsIgnoreCase(c.getVendorId()))
 .collect(Collectors.toList());
 }

 public List<VendorContract> findByStatus(ContractStatus status) {
 return findAll().stream()
 .filter(c -> c.getStatus() == status)
 .collect(Collectors.toList());
 }
}

