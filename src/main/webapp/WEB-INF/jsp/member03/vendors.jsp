<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h3 class="fw-bold" style="color: #8d6a22;"><i class="bi bi-shop"></i> Wedding Vendors Directory</h3>
        <p class="text-secondary small mb-0">Browse top-rated wedding specialists, photographers, venues, and caterers</p>
    </div>
    <a href="${pageContext.request.contextPath}/vendors/new" class="btn btn-purple shadow-sm">
        <i class="bi bi-person-plus me-1"></i> Register as Vendor
    </a>
</div>

<div class="card card-custom p-3 mb-4 shadow-sm">
    <form action="${pageContext.request.contextPath}/vendors" method="get" class="row g-2 align-items-center">
        <div class="col-md-4">
            <div class="input-group">
                <span class="input-group-text"><i class="bi bi-search"></i></span>
                <input type="text" name="query" class="form-control" placeholder="Search by vendor name..." value="${query}">
            </div>
        </div>
        <div class="col-md-2">
            <input type="text" name="location" class="form-control" placeholder="District / city" value="${location}">
        </div>
        <div class="col-md-3">
            <select name="category" class="form-select">
                <option value="">All Categories</option>
                <c:forEach var="cat" items="${categories}">
                    <option value="${cat.name()}" ${cat.name() == selectedCategory ? 'selected' : ''}>${cat.displayName}</option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-3 d-flex gap-2">
            <button type="submit" class="btn btn-purple w-100">Filter</button>
            <a href="${pageContext.request.contextPath}/vendors" class="btn btn-outline-secondary">Reset</a>
        </div>
    </form>
</div>

<div class="row g-4">
    <c:forEach var="v" items="${vendors}">
        <div class="col-md-6 col-lg-4">
            <div class="card card-custom h-100 p-3 shadow-sm d-flex flex-column justify-content-between">
                <div>
                    <div class="d-flex justify-content-between align-items-start mb-2">
                        <span class="badge bg-purple">${v.category.displayName}</span>
                        <c:choose>
                            <c:when test="${v.approvalStatus == 'APPROVED'}">
                                <span class="badge badge-approved">Approved</span>
                            </c:when>
                            <c:when test="${v.approvalStatus == 'PENDING'}">
                                <span class="badge badge-pending">Pending Approval</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge badge-rejected">${v.approvalStatus}</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <h5 class="fw-bold mb-1" style="color: #8d6a22;">${v.vendorName}</h5>
                    <div class="text-muted small mb-2"><i class="bi bi-geo-alt"></i> ${v.address}</div>
                    <p class="text-secondary small text-truncate-2" style="min-height: 40px;">${v.serviceDescription}</p>
                    <div class="d-flex justify-content-between align-items-center pt-2 border-top">
                        <span class="text-muted small">Starting Price</span>
                        <span class="fs-5 fw-bold text-success">LKR ${v.basePrice}</span>
                    </div>
                </div>
                <div class="mt-3 pt-2 border-top d-flex gap-2">
                    <a href="${pageContext.request.contextPath}/vendors/view/${v.vendorId}" class="btn btn-outline-primary btn-sm flex-grow-1">View Details</a>
                    <c:if test="${sessionScope.currentUser.role == 'CUSTOMER'}">
                        <a href="${pageContext.request.contextPath}/bookings/new?vendorId=${v.vendorId}" class="btn btn-purple btn-sm">Book Now</a>
                    </c:if>
                    <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                        <c:if test="${v.approvalStatus == 'PENDING'}">
                            <form action="${pageContext.request.contextPath}/admin/vendors/approve/${v.vendorId}" method="post" style="display:inline;"><button type="submit" class="btn btn-sm btn-success" title="Approve"><i class="bi bi-check-lg"></i></button></form>
                            <form action="${pageContext.request.contextPath}/admin/vendors/reject/${v.vendorId}" method="post" style="display:inline;"><button type="submit" class="btn btn-sm btn-danger" title="Reject"><i class="bi bi-x-lg"></i></button></form>
                        </c:if>
                        <c:if test="${v.approvalStatus == 'APPROVED' && v.active}">
                            <form action="${pageContext.request.contextPath}/admin/vendors/deactivate/${v.vendorId}" method="post" style="display:inline;"><button type="submit" class="btn btn-sm btn-outline-warning">Deactivate</button></form>
                        </c:if>
                        <c:if test="${v.approvalStatus == 'APPROVED' && !v.active}">
                            <form action="${pageContext.request.contextPath}/admin/vendors/activate/${v.vendorId}" method="post" style="display:inline;"><button type="submit" class="btn btn-sm btn-outline-success">Activate</button></form>
                        </c:if>
                        <form action="${pageContext.request.contextPath}/admin/vendors/delete/${v.vendorId}" method="post" style="display:inline;" onsubmit="return confirm('Permanently delete this vendor?');"><button type="submit" class="btn btn-sm btn-outline-danger" title="Delete vendor"><i class="bi bi-trash"></i></button></form>
                    </c:if>
                </div>
            </div>
        </div>
    </c:forEach>
    <c:if test="${empty vendors}">
        <div class="col-12 text-center py-5">
            <i class="bi bi-shop-window fs-1 text-muted d-block mb-2"></i>
            <span class="text-muted">No vendors matching your search criteria.</span><br>
            <a href="${pageContext.request.contextPath}/vendors" class="btn btn-outline-secondary btn-sm mt-3">Clear Filters</a>
        </div>
    </c:if>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
