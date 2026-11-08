<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/member02-planner.css?v=20261108-v7">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/member02-admin-dashboard.css?v=1">
<c:set var="m02Ctx" value="${pageContext.request.contextPath}"/>
<div class="m02 container-fluid px-0">
  <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
    <div class="m02-admin-entry"><span><i class="bi bi-shield-lock"></i> <strong>Admin Wedding Management</strong> · View customer-wise statistics, progress, and admin tools.</span><a class="m02-btn m02-btn-sm" href="${m02Ctx}/admin/wedding-plans"><i class="bi bi-bar-chart-line"></i> Open Admin Dashboard</a></div>
  </c:if>
  <section class="m02-hero m02-hero--plans mb-4" aria-label="Wedding planning introduction">
    <div><div class="m02-eyebrow"><i class="bi bi-stars"></i> Éclat • Your wedding workspace</div><h1 class="m02-hero-title">Every detail, beautifully planned.</h1><p>Organize your big day, track your preparation and keep your dream wedding on schedule.</p></div>
    <c:if test="${sessionScope.currentUser.role == 'CUSTOMER'}"><div class="m02-hero-right"><a class="m02-btn m02-btn-light" href="${m02Ctx}/plans/new"><i class="bi bi-plus-circle"></i> Create a wedding plan</a></div></c:if>
  </section>

  <c:if test="${not empty nextPlan}">
    <div class="m02-banner">
      <i class="bi bi-hourglass-split" style="font-size:1.6rem;color:#a05d76"></i>
      <div><div class="m02-help">Next wedding</div><strong><c:out value="${nextPlan.planName}"/></strong> &nbsp;•&nbsp; <c:out value="${nextPlan.weddingDate}"/></div>
      <div class="ms-auto big">${nextPlan.countdownLabel}</div>
    </div>
  </c:if>

  <c:if test="${attentionTaskCount > 0 && not empty firstAttentionPlanId}">
    <aside class="m02-overview-reminder mb-4" aria-label="Checklist deadline reminders">
      <span class="m02-overview-bell"><i class="bi bi-bell-fill"></i></span>
      <div class="flex-grow-1">
        <strong>${attentionTaskCount} checklist task(s) need attention</strong>
        <p class="mb-0">${lateTaskCount} overdue. Review due dates in your wedding checklist.</p>
      </div>
      <a class="m02-btn m02-btn-ghost m02-btn-sm" href="${m02Ctx}/plans/${firstAttentionPlanId}/checklist"><i class="bi bi-list-check"></i> View reminders</a>
    </aside>
  </c:if>

  <div class="row g-3 mb-4">
    <div class="col-6 col-xl-3"><div class="m02-stat"><div class="m02-stat-icon"><i class="bi bi-calendar-heart"></i></div><div class="label">Active weddings</div><div class="value">${activeCount}</div><div class="sub">Plans in progress</div></div></div>
    <div class="col-6 col-xl-3"><div class="m02-stat"><div class="m02-stat-icon"><i class="bi bi-list-check"></i></div><div class="label">Tasks completed</div><div class="value">${completedTaskCount} / ${taskCount}</div><div class="sub">Across your plans</div></div></div>
    <div class="col-6 col-xl-3"><div class="m02-stat"><div class="m02-stat-icon"><i class="bi bi-pie-chart"></i></div><div class="label">Planning progress</div><div class="value">${overallProgress}%</div><div class="m02-track mt-2"><span style="width:${overallProgress}%"></span></div></div></div>
    <div class="col-6 col-xl-3"><div class="m02-stat"><div class="m02-stat-icon"><i class="bi bi-wallet2"></i></div><div class="label">Total planned budget</div><div class="value" style="font-size:1.27rem">LKR <fmt:formatNumber value="${budgetTotal}" maxFractionDigits="0" groupingUsed="true"/></div><div class="sub">Excludes cancelled weddings</div></div></div>
  </div>

  <div class="d-flex flex-wrap justify-content-between align-items-center gap-3 mb-3">
    <div><h2 class="m02-heading">${sessionScope.currentUser.role == 'ADMIN' ? 'All Wedding Plans' : 'My Wedding Plans'}</h2><p class="m02-lead mb-0">All your celebrations, details and next steps.</p></div>
    <c:if test="${sessionScope.currentUser.role == 'CUSTOMER'}"><a class="m02-btn" href="${m02Ctx}/plans/new"><i class="bi bi-plus"></i> New Plan</a></c:if>
  </div>

  <form class="m02-filterbar" method="get" action="${m02Ctx}/plans" role="search" aria-label="Search and filter wedding plans">
    <div class="row g-2 align-items-end">
      <div class="col-lg-3 col-md-6"><label class="m02-label" for="m02Q">Search</label><input id="m02Q" name="q" class="form-control" maxlength="80" placeholder="Name, venue, theme..." value="<c:out value='${criteria.keyword}'/>"></div>
      <div class="col-lg-2 col-md-3 col-6"><label class="m02-label" for="m02FStatus">Status</label>
        <select id="m02FStatus" name="status" class="form-select"><option value="ALL">All</option><option value="ACTIVE" ${criteria.status == 'ACTIVE' ? 'selected' : ''}>Active</option><option value="COMPLETED" ${criteria.status == 'COMPLETED' ? 'selected' : ''}>Completed</option><option value="CANCELLED" ${criteria.status == 'CANCELLED' ? 'selected' : ''}>Cancelled</option></select></div>
      <div class="col-lg-2 col-md-3 col-6"><label class="m02-label" for="m02FType">Type</label>
        <select id="m02FType" name="type" class="form-select"><option value="ALL">All types</option><c:forEach var="t" items="${types}"><option value="${t.code}" ${criteria.type == t.code ? 'selected' : ''}>${t.label}</option></c:forEach></select></div>
      <div class="col-lg-2 col-md-4 col-6"><label class="m02-label" for="m02From">Wedding from</label><input id="m02From" name="from" type="date" class="form-control" value="<c:out value='${criteria.fromDate}'/>"></div>
      <div class="col-lg-2 col-md-4 col-6"><label class="m02-label" for="m02To">Wedding to</label><input id="m02To" name="to" type="date" class="form-control" value="<c:out value='${criteria.toDate}'/>"></div>
      <div class="col-lg-3 col-md-4"><label class="m02-label" for="m02Sort">Sort by</label>
        <select id="m02Sort" name="sort" class="form-select"><option value="date_asc" ${criteria.sortBy == 'date_asc' ? 'selected' : ''}>Wedding date (soonest)</option><option value="date_desc" ${criteria.sortBy == 'date_desc' ? 'selected' : ''}>Wedding date (latest)</option><option value="name_asc" ${criteria.sortBy == 'name_asc' ? 'selected' : ''}>Name (A-Z)</option><option value="budget_desc" ${criteria.sortBy == 'budget_desc' ? 'selected' : ''}>Budget (highest)</option><option value="newest" ${criteria.sortBy == 'newest' ? 'selected' : ''}>Recently created</option></select></div>
      <div class="col-lg-9 d-flex gap-2 justify-content-lg-end"><button type="submit" class="m02-btn"><i class="bi bi-funnel"></i> Apply</button><a class="m02-btn m02-btn-ghost" href="${m02Ctx}/plans">Reset</a></div>
    </div>
  </form>

  <c:choose>
    <c:when test="${allPlanCount == 0}">
      <div class="m02-empty"><i class="bi bi-flower2"></i><h3>Your story starts here</h3><p>Create your first wedding plan and start organizing the unforgettable details.</p><c:if test="${sessionScope.currentUser.role == 'CUSTOMER'}"><a href="${m02Ctx}/plans/new" class="m02-btn"><i class="bi bi-calendar-plus"></i> Create Your First Plan</a></c:if></div>
    </c:when>
    <c:when test="${empty plans}">
      <div class="m02-empty"><i class="bi bi-search"></i><h3>No plans match your search</h3><p>Try a different keyword or clear the filters.</p><a href="${m02Ctx}/plans" class="m02-btn m02-btn-ghost">Clear filters</a></div>
    </c:when>
    <c:otherwise>
      <div class="m02-help mb-3">Showing ${plans.size()} of ${allPlanCount} plan(s)</div>
      <div class="row g-4">
        <c:forEach var="p" items="${plans}">
          <div class="col-md-6 col-xl-4">
            <article class="m02-plan-card">
              <div class="m02-plan-photo">
                <c:choose>
                  <c:when test="${p.type.code == 'BEACH'}"><img src="${m02Ctx}/images/member02/wedding-beach.jpg" alt="Beach wedding inspiration"></c:when>
                  <c:when test="${p.type.code == 'GARDEN'}"><img src="${m02Ctx}/images/member02/wedding-garden.jpg" alt="Garden wedding inspiration"></c:when>
                  <c:when test="${p.type.code == 'HOTEL'}"><img src="${m02Ctx}/images/member02/wedding-ballroom.jpg" alt="Hotel ballroom wedding inspiration"></c:when>
                  <c:when test="${p.type.code == 'TRADITIONAL'}"><img src="${m02Ctx}/images/member02/wedding-traditional.jpg" alt="Traditional Sri Lankan Poruwa wedding inspiration"></c:when>
                  <c:otherwise><img src="${m02Ctx}/images/member02/wedding-classic.jpg" alt="Classic wedding ceremony inspiration"></c:otherwise>
                </c:choose>
                <div class="m02-photo-cover"></div>
                <div style="position:absolute;top:13px;right:14px"><span class="m02-status ${p.status == 'ACTIVE' ? 'active' : (p.status == 'COMPLETED' ? 'completed' : 'cancelled')}"><c:out value="${p.status}"/></span></div>
              </div>
              <div class="m02-plan-content">
                <div class="m02-plan-title"><c:out value="${p.planName}"/></div>
                <div class="m02-plan-types"><span class="m02-pill type"><i class="bi ${p.typeIcon}"></i> ${p.typeLabel}</span><span class="m02-pill ${p.countdownUrgency}"><i class="bi bi-hourglass-split"></i> ${p.countdownLabel}</span></div>
                <div class="m02-meta"><i class="bi bi-calendar-event"></i><c:out value="${p.weddingDate}"/></div>
                <div class="m02-meta"><i class="bi bi-geo-alt"></i><c:out value="${p.venue}"/></div>
                <div class="m02-meta"><i class="bi bi-palette"></i><c:out value="${p.theme}" default="Wedding celebration"/></div>
                <div class="m02-meta"><i class="bi bi-people"></i>${p.guestCount} guests</div>
                <div class="m02-meta"><i class="bi bi-cash-stack"></i>LKR <fmt:formatNumber value="${p.totalBudget}" groupingUsed="true" maxFractionDigits="0"/></div>
                <c:if test="${p.status == 'ACTIVE'}">
                  <div class="m02-meta"><i class="bi bi-calculator"></i>
                    <c:choose>
                      <c:when test="${p.guestCount > 0}">Typical cost ~LKR <fmt:formatNumber value="${p.estimatedCost}" groupingUsed="true" maxFractionDigits="0"/> • ${p.budgetOutlookLabel}</c:when>
                      <c:otherwise>Add a guest count for a cost estimate</c:otherwise>
                    </c:choose>
                  </div>
                </c:if>
                <c:if test="${sessionScope.currentUser.role == 'ADMIN'}"><div class="m02-meta"><i class="bi bi-person"></i>Customer <c:out value="${p.customerId}"/></div></c:if>
                <hr class="m02-divider">
                <div class="d-flex justify-content-between align-items-center mb-2"><strong style="font-size:.82rem">Checklist progress</strong><span class="m02-help">${completionByPlan[p.planId]}%</span></div>
                <div class="m02-track mb-3"><span style="width:${completionByPlan[p.planId]}%"></span></div>
                <div class="m02-card-actions mb-2">
                  <a class="m02-btn m02-btn-sm" href="${m02Ctx}/plans/${p.planId}/checklist"><i class="bi bi-list-check"></i> Checklist</a>
                  <a class="m02-btn m02-btn-ghost m02-btn-sm" href="${m02Ctx}/plans/${p.planId}/budget"><i class="bi bi-wallet2"></i> Budget</a>
                  <a class="m02-btn m02-btn-ghost m02-btn-sm" href="${m02Ctx}/plans/${p.planId}/guests"><i class="bi bi-people"></i> Guests</a>
                </div>
                <div class="m02-card-actions">
                  <a class="m02-btn m02-btn-ghost m02-btn-sm" href="${m02Ctx}/plans/${p.planId}/timeline" title="Timeline"><i class="bi bi-calendar3"></i></a>
                  <a class="m02-btn m02-btn-ghost m02-btn-sm" href="${m02Ctx}/plans/${p.planId}/summary" title="Printable summary"><i class="bi bi-file-earmark-text"></i></a>
                  <c:if test="${p.status == 'ACTIVE'}">
                    <a class="m02-btn m02-btn-ghost m02-btn-sm" href="${m02Ctx}/plans/edit/${p.planId}" title="Edit plan"><i class="bi bi-pencil"></i></a>
                    <form method="post" action="${m02Ctx}/plans/complete/${p.planId}" data-m02-confirm="Mark this wedding plan as completed? It can no longer be edited."><button type="submit" class="m02-btn m02-btn-ghost m02-btn-sm" title="Mark completed"><i class="bi bi-patch-check"></i></button></form>
                    <form method="post" action="${m02Ctx}/plans/cancel/${p.planId}" data-m02-confirm="Cancel this wedding plan?"><button type="submit" class="m02-btn m02-btn-ghost m02-btn-sm" title="Cancel plan"><i class="bi bi-x-circle"></i></button></form>
                  </c:if>
                  <form method="post" action="${m02Ctx}/plans/delete/${p.planId}" data-m02-confirm="Permanently delete this wedding plan with its checklist, budget and guest list? This cannot be undone."><button type="submit" class="m02-btn m02-btn-danger m02-btn-sm" title="Delete plan"><i class="bi bi-trash"></i></button></form>
                </div>
              </div>
            </article>
          </div>
        </c:forEach>
      </div>
    </c:otherwise>
  </c:choose>
  <div class="text-end mt-4"><span class="m02-help">${completedCount} completed wedding plan(s) • All budgets shown in LKR</span></div>
</div>
<script src="${m02Ctx}/js/member02-planner.js?v=20261108-v7" defer></script>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
