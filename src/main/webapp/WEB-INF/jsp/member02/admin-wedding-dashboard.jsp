<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/member02-planner.css?v=20261108-v7">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/member02-admin-dashboard.css?v=1">
<c:set var="base" value="${pageContext.request.contextPath}"/>

<div class="m02 m02-admin container-fluid px-0">
  <div class="m02-admin-hero mb-4">
    <div class="m02-admin-hero-copy">
      <span class="m02-admin-eyebrow"><i class="bi bi-shield-lock"></i> Éclat · Administration / Member 02</span>
      <h1>Wedding Management <em>Overview</em></h1>
      <p>Monitor every celebration, review planning progress and manage customer wedding plans from one elegant workspace.</p>
      <div class="m02-admin-hero-links">
        <a href="${base}/plans" class="m02-admin-primary"><i class="bi bi-grid"></i> All Wedding Plans</a>
        <a href="${base}/admin/dashboard" class="m02-admin-secondary"><i class="bi bi-arrow-left"></i> System Administration</a>
      </div>
    </div>
    <div class="m02-admin-hero-art" aria-hidden="true"></div>
  </div>

  <div class="m02-admin-section-heading mb-3">
    <div><span class="m02-admin-eyebrow muted">LIVE OVERVIEW</span><h2>At a glance</h2></div>
    <span class="m02-admin-meta"><i class="bi bi-database"></i> From saved wedding records</span>
  </div>

  <div class="row g-3 mb-4">
    <div class="col-6 col-xl-3"><div class="m02-admin-stat"><span class="m02-admin-stat-icon"><i class="bi bi-calendar-heart"></i></span><div class="m02-admin-stat-name">Total Plans</div><div class="m02-admin-stat-value">${stats.totalCount}</div><div class="m02-admin-stat-foot">${stats.customerCount} wedding customers</div></div></div>
    <div class="col-6 col-xl-3"><div class="m02-admin-stat"><span class="m02-admin-stat-icon green"><i class="bi bi-stars"></i></span><div class="m02-admin-stat-name">Active Weddings</div><div class="m02-admin-stat-value">${stats.activeCount}</div><div class="m02-admin-stat-foot">Currently being planned</div></div></div>
    <div class="col-6 col-xl-3"><div class="m02-admin-stat"><span class="m02-admin-stat-icon blue"><i class="bi bi-check-circle"></i></span><div class="m02-admin-stat-name">Completed Weddings</div><div class="m02-admin-stat-value">${stats.completedCount}</div><div class="m02-admin-stat-foot">Celebrations completed</div></div></div>
    <div class="col-6 col-xl-3"><div class="m02-admin-stat"><span class="m02-admin-stat-icon rose"><i class="bi bi-x-circle"></i></span><div class="m02-admin-stat-name">Cancelled Weddings</div><div class="m02-admin-stat-value">${stats.cancelledCount}</div><div class="m02-admin-stat-foot">Cancelled wedding plans</div></div></div>
  </div>

  <div class="row g-3 mb-4">
    <div class="col-lg-7"><div class="m02-admin-summary"><div><span class="m02-admin-eyebrow muted">TOTAL PLANNED BUDGET</span><div class="m02-admin-big-num">LKR <fmt:formatNumber value="${stats.totalBudget}" groupingUsed="true" maxFractionDigits="0"/></div><p>Combined customer budgets, excluding cancelled plans.</p></div><i class="bi bi-wallet2 m02-admin-summary-watermark" aria-hidden="true"></i></div></div>
    <div class="col-lg-5"><div class="m02-admin-summary"><div class="w-100"><div class="d-flex justify-content-between align-items-center"><span class="m02-admin-eyebrow muted">OVERALL CHECKLIST PROGRESS</span><strong>${stats.overallProgress}%</strong></div><div class="m02-admin-progress mt-3" role="progressbar" aria-label="Overall checklist progress" aria-valuenow="${stats.overallProgress}" aria-valuemin="0" aria-valuemax="100"><span style="width:${stats.overallProgress}%"></span></div><p class="mt-3 mb-0">${stats.completedTaskCount} of ${stats.taskCount} tasks completed across all wedding plans.</p></div></div></div>
  </div>

  <div class="row g-4 align-items-start">
    <div class="col-xl-9">
      <section class="m02-admin-table-panel">
        <div class="m02-admin-panel-head">
          <div><span class="m02-admin-eyebrow muted">CUSTOMER MANAGEMENT</span><h2>All Wedding Plans</h2><p>Search by customer name, email, plan title or venue. Apply a status or wedding-type filter.</p></div>
          <span class="m02-admin-count">${stats.rows.size()} shown</span>
        </div>
        <form method="get" action="${base}/admin/wedding-plans" class="m02-admin-filters" role="search">
          <div class="m02-admin-search"><i class="bi bi-search"></i><input type="search" name="q" maxlength="100" value="<c:out value='${query}'/>" placeholder="Search weddings, customers, venues..." aria-label="Search wedding plans"></div>
          <select name="status" aria-label="Filter by status"><option value="ALL">All statuses</option><option value="ACTIVE" ${selectedStatus == 'ACTIVE' ? 'selected' : ''}>Active</option><option value="COMPLETED" ${selectedStatus == 'COMPLETED' ? 'selected' : ''}>Completed</option><option value="CANCELLED" ${selectedStatus == 'CANCELLED' ? 'selected' : ''}>Cancelled</option></select>
          <select name="type" aria-label="Filter by wedding type"><option value="ALL">All types</option><c:forEach var="t" items="${types}"><option value="${t.code}" ${selectedType == t.code ? 'selected' : ''}><c:out value="${t.label}"/></option></c:forEach></select>
          <select name="sort" aria-label="Sort wedding plans"><option value="soonest" ${selectedSort == 'soonest' ? 'selected' : ''}>Date: soonest</option><option value="latest" ${selectedSort == 'latest' ? 'selected' : ''}>Date: latest</option><option value="name" ${selectedSort == 'name' ? 'selected' : ''}>Name: A-Z</option><option value="budget" ${selectedSort == 'budget' ? 'selected' : ''}>Budget: high-low</option></select>
          <button class="m02-admin-filter-button" type="submit"><i class="bi bi-funnel"></i> Apply</button>
          <a class="m02-admin-clear" href="${base}/admin/wedding-plans" aria-label="Reset filters" title="Reset filters"><i class="bi bi-arrow-counterclockwise"></i></a>
        </form>
        <div class="m02-admin-table-scroll">
          <table class="m02-admin-table">
            <thead><tr><th>Wedding Plan</th><th>Customer</th><th>Wedding Date</th><th>Budget (LKR)</th><th>Status</th><th>Checklist</th><th>Manage</th></tr></thead>
            <tbody>
              <c:forEach var="r" items="${stats.rows}">
                <tr>
                  <td><div class="m02-admin-plan-cell"><img src="${base}/images/member02/${r.previewImage}" alt="" loading="lazy"><div><strong><c:out value="${r.plan.planName}"/></strong><small><c:out value="${r.plan.typeLabel}"/> · <c:out value="${r.plan.venue}"/></small></div></div></td>
                  <td><strong class="m02-admin-customer"><c:out value="${r.customerName}"/></strong><small class="m02-admin-muted"><c:out value="${r.customerEmail}"/></small><small class="m02-admin-muted"><c:out value="${r.plan.customerId}"/></small></td>
                  <td class="m02-admin-nowrap"><i class="bi bi-calendar3 me-1"></i><c:out value="${r.plan.weddingDate}"/></td>
                  <td class="m02-admin-nowrap"><fmt:formatNumber value="${r.plan.totalBudget}" groupingUsed="true" maxFractionDigits="0"/></td>
                  <td><span class="m02-admin-status ${r.plan.status == 'ACTIVE' ? 'active' : (r.plan.status == 'COMPLETED' ? 'completed' : 'cancelled')}"><c:out value="${r.plan.status}"/></span></td>
                  <td><div class="m02-admin-task-label">${r.completedTasks}/${r.taskCount} <strong>${r.progress}%</strong></div><div class="m02-admin-progress small"><span style="width:${r.progress}%"></span></div></td>
                  <td><div class="m02-admin-actions"><a href="${base}/plans/${r.plan.planId}/summary" class="m02-admin-action" title="View plan summary"><i class="bi bi-eye"></i> View</a><a href="${base}/plans/${r.plan.planId}/checklist" class="m02-admin-action" title="Checklist"><i class="bi bi-list-check"></i></a><c:if test="${r.plan.status == 'ACTIVE'}"><a class="m02-admin-action" href="${base}/plans/edit/${r.plan.planId}" title="Edit plan"><i class="bi bi-pencil"></i></a><form method="post" action="${base}/admin/wedding-plans/${r.plan.planId}/complete" data-m02-confirm="Mark this wedding as completed? It can no longer be edited."><button class="m02-admin-action" title="Complete plan" aria-label="Complete plan"><i class="bi bi-check2-circle"></i></button></form><form method="post" action="${base}/admin/wedding-plans/${r.plan.planId}/cancel" data-m02-confirm="Cancel this wedding plan?"><button class="m02-admin-action" title="Cancel plan" aria-label="Cancel plan"><i class="bi bi-x-circle"></i></button></form></c:if><form method="post" action="${base}/admin/wedding-plans/${r.plan.planId}/delete" data-m02-confirm="Permanently delete this plan and its Member 02 records? Linked vendor bookings will prevent deletion."><button class="m02-admin-action danger" title="Delete plan" aria-label="Delete plan"><i class="bi bi-trash"></i></button></form></div></td>
                </tr>
              </c:forEach>
              <c:if test="${empty stats.rows}"><tr><td colspan="7" class="m02-admin-empty"><i class="bi bi-search"></i><h3>No matching wedding plans</h3><p>Try a different search, type or status filter.</p><a href="${base}/admin/wedding-plans">Reset search</a></td></tr></c:if>
            </tbody>
          </table>
        </div>
        <div class="m02-admin-panel-foot"><i class="bi bi-shield-check"></i> Admin-only view. Delete is blocked if vendor bookings are linked to a plan.</div>
      </section>
    </div>
    <div class="col-xl-3">
      <aside class="m02-admin-upcoming">
        <div class="m02-admin-upcoming-head"><span class="m02-admin-eyebrow muted">COMING UP</span><h2>Next Celebrations</h2></div>
        <c:forEach var="r" items="${stats.upcoming}">
          <a class="m02-admin-upcoming-item" href="${base}/plans/${r.plan.planId}/summary"><div class="m02-admin-date-icon"><i class="bi bi-heart"></i></div><div><strong><c:out value="${r.plan.planName}"/></strong><span><c:out value="${r.customerName}"/></span><small><i class="bi bi-calendar-event"></i> <c:out value="${r.plan.weddingDate}"/></small></div></a>
        </c:forEach>
        <c:if test="${empty stats.upcoming}"><div class="m02-admin-upcoming-empty">No upcoming active weddings yet.</div></c:if>
        <div class="m02-admin-tip"><i class="bi bi-lightbulb"></i><div><strong>Planning tip</strong><p>Review approaching weddings and their incomplete checklists to stay on schedule.</p></div></div>
      </aside>
    </div>
  </div>
</div>
<script src="${base}/js/member02-planner.js?v=6" defer></script>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
