<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />
<div class="m05-page-head mb-4"><div class="m05-eyebrow">FINANCIAL CONTROL</div><h2 class="m05-page-title"><i class="bi bi-shield-check me-2"></i>Payment Proof Verification</h2><p class="m05-page-subtitle">Review customer-submitted payment claims. Approving a proof records a payment; rejecting it leaves contract balances unchanged.</p></div>
<section class="m05-panel"><div class="table-responsive"><table class="table align-middle m05-payment-table"><thead><tr><th>Proof ID</th><th>Contract</th><th>Customer</th><th>Amount</th><th>Method</th><th>Reference</th><th>Submitted</th><th>Status</th><th>Customer Note</th><th>Admin Review Note</th><th>Decision</th></tr></thead><tbody>
<c:forEach var="proof" items="${paymentProofs}">
<tr><td><c:out value="${proof[0]}"/></td><td><a href="${pageContext.request.contextPath}/contracts/view/${proof[1]}"><c:out value="${proof[1]}"/></a></td><td><c:out value="${proof[2]}"/></td><td>LKR <c:out value="${proof[3]}"/></td><td><c:out value="${proof[4]}"/></td><td><c:out value="${proof[5]}"/></td><td><c:out value="${proof[6]}"/></td><td><span class="m05-status ${proof[7] == 'PENDING' ? 'm05-partial' : (proof[7] == 'APPROVED' ? 'm05-paid' : 'm05-cancelled')}"><c:out value="${proof[7]}"/></span></td><td><c:out value="${proof[8]}"/></td><td><c:out value="${proof[9]}"/></td><td>
<c:if test="${proof[7] == 'PENDING'}"><form method="post" action="${pageContext.request.contextPath}/admin/payment-proofs/decision" class="d-flex flex-wrap gap-1"><input type="hidden" name="proofId" value="${proof[0]}"><button class="btn btn-sm btn-success" name="decision" value="APPROVE" onclick="return confirm('Approve this proof and record it as a payment?')">Approve</button><button class="btn btn-sm btn-outline-danger" name="decision" value="REJECT" onclick="return confirm('Reject this payment proof?')">Reject</button><input type="text" name="reason" class="form-control form-control-sm" maxlength="200" placeholder="Rejection reason (optional)" aria-label="Rejection reason"></form></c:if>
<c:if test="${proof[7] != 'PENDING'}"><span class="small text-secondary">Processed</span></c:if>
</td></tr>
</c:forEach>
<c:if test="${empty paymentProofs}"><tr><td colspan="11" class="text-center py-5 text-secondary">No payment proofs have been submitted.</td></tr></c:if>
</tbody></table></div></section>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
