<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />
<div class="booking-studio">
    <section class="bk-hero mb-4 d-flex align-items-center justify-content-between gap-3 flex-wrap"><div><div class="bk-eyebrow">BOOKING STUDIO / REFERENCE <c:out value="${booking.bookingId}"/></div><h1><c:out value="${vendor != null ? vendor.vendorName : booking.vendorId}"/></h1><p>All the essential details for your wedding vendor reservation.</p></div><a class="bk-btn-ghost position-relative" href="${pageContext.request.contextPath}/bookings"><i class="bi bi-arrow-left"></i> All Bookings</a></section>
    <div class="row g-4">
        <div class="col-lg-8"><div class="bk-panel mb-4">
            <div class="d-flex flex-wrap align-items-center justify-content-between gap-2 mb-4"><div><div class="bk-eyebrow">YOUR BOOKING SUMMARY</div><h2 class="bk-heading">Reservation Details</h2></div><span class="bk-pill bk-pill-${booking.status.name().toLowerCase()}">${booking.status.displayName}</span></div>
            <div class="row g-3 mb-4">
                <div class="col-md-6"><div class="bk-detail-item"><div class="bk-detail-label">Booking Reference</div><div class="bk-detail-value"><c:out value="${booking.bookingId}"/></div></div></div>
                <div class="col-md-6"><div class="bk-detail-item"><div class="bk-detail-label">Event Date</div><div class="bk-detail-value"><i class="bi bi-calendar-event me-1"></i><c:out value="${booking.bookingDate}"/></div></div></div>
                <div class="col-md-6"><div class="bk-detail-item"><div class="bk-detail-label">Customer</div><div class="bk-detail-value"><c:out value="${customerName}"/></div></div></div>
                <div class="col-md-6"><div class="bk-detail-item"><div class="bk-detail-label">Service Fee</div><div class="bk-detail-value">LKR ${booking.agreedAmount}</div></div></div>
                <div class="col-md-6"><div class="bk-detail-item"><div class="bk-detail-label">Wedding Plan</div><div class="bk-detail-value"><c:out value="${plan != null ? plan.planName : booking.planId}"/></div></div></div>
                <div class="col-md-6"><div class="bk-detail-item"><div class="bk-detail-label">Requested On</div><div class="bk-detail-value"><c:out value="${booking.requestedAt}"/></div></div></div>
            </div>
            <h2 class="bk-heading mb-3">Service Requirements</h2><div class="bk-detail-item mb-3" style="white-space:pre-wrap"><c:out value="${booking.serviceDetails}"/></div>
            <c:if test="${not empty booking.notes}"><h2 class="bk-heading mb-3">Additional Notes</h2><div class="bk-detail-item mb-4" style="white-space:pre-wrap"><c:out value="${booking.notes}"/></div></c:if>
            <c:if test="${not empty booking.confirmedAt}"><p class="small text-secondary">Last approval update: <c:out value="${booking.confirmedAt}"/></p></c:if>
            <c:if test="${not empty booking.cancelledAt}"><p class="small text-secondary">Cancelled at: <c:out value="${booking.cancelledAt}"/></p></c:if>
        </div></div>
        <div class="col-lg-4"><div class="bk-panel mb-4"><div class="bk-eyebrow">NEXT STEPS</div><h2 class="bk-heading mb-2">Manage Booking</h2><p class="bk-subtitle">Available actions depend on the current booking status.</p><div class="d-grid gap-2 mt-4">
            <c:if test="${booking.status == 'CONFIRMED' || booking.status == 'COMPLETED'}"><a class="bk-btn" href="${pageContext.request.contextPath}/bookings/receipt/${booking.bookingId}"><i class="bi bi-file-earmark-pdf"></i> Print / Save Booking Receipt as PDF</a></c:if>
            <c:if test="${sessionScope.currentUser.role == 'CUSTOMER' && booking.status == 'PENDING'}"><a class="bk-btn-ghost" href="${pageContext.request.contextPath}/bookings/edit/${booking.bookingId}"><i class="bi bi-pencil"></i> Edit Request</a><form method="post" action="${pageContext.request.contextPath}/bookings/cancel/${booking.bookingId}" onsubmit="return confirm('Cancel this booking request?');"><button type="submit" class="btn btn-outline-danger w-100">Cancel Request</button></form></c:if>
            <c:if test="${sessionScope.currentUser.role == 'CUSTOMER' && booking.status == 'COMPLETED'}"><a class="bk-btn-ghost" href="${pageContext.request.contextPath}/reviews/new?bookingId=${booking.bookingId}&amp;vendorId=${booking.vendorId}"><i class="bi bi-star-fill"></i> Write Verified Review</a></c:if>
            <c:if test="${sessionScope.currentUser.role == 'ADMIN' && booking.status == 'PENDING'}"><form method="post" action="${pageContext.request.contextPath}/bookings/confirm/${booking.bookingId}"><button type="submit" class="bk-btn w-100"><i class="bi bi-check2-circle"></i> Confirm Request</button></form><form method="post" action="${pageContext.request.contextPath}/bookings/reject/${booking.bookingId}" onsubmit="return confirm('Reject this booking request?');"><button type="submit" class="btn btn-outline-danger w-100">Reject Request</button></form></c:if>
            <c:if test="${sessionScope.currentUser.role == 'ADMIN' && booking.status == 'CONFIRMED'}"><a class="bk-btn-ghost" href="${pageContext.request.contextPath}/contracts/new?bookingId=${booking.bookingId}"><i class="bi bi-file-earmark-plus"></i> Generate Contract</a><form method="post" action="${pageContext.request.contextPath}/bookings/complete/${booking.bookingId}"><button type="submit" class="btn btn-outline-success w-100">Mark Completed</button></form></c:if>
            <c:if test="${booking.status == 'REJECTED' || booking.status == 'CANCELLED'}"><div class="bk-alert">This request is closed. You can submit a new one for an available date.</div></c:if>
            <a href="${pageContext.request.contextPath}/bookings/calendar?vendorId=${booking.vendorId}" class="bk-btn-ghost"><i class="bi bi-calendar3"></i> View Vendor Calendar</a>
            </div></div>
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
