<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<style>
/* Scoped to this admin page: no visual changes to public reviews or other modules. */
.eclat-admin-head {
    border-radius: 22px;
    padding: 1.8rem 2rem;
    background: linear-gradient(135deg, #faf6ef 0%, #f4ecdf 100%);
    border: 1px solid #e9dccb;
}
.eclat-admin-title {
    font-family: Georgia, "Times New Roman", serif;
    font-size: clamp(2rem, 3.2vw, 2.65rem);
    line-height: 1.15;
    color: #29231d;
}
.eclat-admin-table-card {
    border-radius: 18px !important;
    border: 1px solid #e6d9c7 !important;
    overflow: hidden;
    background: #fff;
}
.eclat-admin-table-card .table { margin-bottom: 0; }
.eclat-admin-table-card .table thead th {
    background: #f4eee6 !important;
    color: #3f362c !important;
    font-size: .83rem;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: .06em;
    border-bottom: 1px solid #dfd2c1;
    white-space: nowrap;
    padding: 1rem .85rem;
}
.eclat-admin-table-card .table tbody td {
    vertical-align: middle;
    padding: 1.1rem .85rem;
    border-color: #eee6db;
    color: #3a3027;
    font-size: .9rem;
}
.eclat-moderation-ref {
    display: block;
    font-size: .83rem;
    font-weight: 600;
    color: #51483f;
    overflow-wrap: anywhere;
    max-width: 185px;
    line-height: 1.55;
}
.eclat-moderation-story-title { font-weight: 700; margin-bottom: .3rem; color: #332a22; }
.eclat-admin-snippet { color: #6c5a49; max-width: 300px; line-height: 1.55; overflow-wrap: anywhere; }
.eclat-moderation-rating { min-width: 98px; }
.eclat-moderation-stars {
    color: #946c23 !important;
    font-size: 1.06rem;
    white-space: nowrap;
    letter-spacing: .01em;
    line-height: 1.2;
}
.eclat-moderation-score { font-size: .8rem; color: #6a5b4b; margin-top: .2rem; }
.eclat-moderation-actions {
    display: flex;
    flex-direction: column;
    gap: 8px;
    width: 124px;
    margin-left: auto;
}
.eclat-moderation-actions form { margin: 0; width: 100%; }
.eclat-moderation-actions .btn {
    width: 100%;
    min-height: 40px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    border-radius: 7px !important;
    padding: 8px 10px !important;
    font-family: inherit;
    font-size: .88rem;
    font-weight: 700 !important;
    line-height: 1.1;
    box-shadow: none !important;
    opacity: 1 !important;
    transition: background-color .15s ease, border-color .15s ease;
}
.eclat-moderation-actions .eclat-approve-btn {
    background: #286246 !important;
    border: 1px solid #286246 !important;
    color: #fff !important;
}
.eclat-moderation-actions .eclat-approve-btn:hover {
    background: #1c4b34 !important;
    border-color: #1c4b34 !important;
    color: #fff !important;
}
.eclat-moderation-actions .eclat-reject-btn {
    background: #fff7f7 !important;
    border: 1.5px solid #ad3640 !important;
    color: #952732 !important;
}
.eclat-moderation-actions .eclat-reject-btn:hover {
    background: #a32c36 !important;
    border-color: #a32c36 !important;
    color: #fff !important;
}
.eclat-moderation-actions .btn:focus-visible {
    outline: 3px solid #b99a68;
    outline-offset: 2px;
}
@media (max-width: 576px) {
    .eclat-admin-head { padding: 1.3rem; }
    .eclat-admin-table-card .table thead th,
    .eclat-admin-table-card .table tbody td { padding: .85rem .7rem; }
}
</style>

<section class="eclat-admin-head mb-4">
    <div class="d-flex justify-content-between align-items-end flex-wrap gap-3">
        <div>
            <div class="text-uppercase fw-bold small text-muted mb-2" style="letter-spacing:.18em;">Éclat / Administration</div>
            <h1 class="eclat-admin-title mb-2">Review moderation queue</h1>
            <p class="text-muted mb-0">Approve genuine stories, reject unsuitable submissions and keep community feedback polished.</p>
        </div>
        <span class="badge text-bg-dark px-3 py-2 rounded-pill">Pending: ${pendingReviews.size()}</span>
    </div>
</section>

<section class="card shadow-sm eclat-admin-table-card">
    <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
            <thead>
                <tr>
                    <th scope="col">Review ID</th>
                    <th scope="col">Customer</th>
                    <th scope="col">Vendor</th>
                    <th scope="col">Rating</th>
                    <th scope="col">Story</th>
                    <th scope="col">Submitted</th>
                    <th scope="col" class="text-end">Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="r" items="${pendingReviews}">
                    <tr>
                        <td><span class="eclat-moderation-ref"><c:out value="${r.reviewId}" /></span></td>
                        <td><span class="eclat-moderation-ref"><c:out value="${r.customerId}" /></span></td>
                        <td><span class="eclat-moderation-ref"><c:out value="${r.vendorId}" /></span></td>
                        <td class="eclat-moderation-rating">
                            <div class="eclat-moderation-stars" aria-label="${r.rating} out of 5 stars">
                                <c:forEach var="star" begin="1" end="5">
                                    <c:choose>
                                        <c:when test="${star le r.rating}">&#9733;</c:when>
                                        <c:otherwise>&#9734;</c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </div>
                            <div class="eclat-moderation-score">${r.rating} / 5</div>
                        </td>
                        <td>
                            <div class="eclat-moderation-story-title"><c:out value="${r.title}" /></div>
                            <div class="eclat-admin-snippet"><c:out value="${r.feedback}" /></div>
                        </td>
                        <td class="text-muted small"><c:out value="${r.submittedAt}" /></td>
                        <td>
                            <div class="eclat-moderation-actions">
                                <form action="${pageContext.request.contextPath}/admin/reviews/approve/${r.reviewId}" method="post">
                                    <button type="submit" class="btn eclat-approve-btn" aria-label="Approve review ${r.reviewId}">
                                        <i class="bi bi-check-lg" aria-hidden="true"></i> Approve
                                    </button>
                                </form>
                                <form action="${pageContext.request.contextPath}/admin/reviews/reject/${r.reviewId}" method="post" onsubmit="return confirm('Reject this review?');">
                                    <input type="hidden" name="reason" value="Does not meet community standards">
                                    <button type="submit" class="btn eclat-reject-btn" aria-label="Reject review ${r.reviewId}">
                                        <i class="bi bi-x-lg" aria-hidden="true"></i> Reject
                                    </button>
                                </form>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty pendingReviews}">
                    <tr>
                        <td colspan="7" class="text-center text-muted py-5">
                            <i class="bi bi-check-circle fs-1 text-success d-block mb-3"></i>
                            No pending reviews in the moderation queue.
                        </td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</section>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
