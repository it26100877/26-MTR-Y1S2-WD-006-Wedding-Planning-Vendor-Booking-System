<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />
<div class="row justify-content-center">
  <div class="col-lg-8">
    <a href="${pageContext.request.contextPath}/bookings/view/${booking.bookingId}" class="text-decoration-none text-secondary small">← Back to booking</a>
    <div class="card card-custom p-4 shadow-sm mt-3">
      <p class="eyebrow">BOOKING MANAGEMENT</p>
      <h2 class="fw-bold mb-1" style="color:#8d6a22;">Edit Booking Request</h2>
      <p class="text-secondary">Only pending requests can be edited. Vendor and event date remain unchanged.</p>
      <form action="${pageContext.request.contextPath}/bookings/edit/${booking.bookingId}" method="post">
        <div class="mb-3">
          <label class="form-label fw-semibold">Service Details</label>
          <textarea name="serviceDetails" class="form-control" rows="4" required maxlength="1500">${booking.serviceDetails}</textarea>
        </div>
        <div class="mb-3">
          <label class="form-label fw-semibold">Agreed Amount (LKR)</label>
          <input type="number" name="agreedAmount" class="form-control" min="1" step="0.01" value="${booking.agreedAmount}" required>
        </div>
        <div class="mb-3">
          <label class="form-label fw-semibold">Notes</label>
          <textarea name="notes" class="form-control" rows="3" maxlength="1000">${booking.notes}</textarea>
        </div>
        <div class="d-flex gap-2">
          <button type="submit" class="btn btn-purple">Save Changes</button>
          <a href="${pageContext.request.contextPath}/bookings/view/${booking.bookingId}" class="btn btn-outline-secondary">Cancel</a>
        </div>
      </form>
    </div>
  </div>
</div>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
