<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title><c:out value="${pageTitle}"/> | Éclat</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link href="<c:url value='/css/eclat-typography.css'/>" rel="stylesheet">
<link href="<c:url value='/css/eclat-royal.css'/>" rel="stylesheet">
<link href="<c:url value='/css/eclat-documents.css'/>" rel="stylesheet">
</head>
<body class="eclat-document-page">
<section class="eclat-document-sheet">
  <header class="eclat-document-masthead">
    <div>
      <div class="eclat-document-brand">Éclat.</div>
      <div class="eclat-document-brand-subtitle">Wedding Planning &amp; Vendor Booking System</div>
    </div>
    <div>
      <div class="eclat-document-kicker">VENDOR SERVICE CONTRACT</div>
      <div class="eclat-document-muted small text-end">Official contract copy</div>
    </div>
  </header>

  <h1 class="eclat-document-title">Contract <c:out value="${contract.contractId}"/></h1>
  <p class="eclat-document-meta">Generated: <c:out value="${contract.contractDate}"/> &nbsp; | &nbsp; Status: <c:out value="${contract.status}"/></p>

  <h2 class="eclat-document-section-title">Contract parties &amp; event</h2>
  <div class="row g-3 my-3">
    <div class="col-md-6"><div class="eclat-document-panel h-100"><strong>Booking ID</strong><br><c:out value="${contract.bookingId}"/></div></div>
    <div class="col-md-6"><div class="eclat-document-panel h-100"><strong>Customer</strong><br><c:out value="${customer.name}"/><br><span class="eclat-document-muted"><c:out value="${customer.email}"/></span><br><span class="eclat-document-muted"><c:out value="${customer.phone}"/></span></div></div>
    <div class="col-md-6"><div class="eclat-document-panel h-100"><strong>Vendor</strong><br><c:out value="${vendor.vendorName}"/><br><span class="eclat-document-muted"><c:out value="${vendor.email}"/> · <c:out value="${vendor.phone}"/></span></div></div>
    <div class="col-md-6"><div class="eclat-document-panel h-100"><strong>Contract period</strong><br><c:out value="${contract.startDate}"/> – <c:out value="${contract.endDate}"/></div></div>
    <div class="col-md-6"><div class="eclat-document-panel h-100"><strong>Event date</strong><br><c:out value="${booking.bookingDate}"/></div></div>
    <div class="col-md-6"><div class="eclat-document-panel h-100"><strong>Wedding plan</strong><br><c:out value="${plan.planName}"/></div></div>
  </div>

  <div class="eclat-document-panel my-4">
    <div class="eclat-document-muted small text-uppercase fw-semibold">Agreed contract value</div>
    <div class="display-6 fw-bold">LKR <c:out value="${contract.contractAmount}"/></div>
  </div>

  <h2 class="eclat-document-section-title">Terms &amp; conditions</h2>
  <p style="white-space:pre-wrap"><c:out value="${contract.terms}"/></p>

  <h2 class="eclat-document-section-title">Payment summary</h2>
  <table class="table eclat-document-table">
    <tbody>
      <tr><th scope="row">Recorded payments</th><td>LKR <c:out value="${contract.totalPaid}"/></td></tr>
      <tr><th scope="row">Outstanding balance</th><td>LKR <c:out value="${contract.outstandingBalance}"/></td></tr>
      <tr><th scope="row">Payment status</th><td><c:out value="${contract.paymentStatus}"/></td></tr>
    </tbody>
  </table>

  <h2 class="eclat-document-section-title">Payment records</h2>
  <div class="table-responsive">
    <table class="table table-sm eclat-document-table">
      <thead><tr><th>Payment ID</th><th>Date</th><th>Type</th><th>Method</th><th>Amount</th><th>Status</th></tr></thead>
      <tbody>
        <c:forEach var="p" items="${payments}">
          <tr>
            <td><c:out value="${p.paymentId}"/></td>
            <td><c:out value="${p.paymentDate}"/></td>
            <td><c:out value="${p.paymentType}"/></td>
            <td><c:out value="${p.paymentMethod}"/></td>
            <td>LKR <c:out value="${p.amount}"/></td>
            <td><c:out value="${p.voided ? 'VOIDED' : 'VALID'}"/></td>
          </tr>
        </c:forEach>
      </tbody>
    </table>
  </div>

  <div class="eclat-document-signatures">
    <div class="eclat-document-signature">Customer signature</div>
    <div class="eclat-document-signature">Authorized vendor / admin</div>
  </div>

  <footer class="eclat-document-footer">
    This document is generated from the system's stored contract and payment records. For demo purposes it is not a digitally signed agreement.
    <div class="mt-2">Éclat. &nbsp; | &nbsp; Wedding Planning &amp; Vendor Booking System</div>
  </footer>

  <div class="no-print eclat-document-print-action">
    <button class="btn btn-primary" onclick="window.print()">Print / Save as PDF</button>
  </div>
</section>
</body>
</html>
