<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<div class="row justify-content-center">
    <div class="col-md-8">
        <div class="card card-custom p-4 shadow">
            <h4 class="fw-bold mb-4" style="color: #8d6a22;">
                <i class="bi bi-bookmark-plus"></i> Request Vendor Booking
            </h4>

            <form action="${pageContext.request.contextPath}/bookings/new" method="post">
                <div class="row g-3">
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Select Approved Vendor *</label>
                        <select name="vendorId" class="form-select" required>
                            <option value="">-- Choose Vendor --</option>
                            <c:forEach var="v" items="${vendors}">
                                <option value="${v.vendorId}" ${selectedVendor != null && selectedVendor.vendorId == v.vendorId ? 'selected' : ''}>
                                    ${v.vendorName} (${v.category.displayName}) - Starts at LKR ${v.basePrice}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Attach to Wedding Plan *</label>
                        <select name="planId" class="form-select" required>
                            <option value="">-- Choose Wedding Plan --</option>
                            <c:forEach var="p" items="${plans}">
                                <option value="${p.planId}">${p.planName} (${p.weddingDate})</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Event / Booking Date *</label>
                        <input type="date" name="bookingDate" class="form-control" required>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Agreed Fee Amount (USD) *</label>
                        <div class="input-group">
                            <span class="input-group-text">$</span>
                            <input type="number" step="0.01" name="agreedAmount" class="form-control" placeholder="1200.00" value="${selectedVendor != null ? selectedVendor.basePrice : ''}" required>
                        </div>
                    </div>

                    <div class="col-12">
                        <label class="form-label fw-semibold">Service Details &amp; Custom Requirements *</label>
                        <textarea name="serviceDetails" class="form-control" rows="3" placeholder="Outline hours of coverage, deliverables, location details, etc." required></textarea>
                    </div>

                    <div class="col-12">
                        <label class="form-label fw-semibold">Notes / Special Instructions</label>
                        <input type="text" name="notes" class="form-control" placeholder="Optional notes for the vendor...">
                    </div>

                    <div class="col-12 mt-4 d-flex justify-content-between">
                        <a href="${pageContext.request.contextPath}/bookings" class="btn btn-outline-secondary">Cancel</a>
                        <button type="submit" class="btn btn-purple px-4 fw-bold">Submit Booking Request</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />

