<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/member02-planner.css?v=20261108-v7">
<div class="m02 container-fluid px-0">
  <jsp:include page="/WEB-INF/jsp/member02/_plan-nav.jsp"><jsp:param name="tab" value="summary"/><jsp:param name="title" value="Plan summary"/></jsp:include>

  <div class="d-flex flex-wrap gap-2 justify-content-end mb-3 m02-noprint">
    <button type="button" class="m02-btn m02-btn-ghost" data-m02-print><i class="bi bi-printer"></i> Print</button>
    <a class="m02-btn" href="${pageContext.request.contextPath}/plans/${plan.planId}/export.csv"><i class="bi bi-download"></i> Download CSV</a>
  </div>

  <section class="m02-panel mb-4">
    <h2 class="m02-heading mb-3">Wedding details</h2>
    <div class="row g-3">
      <div class="col-md-6"><div class="m02-help">Wedding type</div><strong>${plan.typeLabel}</strong></div>
      <div class="col-md-6"><div class="m02-help">Countdown</div><strong>${plan.countdownLabel}</strong></div>
      <div class="col-md-6"><div class="m02-help">Date</div><strong><c:out value="${plan.weddingDate}"/></strong></div>
      <div class="col-md-6"><div class="m02-help">Venue</div><strong><c:out value="${plan.venue}"/></strong></div>
      <div class="col-md-6"><div class="m02-help">Theme</div><strong><c:out value="${plan.theme}" default="-"/></strong></div>
      <div class="col-md-6"><div class="m02-help">Expected guests</div><strong>${plan.guestCount}</strong> <span class="m02-help">(confirmed ${summary.guestStats.confirmedHeadcount}, awaiting ${summary.guestStats.pendingHeadcount}, declined ${summary.guestStats.declinedHeadcount})</span></div>
      <div class="col-md-6"><div class="m02-help">Total budget</div><strong>LKR <fmt:formatNumber value="${plan.totalBudget}" groupingUsed="true" maxFractionDigits="0"/></strong></div>
      <div class="col-md-6"><div class="m02-help">Typical cost estimate</div><strong>LKR <fmt:formatNumber value="${plan.estimatedCost}" groupingUsed="true" maxFractionDigits="0"/></strong> <span class="m02-help">${plan.budgetOutlookLabel}</span></div>
      <c:if test="${not empty plan.description}"><div class="col-12"><div class="m02-help">Notes</div><div><c:out value="${plan.description}"/></div></div></c:if>
    </div>
  </section>

  <section class="m02-panel mb-4">
    <div class="d-flex justify-content-between align-items-center mb-2"><h2 class="m02-heading">Checklist</h2><strong>${summary.progressPercent}% complete</strong></div>
    <div class="m02-track mb-3"><span style="width:${summary.progressPercent}%"></span></div>
    <c:if test="${empty summary.tasks}"><p class="m02-help mb-0">No tasks yet.</p></c:if>
    <c:if test="${not empty summary.tasks}">
      <div class="m02-table-wrap"><table class="m02-table">
        <thead><tr><th>Task</th><th>Deadline</th><th>Priority</th><th>Status</th></tr></thead>
        <tbody><c:forEach var="t" items="${summary.tasks}"><tr><td><c:out value="${t.taskName}"/></td><td><c:out value="${t.deadline}"/></td><td>${t.priority}</td><td>${t.displayStatus}</td></tr></c:forEach></tbody>
      </table></div>
    </c:if>
  </section>

  <section class="m02-panel mb-4">
    <h2 class="m02-heading mb-3">Budget</h2>
    <c:if test="${empty summary.budgetItems}"><p class="m02-help mb-0">No budget items yet.</p></c:if>
    <c:if test="${not empty summary.budgetItems}">
      <div class="m02-table-wrap"><table class="m02-table">
        <thead><tr><th>Category</th><th>Item</th><th class="num">Planned (LKR)</th><th class="num">Spent (LKR)</th></tr></thead>
        <tbody><c:forEach var="i" items="${summary.budgetItems}"><tr><td>${i.category.label}</td><td><c:out value="${i.itemName}"/></td><td class="num"><fmt:formatNumber value="${i.plannedAmount}" groupingUsed="true" maxFractionDigits="2"/></td><td class="num"><fmt:formatNumber value="${i.spentAmount}" groupingUsed="true" maxFractionDigits="2"/></td></tr></c:forEach></tbody>
        <tfoot><tr><td colspan="2">Total</td><td class="num"><fmt:formatNumber value="${summary.budget.totalPlanned}" groupingUsed="true" maxFractionDigits="2"/></td><td class="num"><fmt:formatNumber value="${summary.budget.totalSpent}" groupingUsed="true" maxFractionDigits="2"/></td></tr></tfoot>
      </table></div>
    </c:if>
  </section>

  <section class="m02-panel">
    <h2 class="m02-heading mb-3">Guests</h2>
    <c:if test="${empty summary.guests}"><p class="m02-help mb-0">No guests yet.</p></c:if>
    <c:if test="${not empty summary.guests}">
      <div class="m02-table-wrap"><table class="m02-table">
        <thead><tr><th>Name</th><th>Group</th><th class="num">Party</th><th>RSVP</th></tr></thead>
        <tbody><c:forEach var="g" items="${summary.guests}"><tr><td><c:out value="${g.guestName}"/></td><td>${g.group.label}</td><td class="num">${g.partySize}</td><td>${g.rsvpStatus}</td></tr></c:forEach></tbody>
      </table></div>
    </c:if>
  </section>
</div>
<script src="${pageContext.request.contextPath}/js/member02-planner.js?v=20261108-v7" defer></script>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
