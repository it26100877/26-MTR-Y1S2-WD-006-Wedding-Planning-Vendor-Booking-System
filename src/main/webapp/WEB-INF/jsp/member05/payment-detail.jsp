<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-3">
    <div>
        <a href="${pageContext.request.contextPath}/contracts" class="text-decoration-none text-secondary small">&larr; Back to Contracts</a>
        <a href="${pageContext.request.contextPath}/contracts/${contract.contractId}/print" target="_blank" rel="noopener" class="btn btn-sm btn-outline-secondary ms-2"><i class="bi bi-file-earmark-pdf me-1"></i>Contract PDF</a>
        <c:if test="${isAdmin && contract.status == 'ACTIVE'}">
            <a href="${pageContext.request.contextPath}/contracts/edit/${contract.contractId}" class="btn btn-sm btn-outline-secondary float-end ms-2">Edit Contract</a>
            <c:if test="${contract.totalPaid == 0}">
                <form action="${pageContext.request.contextPath}/contracts/cancel/${contract.contractId}" method="post" class="d-inline float-end" onsubmit="return confirm('Cancel this unpaid contract?');">
                    <button type="submit" class="btn btn-sm btn-outline-danger">Cancel Contract</button>
                </form>
            </c:if>
        </c:if>
        <h3 class="fw-bold mb-0" style="color: #8d6a22;">
            <i class="bi bi-wallet2"></i> Contract Financials: #${contract.contractId}
        </h3>
        <span class="text-muted small">Linked to Booking #${contract.bookingId} &bull; Vendor: ${contract.vendorId} &bull; Customer: ${contract.customerId}</span>
    </div>
    <div>
        <c:if test="${isAdmin && contract.status == 'ACTIVE' && contract.paymentStatus == 'FULLY_PAID'}">
            <form action="${pageContext.request.contextPath}/contracts/complete/${contract.contractId}" method="post" style="display:inline;">
                <button type="submit" class="btn btn-success fw-bold shadow-sm">
                    <i class="bi bi-check2-all me-1"></i> Mark Contract Completed
                </button>
            </form>
        </c:if>
    </div>
</div>

<!-- Financial Summary Cards -->
<div class="row g-3 mb-4">
    <div class="col-md-3">
        <div class="card card-custom p-3 border-start border-4 border-primary shadow-sm">
            <div class="text-secondary small fw-semibold">Contract Amount</div>
            <div class="fs-3 fw-bold text-dark">LKR ${contract.contractAmount}</div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="card card-custom p-3 border-start border-4 border-success shadow-sm">
            <div class="text-secondary small fw-semibold">Total Paid to Date</div>
            <div class="fs-3 fw-bold text-success">LKR ${contract.totalPaid}</div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="card card-custom p-3 border-start border-4 ${outstanding > 0 ? 'border-danger' : 'border-success'} shadow-sm">
            <div class="text-secondary small fw-semibold">Outstanding Balance</div>
            <div class="fs-3 fw-bold ${outstanding > 0 ? 'text-danger' : 'text-success'}">LKR ${outstanding}</div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="card card-custom p-3 border-start border-4 border-warning shadow-sm">
            <div class="text-secondary small fw-semibold">Payment Status</div>
            <div class="mt-1">
                <c:choose>
                    <c:when test="${contract.paymentStatus == 'FULLY_PAID'}"><span class="badge badge-fully-paid fs-6">Fully Paid</span></c:when>
                    <c:when test="${contract.paymentStatus == 'PARTIALLY_PAID'}"><span class="badge badge-partially-paid fs-6">Partially Paid</span></c:when>
                    <c:otherwise><span class="badge badge-unpaid fs-6">Unpaid</span></c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<!-- Payment Actions -->
<c:if test="${isAdmin && contract.status == 'ACTIVE' && outstanding > 0}">
    <div class="card card-custom p-3 mb-4 shadow-sm bg-light">
        <div class="d-flex align-items-center justify-content-between flex-wrap gap-2">
            <span class="fw-bold" style="color: #8d6a22;"><i class="bi bi-cash-coin me-1"></i> Record New Payment:</span>
            <div class="d-flex gap-2">
                <button class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#advanceModal">
                    <i class="bi bi-plus-circle"></i> + Advance Payment
                </button>
                <button class="btn btn-sm btn-outline-warning text-dark" data-bs-toggle="modal" data-bs-target="#partialModal">
                    <i class="bi bi-plus-circle"></i> + Partial Installment
                </button>
                <button class="btn btn-sm btn-success" data-bs-toggle="modal" data-bs-target="#finalModal">
                    <i class="bi bi-check-circle"></i> + Final Settlement (LKR ${outstanding})
                </button>
            </div>
        </div>
    </div>
