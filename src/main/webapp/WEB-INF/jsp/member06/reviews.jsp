<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h3 class="fw-bold" style="color: #8d6a22;"><i class="bi bi-star-fill text-warning"></i> Customer Reviews &amp; Ratings</h3>
        <p class="text-secondary small mb-0">Authentic feedback from couples who completed vendor service bookings</p>
    </div>
    <c:if test="${sessionScope.currentUser.role == 'CUSTOMER'}">
        <a href="${pageContext.request.contextPath}/reviews/new" class="btn btn-purple shadow-sm">
            <i class="bi bi-pencil-square me-1"></i> Write a Review
        </a>
    </c:if>
</div>

<!-- Vendor Filter -->
<div class="card card-custom p-3 mb-4 shadow-sm">
    <form action="${pageContext.request.contextPath}/reviews" method="get" class="row g-2 align-items-center">
        <div class="col-md-6">
            <select name="vendorId" class="form-select">
                <option value="">All Vendors</option>
                <c:forEach var="v" items="${vendors}">
                    <option value="${v.vendorId}" ${selectedVendorId == v.vendorId ? 'selected' : ''}>
                        ${v.vendorName} (${v.category.displayName})
                    </option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-2">
            <button type="submit" class="btn btn-purple w-100">Filter</button>
        </div>
    </form>
</div>

<!-- Reviews List -->
<div class="row g-4">
    <c:forEach var="r" items="${reviews}">
        <div class="col-md-6">
            <div class="card card-custom p-4 shadow-sm h-100 d-flex flex-column justify-content-between">
                <div>
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <span class="text-warning fs-5 fw-bold">${r.starsDisplay} (${r.rating}/5)</span>
                        <c:choose>
                            <c:when test="${r.status == 'APPROVED'}"><span class="badge badge-approved">Approved</span></c:when>
                            <c:when test="${r.status == 'PENDING_MODERATION'}"><span class="badge badge-pending">Pending Review</span></c:when>
                            <c:otherwise><span class="badge badge-rejected">Rejected</span></c:otherwise>
                        </c:choose>
                    </div>

                    <h5 class="fw-bold mb-1" style="color: #8d6a22;">${r.title}</h5>
                    <div class="text-muted small mb-3">
                        <i class="bi bi-shop"></i> Vendor: <strong>${r.vendorId}</strong> &bull;
                        <c:choose>
                            <c:when test="${sessionScope.currentUser.role == 'ADMIN'}"><i class="bi bi-person"></i> By: ${r.customerId} &bull;</c:when>
                            <c:when test="${sessionScope.currentUser.role == 'CUSTOMER' && r.customerId == sessionScope.currentUser.userId}"><i class="bi bi-person"></i> By: You &bull;</c:when>
                            <c:otherwise><i class="bi bi-person"></i> By: Verified customer &bull;</c:otherwise>
                        </c:choose>
                        <i class="bi bi-calendar"></i> ${r.submittedAt}
                    </div>

                    <p class="text-secondary" style="white-space: pre-line;">"${r.feedback}"</p>
                </div>

                <div class="pt-3 border-top d-flex justify-content-between align-items-center small text-muted">
                    <span><i class="bi bi-patch-check-fill text-success"></i> Verified Booking<c:if test="${sessionScope.currentUser.role == 'ADMIN' || (sessionScope.currentUser.role == 'CUSTOMER' && r.customerId == sessionScope.currentUser.userId)}"> #${r.bookingId}</c:if></span>
                    <c:if test="${sessionScope.currentUser.role == 'CUSTOMER' && r.customerId == sessionScope.currentUser.userId && r.status == 'PENDING_MODERATION'}">
                        <details class="mt-3 w-100">
                            <summary class="text-secondary fw-semibold" style="cursor:pointer;">Edit pending review</summary>
                            <form action="${pageContext.request.contextPath}/reviews/update/${r.reviewId}" method="post" class="mt-3">
                                <div class="mb-2">
                                    <label class="form-label">Rating</label>
                                    <select name="rating" class="form-select" required>
                                        <c:forEach var="score" begin="1" end="5">
                                            <option value="${score}" ${score == r.rating ? 'selected' : ''}>${score} / 5</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="mb-2">
                                    <label class="form-label">Title</label>
                                    <input type="text" name="title" class="form-control" value="${r.title}" maxlength="100" required>
                                </div>
                                <div class="mb-2">
                                    <label class="form-label">Feedback</label>
                                    <textarea name="feedback" class="form-control" rows="3" minlength="10" maxlength="1500" required>${r.feedback}</textarea>
                                </div>
                                <button type="submit" class="btn btn-sm btn-purple">Save Review</button>
                            </form>
                        </details>
                    </c:if>
                    <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                        <form action="${pageContext.request.contextPath}/admin/reviews/delete/${r.reviewId}" method="post" style="display:inline;" onsubmit="return confirm('Delete this review permanently?');">
                            <button type="submit" class="btn btn-sm btn-outline-danger py-0 px-2">Delete</button>
                        </form>
                    </c:if>
                </div>
            </div>
        </div>
    </c:forEach>
    <c:if test="${empty reviews}">
        <div class="col-12 text-center py-5">
            <i class="bi bi-chat-square-quote fs-1 text-muted d-block mb-2"></i>
            <span class="text-muted">No reviews recorded yet.</span>
        </div>
    </c:if>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />

