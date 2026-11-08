<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h3 class="fw-bold" style="color: #8d6a22;"><i class="bi bi-shield-check"></i> Review Moderation Queue</h3>
        <p class="text-secondary small mb-0">Review, verify, and moderate customer reviews before public display</p>
    </div>
</div>

<div class="card card-custom p-4 shadow-sm">
    <div class="table-responsive">
        <table class="table table-hover align-middle">
            <thead class="table-custom">
                <tr>
                    <th>Review ID</th>
                    <th>Customer</th>
                    <th>Vendor</th>
                    <th>Rating</th>
                    <th>Title &amp; Feedback</th>
                    <th>Submitted At</th>
                    <th class="text-end">Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="r" items="${pendingReviews}">
                    <tr>
                        <td class="fw-semibold">${r.reviewId}</td>
                        <td>${r.customerId}</td>
                        <td class="fw-bold">${r.vendorId}</td>
                        <td class="text-warning fw-bold">${r.starsDisplay}</td>
                        <td>
                            <div class="fw-bold">${r.title}</div>
                            <small class="text-muted">"${r.feedback}"</small>
                        </td>
                        <td class="text-muted small">${r.submittedAt}</td>
                        <td class="text-end">
                            <form action="${pageContext.request.contextPath}/admin/reviews/approve/${r.reviewId}" method="post" style="display:inline;">
                                <button type="submit" class="btn btn-sm btn-success me-1 fw-bold">
                                    <i class="bi bi-check-lg"></i> Approve
                                </button>
                            </form>
                            <form action="${pageContext.request.contextPath}/admin/reviews/reject/${r.reviewId}" method="post" style="display:inline;">
                                <input type="hidden" name="reason" value="Does not meet community standards">
                                <button type="submit" class="btn btn-sm btn-danger fw-bold">
                                    <i class="bi bi-x-lg"></i> Reject
                                </button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty pendingReviews}">
                    <tr><td colspan="7" class="text-center text-muted py-5"><i class="bi bi-check-circle fs-2 text-success d-block mb-2"></i>No pending reviews in the moderation queue.</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />

