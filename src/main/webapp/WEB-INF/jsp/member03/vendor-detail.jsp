<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<style>
.vendor-detail-wrap{max-width:1000px;margin:38px auto 60px;padding:0 16px}
.vendor-detail-card{background:#fff;border:1px solid #eadfe3;border-radius:22px;overflow:hidden;box-shadow:0 12px 36px rgba(45,24,36,.07)}
.vendor-detail-head{background:linear-gradient(120deg,#321b2b,#704454);color:#fff;padding:34px}
.vendor-detail-head h1{font-family:Georgia,serif;font-size:clamp(2rem,4vw,3rem)}
.vendor-detail-body{padding:clamp(22px,4vw,38px)}
.vendor-detail-label{font-size:.78rem;text-transform:uppercase;letter-spacing:.08em;color:#927886;font-weight:700}
.vendor-detail-value{color:#46333e;font-size:1.02rem}
.vendor-detail-price{font-size:1.5rem;font-weight:700;color:#704454}
.vendor-detail-action{display:inline-block;background:#704454;color:#fff;text-decoration:none;padding:12px 18px;border-radius:10px;font-weight:600}
</style>

<main class="vendor-detail-wrap">
  <div class="mb-3"><a class="text-decoration-none" href="${pageContext.request.contextPath}/vendors">&larr; Back to vendors</a></div>
  <article class="vendor-detail-card">
    <header class="vendor-detail-head">
      <div class="small text-uppercase fw-semibold opacity-75 mb-2">ÉCLAT · VERIFIED VENDOR DIRECTORY</div>
      <h1 class="mb-2"><c:out value="${vendor.vendorName}"/></h1>
      <div class="opacity-75"><c:out value="${vendor.category.displayName}"/> · <c:out value="${vendor.address}"/></div>
    </header>
    <section class="vendor-detail-body">
      <div class="row g-4">
        <div class="col-md-8">
          <div class="vendor-detail-label mb-2">About this service</div>
          <p class="vendor-detail-value"><c:out value="${vendor.serviceDescription}"/></p>
          <div class="row g-3 mt-2">
            <div class="col-sm-6"><div class="vendor-detail-label">Contact person</div><div class="vendor-detail-value"><c:out value="${vendor.contactPerson}"/></div></div>
            <div class="col-sm-6"><div class="vendor-detail-label">Availability</div><div class="vendor-detail-value"><c:out value="${vendor.availability}"/></div></div>
            <div class="col-sm-6"><div class="vendor-detail-label">Email</div><div class="vendor-detail-value"><a href="mailto:<c:out value='${vendor.email}'/>"><c:out value="${vendor.email}"/></a></div></div>
            <div class="col-sm-6"><div class="vendor-detail-label">Phone</div><div class="vendor-detail-value"><c:out value="${vendor.phone}"/></div></div>
            <div class="col-sm-6"><div class="vendor-detail-label">Vendor reference</div><div class="vendor-detail-value"><c:out value="${vendor.vendorId}"/></div></div>
            <div class="col-sm-6"><div class="vendor-detail-label">Registered</div><div class="vendor-detail-value"><c:out value="${vendor.registeredAt}"/></div></div>
          </div>
        </div>
        <aside class="col-md-4">
          <div class="p-3 rounded-4" style="background:#f8f2f5">
            <div class="vendor-detail-label mb-1">Starting price</div>
            <div class="vendor-detail-price">LKR <c:out value="${vendor.basePrice}"/></div>
            <hr>
            <div class="vendor-detail-label">Customer rating</div>
            <div class="fs-4 fw-bold" style="color:#704454"><c:out value="${avgRating}"/> / 5</div>
            <div class="text-secondary small"><c:out value="${reviewCount}"/> review(s)</div>
            <div class="mt-3"><a href="${pageContext.request.contextPath}/reviews?vendorId=${vendor.vendorId}">Read customer reviews</a></div>
            <c:if test="${sessionScope.currentUser.role == 'CUSTOMER' && vendor.approvalStatus == 'APPROVED' && vendor.active}">
              <a class="vendor-detail-action mt-3 w-100 text-center" href="${pageContext.request.contextPath}/bookings/new?vendorId=${vendor.vendorId}">Request a booking</a>
            </c:if>
            <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
              <a class="vendor-detail-action mt-3 w-100 text-center" href="${pageContext.request.contextPath}/vendors/edit/${vendor.vendorId}">Edit vendor</a>
              <div class="small text-secondary mt-2">Approval: <c:out value="${vendor.approvalStatus}"/></div>
            </c:if>
          </div>
        </aside>
      </div>
    </section>
  </article>
</main>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
