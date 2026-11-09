<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/member02-planner.css?v=20261108-v7">
<div class="m02 container-fluid px-0">
  <jsp:include page="/WEB-INF/jsp/member02/_plan-nav.jsp"><jsp:param name="tab" value="timeline"/><jsp:param name="title" value="Planning timeline"/></jsp:include>
  <section class="m02-panel">
    <h2 class="m02-heading mb-1">Your road to the big day</h2>
    <p class="m02-lead mb-4">Checklist deadlines in date order, ending with your wedding day. ${summary.completedTasks} of ${summary.totalTasks} tasks done.</p>
    <div class="m02-timeline">
      <c:forEach var="entry" items="${timeline}">
        <div class="m02-tl-month"><c:out value="${entry.key}"/></div>
        <c:forEach var="e" items="${entry.value}">
          <div class="m02-tl-item ${e.wedding ? 'wedding' : (e.done ? 'done' : (e.overdue ? 'overdue' : ''))}">
            <div class="d-flex justify-content-between flex-wrap gap-2">
              <div>
                <div class="tl-date">${e.shortDate}</div>
                <div class="tl-title"><c:if test="${e.wedding}"><i class="bi bi-heart-fill" style="color:#b9788b"></i> </c:if><c:out value="${e.title}"/></div>
                <c:if test="${not empty e.detail}"><div class="m02-help"><c:out value="${e.detail}"/></div></c:if>
              </div>
              <div class="d-flex flex-wrap gap-2 align-items-start">
                <c:if test="${!e.wedding}"><span class="m02-chip ${e.priority.toLowerCase()}">${e.priority}</span><span class="m02-chip ${e.status.toLowerCase()}">${e.status}</span></c:if>
                <span class="m02-help">${e.whenLabel}</span>
              </div>
            </div>
          </div>
        </c:forEach>
      </c:forEach>
    </div>
  </section>
</div>
<script src="${pageContext.request.contextPath}/js/member02-planner.js?v=20261108-v7" defer></script>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
