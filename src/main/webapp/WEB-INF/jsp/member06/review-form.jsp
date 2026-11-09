<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<style>
/* Éclat review form — compact projector-friendly typography and photo sidebar */
.eclat-review-form-shell {
    display: grid;
    grid-template-columns: minmax(0, 1.18fr) minmax(310px, .82fr);
    gap: 1.35rem;
    align-items: stretch;
}
.eclat-review-form-panel, .eclat-review-side-panel {
    border-radius: 21px;
    border: 1px solid #e9dccc;
    background: #fff;
    box-shadow: 0 12px 28px rgba(44, 34, 24, .07);
    overflow: hidden;
}
.eclat-review-form-panel { padding: clamp(1.3rem, 2.8vw, 2.2rem); }
.eclat-review-form-panel .eclat-form-title {
    font-family: Georgia, "Times New Roman", serif;
    font-size: clamp(1.9rem, 2.65vw, 2.45rem);
    line-height: 1.16;
    font-weight: 700;
    letter-spacing: -.025em;
    color: #29221b;
    max-width: 760px;
}
.eclat-form-eyebrow {
    text-transform: uppercase;
    letter-spacing: .16em;
    font-size: .7rem;
    font-weight: 800;
    color: #9a7640;
}
.eclat-review-form-intro { color: #6c5e50; font-size: .93rem; line-height: 1.6; }
.eclat-review-form-panel .form-label { color: #4a3b2c; font-size: .87rem; margin-bottom: .4rem; }
.eclat-review-form-panel .form-control, .eclat-review-form-panel .form-select {
    font-size: .93rem;
    border-radius: 8px !important;
    min-height: 46px;
    padding: .65rem .82rem;
}
.eclat-review-form-panel textarea.form-control { min-height: 134px; }
.eclat-review-form-panel .btn { padding: .68rem 1.3rem; font-weight: 700; }
.eclat-review-submit {
    background: #896638; border: 1px solid #896638; color: white;
}
.eclat-review-submit:hover, .eclat-review-submit:focus { background: #74532c; border-color: #74532c; color: white; }
.eclat-review-side-panel { background: #f9f3e9; }
.eclat-review-side-photo {
    position: relative;
    min-height: 215px;
    background: linear-gradient(180deg, rgba(29,23,19,.04), rgba(29,23,19,.38)),
        url('https://images.unsplash.com/photo-1520854221256-17451cc331bf?auto=format&fit=crop&w=950&q=85') center 48%/cover no-repeat;
}
.eclat-review-side-photo span {
    position: absolute; bottom: 17px; left: 24px;
    color: #fff; text-shadow: 0 1px 6px rgba(0,0,0,.45);
    font-weight: 800; font-size: .68rem; letter-spacing: .17em;
}
.eclat-review-side-content { padding: 1.65rem 1.85rem 1.8rem; }
.eclat-review-side-panel .eclat-form-title {
    font-family: Georgia, "Times New Roman", serif;
    font-size: clamp(1.65rem, 2vw, 2.05rem);
    line-height: 1.17;
    font-weight: 700;
    letter-spacing: -.02em;
    color: #30251c;
    margin: .45rem 0 .8rem;
}
.eclat-review-side-panel p { color: #6c5d4e; font-size: .91rem; line-height: 1.6; }
.eclat-help-list { list-style: none; padding: 0; margin: 1.1rem 0 0; }
.eclat-help-list li { display:flex; gap:.78rem; align-items:flex-start; margin-bottom: .83rem; color:#544636; font-size:.88rem; line-height:1.5; }
.eclat-help-list i {
    flex:0 0 29px; width:29px; height:29px;
    display:inline-flex; align-items:center; justify-content:center;
    border-radius:50%; background:#ede1cf; color:#765a34;
}
@media (max-width: 991px) {
    .eclat-review-form-shell { grid-template-columns: 1fr; }
    .eclat-review-side-photo { min-height: 180px; }
}
@media (max-width: 575px) {
    .eclat-review-form-panel { padding: 1.25rem; }
    .eclat-review-side-content { padding: 1.25rem; }
    .eclat-review-form-panel .eclat-form-title { font-size: 1.9rem; }
}
</style>

<div class="eclat-review-form-shell">
    <section class="eclat-review-form-panel">
        <div class="eclat-form-eyebrow mb-2">Éclat / Share your experience</div>
        <h1 class="eclat-form-title mb-3">Tell future couples what made the day special.</h1>
        <p class="eclat-review-form-intro mb-4">Your review helps other couples choose trusted vendors with confidence. Only completed bookings can be reviewed.</p>

        <form action="${pageContext.request.contextPath}/reviews/new" method="post">
            <div class="row g-3">
                <div class="col-12">
                    <label class="form-label fw-semibold">Completed booking</label>
                    <select name="bookingId" id="bookingSelect" class="form-select form-select-lg" required onchange="updateVendorField()">
                        <option value="">Choose a completed booking</option>
                        <c:forEach var="b" items="${eligibleBookings}">
                            <option value="${b.bookingId}" data-vendor="${b.vendorId}"
                                ${preselectedBookingId != null && preselectedBookingId == b.bookingId ? 'selected' : ''}>
                                Booking #${b.bookingId} · Vendor ${b.vendorId} · ${b.bookingDate}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <input type="hidden" name="vendorId" id="vendorIdInput" value="${preselectedVendorId}">

                <div class="col-md-4">
                    <label class="form-label fw-semibold">Rating</label>
                    <select name="rating" class="form-select form-select-lg" required>
                        <option value="5" selected>5 / 5 — Exceptional</option>
                        <option value="4">4 / 5 — Very good</option>
                        <option value="3">3 / 5 — Good</option>
                        <option value="2">2 / 5 — Fair</option>
                        <option value="1">1 / 5 — Poor</option>
                    </select>
                </div>

                <div class="col-md-8">
                    <label class="form-label fw-semibold">Review title</label>
                    <input type="text" name="title" class="form-control form-control-lg" placeholder="Example: Beautiful décor and wonderful service" maxlength="100" required>
                </div>

                <div class="col-12">
                    <label class="form-label fw-semibold">Your feedback</label>
                    <textarea name="feedback" class="form-control" rows="6" placeholder="Share a few details about the service quality, professionalism, communication and how the vendor contributed to your celebration..." minlength="10" maxlength="1500" required></textarea>
                </div>

                <div class="col-12 d-flex justify-content-between flex-wrap gap-2 mt-2">
                    <a href="${pageContext.request.contextPath}/reviews" class="btn btn-outline-secondary px-4">Cancel</a>
                    <button type="submit" class="btn eclat-review-submit px-4 fw-bold">Submit review</button>
                </div>
            </div>
        </form>
    </section>

    <aside class="eclat-review-side-panel">
        <div class="eclat-review-side-photo" role="img" aria-label="Beautiful wedding celebration">
            <span>SHARE YOUR WEDDING STORY</span>
        </div>
        <div class="eclat-review-side-content">
            <div class="eclat-form-eyebrow">Helpful review tips</div>
            <h2 class="eclat-form-title">A little detail means a lot.</h2>
            <p>Help the next couple understand what made your vendor experience memorable.</p>
            <ul class="eclat-help-list">
                <li><i class="bi bi-patch-check"></i><span>Reviews require a completed booking.</span></li>
                <li><i class="bi bi-chat-heart"></i><span>Share what stood out: quality, timing and communication.</span></li>
                <li><i class="bi bi-stars"></i><span>Reviews appear publicly after admin approval.</span></li>
            </ul>
        </div>
    </aside>
</div>

<script>
function updateVendorField() {
    var select = document.getElementById("bookingSelect");
    var opt = select.options[select.selectedIndex];
    var vendorId = opt ? opt.getAttribute("data-vendor") : "";
    document.getElementById("vendorIdInput").value = vendorId || "";
}
window.addEventListener("DOMContentLoaded", updateVendorField);
</script>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
