<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle != null ? pageTitle : "Wedding Planning System"}</title>

    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">

    <!-- Éclat Styles -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/eclat-royal.css?v=20261104-final">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/eclat-premium-pages.css?v=20261105-full-ui">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/member05-contracts.css?v=20261109-ui1">

    <!-- Vendor Marketplace CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/eclat-marketplace.css?v=20261108-vendor">
</head>

<body>

<!-- Navigation Bar -->
<nav class="navbar navbar-expand-lg navbar-light eclat-topbar shadow-sm sticky-top">
    <div class="container-fluid px-4">

        <a class="navbar-brand" href="${pageContext.request.contextPath}/">
            Éclat<span class="eclat-brand-dot">.</span>
        </a>

        <button class="navbar-toggler" type="button"
                data-bs-toggle="collapse" data-bs-target="#navContent">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navContent">

            <c:if test="${sessionScope.currentUser != null}">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">

                    <li class="nav-item">
                        <a class="nav-link ${activePage == 'dashboard' ? 'active fw-bold' : ''}"
                           href="${pageContext.request.contextPath}/dashboard">
                            <i class="bi bi-speedometer2"></i> Dashboard
                        </a>
                    </li>

                    <c:if test="${sessionScope.currentUser.role == 'CUSTOMER'}">
                        <li class="nav-item">
                            <a class="nav-link ${activePage == 'plans' ? 'active fw-bold' : ''}"
                               href="${pageContext.request.contextPath}/plans">
                                <i class="bi bi-calendar-heart"></i> Wedding Plans
                            </a>
                        </li>
                    </c:if>

                    <li class="nav-item">
                        <a class="nav-link ${activePage == 'vendors' ? 'active fw-bold' : ''}"
                           href="${pageContext.request.contextPath}/vendors">
                            <i class="bi bi-shop"></i> Vendors
                        </a>
                    </li>

                    <li class="nav-item">
                        <a class="nav-link ${activePage == 'bookings' ? 'active fw-bold' : ''}"
                           href="${pageContext.request.contextPath}/bookings">
                            <i class="bi bi-bookmark-check"></i> Bookings
                        </a>
                    </li>

                    <li class="nav-item">
                        <a class="nav-link ${activePage == 'contracts' ? 'active fw-bold' : ''}"
                           href="${pageContext.request.contextPath}/contracts">
                            <i class="bi bi-file-earmark-text"></i> Contracts &amp; Payments
                        </a>
                    </li>

                    <li class="nav-item">
                        <a class="nav-link ${activePage == 'reviews' ? 'active fw-bold' : ''}"
                           href="${pageContext.request.contextPath}/reviews">
                            <i class="bi bi-star-fill text-warning"></i> Reviews
                        </a>
                    </li>

                    <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                        <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/payment-proofs"><i class="bi bi-shield-check"></i> Payment Proofs</a></li>
                </c:if>

                <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle text-warning ${activePage == 'admin' ? 'active fw-bold' : ''}"
                               href="#" role="button" data-bs-toggle="dropdown">
                                <i class="bi bi-shield-lock"></i> Admin Tools
                            </a>

                            <ul class="dropdown-menu">
                                <li>
                                    <a class="dropdown-item"
                                       href="${pageContext.request.contextPath}/admin/dashboard">
                                        <i class="bi bi-people"></i> User Management
                                    </a>
                                </li>

                                <li>
                                    <a class="dropdown-item"
                                       href="${pageContext.request.contextPath}/vendors?filter=PENDING">
                                        <i class="bi bi-clock-history"></i> Vendor Approvals
                                    </a>
                                </li>

                                <li>
                                    <a class="dropdown-item"
                                       href="${pageContext.request.contextPath}/admin/reviews/moderation">
                                        <i class="bi bi-chat-left-dots"></i> Review Moderation
                                    </a>
                                </li>
                            </ul>
                        </li>
                    </c:if>
                </ul>

                <ul class="navbar-nav ms-auto align-items-center">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle text-white d-flex align-items-center"
                           href="#" role="button" data-bs-toggle="dropdown">

                            <span class="badge bg-light text-dark me-2">
                                <c:out value="${sessionScope.currentUser.role}"/>
                            </span>

                            <i class="bi bi-person-circle fs-5 me-1"></i>
                            <span><c:out value="${sessionScope.currentUser.name}"/></span>
                        </a>

                        <ul class="dropdown-menu dropdown-menu-end shadow">
                            <c:if test="${sessionScope.currentUser.role == 'CUSTOMER'}">
                                <li>
                                    <a class="dropdown-item"
                                       href="${pageContext.request.contextPath}/profile">
                                        <i class="bi bi-person-lines-fill me-2"></i> My Profile
                                    </a>
                                </li>
                                <li><hr class="dropdown-divider"></li>
                            </c:if>

                            <li>
                                <a class="dropdown-item text-danger"
                                   href="${pageContext.request.contextPath}/logout">
                                    <i class="bi bi-box-arrow-right me-2"></i> Logout
                                </a>
                            </li>
                        </ul>
                    </li>
                </ul>
            </c:if>

            <c:if test="${sessionScope.currentUser == null}">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0 eclat-guest-links">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/vendors">Vendors</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/vendors">Venues</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/vendors">Destinations</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/vendors">Inspiration</a>
                    </li>
                </ul>

                <ul class="navbar-nav ms-auto align-items-center eclat-guest-actions">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/register">Sign Up</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link eclat-nav-login"
                           href="${pageContext.request.contextPath}/login">Log In</a>
                    </li>
                </ul>
            </c:if>

        </div>
    </div>
</nav>

<div class="container py-4 flex-grow-1">
    <jsp:include page="/WEB-INF/jsp/common/alerts.jsp" />
