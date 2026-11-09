<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- Shared header + tab bar for the plan workspace pages. Needs ${plan}; reads param.tab and param.title. --%>
<c:set var="m02Base" value="${pageContext.request.contextPath}/plans/${plan.planId}"/>
<a href="${pageContext.request.contextPath}/plans" class="d-inline-block mb-3 text-decoration-none m02-noprint" style="color:#8b6d3b"><i class="bi bi-arrow-left"></i> My Wedding Plans</a>
<section class="m02-hero m02-check-hero mb-3">
  <div>
    <div class="m02-eyebrow">Éclat • <c:out value="${param.title}"/></div>
    <h1 class="m02-hero-title"><c:out value="${plan.planName}"/></h1>
    <p><i class="bi bi-calendar-heart"></i> <c:out value="${plan.weddingDate}"/> &nbsp;•&nbsp; <i class="bi bi-geo-alt"></i> <c:out value="${plan.venue}"/></p>
    <div class="d-flex flex-wrap gap-2 mt-3">
      <span class="m02-pill type"><i class="bi ${plan.typeIcon}"></i> ${plan.typeLabel}</span>
      <span class="m02-pill ${plan.countdownUrgency}"><i class="bi bi-hourglass-split"></i> ${plan.countdownLabel}</span>
    </div>
  </div>
  <div class="m02-hero-right"><span class="m02-status ${plan.status == 'ACTIVE' ? 'active' : (plan.status == 'COMPLETED' ? 'completed' : 'cancelled')}"><c:out value="${plan.status}"/></span></div>
</section>
<nav class="m02-tabs m02-noprint" aria-label="Wedding plan sections">
  <a class="${param.tab == 'checklist' ? 'active' : ''}" href="${m02Base}/checklist"><i class="bi bi-list-check"></i> Checklist</a>
  <a class="${param.tab == 'budget' ? 'active' : ''}" href="${m02Base}/budget"><i class="bi bi-wallet2"></i> Budget</a>
  <a class="${param.tab == 'guests' ? 'active' : ''}" href="${m02Base}/guests"><i class="bi bi-people"></i> Guests</a>
  <a class="${param.tab == 'timeline' ? 'active' : ''}" href="${m02Base}/timeline"><i class="bi bi-calendar3"></i> Timeline</a>
  <a class="${param.tab == 'summary' ? 'active' : ''}" href="${m02Base}/summary"><i class="bi bi-file-earmark-text"></i> Summary</a>
</nav>
<c:if test="${plan.status != 'ACTIVE'}"><div class="alert alert-warning m02-noprint"><i class="bi bi-info-circle"></i> This plan is ${plan.status}. You can view it, but editing is disabled.</div></c:if>
