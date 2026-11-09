```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<style>
    .vendor-detail-wrapper {
        max-width: 1100px;
        margin: 35px auto;
    }

    .vendor-detail-card {
        background: #fff;
        border-radius: 18px;
        padding: 35px;
        box-shadow: 0 5px 25px rgba(0,0,0,0.07);
    }

    .vendor-detail-title {
        color: #54213a;
        font-weight: 700;
    }

    .vendor-info-label {
        color: #888;
        font-size: 13px;
        margin-bottom: 5px;
    }

    .vendor-info-value {
        color: #333;
        font-weight: 500;
        margin-bottom: 20px;
        overflow-wrap: anywhere;
    }

    .vendor-description {
        background: #faf6f7;
        padding: 20px;
        border-radius: 12px;
        line-height: 1.7;
    }

    .vendor-price {
        color: #8c4968;
        font-size: 25px;
        font-weight: 700;
    }

    .vendor-status {
        display: inline-block;
        padding: 7px 14px;
        border-radius: 20px;
        font-size: 12px;
        font-weight: 600;
        background: #e8f5e9;
        color: #2e7d32;
    }

    .vendor-status.inactive {
        background: #ffebee;
        color: #c62828;
    }
</style>

<div class="vendor-detail-wrapper">

    <div class="mb-3">
        <a href="${pageContext.request.contextPath}/vendors"
           class="btn btn-outline-secondary">
            &larr; Back to Vendors
        </a>
    </div>

    <c:choose>
        <c:when test="${not empty vendor}">

            <div class="vendor-detail-card">

                <div class="d-flex justify-content-between align-items-start flex-wrap gap-3 mb-4">

                    <div>
                        <h2 class="vendor-detail-title">
                            <c:out value="${vendor.vendorName}" />
                        </h2>

                        <p class="text-muted mb-2">
                            <c:out value="${vendor.category.displayName}" />
                        </p>

                        <c:choose>
                            <c:when test="${vendor.active}">
                                <span class="vendor-status">Active</span>
                            </c:when>
                            <c:otherwise>
                                <span class="vendor-status inactive">Inactive</span>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="text-md-end">
                        <div class="vendor-info-label">Starting Price</div>
                        <div class="vendor-price">
                            LKR
                            <fmt:formatNumber value="${vendor.basePrice}" pattern="#,##0.00"/>
                        </div>
                    </div>

                </div>

                <hr>

                <h5 class="vendor-detail-title mb-4">Vendor Information</h5>

                <div class="row">

                    <div class="col-md-6">
                        <div class="vendor-info-label">Vendor ID</div>
                        <div class="vendor-info-value">
                            <c:out value="${vendor.vendorId}" />
                        </div>
                    </div>

                    <div class="col-md-6">
                        <div class="vendor-info-label">Contact Person</div>
                        <div class="vendor-info-value">
                            <c:out value="${vendor.contactPerson}" />
                        </div>
                    </div>

                    <div class="col-md-6">
                        <div class="vendor-info-label">Email Address</div>
                        <div class="vendor-info-value">
                            <c:out value="${vendor.email}" />
                        </div>
                    </div>

                    <div class="col-md-6">
                        <div class="vendor-info-label">Phone Number</div>
                        <div class="vendor-info-value">
                            <c:out value="${vendor.phone}" />
                        </div>
                    </div>

                    <div class="col-md-6">
                        <div class="vendor-info-label">Category</div>
                        <div class="vendor-info-value">
                            <c:out value="${vendor.category.displayName}" />
                        </div>
                    </div>

                    <div class="col-md-6">
                        <div class="vendor-info-label">Availability</div>
                        <div class="vendor-info-value">
                            <c:out value="${vendor.availability}" />
                        </div>
                    </div>

                    <div class="col-12">
                        <div class="vendor-info-label">Address</div>
                        <div class="vendor-info-value">
                            <c:out value="${vendor.address}" />
                        </div>
                    </div>

                </div>

                <hr>

                <h5 class="vendor-detail-title mb-3">Service Description</h5>

                <div class="vendor-description">
                    <c:choose>
                        <c:when test="${not empty vendor.serviceDescription}">
                            <c:out value="${vendor.serviceDescription}" />
                        </c:when>
                        <c:otherwise>
                            <span class="text-muted">
                                No service description available.
                            </span>
                        </c:otherwise>
                    </c:choose>
                </div>

                <hr>

                <div class="row mt-4">

                    <div class="col-md-6">
                        <div class="vendor-info-label">Approval Status</div>
                        <div class="vendor-info-value">
                            <c:out value="${vendor.approvalStatus}" />
                        </div>
                    </div>

                    <div class="col-md-6">
                        <div class="vendor-info-label">Registered Date</div>
                        <div class="vendor-info-value">
                            <c:out value="${vendor.registeredAt}" />
                        </div>
                    </div>

                </div>

                <div class="mt-4">
                    <a href="${pageContext.request.contextPath}/vendors"
                       class="btn btn-outline-secondary">
                        Back to Vendor List
                    </a>
                </div>

            </div>

        </c:when>

        <c:otherwise>

            <div class="vendor-detail-card text-center">
                <h4 class="text-danger">Vendor Not Found</h4>
                <p class="text-muted">
                    The requested vendor could not be found.
                </p>

                <a href="${pageContext.request.contextPath}/vendors"
                   class="btn btn-primary">
                    Return to Vendors
                </a>
            </div>

        </c:otherwise>
    </c:choose>

</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
```