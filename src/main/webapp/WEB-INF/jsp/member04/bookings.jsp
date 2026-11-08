<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h3 class="fw-bold" style="color: #8d6a22;"><i class="bi bi-bookmark-check"></i> Vendor Bookings</h3>
        <p class="text-secondary small mb-0">Track requested services, booking confirmations, schedules, and agreed fees</p>
    </div>
    <c:if test="${sessionScope.currentUser.role == 'CUSTOMER'}">
        <a href="${pageContext.request.contextPath}/bookings/new" class="btn btn-purple shadow-sm">
            <i class="bi bi-plus-circle me-1"></i> Request New Booking
        </a>
    </c:if>
</div>

<!-- Filter Bar -->
<div class="card card-custom p-3 mb-4 shadow-sm">
    <form action="${pageContext.request.contextPath}/bookings" method="get" class="row g-2 align-items-center">
        <div class="col-md-4">
            <select name="status" class="form-select">
                <option value="">All Statuses</option>
                <c:forEach var="st" items="${statuses}">
                    <option value="${st.name()}" ${st.name() == selectedStatus ? 'selected' : ''}>${st.displayName}</option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-2">
            <button type="submit" class="btn btn-purple w-100">Filter</button>
        </div>
    </form>
</div>

<!-- Bookings Table -->
<div class="card card-custom p-4 shadow-sm">
    <div class="table-responsive">
        <table class="table table-hover align-middle">
            <thead class="table-custom">
                <tr>
                    <th>Booking ID</th>
                    <c:if test="${sessionScope.currentUser.role == 'ADMIN'}"><th>Customer</th></c:if>
                    <th>Vendor</th>
                    <th>Event Date</th>
                    <th>Agreed Amount</th>
                    <th>Requested At</th>
                    <th>Status</th>
                    <th class="text-end">Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="b" items="${bookings}">
                    <tr>
                        <td class="fw-semibold">${b.bookingId}</td>
                        <c:if test="${sessionScope.currentUser.role == 'ADMIN'}"><td>${b.customerId}</td></c:if>
                        <td>${b.vendorId}</td>
                        <td><i class="bi bi-calendar-event me-1 text-muted"></i> ${b.bookingDate}</td>
                        <td class="fw-bold text-success">LKR ${b.agreedAmount}</td>
                        <td class="text-muted small">${b.requestedAt}</td>
                        <td>
                            <c:choose>
                                <c:when test="${b.status == 'PENDING'}"><span class="badge badge-pending">Pending</span></c:when>
                                <c:when test="${b.status == 'CONFIRMED'}"><span class="badge badge-confirmed">Confirmed</span></c:when>
                                <c:when test="${b.status == 'COMPLETED'}"><span class="badge badge-completed">Completed</span></c:when>
                                <c:when test="${b.status == 'REJECTED'}"><span class="badge badge-rejected">Rejected</span></c:when>
                                <c:otherwise><span class="badge badge-cancelled">${b.status}</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td class="text-end">
                            <a href="${pageContext.request.contextPath}/bookings/view/${b.bookingId}" class="btn btn-sm btn-outline-primary">
                                Details &amp; Actions
                            </a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty bookings}">
                    <tr><td colspan="8" class="text-center text-muted py-5">No bookings recorded.</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />

