<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />
<div class="booking-studio">
    <section class="bk-hero mb-4 d-flex justify-content-between align-items-center flex-wrap gap-3"><div><div class="bk-eyebrow">ADMIN ANALYTICS</div><h1>Booking Reports</h1><p>Monthly booking performance, value and vendor request activity.</p></div><a class="bk-btn-ghost position-relative" href="${pageContext.request.contextPath}/bookings"><i class="bi bi-arrow-left"></i> Back to Bookings</a></section>
    <section class="bk-panel mb-4">
        <form method="get" action="${pageContext.request.contextPath}/bookings/reports" class="row g-3 align-items-end">
            <div class="col-md-4"><label class="form-label" for="reportMonth">EVENT MONTH</label><input type="month" class="form-control" id="reportMonth" name="month" value="${selectedMonth}" required></div>
            <div class="col-md-5"><label class="form-label" for="reportVendor">VENDOR</label><select class="form-select" id="reportVendor" name="vendorId"><option value="">All vendors</option><c:forEach items="${vendors}" var="v"><option value="${v.vendorId}" ${v.vendorId == selectedVendorId ? 'selected' : ''}><c:out value="${v.vendorName}"/></option></c:forEach></select></div>
            <div class="col-md-3"><button type="submit" class="bk-btn w-100"><i class="bi bi-funnel"></i> Generate Report</button></div>
        </form>
    </section>
    <section class="row g-3 mb-4">
        <div class="col-md-4"><div class="bk-stat"><small>Total bookings this period</small><div class="bk-number">${bookings.size()}</div></div></div>
        <div class="col-md-4"><div class="bk-stat"><small>Confirmed / Completed</small><div class="bk-number">${confirmedReportCount}</div></div></div>
        <div class="col-md-4"><div class="bk-stat"><small>Confirmed service value (LKR)</small><div class="bk-number" style="font-size:1.6rem">${totalValue}</div></div></div>
    </section>
    <section class="bk-panel mb-4">
        <div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-4"><div><h2 class="bk-heading">Status overview</h2><p class="bk-subtitle">Bookings grouped by their current status.</p></div><a class="bk-btn" href="${pageContext.request.contextPath}/bookings/reports/export?month=${selectedMonth}&amp;vendorId=${selectedVendorId}"><i class="bi bi-download"></i> Export CSV</a></div>
        <div class="row g-2 mb-4"><c:forEach items="${statuses}" var="st"><div class="col-6 col-lg"><div class="bk-report-metric"><div class="bk-pill bk-pill-${st.name().toLowerCase()}">${st.displayName}</div><div class="fs-4 fw-bold mt-2">${counts[st]}</div></div></div></c:forEach></div>
        <div class="table-responsive"><table class="table mb-0"><thead><tr><th>Reference</th><th>Customer</th><th>Vendor</th><th>Event Date</th><th>Status</th><th>Fee</th></tr></thead><tbody>
            <c:forEach items="${bookings}" var="b"><tr><td><a class="bk-link" href="${pageContext.request.contextPath}/bookings/view/${b.bookingId}"><c:out value="${b.bookingId}"/></a></td><td><c:out value="${customerNames[b.customerId]}"/></td><td><c:out value="${vendorNames[b.vendorId]}"/></td><td><c:out value="${b.bookingDate}"/></td><td><span class="bk-pill bk-pill-${b.status.name().toLowerCase()}">${b.status.displayName}</span></td><td>LKR ${b.agreedAmount}</td></tr></c:forEach>
            <c:if test="${empty bookings}"><tr><td colspan="6"><div class="bk-empty"><i class="bi bi-graph-down"></i>No bookings match this month and vendor.</div></td></tr></c:if>
        </tbody></table></div>
    </section>
</div>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
