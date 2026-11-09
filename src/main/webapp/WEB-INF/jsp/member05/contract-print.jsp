<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title><c:out value="${pageTitle}"/></title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<style>
body{font-family:Georgia,serif;color:#352f27;background:#f7f3ed}.sheet{max-width:900px;margin:32px auto;background:#fff;padding:48px;border:1px solid #eadfce}
.brand{font-size:30px;color:#8d6a22;font-weight:bold}.muted{color:#776f64}.total{font-size:28px;font-weight:bold}
@media print{body{background:#fff}.sheet{margin:0 auto;border:0;padding:20px;max-width:none}.no-print{display:none!important}}
</style></head><body><main class="sheet">
<div class="d-flex justify-content-between align-items-start"><div><div class="brand">Éclat.</div><div class="muted">Wedding Planning &amp; Vendor Booking System</div></div><div class="text-end"><strong>VENDOR SERVICE CONTRACT</strong><br><span class="muted">Contract copy</span></div></div>
<hr><h2 class="mt-4">Contract <c:out value="${contract.contractId}"/></h2>
<p class="muted">Generated: <c:out value="${contract.contractDate}"/> &nbsp; | &nbsp; Status: <c:out value="${contract.status}"/></p>
<div class="row g-3 my-3"><div class="col-md-6"><strong>Booking ID</strong><br><c:out value="${contract.bookingId}"/></div><div class="col-md-6"><strong>Customer ID</strong><br><c:out value="${contract.customerId}"/></div><div class="col-md-6"><strong>Vendor ID</strong><br><c:out value="${contract.vendorId}"/></div><div class="col-md-6"><strong>Contract period</strong><br><c:out value="${contract.startDate}"/> – <c:out value="${contract.endDate}"/></div></div>
<div class="p-3 my-4 bg-light"><div class="muted">Agreed contract value</div><div class="total">LKR <c:out value="${contract.contractAmount}"/></div></div>
<h5>Terms &amp; Conditions</h5><p style="white-space:pre-wrap"><c:out value="${contract.terms}"/></p>
<h5 class="mt-4">Payment summary</h5><table class="table"><tr><th>Recorded payments</th><td>LKR <c:out value="${contract.totalPaid}"/></td></tr><tr><th>Outstanding balance</th><td>LKR <c:out value="${contract.outstandingBalance}"/></td></tr><tr><th>Payment status</th><td><c:out value="${contract.paymentStatus}"/></td></tr></table>
<h5 class="mt-4">Payment records</h5><div class="table-responsive"><table class="table table-sm"><thead><tr><th>Payment ID</th><th>Date</th><th>Type</th><th>Method</th><th>Amount</th><th>Status</th></tr></thead><tbody><c:forEach var="p" items="${payments}"><tr><td><c:out value="${p.paymentId}"/></td><td><c:out value="${p.paymentDate}"/></td><td><c:out value="${p.paymentType}"/></td><td><c:out value="${p.paymentMethod}"/></td><td>LKR <c:out value="${p.amount}"/></td><td><c:out value="${p.voided ? 'VOIDED' : 'VALID'}"/></td></tr></c:forEach></tbody></table></div>
<div class="d-flex justify-content-between mt-5"><div>________________________<br>Customer signature</div><div>________________________<br>Authorized vendor / admin</div></div>
<p class="muted small mt-5">This document is generated from the system's stored contract and payment records. For demo purposes it is not a digitally signed agreement.</p>
<div class="no-print text-center mt-4"><button class="btn btn-dark" onclick="window.print()">Print / Save as PDF</button></div>
</main></body></html>