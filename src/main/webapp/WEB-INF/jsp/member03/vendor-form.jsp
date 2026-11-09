```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<style>
.vendor-form-page {
    max-width: 900px;
    margin: 0 auto;
}
.vendor-form-hero {
    background: linear-gradient(120deg, #302820, #65513a);
    color: white;
    padding: 35px;
    border-radius: 14px;
    margin-bottom: 25px;
}
.vendor-form-panel {
    background: white;
    border: 1px solid #eee5d9;
    border-radius: 12px;
    padding: 30px;
}
.vendor-form-hero h1 {
    font-family: 'Playfair Display', serif;
}
.vendor-form-label {
    font-weight: 600;
    color: #51483b;
}
.vendor-form-help {
    color: #81796d;
    font-size: .85rem;
}
.vendor-form-submit {
    background: #302820;
    color: white;
    border: 0;
    padding: 12px 25px;
}
.vendor-form-submit:hover {
    background: #80643d;
    color: white;
}
</style>

<div class="vendor-form-page">

    <section class="vendor-form-hero">
        <div class="small text-uppercase mb-2" style="letter-spacing:3px;color:#e0c99f;">
            ÉCLAT · VENDOR MANAGEMENT
        </div>

        <h1>
            <c:choose>
                <c:when test="${isEdit}">Edit Vendor Details</c:when>
                <c:otherwise>Register Your Business</c:otherwise>
            </c:choose>
        </h1>

        <p class="mb-0 text-white-50">
            <c:choose>
                <c:when test="${isEdit}">
                    Update the vendor information below.
                </c:when>
                <c:otherwise>
                    Join our wedding marketplace and connect with customers.
                </c:otherwise>
            </c:choose>
        </p>
    </section>

    <section class="vendor-form-panel">

        <form method="post"
              action="${pageContext.request.contextPath}<c:choose><c:when test='${isEdit}'>/vendors/edit/${vendor.vendorId}</c:when><c:otherwise>/vendors/new</c:otherwise></c:choose>">

            <div class="mb-4">
                <h2 class="h5">Business Information</h2>
                <p class="vendor-form-help">Enter accurate details about your wedding service.</p>
            </div>

            <div class="row g-3">

                <div class="col-md-6">
                    <label class="form-label vendor-form-label" for="vendorName">
                        Business / Vendor Name *
                    </label>

                    <input type="text"
                           class="form-control"
                           id="vendorName"
                           name="vendorName"
                           maxlength="100"
                           required
                           value="<c:out value='${vendor.vendorName}'/>"
                           placeholder="Enter business name">
                </div>

                <div class="col-md-6">
                    <label class="form-label vendor-form-label" for="category">
                        Service Category *
                    </label>

                    <select class="form-select" id="category" name="category" required>
                        <option value="">Select category</option>

                        <c:forEach items="${categories}" var="cat">
                            <option value="${cat.name()}"
                                ${vendor.category == cat ? 'selected' : ''}>
                                <c:out value="${cat.displayName}"/>
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="col-md-6">
                    <label class="form-label vendor-form-label" for="contactPerson">
                        Contact Person *
                    </label>

                    <input type="text"
                           class="form-control"
                           id="contactPerson"
                           name="contactPerson"
                           maxlength="100"
                           required
                           value="<c:out value='${vendor.contactPerson}'/>"
                           placeholder="Contact person's name">
                </div>

                <div class="col-md-6">
                    <label class="form-label vendor-form-label" for="email">
                        Email Address *
                    </label>

                    <input type="email"
                           class="form-control"
                           id="email"
                           name="email"
                           maxlength="150"
                           required
                           value="<c:out value='${vendor.email}'/>"
                           placeholder="example@email.com">
                </div>

                <div class="col-md-6">
                    <label class="form-label vendor-form-label" for="phone">
                        Phone Number *
                    </label>

                    <input type="tel"
                           class="form-control"
                           id="phone"
                           name="phone"
                           maxlength="20"
                           required
                           value="<c:out value='${vendor.phone}'/>"
                           placeholder="07XXXXXXXX">
                </div>

                <div class="col-md-6">
                    <label class="form-label vendor-form-label" for="basePrice">
                        Base Price (LKR) *
                    </label>

                    <input type="number"
                           class="form-control"
                           id="basePrice"
                           name="basePrice"
                           min="0.01"
                           step="0.01"
                           required
                           value="<c:out value='${vendor.basePrice}'/>"
                           placeholder="Enter starting price">
                </div>

                <div class="col-12">
                    <label class="form-label vendor-form-label" for="address">
                        Business Address / Location *
                    </label>

                    <input type="text"
                           class="form-control"
                           id="address"
                           name="address"
                           maxlength="250"
                           required
                           value="<c:out value='${vendor.address}'/>"
                           placeholder="City, District">
                </div>

                <div class="col-12">
                    <label class="form-label vendor-form-label" for="serviceDescription">
                        Service Description *
                    </label>

                    <textarea class="form-control"
                              id="serviceDescription"
                              name="serviceDescription"
                              rows="5"
                              maxlength="1000"
                              required
                              placeholder="Describe the services your business provides"><c:out value="${vendor.serviceDescription}"/></textarea>
                </div>

            </div>

            <hr class="my-4">

            <div class="d-flex flex-wrap justify-content-between align-items-center gap-3">

                <a href="${pageContext.request.contextPath}/vendors"
                   class="btn btn-outline-secondary px-4">
                    <i class="bi bi-arrow-left me-1"></i> Cancel
                </a>

                <button type="submit" class="btn vendor-form-submit">
                    <c:choose>
                        <c:when test="${isEdit}">
                            <i class="bi bi-save me-2"></i> Update Vendor
                        </c:when>
                        <c:otherwise>
                            <i class="bi bi-check-circle me-2"></i> Submit Registration
                        </c:otherwise>
                    </c:choose>
                </button>

            </div>

            <c:if test="${!isEdit}">
                <p class="vendor-form-help mt-4 mb-0">
                    <i class="bi bi-info-circle me-1"></i>
                    New vendor registrations require administrator approval before appearing publicly.
                </p>
            </c:if>

        </form>
    </section>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
```