</c:if>

<c:if test="${!isAdmin && contract.status == 'ACTIVE' && outstanding > 0}">
<div class="card card-custom p-3 mb-4 shadow-sm">
 <h6 class="fw-bold mb-3">Submit Payment Proof</h6>
 <div class="alert alert-info small">This submits a payment claim for Admin verification. It does not transfer money or update your paid balance until verified.</div>
 <form method="post" action="${pageContext.request.contextPath}/contracts/${contract.contractId}/payments/proof" class="row g-3">
  <div class="col-md-4"><label class="form-label">Amount (LKR)</label><input type="number" name="amount" min="0.01" max="${outstanding}" step="0.01" class="form-control" required></div>
  <div class="col-md-4"><label class="form-label">Payment Method</label><select name="method" class="form-select" required><option value="BANK_TRANSFER">Bank Transfer</option><option value="CARD">Card</option><option value="CASH">Cash</option></select></div>
  <div class="col-md-4"><label class="form-label">Transaction Reference</label><input name="transactionRef" minlength="4" maxlength="100" class="form-control" required></div>
  <div class="col-12"><label class="form-label">Notes (optional)</label><input name="notes" maxlength="250" class="form-control"></div>
  <div class="col-12"><button class="btn btn-outline-primary" type="submit">Submit for verification</button></div>
 </form>
</div>
</c:if>

<!-- Payment History Table -->
<div class="card card-custom p-4 shadow-sm mb-4">
    <h5 class="fw-bold mb-3" style="color: #8d6a22;"><i class="bi bi-receipt"></i> Payment Audit Trail &amp; History</h5>
    <div class="d-flex flex-wrap gap-2 mb-3">
        <a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/contracts/${contract.contractId}/export.csv"><i class="bi bi-filetype-csv me-1"></i>Export CSV</a>
        <a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/contracts/${contract.contractId}/print" target="_blank" rel="noopener"><i class="bi bi-printer me-1"></i>Print / Save PDF</a>
    </div>
    <div class="table-responsive mb-3">
      <table class="table table-sm table-bordered m05-audit-table"><thead><tr><th>System Audit Event</th></tr></thead><tbody>
      <c:forEach var="event" items="${auditEntries}"><tr><td><c:out value="${event}"/></td></tr></c:forEach>
      </tbody></table>
    </div>
    <div class="table-responsive">
        <table class="table table-hover align-middle m05-payment-table" id="paymentHistoryTable">
            <thead class="table-custom">
                <tr>
                    <th>Payment ID</th>
                    <th>Type</th>
                    <th>Amount</th>
                    <th>Payment Date</th>
                    <th>Method</th>
                    <th>Transaction Ref</th>
                    <th>Notes</th>
                    <th>Status</th>
                    <th class="text-end">Action</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="p" items="${payments}">
                    <tr class="${p.voided ? 'table-danger text-muted text-decoration-line-through' : ''}">
                        <td class="fw-semibold"><c:out value="${p.paymentId}"/><br><a class="small" href="${pageContext.request.contextPath}/payments/${p.paymentId}/receipt" target="_blank" rel="noopener">Receipt</a></td>
                        <td>
                            <c:choose>
                                <c:when test="${p.paymentType == 'ADVANCE'}"><span class="badge bg-primary">Advance</span></c:when>
                                <c:when test="${p.paymentType == 'PARTIAL'}"><span class="badge bg-warning text-dark">Partial</span></c:when>
                                <c:otherwise><span class="badge bg-success">Final</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td class="fw-bold">LKR ${p.amount}</td>
                        <td>${p.paymentDate}</td>
                        <td><span class="badge bg-light text-dark border">${p.paymentMethod}</span></td>
                        <td><code>${p.transactionRef}</code></td>
                        <td><small>${p.notes}</small></td>
                        <td>
                            <c:if test="${p.voided}"><span class="badge bg-danger">VOIDED</span></c:if>
                            <c:if test="${!p.voided}"><span class="badge bg-success">Valid</span></c:if>
                        </td>
                        <td class="text-end">
                            <c:if test="${!p.voided && isAdmin}">
                                <form action="${pageContext.request.contextPath}/payments/void" method="post" style="display:inline;" onsubmit="return confirm('Reverse and void this payment record?');">
                                    <input type="hidden" name="paymentId" value="${p.paymentId}">
                                    <input type="hidden" name="contractId" value="${contract.contractId}">
                                    <input type="text" name="reason" class="form-control form-control-sm d-inline-block me-1" style="width: 180px;" minlength="5" maxlength="250" placeholder="Reason for reversal" required aria-label="Reason for reversal">
                                    <button type="submit" class="btn btn-sm btn-outline-danger" title="Void Payment">
                                        <i class="bi bi-arrow-counterclockwise"></i> Reverse
                                    </button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty payments}">
                    <tr><td colspan="9" class="text-center text-muted py-4">No payments recorded for this contract yet.</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<!-- Advance Payment Modal -->
