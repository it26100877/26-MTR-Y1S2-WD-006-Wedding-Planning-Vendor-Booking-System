<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<div class="row justify-content-center">
    <div class="col-md-8">
        <div class="card card-custom p-4 shadow">
            <h4 class="fw-bold mb-4" style="color: #8d6a22;">
                <i class="bi bi-shop"></i> ${isEdit ? "Edit Vendor Details" : "Register New Vendor"}
            </h4>

            <form action="${isEdit ? pageContext.request.contextPath.concat('/vendors/edit/').concat(vendor.vendorId) : pageContext.request.contextPath.concat('/vendors/new')}" method="post">
                <div class="row g-3">
                    <div class="col-md-8">
                        <label class="form-label fw-semibold">Vendor Business Name *</label>
                        <input type="text" name="vendorName" class="form-control" placeholder="e.g. Dream Moments Photography" value="${vendor != null ? vendor.vendorName : ''}" required>
                    </div>

                    <div class="col-md-4">
                        <label class="form-label fw-semibold">Service Category *</label>
                        <select name="category" class="form-select" required>
                            <c:forEach var="cat" items="${categories}">
                                <option value="${cat.name()}" ${vendor != null && vendor.category == cat ? 'selected' : ''}>${cat.displayName}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Contact Person *</label>
                        <input type="text" name="contactPerson" class="form-control" placeholder="Contact person name" value="${vendor != null ? vendor.contactPerson : ''}" required>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Starting / Base Price (USD) *</label>
                        <div class="input-group">
                            <span class="input-group-text">$</span>
                            <input type="number" step="0.01" name="basePrice" class="form-control" placeholder="1200.00" value="${vendor != null ? vendor.basePrice : ''}" required>
                        </div>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Email Address *</label>
                        <input type="email" name="email" class="form-control" placeholder="vendor@example.com" value="${vendor != null ? vendor.email : ''}" required>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Phone Number *</label>
                        <input type="text" name="phone" class="form-control" placeholder="+94712345678" value="${vendor != null ? vendor.phone : ''}" required>
                    </div>

                    <div class="col-12">
                        <label class="form-label fw-semibold">Business Address / City *</label>
                        <input type="text" name="address" class="form-control" placeholder="Colombo 03, Sri Lanka" value="${vendor != null ? vendor.address : ''}" required>
                    </div>

                    <div class="col-12">
                        <label class="form-label fw-semibold">Service Description &amp; Packages Offered *</label>
                        <textarea name="serviceDescription" class="form-control" rows="4" placeholder="Detail the services, deliverables, equipment, package options, and terms..." required>${vendor != null ? vendor.serviceDescription : ''}</textarea>
                    </div>

                    <div class="col-12 mt-4 d-flex justify-content-between">
                        <a href="${pageContext.request.contextPath}/vendors" class="btn btn-outline-secondary">Cancel</a>
                        <button type="submit" class="btn btn-purple px-4 fw-bold">${isEdit ? "Update Vendor" : "Register Vendor"}</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />

