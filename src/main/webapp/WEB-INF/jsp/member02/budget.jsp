<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/member02-planner.css?v=20261108-v7">
<div class="m02 container-fluid px-0">
  <jsp:include page="/WEB-INF/jsp/member02/_plan-nav.jsp"><jsp:param name="tab" value="budget"/><jsp:param name="title" value="Budget tracker"/></jsp:include>

  <div class="row g-3 mb-3">
    <div class="col-6 col-xl-3"><div class="m02-stat"><div class="m02-stat-icon"><i class="bi bi-wallet2"></i></div><div class="label">Total budget</div><div class="value" style="font-size:1.25rem">LKR <fmt:formatNumber value="${budget.totalBudget}" groupingUsed="true" maxFractionDigits="0"/></div></div></div>
    <div class="col-6 col-xl-3"><div class="m02-stat"><div class="m02-stat-icon"><i class="bi bi-clipboard-data"></i></div><div class="label">Planned (allocated)</div><div class="value" style="font-size:1.25rem">LKR <fmt:formatNumber value="${budget.totalPlanned}" groupingUsed="true" maxFractionDigits="0"/></div><div class="m02-track mt-2"><span style="width:${budget.allocatedBarPercent}%"></span></div><div class="sub mt-1">${budget.allocatedPercent}% of budget</div></div></div>
    <div class="col-6 col-xl-3"><div class="m02-stat"><div class="m02-stat-icon"><i class="bi bi-receipt"></i></div><div class="label">Spent so far</div><div class="value" style="font-size:1.25rem">LKR <fmt:formatNumber value="${budget.totalSpent}" groupingUsed="true" maxFractionDigits="0"/></div><div class="m02-track mt-2"><span style="width:${budget.spentBarPercent}%"></span></div><div class="sub mt-1">${budget.spentPercent}% of budget</div></div></div>
    <div class="col-6 col-xl-3"><div class="m02-stat"><div class="m02-stat-icon"><i class="bi bi-piggy-bank"></i></div><div class="label">Remaining to spend</div><div class="value ${budget.spendingOverBudget ? 'm02-over' : ''}" style="font-size:1.25rem">LKR <fmt:formatNumber value="${budget.remaining}" groupingUsed="true" maxFractionDigits="0"/></div><div class="sub">Unallocated: LKR <fmt:formatNumber value="${budget.unallocated}" groupingUsed="true" maxFractionDigits="0"/></div></div></div>
  </div>

  <c:if test="${budget.spendingOverBudget}"><div class="m02-warn bad mb-2"><i class="bi bi-exclamation-triangle-fill"></i> You have spent more than your total budget. Review your costs or raise the budget in the plan details.</div></c:if>
  <c:if test="${budget.allocationOverBudget && !budget.spendingOverBudget}"><div class="m02-warn mb-2"><i class="bi bi-exclamation-triangle"></i> The planned amounts add up to more than your total budget.</div></c:if>
  <c:if test="${plan.guestCount > 0}"><div class="m02-help mb-3"><i class="bi bi-calculator"></i> Typical cost for a ${plan.typeLabel} with ${plan.guestCount} guests: LKR <fmt:formatNumber value="${plan.estimatedCost}" groupingUsed="true" maxFractionDigits="0"/> — ${plan.budgetOutlookLabel}.</div></c:if>

  <div class="row g-4">
    <div class="col-xl-8">
      <section class="m02-panel mb-4">
        <h2 class="m02-heading mb-3">Where your budget goes</h2>
        <div data-m02-donut data-money="true" data-aria="Planned budget by category"
             data-values="<c:out value='${chartValues}'/>" data-labels="<c:out value='${chartLabels}'/>" data-colors="<c:out value='${chartColors}'/>"
             data-center="${budget.allocatedPercent}%" data-center-label="ALLOCATED" data-none="Add budget items to see the chart"></div>
        <c:if test="${not empty budget.categories}">
          <hr class="m02-divider">
          <c:forEach var="ct" items="${budget.categories}">
            <div class="m02-catbar">
              <div class="head"><span><i class="bi ${ct.category.icon}" style="color:${ct.category.color}"></i> ${ct.category.label}</span><span><fmt:formatNumber value="${ct.spent}" groupingUsed="true" maxFractionDigits="0"/> / <fmt:formatNumber value="${ct.planned}" groupingUsed="true" maxFractionDigits="0"/></span></div>
              <div class="m02-track"><span class="${ct.overBudget ? 'over' : ''}" style="width:${ct.barPercent}%"></span></div>
            </div>
          </c:forEach>
        </c:if>
      </section>

      <section class="m02-panel">
        <h2 class="m02-heading mb-3">Budget items</h2>
        <c:if test="${empty items}"><div class="m02-empty"><i class="bi bi-wallet2"></i><h3>No budget items yet</h3><p>Add the first cost, such as the venue deposit or photography package.</p></div></c:if>
        <c:if test="${not empty items}">
          <div class="m02-table-wrap"><table class="m02-table">
            <thead><tr><th>Category</th><th>Item</th><th class="num">Planned</th><th class="num">Spent</th><th class="num">Remaining</th><c:if test="${plan.status == 'ACTIVE'}"><th></th></c:if></tr></thead>
            <tbody>
              <c:forEach var="i" items="${items}">
                <tr>
                  <td><i class="bi ${i.category.icon}" style="color:${i.category.color}"></i> ${i.category.label}</td>
                  <td><strong><c:out value="${i.itemName}"/></strong><c:if test="${not empty i.note}"><div class="m02-help"><c:out value="${i.note}"/></div></c:if></td>
                  <td class="num"><fmt:formatNumber value="${i.plannedAmount}" groupingUsed="true" maxFractionDigits="2"/></td>
                  <td class="num"><fmt:formatNumber value="${i.spentAmount}" groupingUsed="true" maxFractionDigits="2"/></td>
                  <td class="num ${i.overBudget ? 'm02-over' : 'm02-ok'}"><fmt:formatNumber value="${i.remaining}" groupingUsed="true" maxFractionDigits="2"/></td>
                  <c:if test="${plan.status == 'ACTIVE'}">
                    <td class="num">
                      <div class="d-inline-flex gap-1">
                        <button type="button" class="m02-btn m02-btn-ghost m02-btn-sm" title="Edit item" data-m02-modal-open="m02BudgetModal"
                                data-f-itemid="<c:out value='${i.itemId}'/>" data-f-category="${i.category.code}" data-f-itemname="<c:out value='${i.itemName}'/>"
                                data-f-plannedamount="${i.plannedAmount}" data-f-spentamount="${i.spentAmount}" data-f-note="<c:out value='${i.note}'/>"><i class="bi bi-pencil"></i></button>
                        <form action="${pageContext.request.contextPath}/plans/${plan.planId}/budget/delete" method="post" data-m02-confirm="Delete this budget item?"><input type="hidden" name="itemId" value="<c:out value='${i.itemId}'/>"><button class="m02-btn m02-btn-danger m02-btn-sm" title="Delete item"><i class="bi bi-trash"></i></button></form>
                      </div>
                    </td>
                  </c:if>
                </tr>
              </c:forEach>
            </tbody>
            <tfoot><tr><td colspan="2">Total</td><td class="num"><fmt:formatNumber value="${budget.totalPlanned}" groupingUsed="true" maxFractionDigits="2"/></td><td class="num"><fmt:formatNumber value="${budget.totalSpent}" groupingUsed="true" maxFractionDigits="2"/></td><td class="num"><fmt:formatNumber value="${budget.totalPlanned - budget.totalSpent}" groupingUsed="true" maxFractionDigits="2"/></td><c:if test="${plan.status == 'ACTIVE'}"><td></td></c:if></tr></tfoot>
          </table></div>
        </c:if>
      </section>
    </div>

    <div class="col-xl-4">
      <section class="m02-panel">
        <h2 class="m02-heading mb-1">Add a budget item</h2>
        <p class="m02-lead">All amounts are in Sri Lankan Rupees (LKR).</p>
        <c:choose>
          <c:when test="${plan.status == 'ACTIVE'}">
            <form action="${pageContext.request.contextPath}/plans/${plan.planId}/budget/add" method="post">
              <div class="mb-3"><label for="m02BCat" class="m02-label">Category</label><select id="m02BCat" name="category" class="form-select"><c:forEach var="cat" items="${categories}"><option value="${cat.code}">${cat.label}</option></c:forEach></select></div>
              <div class="mb-3"><label for="m02BName" class="m02-label">Item *</label><input id="m02BName" name="itemName" class="form-control" required maxlength="100" placeholder="e.g. Venue deposit"></div>
              <div class="mb-3"><label for="m02BPlanned" class="m02-label">Planned amount *</label><input id="m02BPlanned" name="plannedAmount" class="form-control" type="number" step="0.01" min="0" max="999999999999" required></div>
              <div class="mb-3"><label for="m02BSpent" class="m02-label">Spent so far</label><input id="m02BSpent" name="spentAmount" class="form-control" type="number" step="0.01" min="0" max="999999999999" value="0"></div>
              <div class="mb-4"><label for="m02BNote" class="m02-label">Note</label><input id="m02BNote" name="note" class="form-control" maxlength="300" placeholder="Optional"></div>
              <button class="m02-btn w-100" type="submit"><i class="bi bi-plus-circle"></i> Add item</button>
            </form>
          </c:when>
          <c:otherwise><p class="m02-help">The budget cannot be changed for this plan.</p></c:otherwise>
        </c:choose>
      </section>
    </div>
  </div>

  <div id="m02BudgetModal" class="m02-modal-bg" hidden>
    <section class="m02-modal" role="dialog" aria-modal="true" aria-labelledby="m02BudgetModalTitle">
      <div class="d-flex justify-content-between align-items-center mb-3"><h2 class="m02-heading" id="m02BudgetModalTitle">Edit budget item</h2><button type="button" class="m02-btn m02-btn-ghost m02-btn-sm" data-m02-close aria-label="Close">×</button></div>
      <form action="${pageContext.request.contextPath}/plans/${plan.planId}/budget/edit" method="post">
        <input type="hidden" name="itemId">
        <div class="mb-3"><label class="m02-label">Category</label><select name="category" class="form-select"><c:forEach var="cat" items="${categories}"><option value="${cat.code}">${cat.label}</option></c:forEach></select></div>
        <div class="mb-3"><label class="m02-label">Item *</label><input class="form-control" type="text" name="itemName" required maxlength="100"></div>
        <div class="row g-3 mb-3"><div class="col-6"><label class="m02-label">Planned *</label><input class="form-control" type="number" step="0.01" min="0" max="999999999999" name="plannedAmount" required></div><div class="col-6"><label class="m02-label">Spent</label><input class="form-control" type="number" step="0.01" min="0" max="999999999999" name="spentAmount"></div></div>
        <div class="mb-4"><label class="m02-label">Note</label><input class="form-control" name="note" maxlength="300"></div>
        <div class="d-flex justify-content-end gap-2"><button type="button" class="m02-btn m02-btn-ghost" data-m02-close>Cancel</button><button type="submit" class="m02-btn"><i class="bi bi-check2"></i> Save item</button></div>
      </form>
    </section>
  </div>
</div>
<script src="${pageContext.request.contextPath}/js/member02-planner.js?v=20261108-v7" defer></script>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
