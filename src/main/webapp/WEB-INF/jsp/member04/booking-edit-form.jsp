<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/eclat-bookings.css?v=20261111-booking-hero">
<div class="booking-studio">
    <section class="bk-hero mb-4"><div class="bk-eyebrow">BOOKING STUDIO / EDIT REQUEST</div><h1>Refine Your Booking Request</h1><p>Update your service requirements and agreed fee while the booking is pending.</p></section>
    <div class="row justify-content-center"><div class="col-lg-9 col-xl-8"><div class="bk-panel mb-4">
        <a class="bk-link d-inline-block mb-3" href="${pageContext.request.contextPath}/bookings/view/${booking.bookingId}"><i class="bi bi-arrow-left"></i> Back to details</a>
        <div class="bk-alert mb-4"><i class="bi bi-info-circle me-2"></i>Only pending requests can be changed. Vendor and event date stay as originally selected.</div>
        <form action="${pageContext.request.contextPath}/bookings/edit/${booking.bookingId}" method="post" class="row g-3">
            <div class="col-12"><label class="form-label" for="editDetails">SERVICE DETAILS *</label><textarea id="editDetails" name="serviceDetails" class="form-control" rows="5" required maxlength="1500"><c:out value="${booking.serviceDetails}"/></textarea></div>
            <div class="col-md-6"><label class="form-label" for="editPrice">AGREED FEE (LKR) *</label><div class="input-group"><span class="input-group-text">LKR</span><input id="editPrice" name="agreedAmount" type="number" step="0.01" min="0.01" required class="form-control" value="${booking.agreedAmount}"></div></div>
            <div class="col-12"><label class="form-label" for="editNotes">SPECIAL NOTES</label><textarea id="editNotes" class="form-control" name="notes" rows="3" maxlength="1000"><c:out value="${booking.notes}"/></textarea></div>
            <div class="col-12 d-flex flex-wrap gap-2 mt-4"><button class="bk-btn" type="submit"><i class="bi bi-check-circle"></i> Save Changes</button><a href="${pageContext.request.contextPath}/bookings/view/${booking.bookingId}" class="bk-btn-ghost">Discard</a></div>
        </form>
    </div></div></div>
</div>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
