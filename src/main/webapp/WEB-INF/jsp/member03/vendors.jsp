<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<style>
.vendor-page {
    max-width: 1250px;
    margin: 0 auto;
}
.vendor-hero {
    background: linear-gradient(120deg, #302820, #65513a);
    color: #fff;
    padding: 42px;
    border-radius: 14px;
    margin-bottom: 28px;
}
.vendor-hero h1 {
    font-family: 'Playfair Display', serif;
    font-size: clamp(2rem, 4vw, 3rem);
}
.vendor-panel {
    background: #fff;
    border: 1px solid #eee5d9;
    border-radius: 12px;
    padding: 24px;
    margin-bottom: 24px;
}
.vendor-card {
    height: 100%;
    background: #fff;
    border: 1px solid #eee5d9;
    border-radius: 12px;
    padding: 22px;
    transition: .2s ease;
}
.vendor-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 12px 28px #30282012;
}
.vendor-card h3 {
    font-family: 'Playfair Display', serif;
    font-size: 1.35rem;
}
.vendor-category {
    color: #98794b;
    font-size: .78rem;
    font-weight: 700;
    letter-spacing: .08em;
    text-transform: uppercase;
}
.vendor-price {
    color: #80643d;
    font-size: 1.1rem;
    font-weight: 700;
}
.vendor-status {
    display: inline-block;
    padding: 5px 11px;
    border-radius: 30px;
    font-size: .72rem;
    font-weight: 700;
    background: #f1eee8;
    color: #65513a;
}
.vendor-status.approved {
    background: #e6f4ea;
    color: #24713c;
}
.vendor-status.pending {
    background: #fff3d8;
    color: #946200;
}
.vendor-status.rejected {
    background: #fde8e8;
    color: #a52d2d;
}
.vendor-muted {
    color: #81796d;
    font-size: .88rem;
}
.vendor-actions {
    border-top: 1px solid #eee8df;
    padding-top: 14px;
    margin-top: 16px;
}
.vendor-empty {
    text-align: center;
    padding: 55px 20px;
    color: #81796d;
}
.vendor-empty i {
    font-size: 2.5rem;
    display: block;
    margin-bottom: 12px;
}
</style>

