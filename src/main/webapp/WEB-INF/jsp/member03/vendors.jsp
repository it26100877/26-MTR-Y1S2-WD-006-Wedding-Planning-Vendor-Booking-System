<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<style>
/* Vendor directory page — compact Éclat layout matching the local reference UI. */
.vendor-directory-page{width:min(1320px,calc(100% - 24px));margin:0 auto;padding:22px 0 42px}
.vendor-directory-heading{display:flex;align-items:center;justify-content:space-between;gap:20px;margin:0 0 24px}
.vendor-directory-heading h1{font:600 clamp(1.65rem,2.5vw,2.15rem) 'Playfair Display',Georgia,serif;color:#98702d;margin:0 0 5px}
.vendor-directory-heading p{color:#8a8177;font-size:.91rem;margin:0}
.vendor-directory-register{display:inline-flex;align-items:center;justify-content:center;gap:9px;min-height:46px;padding:0 22px;border:1px solid #b99a68;border-radius:7px;background:#b99a68;color:#fff!important;text-decoration:none;font-weight:600;white-space:nowrap}
.vendor-directory-register:hover{background:#96764a;border-color:#96764a}
.vendor-directory-filter{display:grid;grid-template-columns:minmax(220px,1.7fr) minmax(160px,.85fr) minmax(220px,1.3fr) minmax(150px,.95fr) auto;gap:9px;align-items:center;padding:16px 18px;margin:0 0 25px;background:#fff;border:1px solid #eadfD1;border-radius:15px;box-shadow:0 12px 34px rgba(55,43,27,.055)}
.vendor-directory-filter .input-group-text{display:flex;align-items:center;justify-content:center;width:44px;min-height:48px;background:#f7f2eb;border:1px solid #e4d9ca;border-right:0;border-radius:8px 0 0 8px;color:#a18455}
.vendor-directory-filter .form-control,.vendor-directory-filter .form-select{min-height:48px;border-radius:8px!important;font-size:.94rem}
.vendor-directory-filter .input-group .form-control{border-radius:0 8px 8px 0!important}
.vendor-filter-actions{display:flex;gap:8px}
.vendor-filter-actions .btn{min-height:48px;padding:0 22px;font-weight:600}
.vendor-filter-submit{background:#b99a68!important;border-color:#b99a68!important;color:#fff!important;min-width:150px}
.vendor-filter-submit:hover{background:#96764a!important;border-color:#96764a!important}
.vendor-filter-reset{border:1px solid #cdb894!important;color:#5b4b35!important;background:#fff!important}
.vendor-directory-grid{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:26px}
.vendor-directory-card{min-width:0;min-height:318px;display:flex;flex-direction:column;justify-content:space-between;padding:22px 18px 16px;background:#fff;border:1px solid #eadfD1;border-radius:17px;box-shadow:0 12px 34px rgba(55,43,27,.055);transition:transform .18s ease,box-shadow .18s ease}
.vendor-directory-card:hover{transform:translateY(-2px);box-shadow:0 16px 38px rgba(55,43,27,.09)}
.vendor-directory-card h2{font:600 1.28rem/1.3 'Playfair Display',Georgia,serif;color:#98702d;margin:0 0 4px}
.vendor-directory-location{display:flex;align-items:center;gap:6px;color:#6f6a63;font-size:.88rem;margin:0 0 10px}
.vendor-directory-location i{color:#9a8058}
.vendor-directory-description{min-height:48px;color:#8a8177;font-size:.88rem;line-height:1.55;margin:0 0 16px}
.vendor-directory-price{display:flex;align-items:center;justify-content:space-between;gap:12px;padding:12px 0 15px;border-top:1px solid #e4e6e8;border-bottom:1px solid #e4e6e8}
.vendor-directory-price span{font-size:.86rem;color:#5f5a53}
.vendor-directory-price strong{font-size:1.23rem;color:#64836b;white-space:nowrap}
.vendor-directory-card-actions{display:flex;align-items:center;gap:8px;padding-top:8px}
.vendor-directory-card-actions .btn{min-height:43px;border-radius:7px!important;font-size:.85rem}
.vendor-view-btn{flex:1;border:1px solid #cdb894!important;color:#5b4b35!important;background:#fff!important}
.vendor-view-btn:hover{background:#f8f3eb!important}
.vendor-deactivate-btn{border:1px solid #e5a000!important;color:#d58a00!important;background:#fff!important;padding:0 13px}
.vendor-delete-btn{width:47px;flex:none;border:1px solid #ef646c!important;color:#e44e58!important;background:#fff!important}
.vendor-activate-btn{border:1px solid #6b9a77!important;color:#54805f!important;background:#fff!important;padding:0 13px}
.vendor-approval-actions{display:flex;gap:6px}
.vendor-approval-actions .btn{width:42px}
.vendor-status-note{font-size:.73rem;color:#9a8058;margin:0 0 8px}
.vendor-directory-empty{grid-column:1/-1;text-align:center;padding:55px 20px;background:#fff;border:1px solid #eadfd1;border-radius:15px;color:#82796f}
@media(max-width:1050px){.vendor-directory-filter{grid-template-columns:1.4fr 1fr 1.2fr}.vendor-filter-actions{grid-column:1/-1;justify-content:flex-end}.vendor-directory-grid{grid-template-columns:repeat(2,minmax(0,1fr))}}
@media(max-width:680px){.vendor-directory-page{width:calc(100% - 22px);padding-top:18px}.vendor-directory-heading{align-items:flex-start;flex-direction:column}.vendor-directory-register{width:100%}.vendor-directory-filter{grid-template-columns:1fr;padding:13px}.vendor-filter-actions{grid-column:auto}.vendor-filter-actions .btn{flex:1}.vendor-directory-grid{grid-template-columns:1fr;gap:16px}.vendor-directory-card{min-height:285px}.vendor-directory-card h2{font-size:1.2rem}}
/* Vendor photography cards */
.vendor-directory-card{padding:0;overflow:hidden;min-height:390px}
.vendor-directory-card-media{height:190px;position:relative;overflow:hidden;background:#eee8df}
.vendor-directory-card-media img{display:block;width:100%;height:100%;object-fit:cover;transition:transform .45s ease}
.vendor-directory-card:hover .vendor-directory-card-media img{transform:scale(1.045)}
.vendor-directory-card-content{display:flex;flex:1;flex-direction:column;justify-content:space-between;padding:17px 18px 16px}
.vendor-directory-card h2{font-size:1.18rem}
.vendor-directory-card .vendor-directory-description{min-height:44px}
.vendor-directory-card .vendor-directory-card-actions{padding-top:10px}
.vendor-directory-category{position:absolute;left:13px;bottom:12px;padding:5px 10px;border-radius:30px;background:rgba(39,36,31,.78);color:#fff;font-size:.72rem;font-weight:700;letter-spacing:.04em;backdrop-filter:blur(8px)}
@media(max-width:680px){.vendor-directory-card{min-height:360px}.vendor-directory-card-media{height:180px}}
@media(prefers-reduced-motion:reduce){.vendor-directory-card-media img{transition:none}}

</style>

<section class="vendor-directory-page">
    <div class="vendor-directory-heading">
        <div>
            <h1><i class="bi bi-shop"></i> Wedding Vendors Directory</h1>
            <p>Browse top-rated wedding specialists, photographers, venues, and caterers</p>
        </div>
        <a href="${pageContext.request.contextPath}/vendors/new" class="vendor-directory-register">
            <i class="bi bi-person-plus"></i> Register as Vendor
        </a>
    </div>

    <form action="${pageContext.request.contextPath}/vendors" method="get" class="vendor-directory-filter">
        <div class="input-group">
            <span class="input-group-text"><i class="bi bi-search"></i></span>
            <input type="text" name="query" class="form-control" placeholder="Search by vendor name..." value="${query}">
        </div>
        <input type="text" name="location" class="form-control" placeholder="District / city" value="${location}">
        <select name="category" class="form-select">
            <option value="">All Categories</option>
            <c:forEach var="cat" items="${categories}">
                <option value="${cat.name()}" ${cat.name() == selectedCategory ? 'selected' : ''}>${cat.displayName}</option>
            </c:forEach>
        </select>
        <div class="vendor-filter-actions">
            <button type="submit" class="btn vendor-filter-submit">Filter</button>
            <a href="${pageContext.request.contextPath}/vendors" class="btn vendor-filter-reset">Reset</a>
        </div>
    </form>

    <section class="vendor-directory-grid" aria-label="Available wedding vendors">
        <c:forEach var="v" items="${vendors}">
            <article class="vendor-directory-card">
                <div class="vendor-directory-card-media">
                    <img src="${pageContext.request.contextPath}/vendor-images/${v.vendorId}"
                         alt="${v.vendorName} wedding service portfolio"
                         loading="lazy"
                         onerror="this.onerror=null;this.src='https://images.unsplash.com/photo-1511285560929-80b456fea0bc?auto=format&amp;fit=crop&amp;w=900&amp;q=82';">
                    <span class="vendor-directory-category"><c:out value="${v.category.displayName}"/></span>
                </div>
                <div class="vendor-directory-card-content">
                    <div>
                    <h2>${v.vendorName}</h2>
                    <p class="vendor-directory-location"><i class="bi bi-geo-alt"></i> ${v.address}</p>
                    <c:if test="${v.approvalStatus != 'APPROVED'}">
                        <p class="vendor-status-note">${v.approvalStatus == 'PENDING' ? 'Pending approval' : v.approvalStatus}</p>
                    </c:if>
                    <p class="vendor-directory-description">${v.serviceDescription}</p>
                    <div class="vendor-directory-price">
                        <span>Starting Price</span>
                        <strong>LKR ${v.basePrice}</strong>
                    </div>
                </div>
                <div class="vendor-directory-card-actions">
                    <a href="${pageContext.request.contextPath}/vendors/view/${v.vendorId}" class="btn vendor-view-btn">View Details</a>
                    <c:if test="${sessionScope.currentUser.role == 'CUSTOMER'}">
                        <a href="${pageContext.request.contextPath}/bookings/new?vendorId=${v.vendorId}" class="btn vendor-directory-register">Book Now</a>
                    </c:if>
                    <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                        <c:if test="${v.approvalStatus == 'PENDING'}">
                            <div class="vendor-approval-actions">
                                <form action="${pageContext.request.contextPath}/admin/vendors/approve/${v.vendorId}" method="post"><button type="submit" class="btn btn-sm btn-success" title="Approve"><i class="bi bi-check-lg"></i></button></form>
                                <form action="${pageContext.request.contextPath}/admin/vendors/reject/${v.vendorId}" method="post"><button type="submit" class="btn btn-sm btn-danger" title="Reject"><i class="bi bi-x-lg"></i></button></form>
                            </div>
                        </c:if>
                        <c:if test="${v.approvalStatus == 'APPROVED' && v.active}">
                            <form action="${pageContext.request.contextPath}/admin/vendors/deactivate/${v.vendorId}" method="post">
                                <button type="submit" class="btn vendor-deactivate-btn">Deactivate</button>
                            </form>
                        </c:if>
                        <c:if test="${v.approvalStatus == 'APPROVED' && !v.active}">
                            <form action="${pageContext.request.contextPath}/admin/vendors/activate/${v.vendorId}" method="post">
                                <button type="submit" class="btn vendor-activate-btn">Activate</button>
                            </form>
                        </c:if>
                        <form action="${pageContext.request.contextPath}/admin/vendors/delete/${v.vendorId}" method="post" onsubmit="return confirm('Permanently delete this vendor?');">
                            <button type="submit" class="btn vendor-delete-btn" title="Delete vendor"><i class="bi bi-trash"></i></button>
                        </form>
                    </c:if>
                </div>
                </div>
            </article>
        </c:forEach>
        <c:if test="${empty vendors}">
            <div class="vendor-directory-empty">
                <i class="bi bi-shop-window fs-1 d-block mb-2"></i>
                <span>No vendors matching your search criteria.</span><br>
                <a href="${pageContext.request.contextPath}/vendors" class="btn vendor-filter-reset mt-3">Clear Filters</a>
            </div>
        </c:if>
    </section>
</section>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
