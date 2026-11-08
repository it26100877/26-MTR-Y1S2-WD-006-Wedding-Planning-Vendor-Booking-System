<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<div class="row justify-content-center">
    <div class="col-md-8">
        <div class="card card-custom p-4 shadow">
            <h4 class="fw-bold mb-3" style="color: #8d6a22;">
                <i class="bi bi-file-earmark-plus"></i> Generate Vendor Service Contract
            </h4>
            <p class="text-secondary small mb-4">Creating a legally binding contract from confirmed Booking #${booking.bookingId}</p>

            <form action="${pageContext.request.contextPath}/contracts/new" method="post">
                <input type="hidden" name="bookingId" value="${booking.bookingId}">
                <input type="hidden" name="customerId" value="${booking.customerId}">
                <input type="hidden" name="vendorId" value="${booking.vendorId}">

                <div class="row g-3">
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Contract Amount (USD) *</label>
                        <div class="input-group">
                            <span class="input-group-text">$</span>
                            <input type="number" step="0.01" name="contractAmount" class="form-control" value="${booking.agreedAmount}" required>
                        </div>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Service Date (From Booking)</label>
                        <input type="text" class="form-control bg-light" value="${booking.bookingDate}" readonly>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Contract Start Date *</label>
                        <input type="date" name="startDate" class="form-control" value="${booking.bookingDate}" required>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Contract End Date *</label>
                        <input type="date" name="endDate" class="form-control" value="${booking.bookingDate}" required>
                    </div>

                    <div class="col-12">
                        <label class="form-label fw-semibold">Contract Terms &amp; Conditions</label>
                        <textarea name="terms" class="form-control" rows="4">1. Vendor agrees to provide the specified services on the scheduled date.
2. An advance deposit is required to secure the reservation.
3. Balance settlement must be paid in full on or prior to the final event date.
4. Cancellations made within 14 days of the event forfeit the initial deposit.</textarea>
                    </div>

                    <div class="col-12 mt-4 d-flex justify-content-between">
                        <a href="${pageContext.request.contextPath}/bookings/view/${booking.bookingId}" class="btn btn-outline-secondary">Cancel</a>
                        <button type="submit" class="btn btn-purple px-4 fw-bold">Generate &amp; Activate Contract</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />

