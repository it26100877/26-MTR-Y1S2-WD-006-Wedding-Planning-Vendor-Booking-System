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
.vendor-image-upload{background:#fbf8f3;border:1px dashed #cdb894}
.vendor-image-upload .form-control{background:#fff}
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

        <form method="post" enctype="multipart/form-data"
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
                           inputmode="tel"
                           pattern="(0|\\+94)7[0-9]{8}"
                           minlength="10"
                           maxlength="12"
                           required
                           value="<c:out value='${vendor.phone}'/>"
                           placeholder="0771234567 or +94771234567"
                           title="Enter a valid Sri Lankan mobile number: 07XXXXXXXX or +947XXXXXXXX">
                    <small class="text-muted">Use a Sri Lankan mobile number (07XXXXXXXX or +947XXXXXXXX).</small>
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
                    <label class="form-label vendor-form-label" for="vendorImage">Business / Portfolio Photos</label>
                    <div class="vendor-image-upload p-3 p-md-4 rounded-3">
                        <input type="file" class="form-control" id="vendorImage" name="vendorImage" multiple
                               accept="image/jpeg,image/png,image/gif,.jpg,.jpeg,.png,.gif">
                        <small class="vendor-form-help d-block mt-2">Choose a photo from your computer or phone. JPG, PNG or GIF · maximum 5 MB. Select up to 8 portfolio photos. JPG, PNG or GIF · maximum 5 MB each. The first image is used as the directory cover.</small>
                        <div class="mt-3 d-flex flex-wrap gap-2" id="vendorImagePreviewWrap" style="display:none">
                            
                        </div>
                        <c:if test="${isEdit}">
                            <div class="mt-3">
                                <img src="${pageContext.request.contextPath}/vendor-images/${vendor.vendorId}"
                                     alt="Current vendor photo" onerror="this.parentElement.style.display='none'"
                                     style="width:100%;max-width:420px;max-height:230px;object-fit:cover;border-radius:12px;border:1px solid #e9e2d8">
                            </div>
                        </c:if>
                    </div>
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

<script>
(function () {
    const input = document.getElementById('vendorImage');
    const wrap = document.getElementById('vendorImagePreviewWrap');
    if (!input || !wrap) return;
    input.addEventListener('change', function () {
        wrap.innerHTML = '';
        const files = Array.from(input.files || []);
        if (files.length > 8) { alert('Please select up to 8 photos.'); input.value = ''; wrap.style.display = 'none'; return; }
        const allowed = ['image/jpeg', 'image/png', 'image/gif'];
        for (const file of files) {
            if (!allowed.includes(file.type)) { alert('Please select only JPG, PNG or GIF photos.'); input.value = ''; wrap.style.display = 'none'; return; }
            if (file.size > 5 * 1024 * 1024) { alert('Each photo must be 5 MB or smaller.'); input.value = ''; wrap.style.display = 'none'; return; }
            const image = document.createElement('img');
            image.alt = 'Selected portfolio photo preview';
            image.src = URL.createObjectURL(file);
            image.style.cssText = 'width:128px;height:96px;object-fit:cover;border-radius:10px;border:1px solid #e9e2d8';
            wrap.appendChild(image);
        }
        wrap.style.display = files.length ? 'flex' : 'none';
    });
})();
</script>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
