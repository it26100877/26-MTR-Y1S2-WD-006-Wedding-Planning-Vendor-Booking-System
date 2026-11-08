<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />
<div class="booking-studio">
    <section class="bk-hero mb-4"><div class="bk-eyebrow">ECLAT / NEW REQUEST</div><h1>Reserve Your Perfect Vendor</h1><p>Choose the right team, select your special date and submit a personalized booking request.</p></section>
    <div class="row g-4">
        <div class="col-xl-8"><div class="bk-panel mb-4">
            <div class="mb-4"><div class="bk-eyebrow">STEP 01 — YOUR EVENT</div><h2 class="bk-heading">Booking Details</h2><p class="bk-subtitle">All fields marked * are required.</p></div>
            <form action="${pageContext.request.contextPath}/bookings/new" method="post" id="bookingRequestForm">
                <div class="row g-3">
                    <div class="col-md-6"><label class="form-label" for="selectVendor">APPROVED VENDOR *</label><select class="form-select" name="vendorId" id="selectVendor" required><option value="">Choose a vendor</option><c:forEach items="${vendors}" var="v"><option value="${v.vendorId}" data-price="${v.basePrice}" ${selectedVendor != null && selectedVendor.vendorId == v.vendorId ? 'selected' : ''}><c:out value="${v.vendorName}"/> — LKR ${v.basePrice}</option></c:forEach></select><small class="text-secondary">Only approved vendors appear here.</small></div>
                    <div class="col-md-6"><label class="form-label" for="selectPlan">WEDDING PLAN *</label><select class="form-select" name="planId" id="selectPlan" required><option value="">Choose a plan</option><c:forEach items="${plans}" var="p"><option value="${p.planId}"><c:out value="${p.planName}"/> (${p.weddingDate})</option></c:forEach></select></div>
                    <div class="col-md-6"><label class="form-label" for="bookingDate">EVENT DATE *</label><input class="form-control" type="date" name="bookingDate" id="bookingDate" min="${earliestDate}" value="<c:out value='${requestedDate}'/>" required><small class="text-secondary">Future dates only; confirmed vendor dates are blocked.</small></div>
                    <div class="col-md-6"><label class="form-label" for="agreedAmount">AGREED SERVICE FEE (LKR) *</label><div class="input-group"><span class="input-group-text">LKR</span><input class="form-control" type="number" min="0.01" step="0.01" name="agreedAmount" id="agreedAmount" value="${selectedVendor != null ? selectedVendor.basePrice : ''}" required></div></div>
                    <div class="col-12"><label class="form-label" for="serviceDetails">SERVICE DETAILS *</label><textarea class="form-control" id="serviceDetails" name="serviceDetails" rows="4" maxlength="1500" placeholder="Describe venue, hours, deliverables, guest count and expectations" required></textarea></div>
                    <div class="col-12"><label class="form-label" for="specialNotes">SPECIAL NOTES (OPTIONAL)</label><textarea class="form-control" id="specialNotes" name="notes" rows="3" maxlength="1000" placeholder="Any special instructions for this booking?"></textarea></div>
                    <div class="col-12 mt-4 d-flex flex-wrap gap-2 align-items-center"><button class="bk-btn" type="submit"><i class="bi bi-send"></i> Submit Request</button><a class="bk-btn-ghost" href="${pageContext.request.contextPath}/bookings">Cancel</a></div>
                </div>
            </form>
        </div></div>
        <div class="col-xl-4"><div class="bk-panel mb-4"><div class="bk-eyebrow">MAKE AN INFORMED CHOICE</div><h2 class="bk-heading mb-2">Before you book</h2><p class="bk-subtitle">Check which dates are still open, then send your request.</p><a class="bk-btn-ghost w-100 mt-3" href="${pageContext.request.contextPath}/bookings/calendar"><i class="bi bi-calendar-check"></i> Check Availability</a><hr><div class="small text-secondary"><i class="bi bi-shield-check text-success me-1"></i> Your request begins as <strong>Pending</strong> and can be edited or cancelled until confirmed. The administrator will review your request.</div></div></div>
    </div>
</div>
<script>
(function(){
 var vendor=document.getElementById('selectVendor'), amount=document.getElementById('agreedAmount');
 if(vendor&&amount){vendor.addEventListener('change',function(){var opt=vendor.options[vendor.selectedIndex];if(opt&&opt.dataset.price)amount.value=opt.dataset.price;});}
})();
</script>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
