<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<div class="row justify-content-center">
    <div class="col-md-9">
        <a href="${pageContext.request.contextPath}/vendors" class="text-decoration-none text-secondary small d-inline-block mb-3">&larr; Back to Vendors Directory</a>

        <div class="card card-custom p-4 shadow mb-4">
            <div class="d-flex justify-content-between align-items-start mb-3">
                <div>
                    <span class="badge bg-purple mb-2">${vendor.category.displayName}</span>
                    <h3 class="fw-bold mb-1" style="color: #8d6a22;">${vendor.vendorName}</h3>
                    <div class="text-muted"><i class="bi bi-geo-alt"></i> ${vendor.address}</div>
                </div>
                <div class="text-end">
                    <div class="text-warning fs-4">
                        <c:choose>
                            <c:when test="${avgRating > 0}">
                                ★ <span class="fw-bold text-dark fs-5">${avgRating} / 5.0</span>
                            </c:when>
                            <c:otherwise>
                                <span class="text-muted small">No reviews yet</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <small class="text-secondary">(${reviewCount} verified reviews)</small>
                </div>
            </div>

            <hr>

            <div class="row g-4 my-2">
                <div class="col-md-4">
                    <div class="card bg-light p-3 border-0 rounded-3">
                        <small class="text-secondary fw-semibold">Starting Price</small>
                        <div class="fs-4 fw-bold text-success">LKR ${vendor.basePrice}</div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card bg-light p-3 border-0 rounded-3">
                        <small class="text-secondary fw-semibold">Contact Person</small>
                        <div class="fw-bold text-dark">${vendor.contactPerson}</div>
                        <small class="text-muted">${vendor.phone}</small>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card bg-light p-3 border-0 rounded-3">
                        <small class="text-secondary fw-semibold">Availability Status</small>
                        <div>
                            <c:choose>
                                <c:when test="${vendor.availability == 'AVAILABLE'}"><span class="badge badge-active">Available</span></c:when>
                                <c:otherwise><span class="badge badge-pending">${vendor.availability}</span></c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>

            <div class="my-4">
                <h5 class="fw-bold mb-2" style="color: #8d6a22;">Service Description &amp; Offerings</h5>
                <p class="text-secondary" style="white-space: pre-line;">${vendor.serviceDescription}</p>
            </div>

            <div class="d-flex justify-content-between align-items-center pt-3 border-top">
                <div>
                    <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                        <a href="${pageContext.request.contextPath}/vendors/edit/${vendor.vendorId}" class="btn btn-outline-secondary me-2">
                            <i class="bi bi-pencil"></i> Edit Vendor
                        </a>
                    </c:if>
                </div>
                <div>
                    <c:if test="${sessionScope.currentUser.role == 'CUSTOMER'}">
                        <a href="${pageContext.request.contextPath}/bookings/new?vendorId=${vendor.vendorId}" class="btn btn-purple px-4 py-2 fw-bold">
                            <i class="bi bi-bookmark-plus me-1"></i> Book This Vendor
                        </a>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />

