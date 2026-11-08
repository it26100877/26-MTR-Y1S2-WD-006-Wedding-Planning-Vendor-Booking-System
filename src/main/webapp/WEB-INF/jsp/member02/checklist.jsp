<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-3">
    <div>
        <a href="${pageContext.request.contextPath}/plans" class="text-decoration-none text-secondary small">&larr; Back to Wedding Plans</a>
        <h3 class="fw-bold mb-0" style="color: #8d6a22;">
            <i class="bi bi-card-checklist"></i> Pre-Wedding Checklist: ${plan.planName}
        </h3>
        <span class="text-muted small"><i class="bi bi-calendar-event"></i> Event Date: ${plan.weddingDate} &bull; Budget: LKR ${plan.totalBudget}</span>
    </div>
</div>

<!-- Progress Card -->
<div class="card card-custom p-4 mb-4 shadow-sm">
    <div class="d-flex justify-content-between align-items-center mb-2">
        <span class="fw-bold text-secondary">Planning Completion Progress</span>
        <span class="fw-bold fs-5 text-purple" style="color: #8d6a22;">${progress}% Completed</span>
    </div>
    <div class="progress" style="height: 18px;">
        <div class="progress-bar progress-bar-striped progress-bar-animated bg-success"
             role="progressbar" style="width: ${progress}%;" aria-valuenow="${progress}" aria-valuemin="0" aria-valuemax="100">
            ${progress}%
        </div>
    </div>
</div>

<div class="row g-4">
    <!-- Tasks Table -->
    <div class="col-lg-8">
        <div class="card card-custom p-4 shadow-sm">
            <h5 class="fw-bold mb-3" style="color: #8d6a22;">Checklist Tasks</h5>
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead class="table-custom">
                        <tr>
                            <th>Task</th>
                            <th>Deadline</th>
                            <th>Priority</th>
                            <th>Status</th>
                            <th class="text-end">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="t" items="${tasks}">
                            <tr class="${t.status == 'COMPLETED' ? 'table-light text-decoration-line-through' : ''}">
                                <td>
                                    <div class="fw-bold">${t.taskName}</div>
                                    <small class="text-muted">${t.description}</small>
                                </td>
                                <td><i class="bi bi-clock me-1 text-muted"></i> ${t.deadline}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${t.priority == 'HIGH'}"><span class="badge bg-danger">High</span></c:when>
                                        <c:when test="${t.priority == 'MEDIUM'}"><span class="badge bg-warning text-dark">Medium</span></c:when>
                                        <c:otherwise><span class="badge bg-secondary">Low</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${t.status == 'COMPLETED'}"><span class="badge badge-completed">Completed</span></c:when>
                                        <c:when test="${t.status == 'IN_PROGRESS'}"><span class="badge bg-info text-dark">In Progress</span></c:when>
                                        <c:otherwise><span class="badge badge-pending">Pending</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-end">
                                    <c:if test="${t.status != 'COMPLETED'}">
                                        <form action="${pageContext.request.contextPath}/plans/${plan.planId}/checklist/status" method="post" style="display:inline;">
                                            <input type="hidden" name="taskId" value="${t.taskId}">
                                            <input type="hidden" name="status" value="COMPLETED">
                                            <button type="submit" class="btn btn-sm btn-outline-success me-1" title="Mark as Completed">
                                                <i class="bi bi-check-lg"></i>
                                            </button>
                                        </form>
                                    </c:if>
                                    <form action="${pageContext.request.contextPath}/plans/${plan.planId}/checklist/delete" method="post" style="display:inline;" onsubmit="return confirm('Delete task?');">
                                        <input type="hidden" name="taskId" value="${t.taskId}">
                                        <button type="submit" class="btn btn-sm btn-outline-danger" title="Delete Task">
                                            <i class="bi bi-trash"></i>
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty tasks}">
                            <tr><td colspan="5" class="text-center text-muted py-4">No tasks added to this checklist yet.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Add Task Card -->
    <div class="col-lg-4">
        <div class="card card-custom p-4 shadow-sm">
            <h5 class="fw-bold mb-3" style="color: #8d6a22;"><i class="bi bi-plus-circle"></i> Add New Task</h5>
            <form action="${pageContext.request.contextPath}/plans/${plan.planId}/checklist/add" method="post">
                <div class="mb-3">
                    <label class="form-label fw-semibold">Task Name *</label>
                    <input type="text" name="taskName" class="form-control" placeholder="Book Photographer" required>
                </div>
                <div class="mb-3">
                    <label class="form-label fw-semibold">Description</label>
                    <textarea name="description" class="form-control" rows="2" placeholder="Notes or instructions..."></textarea>
                </div>
                <div class="mb-3">
                    <label class="form-label fw-semibold">Target Deadline *</label>
                    <input type="date" name="deadline" class="form-control" required>
                </div>
                <div class="mb-3">
                    <label class="form-label fw-semibold">Priority Level</label>
                    <select name="priority" class="form-select">
                        <option value="HIGH">High Priority</option>
                        <option value="MEDIUM" selected>Medium Priority</option>
                        <option value="LOW">Low Priority</option>
                    </select>
                </div>
                <div class="d-grid">
                    <button type="submit" class="btn btn-purple fw-bold">Add to Checklist</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />

