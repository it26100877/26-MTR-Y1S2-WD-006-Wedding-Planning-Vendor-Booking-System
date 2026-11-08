<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />
<div class="booking-studio">
    <section class="bk-hero bk-hero-visual mb-4 d-flex flex-wrap gap-4 justify-content-between align-items-center">
        <div class="bk-hero-copy">
            <div class="bk-eyebrow">ECLAT &bull; BOOKING STUDIO</div>
            <h1>${isBookingAdmin ? 'Vendor Request Management' : 'Your Booking Journey'}</h1>
            <p>Beautifully organized vendor requests, event schedules and booking updates in one elegant workspace.</p>
            <div class="d-flex flex-wrap gap-2 mt-3 position-relative" style="z-index:1">
                <a class="bk-btn-ghost" href="${pageContext.request.contextPath}/bookings/calendar"><i class="bi bi-calendar3"></i> Availability</a>
                <c:if test="${!isBookingAdmin}"><a class="bk-btn" href="${pageContext.request.contextPath}/bookings/new"><i class="bi bi-plus-circle"></i> New Booking</a></c:if>
                <c:if test="${isBookingAdmin}"><a class="bk-btn" href="${pageContext.request.contextPath}/bookings/reports"><i class="bi bi-graph-up"></i> Reports</a></c:if>
            </div>
        </div>
    </section>

    <div class="row g-3 mb-4">
        <div class="col-6 col-md-4 col-xl-2"><div class="bk-stat"><div class="d-flex justify-content-between align-items-center"><small>All Bookings</small><i class="bk-icon bi bi-journal-bookmark"></i></div><div class="bk-number">${totalCount}</div></div></div>
        <div class="col-6 col-md-4 col-xl-2"><div class="bk-stat"><div class="d-flex justify-content-between align-items-center"><small>Pending</small><i class="bk-icon bi bi-hourglass-split"></i></div><div class="bk-number">${pendingCount}</div></div></div>
        <div class="col-6 col-md-4 col-xl-2"><div class="bk-stat"><div class="d-flex justify-content-between align-items-center"><small>Confirmed</small><i class="bk-icon bi bi-check2-circle"></i></div><div class="bk-number">${confirmedCount}</div></div></div>
        <div class="col-6 col-md-4 col-xl-2"><div class="bk-stat"><div class="d-flex justify-content-between align-items-center"><small>Completed</small><i class="bk-icon bi bi-award"></i></div><div class="bk-number">${completedCount}</div></div></div>
        <div class="col-6 col-md-4 col-xl-2"><div class="bk-stat"><div class="d-flex justify-content-between align-items-center"><small>Cancelled</small><i class="bk-icon bi bi-x-circle"></i></div><div class="bk-number">${cancelledCount}</div></div></div>
        <div class="col-6 col-md-4 col-xl-2"><div class="bk-stat"><div class="d-flex justify-content-between align-items-center"><small>Rejected</small><i class="bk-icon bi bi-slash-circle"></i></div><div class="bk-number">${rejectedCount}</div></div></div>
    </div>

    <div class="bk-panel mb-4">
        <div class="d-flex justify-content-between flex-wrap gap-2 align-items-center mb-3">
            <div><h2 class="bk-heading">Explore your bookings</h2><div class="bk-subtitle">Find a vendor, booking reference or event date.</div></div>
            <a class="bk-btn-ghost" href="${pageContext.request.contextPath}/bookings/notifications"><i class="bi bi-bell"></i> Updates <c:if test="${unreadNotifications gt 0}"><span class="badge rounded-pill bg-danger">${unreadNotifications}</span></c:if></a>
        </div>
        <form method="get" action="${pageContext.request.contextPath}/bookings" class="row g-3">
            <div class="col-lg-4 col-md-6"><label class="form-label" for="bk-query">SEARCH</label><input id="bk-query" name="q" type="search" class="form-control" value="<c:out value='${query}'/>" placeholder="Booking ID, vendor or date"></div>
            <div class="col-lg-2 col-md-6"><label class="form-label" for="bk-status">STATUS</label><select id="bk-status" class="form-select" name="status"><option value="">All statuses</option><c:forEach items="${statuses}" var="st"><option value="${st.name()}" ${st.name() == selectedStatus ? 'selected' : ''}>${st.displayName}</option></c:forEach></select></div>
            <div class="col-lg-2 col-md-6"><label class="form-label" for="bk-vendor">VENDOR</label><select id="bk-vendor" name="vendorId" class="form-select"><option value="">All vendors</option><c:forEach var="v" items="${approvedVendors}"><option value="${v.vendorId}" ${v.vendorId == selectedVendorId ? 'selected' : ''}><c:out value="${v.vendorName}"/></option></c:forEach></select></div>
            <div class="col-lg-2 col-md-6"><label class="form-label" for="bk-from">FROM</label><input id="bk-from" name="from" type="date" class="form-control" value="<c:out value='${dateFrom}'/>"></div>
            <div class="col-lg-2 col-md-6"><label class="form-label" for="bk-to">TO</label><input id="bk-to" name="to" type="date" class="form-control" value="<c:out value='${dateTo}'/>"></div>
            <div class="col-12 d-flex gap-2 flex-wrap"><button class="bk-btn" type="submit"><i class="bi bi-search"></i> Apply Filters</button><a class="bk-btn-ghost" href="${pageContext.request.contextPath}/bookings">Clear Filters</a></div>
        </form>
    </div>

    <div class="bk-panel mb-4">
        <div class="d-flex flex-wrap justify-content-between gap-2 align-items-center mb-3">
            <div><h2 class="bk-heading">Booking records</h2><div class="bk-subtitle">Showing ${bookings.size()} record(s)</div></div>
            <c:if test="${isBookingAdmin}"><span class="bk-tag"><i class="bi bi-shield-check"></i> Administrator review enabled</span></c:if>
        </div>
        <div class="table-responsive"><table class="table mb-0">
            <thead><tr><th>Reference</th><th>Customer</th><th>Vendor</th><th>Event Date</th><th>Fee</th><th>Status</th><th class="text-end">Details</th></tr></thead>
            <tbody><c:forEach var="b" items="${bookings}"><tr>
                <td class="fw-semibold"><c:out value="${b.bookingId}"/></td>
                <td><c:out value="${customerNames[b.customerId]}"/></td>
                <td class="fw-semibold"><c:out value="${vendorNames[b.vendorId]}"/></td>
                <td><i class="bi bi-calendar-event text-muted me-1"></i><c:out value="${b.bookingDate}"/></td>
                <td class="fw-semibold">LKR ${b.agreedAmount}</td>
                <td><span class="bk-pill bk-pill-${b.status.name().toLowerCase()}">${b.status.displayName}</span></td>
                <td class="text-end"><a class="bk-link" href="${pageContext.request.contextPath}/bookings/view/${b.bookingId}">Open <i class="bi bi-arrow-up-right"></i></a></td>
            </tr></c:forEach>
            <c:if test="${empty bookings}"><tr><td colspan="7"><div class="bk-empty"><i class="bi bi-inbox"></i>No bookings match these filters.<div class="mt-2"><a href="${pageContext.request.contextPath}/bookings" class="bk-link">Show all bookings</a></div></div></td></tr></c:if>
            </tbody></table></div>
    </div>
</div>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