<div class="vendor-page">

    <!-- Page heading -->
    <section class="vendor-hero">
        <div class="d-flex flex-wrap justify-content-between align-items-center gap-3">
            <div>
                <div class="text-uppercase small mb-2" style="letter-spacing:3px;color:#e0c99f;">
                    ÉCLAT · VENDOR MARKETPLACE
                </div>

                <h1>Discover Your Perfect Vendors</h1>

                <p class="mb-0 text-white-50">
                    Find trusted wedding professionals for your special day.
                </p>
            </div>

            <c:if test="${sessionScope.currentUser != null}">
                <a href="${pageContext.request.contextPath}/vendors/new"
                   class="btn btn-light px-4 py-3">
                    <i class="bi bi-plus-circle me-2"></i> Register Vendor
                </a>
            </c:if>
        </div>
    </section>

    <!-- Search and filters -->
    <section class="vendor-panel">
        <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-3">
            <div>
                <h2 class="h4 mb-1">Find Vendors</h2>
                <p class="vendor-muted mb-0">Search by name, category or location.</p>
            </div>

            <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                <c:if test="${filter == 'PENDING'}">
                    <span class="vendor-status pending">Pending Approvals</span>
                </c:if>
            </c:if>
        </div>

        <form method="get" action="${pageContext.request.contextPath}/vendors"
              class="row g-3">

            <div class="col-lg-4 col-md-6">
                <label for="vendorQuery" class="form-label">Search vendor</label>
                <input type="search"
                       class="form-control"
                       id="vendorQuery"
                       name="query"
                       value="<c:out value='${query}'/>"
                       placeholder="Enter vendor name">
            </div>

            <div class="col-lg-3 col-md-6">
                <label for="vendorCategory" class="form-label">Category</label>
                <select class="form-select" id="vendorCategory" name="category">
                    <option value="">All Categories</option>

                    <c:forEach items="${categories}" var="cat">
                        <option value="${cat.name()}"
                            ${cat.name() == selectedCategory ? 'selected' : ''}>
                            <c:out value="${cat.displayName}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="col-lg-3 col-md-6">
                <label for="vendorLocation" class="form-label">Location</label>
                <input type="text"
                       class="form-control"
                       id="vendorLocation"
                       name="location"
                       value="<c:out value='${location}'/>"
                       placeholder="Enter district or city">
            </div>

            <div class="col-lg-2 col-md-6">
                <label class="form-label d-block">&nbsp;</label>
                <button type="submit" class="btn btn-dark w-100">
                    <i class="bi bi-search me-1"></i> Search
                </button>
            </div>

            <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                <input type="hidden" name="filter" value="<c:out value='${filter}'/>">
            </c:if>

            <div class="col-12">
                <a href="${pageContext.request.contextPath}/vendors"
                   class="text-decoration-none small">
                    <i class="bi bi-arrow-counterclockwise me-1"></i> Clear filters
                </a>
            </div>
        </form>
    </section>

    <!-- Vendor results -->
    <section class="vendor-panel">

        <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-4">
            <div>
                <h2 class="h4 mb-1">
                    <c:choose>
                        <c:when test="${filter == 'PENDING'}">
                            Vendor Approval Requests
                        </c:when>
                        <c:otherwise>
                            Available Vendors
                        </c:otherwise>
                    </c:choose>
                </h2>

                <p class="vendor-muted mb-0">
                    Showing ${vendors.size()} vendor(s)
                </p>
            </div>
        </div>

        <div class="row g-4">

            <c:forEach items="${vendors}" var="vendor">

                <div class="col-lg-4 col-md-6">
                    <div class="vendor-card">

                        <div class="d-flex justify-content-between align-items-start gap-2 mb-3">
                            <span class="vendor-category">
                                <c:out value="${vendor.category.displayName}"/>
                            </span>

                            <c:choose>
                                <c:when test="${vendor.approvalStatus == 'APPROVED'}">
                                    <span class="vendor-status approved">Approved</span>
                                </c:when>

                                <c:when test="${vendor.approvalStatus == 'PENDING'}">
                                    <span class="vendor-status pending">Pending</span>
                                </c:when>

                                <c:otherwise>
                                    <span class="vendor-status rejected">
                                        <c:out value="${vendor.approvalStatus}"/>
                                    </span>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <h3>
                            <c:out value="${vendor.vendorName}"/>
                        </h3>

                        <p class="vendor-muted mb-2">
                            <i class="bi bi-geo-alt me-1"></i>
                            <c:out value="${vendor.address}"/>
                        </p>

                        <p class="vendor-muted mb-3">
                            <c:out value="${vendor.serviceDescription}"/>
                        </p>

                        <div class="vendor-price mb-2">
                            LKR <c:out value="${vendor.basePrice}"/>
                        </div>

                        <div class="vendor-muted">
                            <i class="bi bi-person me-1"></i>
                            <c:out value="${vendor.contactPerson}"/>
                        </div>

                        <div class="vendor-muted">
                            <i class="bi bi-telephone me-1"></i>
                            <c:out value="${vendor.phone}"/>
                        </div>

                        <div class="vendor-actions">

                            <a href="${pageContext.request.contextPath}/vendors/view/${vendor.vendorId}"
                               class="btn btn-outline-dark btn-sm w-100 mb-2">
                                <i class="bi bi-eye me-1"></i> View Details
                            </a>

                            <!-- Admin actions -->
                            <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">

                                <c:if test="${vendor.approvalStatus == 'PENDING'}">
                                    <div class="d-flex gap-2 mb-2">

                                        <form method="post"
                                              action="${pageContext.request.contextPath}/admin/vendors/approve/${vendor.vendorId}"
                                              class="flex-fill">
                                            <button type="submit"
                                                    class="btn btn-success btn-sm w-100">
                                                <i class="bi bi-check-circle me-1"></i> Approve
                                            </button>
                                        </form>

                                        <form method="post"
                                              action="${pageContext.request.contextPath}/admin/vendors/reject/${vendor.vendorId}"
                                              class="flex-fill">
                                            <button type="submit"
                                                    class="btn btn-danger btn-sm w-100">
                                                <i class="bi bi-x-circle me-1"></i> Reject
                                            </button>
                                        </form>

                                    </div>
                                </c:if>

                                <a href="${pageContext.request.contextPath}/vendors/edit/${vendor.vendorId}"
                                   class="btn btn-outline-secondary btn-sm w-100 mb-2">
                                    <i class="bi bi-pencil me-1"></i> Edit Vendor
                                </a>

                                <c:choose>
                                    <c:when test="${vendor.active}">
                                        <form method="post"
                                              action="${pageContext.request.contextPath}/admin/vendors/deactivate/${vendor.vendorId}"
                                              class="mb-2">
                                            <button type="submit"
                                                    class="btn btn-outline-warning btn-sm w-100">
                                                <i class="bi bi-pause-circle me-1"></i> Deactivate
                                            </button>
                                        </form>
                                    </c:when>

                                    <c:otherwise>
                                        <form method="post"
                                              action="${pageContext.request.contextPath}/admin/vendors/activate/${vendor.vendorId}"
                                              class="mb-2">
                                            <button type="submit"
                                                    class="btn btn-outline-success btn-sm w-100">
                                                <i class="bi bi-play-circle me-1"></i> Activate
                                            </button>
                                        </form>
                                    </c:otherwise>
                                </c:choose>

                                <form method="post"
                                      action="${pageContext.request.contextPath}/admin/vendors/delete/${vendor.vendorId}"
                                      onsubmit="return confirm('Are you sure you want to delete this vendor?');">
                                    <button type="submit"
                                            class="btn btn-outline-danger btn-sm w-100">
                                        <i class="bi bi-trash me-1"></i> Delete Vendor
                                    </button>
                                </form>

                            </c:if>
                        </div>

                    </div>
                </div>

            </c:forEach>

            <c:if test="${empty vendors}">
                <div class="col-12">
                    <div class="vendor-empty">
                        <i class="bi bi-shop-window"></i>

                        <h4>No vendors found</h4>

                        <p class="mb-3">
                            No vendors match your current search or filters.
                        </p>

                        <a href="${pageContext.request.contextPath}/vendors"
                           class="btn btn-outline-dark">
                            View All Vendors
                        </a>
                    </div>
                </div>
            </c:if>

        </div>
    </section>

</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />