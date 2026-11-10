<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>Booking Receipt <c:out value="${booking.bookingId}"/></title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<style>
:root{--ink:#352f27;--gold:#8d6a22;--paper:#f7f3ed;--line:#eadfce}
body{font-family:Georgia,serif;color:var(--ink);background:var(--paper)}
.sheet{max-width:820px;margin:36px auto;background:#fff;padding:48px;border:1px solid var(--line)}
.brand{font-size:32px;font-weight:bold;color:var(--gold);letter-spacing:-1px}.muted{color:#776f64}
.total{font-size:30px;font-weight:bold;color:var(--gold)}.rule{border-top:1px solid var(--line)}
@media print{body{background:#fff}.sheet{margin:0 auto;padding:22px;border:0;max-width:none}.no-print{display:none!important}}
</style></head><body>
<section class="sheet">
 <header class="d-flex justify-content-between align-items-start gap-3">
  <div><div class="brand">Éclat.</div><div class="muted">Wedding Planning &amp; Vendor Booking System</div></div>
  <div class="text-end"><strong>BOOKING RECEIPT</strong><div class="muted">Reference <c:out value="${booking.bookingId}"/></div></div>
 </header>
 <hr class="rule my-4">
 <h2>Booking confirmation</h2>
 <p class="muted">This document confirms the booking details currently stored in the system.</p>
 <div class="bg-light p-3 my-4"><div class="muted">Agreed service amount</div><div class="total">LKR <c:out value="${booking.agreedAmount}"/></div><div class="mt-1">Status: <strong><c:out value="${booking.status.displayName}"/></strong></div></div>
 <div class="row g-4">
  <div class="col-md-6"><h5>Customer</h5><strong><c:out value="${customerName}"/></strong></div>
  <div class="col-md-6"><h5>Vendor</h5><strong><c:out value="${vendor.vendorName}"/></strong><div class="muted"><c:out value="${vendor.category.displayName}"/></div></div>
  <div class="col-md-6"><h5>Wedding plan</h5><c:out value="${plan != null ? plan.planName : booking.planId}"/></div>
  <div class="col-md-6"><h5>Event date</h5><c:out value="${booking.bookingDate}"/></div>
  <div class="col-md-6"><h5>Requested on</h5><c:out value="${booking.requestedAt}"/></div>
  <div class="col-md-6"><h5>Confirmed on</h5><c:out value="${booking.confirmedAt}"/></div>
 </div>
 <hr class="rule my-4"><h5>Service requirements</h5><p style="white-space:pre-wrap"><c:out value="${booking.serviceDetails}"/></p>
 <c:if test="${not empty booking.notes}"><h5>Additional notes</h5><p style="white-space:pre-wrap"><c:out value="${booking.notes}"/></p></c:if>
 <p class="muted small mt-5">Éclat. — This is a system-generated booking summary, not a tax invoice or proof of bank settlement. Contract and payment obligations are governed by the linked contract, where applicable.</p>
 <div class="no-print text-center mt-4"><button class="btn btn-dark" onclick="window.print()">Print / Save as PDF</button></div>
</section></body></html>