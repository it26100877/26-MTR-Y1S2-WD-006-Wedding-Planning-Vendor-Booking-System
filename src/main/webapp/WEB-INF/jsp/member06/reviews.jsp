<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />


<style>
.eclat-review-hero {
    position: relative;
    overflow: hidden;
    border-radius: 32px;
    padding: 3.25rem;
    min-height: 350px;
    color: #fff;
    background:
        linear-gradient(90deg, rgba(23,20,17,0.52) 0%, rgba(23,20,17,0.34) 46%, rgba(23,20,17,0.10) 100%),
        linear-gradient(180deg, rgba(255,255,255,0.08) 0%, rgba(255,255,255,0.00) 100%),
        url('https://images.unsplash.com/photo-1519225421980-715cb0215aed?auto=format&fit=crop&w=1600&q=80') center/cover no-repeat;
    box-shadow: 0 24px 60px rgba(34, 27, 18, 0.18);
}
.eclat-review-hero::before {
    content: "";
    position: absolute;
    inset: 0;
    background: linear-gradient(0deg, rgba(255,255,255,0.06), rgba(255,255,255,0.06));
    pointer-events: none;
}
.eclat-review-hero::after {
    content: "";
    position: absolute;
    inset: 0;
    background: radial-gradient(circle at top right, rgba(226, 192, 128, 0.22), transparent 34%);
    pointer-events: none;
}
.eclat-review-eyebrow,
.eclat-review-section-label {
    text-transform: uppercase;
    letter-spacing: .22em;
    font-size: .8rem;
    font-weight: 700;
}
.eclat-review-hero h1,
.eclat-review-section-title {
    font-family: Georgia, "Times New Roman", serif;
}
.eclat-review-hero h1 {
    font-size: clamp(2.5rem, 5vw, 4.4rem);
    line-height: .98;
    margin-top: .75rem;
    margin-bottom: 1rem;
}
.eclat-review-hero p {
    max-width: 720px;
    color: rgba(255,255,255,.92);
    font-size: 1.06rem;
}
.eclat-review-hero h1 {
    color: #fff;
    text-shadow: 0 10px 24px rgba(0,0,0,.18);
}
.eclat-review-actions {
    position: relative;
    z-index: 2;
}
.eclat-review-actions .btn {
    padding: .9rem 1.35rem;
    border-radius: 999px;
    font-weight: 700;
    min-width: 180px;
    border-width: 1px;
}
.eclat-hero-btn-primary {
    background: #ffffff;
    color: #241d16;
    border-color: #ffffff;
    box-shadow: 0 12px 30px rgba(0,0,0,.12);
}
.eclat-hero-btn-primary:hover {
    background: #f6efe6;
    color: #241d16;
    border-color: #f6efe6;
}
.eclat-hero-btn-secondary {
    background: rgba(255,255,255,.14);
    color: #ffffff;
    border-color: rgba(255,255,255,.72);
    backdrop-filter: blur(8px);
}
.eclat-hero-btn-secondary:hover {
    background: rgba(255,255,255,.24);
    color: #ffffff;
    border-color: #ffffff;
}
.eclat-review-hero .btn:focus-visible {
    box-shadow: 0 0 0 .22rem rgba(255,255,255,.28);
}
/* Compact hero statistics: do not affect cards on other pages */
.eclat-review-hero .eclat-stat-grid {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: .8rem;
    width: min(100%, 920px);
    margin-top: 1.35rem;
}
.eclat-review-hero .eclat-stat-card {
    box-sizing: border-box;
    height: 88px;
    min-height: 0;
    max-height: 88px;
    padding: .85rem 1.05rem;
    border-radius: 16px;
    /* Soft ivory glass: photography shows through without hurting projector legibility. */
    background: linear-gradient(135deg, rgba(255, 255, 255, .83), rgba(250, 245, 235, .77));
    border: 1px solid rgba(255, 247, 227, .86);
    box-shadow: inset 0 1px 0 rgba(255, 255, 255, .82), 0 10px 24px rgba(42, 32, 24, .13);
    -webkit-backdrop-filter: blur(16px) saturate(135%);
    backdrop-filter: blur(16px) saturate(135%);
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    gap: .7rem;
}
.eclat-review-hero .eclat-stat-label {
    color: #695744;
    text-transform: uppercase;
    letter-spacing: .075em;
    font-size: .69rem;
    font-weight: 800;
    line-height: 1.4;
    margin: 0;
}
.eclat-review-hero .eclat-stat-value {
    color: #30241c;
    font-family: Georgia, "Times New Roman", serif;
    font-size: clamp(1.45rem, 2.4vw, 1.9rem);
    font-weight: 700;
    line-height: 1;
    white-space: nowrap;
    margin: 0;
}
.eclat-floating-filter {
    margin-top: -2rem;
    position: relative;
    z-index: 3;
}
.eclat-panel {
    background: #fff;
    border-radius: 26px;
    border: 1px solid rgba(191, 161, 107, .2);
    box-shadow: 0 18px 40px rgba(44, 34, 24, 0.08);
}
.eclat-filter-grid {
    display: grid;
    grid-template-columns: minmax(0, 1fr) auto;
    gap: 1rem;
    align-items: end;
}
.eclat-filter-title {
    font-weight: 700;
    color: #2f261c;
}
.eclat-filter-submit {
    background: #b99a68;
    border-color: #b99a68;
    color: #fff;
}
.eclat-filter-submit:hover {
    background: #9a7b49;
    border-color: #9a7b49;
    color: #fff;
}
.eclat-filter-reset {
    border-color: #d9cab4;
    color: #5d4c37;
    background: #fff;
}
.eclat-filter-reset:hover {
    background: #f8f1e7;
    border-color: #c9b190;
    color: #4b3d2d;
}
.eclat-review-btn-dark {
    background: #b99a68;
    border-color: #b99a68;
    color: #fff;
}
.eclat-review-btn-dark:hover {
    background: #947448;
    border-color: #947448;
    color: #fff;
}
.eclat-reviews-wrap {
    margin-top: 2rem;
}
.eclat-review-section-title {
    font-size: clamp(2.15rem, 3vw, 3rem);
    line-height: 1.05;
    color: #251f18;
}
/* Review cards: trust-focused and text-first, without repeated stock photographs. */
.eclat-review-card {
    background: #fff;
    border: 1px solid #e9dfd2;
    border-radius: 22px;
    box-shadow: 0 12px 28px rgba(55, 41, 28, .07);
    padding: 24px 26px 22px;
    min-width: 0;
    height: 100%;
    transition: border-color .2s ease, box-shadow .2s ease;
}
.eclat-review-card:hover {
    border-color: #d8c6a7;
    box-shadow: 0 15px 34px rgba(55, 41, 28, .09);
}
.eclat-review-card-head {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 16px;
}
.eclat-review-vendor-identity {
    display: flex;
    align-items: center;
    min-width: 0;
}
.eclat-review-vendor-name {
    color: #2e271f;
    font-size: 1.01rem;
    font-weight: 750;
    line-height: 1.35;
    overflow-wrap: anywhere;
}
.eclat-review-vendor-category {
    font-size: .83rem;
    color: #80705d;
    margin-top: 3px;
}
.eclat-review-status {
    padding: 7px 12px;
    border-radius: 999px;
    font-weight: 700;
    font-size: .75rem;
    letter-spacing: 0;
    white-space: nowrap;
    color: #286145;
    background: #e7f4ec;
    border: 1px solid #c8e5d3;
}
.eclat-review-status--pending { background:#fff4d9; color:#795918; border-color:#efdba9; }
.eclat-review-status--rejected { background:#fce8e7; color:#923936; border-color:#f1cdcb; }
.eclat-review-divider { height: 1px; background: #eee5d8; margin: 20px 0 17px; }
.eclat-review-rating-row { display:flex; align-items:center; gap: 10px; margin-bottom:12px; }
.eclat-review-stars { font-size: 1.23rem; letter-spacing: 1px; color:#b68432; white-space:nowrap; line-height: 1; }
.eclat-review-stars .is-empty { color: #d7d0c5; }
.eclat-review-score { font-size:.89rem; font-weight:750; color:#675a4e; }
.eclat-review-title {
    font-family: Georgia, "Times New Roman", serif;
    font-size: clamp(1.55rem, 2.2vw, 1.95rem);
    line-height: 1.17;
    color:#2e251d;
    overflow-wrap: anywhere;
    margin:0 0 13px;
}
.eclat-review-feedback {
    border-left:3px solid #c7a675;
    background:#fcf9f4;
    padding:13px 16px;
    border-radius:0 10px 10px 0;
    margin:0 0 18px;
    font-size:.97rem;
    line-height:1.68;
    min-height:0;
    color:#504337;
    white-space:pre-line;
    overflow-wrap:anywhere;
}
.eclat-review-footer {
    padding-top:16px;
    border-top:1px solid #eee5d8;
    display:flex;
    gap:12px 18px;
    flex-wrap:wrap;
    align-items:center;
    justify-content:space-between;
    color:#7b6e61;
    font-size:.84rem;
}
.eclat-review-footer-meta { display:flex; align-items:center; gap:7px; flex-wrap:wrap; }
.eclat-review-footer-meta i { color:#9a825d; }
.eclat-review-note { color:#5c7663; font-weight:600; font-size:.83rem; }
.eclat-review-note i { color:#47795b; }
.eclat-reviews-wrap .eclat-review-grid--single { justify-content:center; }
.eclat-reviews-wrap .eclat-review-grid--single > .col-12 { max-width:900px; }
@media (max-width: 575px) {
    .eclat-review-card { padding:18px 17px; }
    .eclat-review-card-head { gap:10px; }
    .eclat-review-vendor-name {font-size:.94rem;}
    .eclat-review-title { font-size:1.55rem; }
    .eclat-review-status {padding:6px 9px;font-size:.7rem;}
}
.eclat-empty-box {
    text-align: center;
    padding: 3rem 1.25rem;
}
.eclat-empty-icon {
    width: 84px;
    height: 84px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    background: #f8f2ea;
    color: #a17c38;
    font-size: 2rem;
    margin-bottom: 1rem;
}
.eclat-inline-edit {
    background: #fcf8f3;
    border: 1px solid #efe3d3;
    border-radius: 20px;
    padding: 1rem;
}
.eclat-inline-edit summary {
    cursor: pointer;
    font-weight: 700;
    color: #584832;
}
.eclat-inline-edit .form-label {
    font-size: .9rem;
    font-weight: 700;
    color: #554637;
}
@media (max-width: 991px) {
    .eclat-review-hero { padding: 2rem 1.35rem; }
    .eclat-review-hero .eclat-stat-grid { width: 100%; }
    .eclat-filter-grid { grid-template-columns: 1fr; }
}
@media (max-width: 650px) {
    .eclat-review-hero .eclat-stat-grid { grid-template-columns: 1fr; gap: .55rem; }
    .eclat-review-hero .eclat-stat-card { height: 64px; min-height: 0; max-height: 64px; padding: .7rem .85rem; }
    .eclat-review-hero h1 { font-size: clamp(2.1rem, 9vw, 3rem); }
}
@media (prefers-reduced-motion: reduce) {
    .eclat-review-card {transition:none}
}

</style>

<section class="eclat-review-hero mb-4">
    <div class="position-relative" style="z-index: 1;">
        <div class="eclat-review-eyebrow">Éclat / Reviews & ratings</div>
        <h1>Love notes &amp; honest stories.</h1>
        <p>Explore genuine wedding experiences, celebrate trusted vendors and help future couples choose with confidence.</p>

        <div class="eclat-review-actions d-flex flex-wrap gap-3 mt-4">
            <c:if test="${sessionScope.currentUser.role == 'CUSTOMER'}">
                <a href="${pageContext.request.contextPath}/reviews/new" class="btn eclat-hero-btn-primary shadow-sm">
                    <i class="bi bi-pencil-square me-2"></i>Write a review
                </a>
            </c:if>
            <a href="#review-grid" class="btn eclat-hero-btn-secondary">
                <i class="bi bi-stars me-2"></i>Browse stories
            </a>
        </div>

        <div class="eclat-stat-grid">
            <div class="eclat-stat-card">
                <div class="eclat-stat-label">Community reviews</div>
                <div class="eclat-stat-value">${approvedReviewCount}</div>
            </div>
            <div class="eclat-stat-card">
                <div class="eclat-stat-label">Average rating</div>
                <div class="eclat-stat-value"><fmt:formatNumber value="${approvedAverageRating}" maxFractionDigits="1" minFractionDigits="1" /></div>
            </div>
            <div class="eclat-stat-card">
                <div class="eclat-stat-label">Five-star moments</div>
                <div class="eclat-stat-value">${approvedFiveStarCount}</div>
            </div>
        </div>
    </div>
</section>

<section class="eclat-floating-filter mb-4">
    <div class="eclat-panel p-4 p-lg-4">
        <form action="${pageContext.request.contextPath}/reviews" method="get" class="eclat-filter-grid">
            <div>
                <div class="eclat-filter-title mb-2">Refine the stories</div>
                <select name="vendorId" class="form-select form-select-lg">
                    <option value="">All Vendors</option>
                    <c:forEach var="v" items="${vendors}">
                        <option value="${v.vendorId}" ${selectedVendorId == v.vendorId ? 'selected' : ''}>
                            ${v.vendorName} (${v.category.displayName})
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="d-flex gap-2 flex-wrap">
                <button type="submit" class="btn eclat-filter-submit px-4">
                    <i class="bi bi-funnel me-2"></i>Filter
                </button>
                <a href="${pageContext.request.contextPath}/reviews" class="btn eclat-filter-reset px-4">Reset</a>
            </div>
        </form>
    </div>
</section>

<section class="eclat-reviews-wrap" id="review-grid">
    <div class="d-flex justify-content-between align-items-end flex-wrap gap-3 mb-4">
        <div>
            <div class="eclat-review-section-label text-muted mb-2">Community voices</div>
            <h2 class="eclat-review-section-title mb-0">Experiences that matter.</h2>
        </div>
        <div class="text-muted small">Showing ${visibleReviewCount} review<c:if test="${visibleReviewCount != 1}">s</c:if>.</div>
    </div>

    <div class="row g-4 eclat-review-grid ${visibleReviewCount == 1 ? 'eclat-review-grid--single' : ''}">
        <c:forEach var="r" items="${reviews}">
            <c:set var="vendorName" value="${r.vendorId}" />
            <c:set var="vendorCategory" value="Wedding service" />
            <c:forEach var="v" items="${vendors}">
                <c:if test="${v.vendorId == r.vendorId}">
                    <c:set var="vendorName" value="${v.vendorName}" />
                    <c:set var="vendorCategory" value="${v.category.displayName}" />
                </c:if>
            </c:forEach>

            <div class="${visibleReviewCount == 1 ? 'col-12' : 'col-lg-6'}">
                <article class="eclat-review-card">
                    <div class="eclat-review-card-head">
                        <div class="eclat-review-vendor-identity">
                            <div>
                                <div class="eclat-review-vendor-name"><c:out value="${vendorName}" /></div>
                                <div class="eclat-review-vendor-category"><c:out value="${vendorCategory}" /></div>
                            </div>
                        </div>
                        <c:if test="${sessionScope.currentUser != null}">
                            <c:choose>
                                <c:when test="${r.status == 'APPROVED'}"><span class="eclat-review-status"><i class="bi bi-patch-check-fill me-1"></i>Approved</span></c:when>
                                <c:when test="${r.status == 'PENDING_MODERATION'}"><span class="eclat-review-status eclat-review-status--pending">Pending review</span></c:when>
                                <c:otherwise><span class="eclat-review-status eclat-review-status--rejected">Rejected</span></c:otherwise>
                            </c:choose>
                        </c:if>
                    </div>

                    <div class="eclat-review-divider" aria-hidden="true"></div>

                    <div class="eclat-review-rating-row" aria-label="Rating: ${r.rating} out of 5">
                        <span class="eclat-review-stars" aria-hidden="true"><c:forEach var="star" begin="1" end="5"><c:choose><c:when test="${star <= r.rating}">&#9733;</c:when><c:otherwise><span class="is-empty">&#9733;</span></c:otherwise></c:choose></c:forEach></span>
                        <span class="eclat-review-score">${r.rating}.0 / 5</span>
                    </div>
                    <h3 class="eclat-review-title"><c:out value="${r.title}" /></h3>
                    <p class="eclat-review-feedback"><c:out value="${r.feedback}" /></p>

                    <div class="eclat-review-footer">
                        <span class="eclat-review-footer-meta"><i class="bi bi-calendar3" aria-hidden="true"></i><c:out value="${r.submittedAt}" /></span>
                        <span class="eclat-review-note"><i class="bi bi-patch-check-fill me-1" aria-hidden="true"></i>Review from a completed booking</span>
                        <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                            <form action="${pageContext.request.contextPath}/admin/reviews/delete/${r.reviewId}" method="post" class="m-0" onsubmit="return confirm('Delete this review permanently?');">
                                <button type="submit" class="btn btn-sm btn-outline-danger">Delete</button>
                            </form>
                        </c:if>
                    </div>

                    <c:if test="${sessionScope.currentUser.role == 'CUSTOMER' && r.customerId == sessionScope.currentUser.userId && r.status == 'PENDING_MODERATION'}">
                        <details class="eclat-inline-edit mt-3">
                            <summary>Edit your pending review</summary>
                            <form action="${pageContext.request.contextPath}/reviews/update/${r.reviewId}" method="post" class="mt-3">
                                <div class="row g-3">
                                    <div class="col-md-4">
                                        <label class="form-label">Rating</label>
                                        <select name="rating" class="form-select" required>
                                            <c:forEach var="score" begin="1" end="5">
                                                <option value="${score}" ${score == r.rating ? 'selected' : ''}>${score} / 5</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="col-md-8">
                                        <label class="form-label">Title</label>
                                        <input type="text" name="title" class="form-control" value="${fn:escapeXml(r.title)}" maxlength="100" required>
                                    </div>
                                    <div class="col-12">
                                        <label class="form-label">Feedback</label>
                                        <textarea name="feedback" class="form-control" rows="4" minlength="10" maxlength="1500" required><c:out value="${r.feedback}" /></textarea>
                                    </div>
                                    <div class="col-12">
                                        <button type="submit" class="btn eclat-review-btn-dark btn-sm px-4">Save changes</button>
                                    </div>
                                </div>
                            </form>
                        </details>
                    </c:if>
                </article>
            </div>
        </c:forEach>

        <c:if test="${empty reviews}">
            <div class="col-12">
                <div class="eclat-panel eclat-empty-box">
                    <div class="eclat-empty-icon"><i class="bi bi-chat-heart"></i></div>
                    <h3 class="eclat-review-title mb-2" style="font-size:2rem;">No reviews yet</h3>
                    <p class="text-muted mb-4">Once couples start sharing their stories, beautiful feedback will appear here.</p>
                    <c:if test="${sessionScope.currentUser.role == 'CUSTOMER'}">
                        <a href="${pageContext.request.contextPath}/reviews/new" class="btn eclat-filter-submit px-4">Be the first to write a review</a>
                    </c:if>
                </div>
            </div>
        </c:if>
    </div>
</section>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
