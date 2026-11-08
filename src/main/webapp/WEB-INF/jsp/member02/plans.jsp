<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h3 class="fw-bold" style="color: #8d6a22;"><i class="bi bi-calendar-heart"></i> Wedding Plans</h3>
        <p class="text-secondary small mb-0">Organize your wedding timeline, theme, venue, and overall budget</p>
    </div>
    <a href="${pageContext.request.contextPath}/plans/new" class="btn btn-purple shadow-sm">
        <i class="bi bi-plus-circle me-1"></i> Create New Plan
    </a>
</div>

<div class="card card-custom p-4 shadow-sm">
    <div class="table-responsive">
        <table class="table table-hover align-middle">
            <thead class="table-custom">
                <tr>
                    <th>Plan ID</th>
                    <th>Plan Name</th>
                    <th>Wedding Date</th>
                    <th>Venue</th>
                    <th>Theme</th>
                    <th>Budget</th>
                    <th>Status</th>
                    <th class="text-end">Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="p" items="${plans}">
                    <tr>
                        <td class="fw-semibold">${p.planId}</td>
                        <td class="fw-bold" style="color: #8d6a22;">${p.planName}</td>
                        <td><i class="bi bi-calendar3 me-1 text-muted"></i> ${p.weddingDate}</td>
                        <td><i class="bi bi-geo-alt me-1 text-muted"></i> ${p.venue}</td>
                        <td><span class="badge bg-light text-dark border">${p.theme}</span></td>
                        <td class="fw-bold text-success">LKR ${p.totalBudget}</td>
                        <td>
                            <c:choose>
                                <c:when test="${p.status == 'ACTIVE'}"><span class="badge badge-active">Active</span></c:when>
                                <c:when test="${p.status == 'COMPLETED'}"><span class="badge badge-completed">Completed</span></c:when>
                                <c:otherwise><span class="badge badge-cancelled">${p.status}</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td class="text-end">
                            <a href="${pageContext.request.contextPath}/plans/${p.planId}/checklist" class="btn btn-sm btn-outline-primary me-1" title="Manage Checklist">
                                <i class="bi bi-card-checklist"></i> Checklist
                            </a>
                            <a href="${pageContext.request.contextPath}/plans/edit/${p.planId}" class="btn btn-sm btn-outline-secondary me-1" title="Edit Plan">
                                <i class="bi bi-pencil"></i>
                            </a>
                            <c:if test="${p.status == 'ACTIVE'}">
                                <form action="${pageContext.request.contextPath}/plans/cancel/${p.planId}" method="post" style="display:inline;" onsubmit="return confirm('Are you sure you want to cancel this plan?');">
                                    <button type="submit" class="btn btn-sm btn-outline-warning me-1" title="Cancel Plan">
                                        <i class="bi bi-x-circle"></i>
                                    </button>
                                </form>
                            </c:if>
                            <form action="${pageContext.request.contextPath}/plans/delete/${p.planId}" method="post" style="display:inline;" onsubmit="return confirm('Are you sure you want to delete this plan?');">
                                <button type="submit" class="btn btn-sm btn-outline-danger" title="Delete Plan">
                                    <i class="bi bi-trash"></i>
                                </button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty plans}">
                    <tr>
                        <td colspan="8" class="text-center py-5">
                            <i class="bi bi-calendar-x fs-1 text-muted d-block mb-2"></i>
                            <span class="text-muted">No wedding plans found.</span><br>
                            <a href="${pageContext.request.contextPath}/plans/new" class="btn btn-purple btn-sm mt-3">Start Planning Now</a>
                        </td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />

