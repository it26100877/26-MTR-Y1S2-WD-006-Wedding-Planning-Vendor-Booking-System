<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<div class="row justify-content-center">
    <div class="col-md-9">
        <a href="${pageContext.request.contextPath}/bookings" class="text-decoration-none text-secondary small d-inline-block mb-3">&larr; Back to Bookings</a>

        <div class="card card-custom p-4 shadow mb-4">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <div>
                    <span class="badge bg-purple mb-1">Booking #${booking.bookingId}</span>
                    <h4 class="fw-bold mb-0" style="color: #8d6a22;">
                        ${vendor != null ? vendor.vendorName : booking.vendorId}
                    </h4>
                    <small class="text-muted">Associated Plan: ${plan != null ? plan.planName : booking.planId}</small>
                </div>
                <div>
                    <c:choose>
                        <c:when test="${booking.status == 'PENDING'}"><span class="badge badge-pending fs-6">Pending Approval</span></c:when>
                        <c:when test="${booking.status == 'CONFIRMED'}"><span class="badge badge-confirmed fs-6">Confirmed</span></c:when>
                        <c:when test="${booking.status == 'COMPLETED'}"><span class="badge badge-completed fs-6">Completed</span></c:when>
                        <c:when test="${booking.status == 'REJECTED'}"><span class="badge badge-rejected fs-6">Rejected</span></c:when>
                        <c:otherwise><span class="badge badge-cancelled fs-6">${booking.status}</span></c:otherwise>
                    </c:choose>
                </div>
            </div>

            <hr>

            <div class="row g-4 my-2">
                <div class="col-md-4">
                    <small class="text-secondary fw-semibold">Event Date</small>
                    <div class="fw-bold fs-5 text-dark"><i class="bi bi-calendar-event text-primary"></i> ${booking.bookingDate}</div>
                </div>
                <div class="col-md-4">
                    <small class="text-secondary fw-semibold">Agreed Service Fee</small>
                    <div class="fw-bold fs-5 text-success">LKR ${booking.agreedAmount}</div>
                </div>
                <div class="col-md-4">
                    <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                        <small class="text-secondary fw-semibold">Customer ID</small>
                        <div class="fw-bold text-dark">${booking.customerId}</div>
                    </c:if>
                    <small class="text-muted">Requested: ${booking.requestedAt}</small>
                </div>
            </div>

            <div class="my-3">
                <h6 class="fw-bold text-secondary">Service Details</h6>
                <div class="p-3 bg-light rounded-3">${booking.serviceDetails}</div>
            </div>

            <c:if test="${not empty booking.notes}">
                <div class="mb-3">
                    <h6 class="fw-bold text-secondary">Notes</h6>
                    <div class="p-3 bg-light rounded-3 text-muted">${booking.notes}</div>
                </div>
            </c:if>

            <div class="d-flex justify-content-between align-items-center pt-3 border-top mt-4">
                <div>
                    <!-- Customer Cancel action -->
                    <c:if test="${sessionScope.currentUser.role == 'CUSTOMER' && booking.status == 'PENDING'}">
                        <a href="${pageContext.request.contextPath}/bookings/edit/${booking.bookingId}" class="btn btn-outline-secondary btn-sm me-1">Edit Request</a>
                        <form action="${pageContext.request.contextPath}/bookings/cancel/${booking.bookingId}" method="post" style="display:inline;" onsubmit="return confirm('Cancel this booking request?');">
                            <button type="submit" class="btn btn-outline-danger btn-sm">Cancel Booking Request</button>
                        </form>
                    </c:if>

                    <!-- Write Review shortcut if Completed -->
                    <c:if test="${sessionScope.currentUser.role == 'CUSTOMER' && booking.status == 'COMPLETED'}">
                        <a href="${pageContext.request.contextPath}/reviews/new?bookingId=${booking.bookingId}&vendorId=${booking.vendorId}" class="btn btn-warning btn-sm text-dark fw-bold">
                            <i class="bi bi-star-fill"></i> Write Verified Review
                        </a>
                    </c:if>
                </div>

                <div class="d-flex gap-2">
                    <!-- Admin / Vendor actions -->
                    <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                        <c:if test="${booking.status == 'PENDING'}">
                            <form action="${pageContext.request.contextPath}/bookings/confirm/${booking.bookingId}" method="post" style="display:inline;">
                                <button type="submit" class="btn btn-success fw-bold">Confirm Booking</button>
                            </form>
                            <form action="${pageContext.request.contextPath}/bookings/reject/${booking.bookingId}" method="post" style="display:inline;">
                                <button type="submit" class="btn btn-danger fw-bold">Reject</button>
                            </form>
                        </c:if>

                        <c:if test="${booking.status == 'CONFIRMED'}">
                            <a href="${pageContext.request.contextPath}/contracts/new?bookingId=${booking.bookingId}" class="btn btn-purple fw-bold">
                                <i class="bi bi-file-earmark-plus"></i> Generate Contract
                            </a>
                            <form action="${pageContext.request.contextPath}/bookings/complete/${booking.bookingId}" method="post" style="display:inline;">
                                <button type="submit" class="btn btn-outline-success">Mark Completed</button>
                            </form>
                        </c:if>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />

