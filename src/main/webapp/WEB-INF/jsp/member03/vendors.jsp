<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<style>
.vendor-page{max-width:1180px;margin:0 auto;padding:28px 16px 56px}
.vendor-hero{background:linear-gradient(120deg,#321b2b,#704454);color:#fff;border-radius:22px;padding:34px;margin-bottom:24px}
.vendor-hero h1{font-family:Georgia,serif;font-size:clamp(2rem,4vw,3rem);margin:0 0 8px}
.vendor-filter,.vendor-card{background:#fff;border:1px solid #eadfe3;border-radius:16px;box-shadow:0 8px 28px rgba(45,24,36,.06)}
.vendor-filter{padding:18px;margin-bottom:22px}
.vendor-card{height:100%;padding:20px;display:flex;flex-direction:column;gap:10px}
.vendor-card h3{font-family:Georgia,serif;color:#432638;font-size:1.35rem;margin:0}
.vendor-muted{color:#786b72;font-size:.92rem}
.vendor-chip{display:inline-block;border-radius:30px;padding:5px 11px;background:#f5edf0;color:#704454;font-size:.8rem;font-weight:600}
.vendor-price{font-size:1.1rem;font-weight:700;color:#694153}
.vendor-actions{display:flex;gap:8px;flex-wrap:wrap;margin-top:auto;padding-top:8px}
.vendor-btn{border:0;border-radius:9px;padding:9px 14px;text-decoration:none;display:inline-block;font-weight:600;font-size:.9rem}
.vendor-btn-primary{background:#704454;color:#fff}.vendor-btn-light{background:#f3e9ed;color:#563448}
.vendor-btn-success{background:#e4f3e9;color:#21633a}.vendor-btn-danger{background:#fbe8e8;color:#922d2d}
</style>

<main class="vendor-page">
  <section class="vendor-hero">
    <div class="text-uppercase small fw-semibold mb-2">ÉCLAT · WEDDING DIRECTORY</div>
    <h1>Find the right people for your day.</h1>
    <p class="mb-0 opacity-75">Explore wedding professionals and compare the services they offer.</p>
  </section>

  <c:if test="${sessionScope.currentUser != null}">
    <div class="d-flex justify-content-end mb-3">
      <a class="vendor-btn vendor-btn-primary" href="${pageContext.request.contextPath}/vendors/new">Register a Vendor</a>
    </div>
  </c:if>

  <section class="vendor-filter">
    <form method="get" action="${pageContext.request.contextPath}/vendors" class="row g-3 align-items-end">
      <div class="col-md-4">
        <label class="form-label" for="query">Vendor name</label>
        <input class="form-control" id="query" name="query" value="<c:out value='${query}'/>" placeholder="Search vendors">
      </div>
      <div class="col-md-3">
        <label class="form-label" for="category">Category</label>
        <select class="form-select" id="category" name="category">
          <option value="">All categories</option>
          <c:forEach items="${categories}" var="cat">
            <option value="${cat.name()}" ${selectedCategory == cat.name() ? 'selected' : ''}><c:out value="${cat.displayName}"/></option>
          </c:forEach>
        </select>
      </div>
      <div class="col-md-3">
        <label class="form-label" for="location">Location</label>
        <input class="form-control" id="location" name="location" value="<c:out value='${location}'/>" placeholder="District / town">
      </div>
      <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
        <input type="hidden" name="filter" value="<c:out value='${filter}'/>">
      </c:if>
      <div class="col-md-2 d-flex gap-2">
        <button class="vendor-btn vendor-btn-primary flex-grow-1" type="submit">Search</button>
        <a class="vendor-btn vendor-btn-light" href="${pageContext.request.contextPath}/vendors">Reset</a>
      </div>
    </form>
  </section>

  <c:if test="${sessionScope.currentUser.role == 'ADMIN' && filter == 'PENDING'}">
    <div class="alert alert-warning">Pending vendor registrations awaiting review.</div>
  </c:if>

  <div class="d-flex justify-content-between align-items-center mb-3">
    <h2 class="h4 mb-0">Vendor directory</h2>
    <span class="vendor-muted"><c:out value="${vendors.size()}"/> result(s)</span>
  </div>

  <c:choose>
    <c:when test="${not empty vendors}">
      <div class="row g-3">
        <c:forEach items="${vendors}" var="vendor">
          <div class="col-md-6 col-xl-4">
            <article class="vendor-card">
              <div class="d-flex justify-content-between align-items-start gap-2">
                <span class="vendor-chip"><c:out value="${vendor.category.displayName}"/></span>
                <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                  <span class="badge text-bg-secondary"><c:out value="${vendor.approvalStatus}"/></span>
                </c:if>
              </div>
              <h3><c:out value="${vendor.vendorName}"/></h3>
              <div class="vendor-muted"><c:out value="${vendor.address}"/></div>
              <p class="vendor-muted mb-0"><c:out value="${vendor.serviceDescription}"/></p>
              <div class="vendor-price">LKR <c:out value="${vendor.basePrice}"/></div>
              <div class="vendor-muted">Availability: <c:out value="${vendor.availability}"/></div>
              <div class="vendor-actions">
                <a class="vendor-btn vendor-btn-primary" href="${pageContext.request.contextPath}/vendors/view/${vendor.vendorId}">View details</a>
                <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                  <a class="vendor-btn vendor-btn-light" href="${pageContext.request.contextPath}/vendors/edit/${vendor.vendorId}">Edit</a>
                  <c:choose>
                    <c:when test="${vendor.approvalStatus == 'PENDING'}">
                      <form method="post" action="${pageContext.request.contextPath}/admin/vendors/approve/${vendor.vendorId}">
                        <button class="vendor-btn vendor-btn-success" type="submit">Approve</button>
                      </form>
                      <form method="post" action="${pageContext.request.contextPath}/admin/vendors/reject/${vendor.vendorId}">
                        <button class="vendor-btn vendor-btn-danger" type="submit">Reject</button>
                      </form>
                    </c:when>
                  </c:choose>
                  <c:choose>
                    <c:when test="${vendor.active}">
                      <form method="post" action="${pageContext.request.contextPath}/admin/vendors/deactivate/${vendor.vendorId}">
                        <button class="vendor-btn vendor-btn-light" type="submit">Deactivate</button>
                      </form>
                    </c:when>
                    <c:otherwise>
                      <form method="post" action="${pageContext.request.contextPath}/admin/vendors/activate/${vendor.vendorId}">
                        <button class="vendor-btn vendor-btn-success" type="submit">Activate</button>
                      </form>
                    </c:otherwise>
                  </c:choose>
                  <form method="post" action="${pageContext.request.contextPath}/admin/vendors/delete/${vendor.vendorId}" onsubmit="return confirm('Permanently delete this vendor?')">
                    <button class="vendor-btn vendor-btn-danger" type="submit">Delete</button>
                  </form>
                </c:if>
              </div>
            </article>
          </div>
        </c:forEach>
      </div>
    </c:when>
    <c:otherwise>
      <div class="text-center p-5 vendor-filter">
        <h3 class="h5">No vendors found</h3>
        <p class="vendor-muted mb-0">Try changing the search terms or category.</p>
      </div>
    </c:otherwise>
  </c:choose>
</main>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
