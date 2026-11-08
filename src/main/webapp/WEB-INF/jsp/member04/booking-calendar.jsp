<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />
<div class="booking-studio">
    <div class="bk-hero mb-4 d-flex justify-content-between align-items-center flex-wrap gap-3">
        <div><p class="bk-eyebrow">PLANNING TOOLS / AVAILABILITY</p><h1>Vendor Availability Calendar</h1><p>View confirmed dates and pending requests before you plan your event.</p></div>
        <a class="bk-btn-ghost position-relative" href="${pageContext.request.contextPath}/bookings"><i class="bi bi-arrow-left"></i> Back to Bookings</a>
    </div>
    <div class="bk-panel mb-4">
        <form method="get" action="${pageContext.request.contextPath}/bookings/calendar" class="row g-3 align-items-end mb-4">
            <div class="col-md-7"><label class="form-label" for="calendarVendor">SELECT APPROVED VENDOR</label><select class="form-select" id="calendarVendor" name="vendorId" required>
                <c:forEach var="v" items="${vendors}"><option value="${v.vendorId}" ${selectedVendor != null && v.vendorId == selectedVendor.vendorId ? 'selected' : ''}><c:out value="${v.vendorName}"/></option></c:forEach>
            </select></div>
            <div class="col-md-3"><label class="form-label" for="calendarMonth">MONTH</label><input class="form-control" type="month" id="calendarMonth" name="month" value="${month}"></div>
            <div class="col-md-2"><button class="bk-btn w-100" type="submit"><i class="bi bi-search"></i> View</button></div>
        </form>
        <c:choose><c:when test="${empty vendors}"><div class="bk-empty"><i class="bi bi-calendar-x"></i>No active approved vendors are available yet.</div></c:when>
        <c:otherwise>
            <div class="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-4">
                <div><div class="bk-eyebrow">SELECTED SCHEDULE</div><h2 class="bk-heading"><c:out value="${monthLabel}"/></h2><p class="bk-subtitle mb-0"><c:out value="${selectedVendor.vendorName}"/></p></div>
                <div class="d-flex gap-2">
                    <a class="bk-btn-ghost" href="${pageContext.request.contextPath}/bookings/calendar?vendorId=${selectedVendor.vendorId}&amp;month=${previousMonth}" aria-label="Previous month"><i class="bi bi-chevron-left"></i></a>
                    <a class="bk-btn-ghost" href="${pageContext.request.contextPath}/bookings/calendar?vendorId=${selectedVendor.vendorId}&amp;month=${nextMonth}" aria-label="Next month"><i class="bi bi-chevron-right"></i></a>
                </div>
            </div>
            <div class="d-flex flex-wrap gap-3 mb-3"><span class="bk-legend"><span class="bk-dot"></span> Available</span><span class="bk-legend"><span class="bk-dot requested"></span> Pending enquiry</span><span class="bk-legend"><span class="bk-dot booked"></span> Reserved</span><span class="bk-legend"><span class="bk-dot past"></span> Past date</span></div>
            <div class="bk-calendar mb-3">
                <div class="bk-weekday">Mon</div><div class="bk-weekday">Tue</div><div class="bk-weekday">Wed</div><div class="bk-weekday">Thu</div><div class="bk-weekday">Fri</div><div class="bk-weekday">Sat</div><div class="bk-weekday">Sun</div>
                <c:forEach items="${calendarDays}" var="day">
                    <c:choose>
                        <c:when test="${day.currentMonth && day.state != 'past' && day.state != 'booked' && !isBookingAdmin}">
                            <a class="bk-day ${day.state}" href="${pageContext.request.contextPath}/bookings/new?vendorId=${selectedVendor.vendorId}&amp;date=${day.date}" title="Request a booking for ${day.date}"><span class="bk-day-number">${day.dayOfMonth}</span><span class="bk-day-label">${day.state == 'requested' ? 'Enquiries' : 'Available'}<c:if test="${day.requests gt 0}"> (${day.requests})</c:if></span></a>
                        </c:when>
                        <c:otherwise><div class="bk-day ${day.state} ${!day.currentMonth ? 'outside' : ''}"><span class="bk-day-number">${day.dayOfMonth}</span><span class="bk-day-label"><c:choose><c:when test="${day.state == 'booked'}">Reserved</c:when><c:when test="${day.state == 'requested'}">Enquiries (${day.requests})</c:when><c:when test="${day.state == 'available'}">Available</c:when><c:otherwise>Unavailable</c:otherwise></c:choose></span></div></c:otherwise>
                    </c:choose>
                </c:forEach>
            </div>
            <div class="bk-alert"><i class="bi bi-info-circle me-2"></i>Green days have no confirmed bookings. Yellow days have pending requests and may still be booked. A vendor can only have one confirmed booking on the same date. Check availability again when submitting.</div>
        </c:otherwise></c:choose>
    </div>
</div>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
