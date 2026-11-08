<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<div class="row justify-content-center">
    <div class="col-md-7">
        <div class="card card-custom p-4 shadow">
            <h4 class="fw-bold mb-3" style="color: #8d6a22;">
                <i class="bi bi-star-fill text-warning"></i> Submit Verified Vendor Review
            </h4>
            <p class="text-secondary small mb-4">
                Share your authentic wedding experience. Only customers with completed bookings are eligible.
            </p>

            <form action="${pageContext.request.contextPath}/reviews/new" method="post">
                <div class="row g-3">
                    <div class="col-12">
                        <label class="form-label fw-semibold">Select Completed Booking *</label>
                        <select name="bookingId" id="bookingSelect" class="form-select" required onchange="updateVendorField()">
                            <option value="">-- Choose Completed Booking --</option>
                            <c:forEach var="b" items="${eligibleBookings}">
                                <option value="${b.bookingId}" data-vendor="${b.vendorId}"
                                    ${preselectedBookingId != null && preselectedBookingId == b.bookingId ? 'selected' : ''}>
                                    Booking #${b.bookingId} - Vendor: ${b.vendorId} (Date: ${b.bookingDate})
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <input type="hidden" name="vendorId" id="vendorIdInput" value="${preselectedVendorId}">

                    <div class="col-12">
                        <label class="form-label fw-semibold">Star Rating *</label>
                        <select name="rating" class="form-select text-warning fw-bold fs-5" required>
                            <option value="5" selected>★★★★★ - 5 Stars (Exceptional)</option>
                            <option value="4">★★★★☆ - 4 Stars (Very Good)</option>
                            <option value="3">★★★☆☆ - 3 Stars (Average)</option>
                            <option value="2">★★☆☆☆ - 2 Stars (Below Average)</option>
                            <option value="1">★☆☆☆☆ - 1 Star (Poor Experience)</option>
                        </select>
                    </div>

                    <div class="col-12">
                        <label class="form-label fw-semibold">Review Title *</label>
                        <input type="text" name="title" class="form-control" placeholder="e.g. Unforgettable photography on our big day!" required>
                    </div>

                    <div class="col-12">
                        <label class="form-label fw-semibold">Detailed Feedback Comments * (Min 10 characters)</label>
                        <textarea name="feedback" class="form-control" rows="4" placeholder="Detail the vendor's punctuality, professionalism, service quality, and teamwork..." minlength="10" required></textarea>
                    </div>

                    <div class="col-12 mt-4 d-flex justify-content-between">
                        <a href="${pageContext.request.contextPath}/reviews" class="btn btn-outline-secondary">Cancel</a>
                        <button type="submit" class="btn btn-purple px-4 fw-bold">Submit for Verification</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
function updateVendorField() {
    var select = document.getElementById("bookingSelect");
    var opt = select.options[select.selectedIndex];
    var vendorId = opt.getAttribute("data-vendor");
    if (vendorId) {
        document.getElementById("vendorIdInput").value = vendorId;
    }
}
window.addEventListener("DOMContentLoaded", updateVendorField);
</script>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />

