<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<section class="eclat-dashboard-hero">
    <div class="eclat-dashboard-copy">
        <span class="eclat-eyebrow">ÉCLAT · YOUR WEDDING WORKSPACE</span>
        <h1>Welcome, ${sessionScope.currentUser.name}</h1>
        <p>
            <c:choose>
                <c:when test="${sessionScope.currentUser.role == 'ADMIN'}">
                    Manage the platform, review activity and keep every service running smoothly.
                </c:when>
                <c:otherwise>
                    Your wedding plans, trusted vendors and important details, thoughtfully organised in one place.
                </c:otherwise>
            </c:choose>
        </p>
    </div>
    <div class="eclat-dashboard-actions">
        <span class="eclat-role-label">${sessionScope.currentUser.role}</span>
        <c:if test="${sessionScope.currentUser.role == 'CUSTOMER'}">
            <a href="${pageContext.request.contextPath}/plans/new" class="eclat-primary-btn">
                Create Wedding Plan <i class="bi bi-arrow-right"></i>
            </a>
            <a href="${pageContext.request.contextPath}/vendors" class="eclat-secondary-btn">
                Explore Vendors
            </a>
        </c:if>
        <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="eclat-primary-btn">
                User Management <i class="bi bi-arrow-right"></i>
            </a>
        </c:if>
    </div>
</section>

<section class="eclat-dashboard-section">
    <div class="eclat-section-heading">
        <div>
            <c:choose>
                <c:when test="${sessionScope.currentUser.role == 'ADMIN'}">
                    <span class="eclat-eyebrow">PLATFORM AT A GLANCE</span>
                    <h2>System overview</h2>
                </c:when>
                <c:otherwise>
                    <span class="eclat-eyebrow">YOUR ACCOUNT AT A GLANCE</span>
                    <h2>Your overview</h2>
                </c:otherwise>
            </c:choose>
        </div>
        <span class="eclat-section-note">
            <c:choose>
                <c:when test="${sessionScope.currentUser.role == 'ADMIN'}">Live totals from the application records</c:when>
                <c:otherwise>Only your own plans, bookings, contracts and reviews</c:otherwise>
            </c:choose>
        </span>
    </div>

    <div class="row g-3 eclat-stat-grid">
        <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
        <div class="col-6 col-md-4 col-xl-2">
            <article class="eclat-stat-card stat-customer">
                <span class="eclat-stat-kicker">MEMBER 01</span>
                <strong>${totalCustomers}</strong>
                <span>Customers</span>
            </article>
        </div>
        </c:if>
        <div class="col-6 col-md-4 col-xl-2">
            <article class="eclat-stat-card stat-plans">
                <span class="eclat-stat-kicker">MEMBER 02</span>
                <strong>${totalPlans}</strong>
                <span>Wedding plans</span>
            </article>
        </div>
        <div class="col-6 col-md-4 col-xl-2">
            <article class="eclat-stat-card stat-vendors">
                <span class="eclat-stat-kicker">MEMBER 03</span>
                <strong>${totalVendors}</strong>
                <span><c:choose><c:when test="${sessionScope.currentUser.role == 'ADMIN'}">Vendors</c:when><c:otherwise>Available vendors</c:otherwise></c:choose></span>
            </article>
        </div>
        <div class="col-6 col-md-4 col-xl-2">
            <article class="eclat-stat-card stat-bookings">
                <span class="eclat-stat-kicker">MEMBER 04</span>
                <strong>${totalBookings}</strong>
                <span>Bookings</span>
            </article>
        </div>
        <div class="col-6 col-md-4 col-xl-2">
            <article class="eclat-stat-card stat-contracts">
                <span class="eclat-stat-kicker">MEMBER 05</span>
                <strong>${totalContracts}</strong>
                <span>Contracts</span>
            </article>
        </div>
        <div class="col-6 col-md-4 col-xl-2">
            <article class="eclat-stat-card stat-reviews">
                <span class="eclat-stat-kicker">MEMBER 06</span>
                <strong>${totalReviews}</strong>
                <span>Reviews</span>
            </article>
        </div>
    </div>
</section>

<section class="eclat-dashboard-note">
    <div>
        <span class="eclat-eyebrow">ÉCLAT MANAGEMENT SYSTEM</span>
        <h2>Thoughtfully managed. Beautifully organised.</h2>
        <p>Use the navigation above to manage the areas available to your account. Changes are saved through the application’s Java services and file storage.</p>
    </div>
    <div class="eclat-note-mark">É.</div>
</section>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