<div class="modal fade" id="advanceModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/contracts/${contract.contractId}/payments/advance" method="post">
                <div class="modal-header bg-purple text-white">
                    <h5 class="modal-title">Record Advance Deposit</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="alert alert-warning py-2 small">Demo payment record only. This form does not transfer money or verify a bank/card transaction. CARD and BANK TRANSFER require a reference for audit purposes.</div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Amount (Max: LKR ${outstanding}) *</label>
                        <input type="number" step="0.01" max="${outstanding}" name="amount" class="form-control" placeholder="500.00" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Advance Percentage (%)</label>
                        <input type="number" step="0.1" name="advancePct" class="form-control" placeholder="25.0" value="25.0">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Payment Date</label>
                        <input type="text" class="form-control bg-light" value="${serverPaymentDate}" readonly>
                        <small class="text-secondary">Automatically assigned by the system when this payment is saved.</small>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Payment Method *</label>
                        <select name="method" class="form-select" required>
                            <option value="BANK_TRANSFER">Bank Transfer</option>
                            <option value="CARD">Credit / Debit Card</option>
                            <option value="CASH">Cash</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Transaction Reference / Cheque #</label>
                        <input type="text" name="transactionRef" class="form-control" placeholder="Required for Card / Bank Transfer">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Notes</label>
                        <input type="text" name="notes" class="form-control" placeholder="Advance deposit for booking security">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-purple fw-bold">Save Advance Payment</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Partial Payment Modal -->
<div class="modal fade" id="partialModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/contracts/${contract.contractId}/payments/partial" method="post">
                <div class="modal-header bg-purple text-white">
                    <h5 class="modal-title">Record Partial Installment</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="alert alert-warning py-2 small">Demo payment record only. This form does not transfer money or verify a bank/card transaction. CARD and BANK TRANSFER require a reference for audit purposes.</div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Amount (Max: LKR ${outstanding}) *</label>
                        <input type="number" step="0.01" max="${outstanding}" name="amount" class="form-control" placeholder="300.00" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Installment Number *</label>
                        <input type="number" name="installmentNumber" class="form-control" value="2" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Payment Date</label>
                        <input type="text" class="form-control bg-light" value="${serverPaymentDate}" readonly>
                        <small class="text-secondary">Automatically assigned by the system when this payment is saved.</small>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Payment Method *</label>
                        <select name="method" class="form-select" required>
                            <option value="BANK_TRANSFER">Bank Transfer</option>
                            <option value="CARD">Credit / Debit Card</option>
                            <option value="CASH">Cash</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Transaction Reference</label>
                        <input type="text" name="transactionRef" class="form-control" placeholder="Required for Card / Bank Transfer">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Notes</label>
                        <input type="text" name="notes" class="form-control" placeholder="Milestone payment 2">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-purple fw-bold">Save Installment</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Final Payment Modal -->
<div class="modal fade" id="finalModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/contracts/${contract.contractId}/payments/final" method="post">
                <div class="modal-header bg-success text-white">
                    <h5 class="modal-title">Record Final Settlement Payment</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="alert alert-warning py-2 small">Demo payment record only. No actual card charge or bank transfer is performed. CARD and BANK TRANSFER require a reference.</div>
                    <div class="alert alert-info py-2 small">
                        Final payment must exactly settle the full remaining outstanding balance of <strong>LKR ${outstanding}</strong>.
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Exact Amount to Settle (LKR) *</label>
                        <input type="number" step="0.01" name="amount" class="form-control fw-bold" value="${outstanding}" readonly required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Final Receipt Number</label>
                        <input type="text" name="receiptNumber" class="form-control" placeholder="REC-FINAL-001">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Payment Date</label>
                        <input type="text" class="form-control bg-light" value="${serverPaymentDate}" readonly>
                        <small class="text-secondary">Automatically assigned by the system when this payment is saved.</small>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Payment Method *</label>
                        <select name="method" class="form-select" required>
                            <option value="BANK_TRANSFER">Bank Transfer</option>
                            <option value="CARD">Credit / Debit Card</option>
                            <option value="CASH">Cash</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Transaction Reference</label>
                        <input type="text" name="transactionRef" class="form-control" placeholder="Required for Card / Bank Transfer">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Notes</label>
                        <input type="text" name="notes" class="form-control" value="Full contract balance settled">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-success fw-bold">Record Final Settlement</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />

