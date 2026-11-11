<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/eclat-bookings.css?v=20261111-booking-hero">
<div class="booking-studio">
    <section class="bk-hero mb-4 d-flex justify-content-between align-items-center flex-wrap gap-3"><div><div class="bk-eyebrow">YOUR ACTIVITY</div><h1>Booking Notifications</h1><p>Stay informed about requests, approvals and completed bookings.</p></div><a class="bk-btn-ghost position-relative" href="${pageContext.request.contextPath}/bookings"><i class="bi bi-arrow-left"></i> Back to Bookings</a></section>
    <section class="bk-panel mb-4">
        <div class="d-flex flex-wrap gap-2 align-items-center justify-content-between mb-4"><div><h2 class="bk-heading">Recent updates</h2><p class="bk-subtitle mb-0">${unreadNotifications} unread notification(s)</p></div><c:if test="${unreadNotifications gt 0}"><form method="post" action="${pageContext.request.contextPath}/bookings/notifications/read-all"><button class="bk-btn" type="submit"><i class="bi bi-check2-all"></i> Mark all as read</button></form></c:if></div>
        <div class="d-grid gap-3">
            <c:forEach var="notice" items="${notifications}"><div class="bk-notice ${notice.unread ? 'unread' : ''}">
                <div class="bk-icon"><i class="bi bi-bell" style="font-size:1.3rem;color:#987044"></i></div>
                <div class="flex-grow-1"><div class="fw-bold mb-1"><c:out value="${notice.message}"/> <c:if test="${notice.unread}"><span class="bk-tag">New</span></c:if></div><div class="small text-secondary"><c:out value="${notice.createdAt}"/> &nbsp; &bull; &nbsp; <c:out value="${notice.bookingId}"/></div>
                    <a class="bk-link d-inline-block mt-2" href="${pageContext.request.contextPath}/bookings/view/${notice.bookingId}">View booking <i class="bi bi-arrow-up-right"></i></a></div>
                <c:if test="${notice.unread}"><form method="post" action="${pageContext.request.contextPath}/bookings/notifications/read/${notice.notificationId}"><button type="submit" class="btn btn-sm btn-outline-secondary">Mark read</button></form></c:if>
            </div></c:forEach>
            <c:if test="${empty notifications}"><div class="bk-empty"><i class="bi bi-bell-slash"></i>No notifications yet. Booking actions will appear here automatically.</div></c:if>
        </div>
    </section>
</div>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
