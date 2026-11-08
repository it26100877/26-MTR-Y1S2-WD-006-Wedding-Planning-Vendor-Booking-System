<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<style>
.vendor-form-wrap{max-width:850px;margin:38px auto 60px;padding:0 16px}
.vendor-form-card{background:#fff;border:1px solid #eadfe3;border-radius:20px;padding:clamp(20px,4vw,38px);box-shadow:0 12px 36px rgba(45,24,36,.07)}
.vendor-form-card h1{font-family:Georgia,serif;color:#432638}
.vendor-form-card .form-label{font-weight:600;color:#55444d}
.vendor-form-submit{background:#704454;color:white;border:0;border-radius:10px;padding:12px 20px;font-weight:600}
</style>

<main class="vendor-form-wrap">
  <section class="vendor-form-card">
    <div class="text-uppercase small fw-semibold mb-2" style="color:#9a7182">ÉCLAT · VENDOR MANAGEMENT</div>
    <h1 class="mb-2"><c:choose><c:when test="${isEdit}">Edit vendor details</c:when><c:otherwise>Register as a vendor</c:otherwise></c:choose></h1>
    <p class="text-secondary mb-4"><c:choose><c:when test="${isEdit}">Update the vendor's service information.</c:when><c:otherwise>Submit your business details for administrator review.</c:otherwise></c:choose></p>

    <c:choose>
      <c:when test="${isEdit}">
        <c:url var="vendorFormAction" value="/vendors/edit/${vendor.vendorId}"/>
      </c:when>
      <c:otherwise>
        <c:url var="vendorFormAction" value="/vendors/new"/>
      </c:otherwise>
    </c:choose>
    <form method="post" action="${vendorFormAction}">
      <div class="row g-3">
        <div class="col-md-6">
          <label class="form-label" for="vendorName">Business / vendor name *</label>
          <input class="form-control" id="vendorName" name="vendorName" maxlength="100" value="<c:out value='${vendor.vendorName}'/>" required>
        </div>
        <div class="col-md-6">
          <label class="form-label" for="category">Service category *</label>
          <select class="form-select" id="category" name="category" required>
            <option value="">Select category</option>
            <c:forEach items="${categories}" var="cat">
              <option value="${cat.name()}" ${vendor.category == cat ? 'selected' : ''}><c:out value="${cat.displayName}"/></option>
            </c:forEach>
          </select>
        </div>
        <div class="col-md-6">
          <label class="form-label" for="contactPerson">Contact person *</label>
          <input class="form-control" id="contactPerson" name="contactPerson" maxlength="100" value="<c:out value='${vendor.contactPerson}'/>" required>
        </div>
        <div class="col-md-6">
          <label class="form-label" for="email">Email address *</label>
          <input class="form-control" id="email" name="email" type="email" maxlength="150" value="<c:out value='${vendor.email}'/>" required>
        </div>
        <div class="col-md-6">
          <label class="form-label" for="phone">Phone number *</label>
          <input class="form-control" id="phone" name="phone" maxlength="20" value="<c:out value='${vendor.phone}'/>" required>
        </div>
        <div class="col-md-6">
          <label class="form-label" for="basePrice">Base price (LKR) *</label>
          <input class="form-control" id="basePrice" name="basePrice" type="number" min="0.01" step="0.01" value="<c:out value='${vendor.basePrice}'/>" required>
        </div>
        <div class="col-12">
          <label class="form-label" for="address">Address / service area *</label>
          <input class="form-control" id="address" name="address" maxlength="250" value="<c:out value='${vendor.address}'/>" required>
        </div>
        <div class="col-12">
          <label class="form-label" for="serviceDescription">Service description *</label>
          <textarea class="form-control" id="serviceDescription" name="serviceDescription" rows="5" maxlength="2000" required><c:out value="${vendor.serviceDescription}"/></textarea>
        </div>
      </div>
      <div class="d-flex flex-wrap gap-2 mt-4">
        <button class="vendor-form-submit" type="submit"><c:choose><c:when test="${isEdit}">Save changes</c:when><c:otherwise>Submit registration</c:otherwise></c:choose></button>
        <a class="btn btn-outline-secondary rounded-3 px-4" href="${pageContext.request.contextPath}/vendors">Cancel</a>
      </div>
      <c:if test="${not isEdit}">
        <p class="small text-secondary mt-3 mb-0">New vendor registrations remain pending until an administrator approves them.</p>
      </c:if>
    </form>
  </section>
</main>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
