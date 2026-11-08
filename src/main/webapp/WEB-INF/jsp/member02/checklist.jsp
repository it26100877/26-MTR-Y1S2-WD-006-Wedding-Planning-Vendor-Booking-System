<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/member02-planner.css?v=20261108-v7">
<div class="m02 container-fluid px-0">
  <jsp:include page="/WEB-INF/jsp/member02/_plan-nav.jsp"><jsp:param name="tab" value="checklist"/><jsp:param name="title" value="Wedding checklist"/></jsp:include>

  <div class="row g-3 mb-4">
    <div class="col-6 col-lg-3"><div class="m02-stat"><div class="m02-stat-icon"><i class="bi bi-list-ul"></i></div><div class="label">Total tasks</div><div class="value">${summary.totalTasks}</div></div></div>
    <div class="col-6 col-lg-3"><div class="m02-stat"><div class="m02-stat-icon"><i class="bi bi-check-circle"></i></div><div class="label">Completed</div><div class="value">${summary.completedTasks}</div></div></div>
    <div class="col-6 col-lg-3"><div class="m02-stat"><div class="m02-stat-icon"><i class="bi bi-arrow-repeat"></i></div><div class="label">In progress</div><div class="value">${summary.inProgressTasks}</div></div></div>
    <div class="col-6 col-lg-3"><div class="m02-stat"><div class="m02-stat-icon"><i class="bi bi-exclamation-circle"></i></div><div class="label">Overdue</div><div class="value">${summary.overdueTasks}</div></div></div>
  </div>

  <c:if test="${plan.status == 'ACTIVE'}">
    <section class="m02-reminder-board mb-4" aria-labelledby="m02RemindersHeading">
      <div class="m02-reminder-head">
        <div>
          <span class="m02-kicker"><i class="bi bi-bell"></i> Your task reminders</span>
          <h2 class="m02-heading" id="m02RemindersHeading">Deadlines to keep in mind</h2>
          <p class="m02-lead mb-0">Automatic in-app reminders based on checklist due dates. No notifications are sent.</p>
        </div>
        <span class="m02-reminder-total" aria-label="Tasks needing attention">${summary.reminderCount} to review</span>
      </div>
      <div class="m02-reminder-grid">
        <div class="m02-reminder-count m02-reminder-count--late"><i class="bi bi-exclamation-triangle"></i><strong>${summary.overdueTasks}</strong><span>Overdue</span></div>
        <div class="m02-reminder-count m02-reminder-count--today"><i class="bi bi-clock-history"></i><strong>${summary.dueTodayTasks}</strong><span>Due today</span></div>
        <div class="m02-reminder-count"><i class="bi bi-calendar3-event"></i><strong>${summary.dueInThreeDaysTasks}</strong><span>Next 3 days</span></div>
        <div class="m02-reminder-count"><i class="bi bi-calendar-week"></i><strong>${summary.dueInSevenDaysTasks}</strong><span>Days 4–7</span></div>
      </div>
      <c:if test="${summary.reminderCount == 0}">
        <p class="m02-reminder-clear mb-0"><i class="bi bi-check-circle"></i> All clear — no overdue tasks or deadlines within the next 7 days.</p>
      </c:if>
    </section>
  </c:if>

  <section class="m02-panel mb-4">
    <div class="row g-4 align-items-center">
      <div class="col-md-7">
        <div class="d-flex align-items-center justify-content-between mb-3 gap-2">
          <div><h2 class="m02-heading">Preparation progress</h2><div class="m02-lead">${summary.completedTasks} out of ${summary.totalTasks} tasks complete</div></div>
          <div style="font-size:1.8rem;font-weight:800;color:#7e3b58">${summary.progressPercent}%</div>
        </div>
        <div class="m02-track" style="height:14px"><span style="width:${summary.progressPercent}%"></span></div>
      </div>
      <div class="col-md-5">
        <div data-m02-donut data-aria="Tasks by status"
             data-values="${summary.completedTasks},${summary.inProgressTasks},${summary.pendingTasks},${summary.overdueTasks}"
             data-labels="Completed|In progress|Pending|Overdue"
             data-colors="#4c9a6b,#4f7cac,#b9788b,#c4561a"
             data-center="${summary.totalTasks}" data-center-label="TASKS" data-none="No tasks yet"></div>
      </div>
    </div>
  </section>

  <div class="row g-4">
    <div class="col-xl-8">
      <section class="m02-panel">
        <div class="d-flex align-items-center justify-content-between mb-3">
          <div><h2 class="m02-heading">Your planning checklist</h2><p class="m02-lead mb-0">One lovely detail at a time.</p></div>
          <span class="m02-help">${summary.totalTasks} tasks</span>
        </div>

        <c:if test="${not empty tasks}">
          <div data-m02-filterbar data-target="[data-m02-task]" data-empty="#m02NoMatch">
            <div class="m02-filter-tabs">
              <button type="button" class="active" data-filter="ALL">All</button>
              <button type="button" data-filter="PENDING">Pending</button>
              <button type="button" data-filter="IN_PROGRESS">In progress</button>
              <button type="button" data-filter="COMPLETED">Completed</button>
              <button type="button" data-filter="OVERDUE">Overdue</button>
              <button type="button" data-filter="REMINDERS"><i class="bi bi-bell"></i> Needs attention (${summary.reminderCount})</button>
            </div>
            <div class="mb-3"><label class="visually-hidden" for="m02TaskSearch">Search tasks</label><input id="m02TaskSearch" class="form-control" data-m02-q placeholder="Search tasks"></div>
          </div>
          <div id="m02NoMatch" class="m02-help mb-3" hidden>No tasks match this filter.</div>
        </c:if>

        <c:if test="${empty tasks}">
          <div class="m02-empty"><i class="bi bi-journal-check"></i><h3>Ready to start planning?</h3><p>Add your first task, or load the recommended checklist for a ${plan.typeLabel}.</p></div>
        </c:if>

        <c:forEach var="t" items="${tasks}">
          <article class="m02-task ${t.completed ? 'done' : ''}" data-m02-task data-status="${t.displayStatus}" data-reminder="${t.reminderCategory}">
            <div class="d-flex justify-content-between align-items-start gap-3 flex-wrap">
              <div style="min-width:0;flex:1">
                <div class="task-title"><c:out value="${t.taskName}"/></div>
                <div class="m02-help mt-1"><c:out value="${t.description}"/></div>
                <div class="d-flex flex-wrap gap-2 align-items-center mt-3">
                  <span class="m02-chip ${t.priority.toLowerCase()}">${t.priority} priority</span>
                  <span class="m02-chip ${t.displayStatus.toLowerCase()}">${t.displayStatus}</span>
                  <span class="m02-help"><i class="bi bi-calendar3"></i> <c:out value="${t.deadline}"/> &nbsp;•&nbsp; ${t.dueLabel}</span>
                  <c:if test="${plan.status == 'ACTIVE' && t.reminderDue}">
                    <span class="m02-reminder-tag ${t.reminderCategory.toLowerCase()}"><i class="bi bi-bell-fill"></i>
                      <c:choose>
                        <c:when test="${t.reminderCategory == 'OVERDUE'}">Overdue reminder</c:when>
                        <c:when test="${t.reminderCategory == 'TODAY'}">Due today</c:when>
                        <c:when test="${t.reminderCategory == 'THREE_DAYS'}">Due within 3 days</c:when>
                        <c:otherwise>Due within 7 days</c:otherwise>
                      </c:choose>
                    </span>
                  </c:if>
                </div>
              </div>
              <c:if test="${plan.status == 'ACTIVE'}">
                <div class="d-flex flex-wrap gap-1">
                  <c:if test="${!t.completed}">
                    <c:if test="${t.status != 'IN_PROGRESS'}">
                      <form action="${pageContext.request.contextPath}/plans/${plan.planId}/checklist/status" method="post"><input type="hidden" name="taskId" value="<c:out value='${t.taskId}'/>"><input type="hidden" name="status" value="IN_PROGRESS"><button class="m02-btn m02-btn-ghost m02-btn-sm" type="submit" title="Start task" aria-label="Start task"><i class="bi bi-play-fill"></i></button></form>
                    </c:if>
                    <form action="${pageContext.request.contextPath}/plans/${plan.planId}/checklist/status" method="post"><input type="hidden" name="taskId" value="<c:out value='${t.taskId}'/>"><input type="hidden" name="status" value="COMPLETED"><button class="m02-btn m02-btn-ghost m02-btn-sm" type="submit" title="Mark completed" aria-label="Mark task completed"><i class="bi bi-check-lg"></i></button></form>
                  </c:if>
                  <c:if test="${t.completed}">
                    <form action="${pageContext.request.contextPath}/plans/${plan.planId}/checklist/status" method="post"><input type="hidden" name="taskId" value="<c:out value='${t.taskId}'/>"><input type="hidden" name="status" value="PENDING"><button class="m02-btn m02-btn-ghost m02-btn-sm" type="submit" title="Re-open task" aria-label="Re-open task"><i class="bi bi-arrow-counterclockwise"></i></button></form>
                  </c:if>
                  <button class="m02-btn m02-btn-ghost m02-btn-sm" type="button" title="Edit task" aria-label="Edit checklist task" data-m02-modal-open="m02TaskModal"
                          data-f-taskid="<c:out value='${t.taskId}'/>" data-f-taskname="<c:out value='${t.taskName}'/>"
                          data-f-description="<c:out value='${t.description}'/>" data-f-deadline="<c:out value='${t.deadline}'/>"
                          data-f-priority="<c:out value='${t.priority}'/>"><i class="bi bi-pencil"></i></button>
                  <form action="${pageContext.request.contextPath}/plans/${plan.planId}/checklist/delete" method="post" data-m02-confirm="Delete this checklist task permanently? This cannot be undone."
                        data-m02-task-name="<c:out value='${t.taskName}'/>"><input type="hidden" name="taskId" value="<c:out value='${t.taskId}'/>"><button class="m02-btn m02-btn-danger m02-btn-sm" type="submit" title="Delete task" aria-label="Delete checklist task"><i class="bi bi-trash"></i></button></form>
                </div>
              </c:if>
            </div>
          </article>
        </c:forEach>
      </section>
    </div>

    <div class="col-xl-4">
      <section class="m02-panel">
        <h2 class="m02-heading mb-1">Add a new task</h2>
        <p class="m02-lead">Turn your wedding vision into achievable steps.</p>
        <c:choose>
          <c:when test="${plan.status == 'ACTIVE'}">
            <form action="${pageContext.request.contextPath}/plans/${plan.planId}/checklist/add" method="post">
              <div class="mb-3"><label for="m02TaskName" class="m02-label">Task name *</label><input id="m02TaskName" class="form-control" name="taskName" required maxlength="120" placeholder="e.g. Book the photographer"></div>
              <div class="mb-3"><label for="m02TaskDesc" class="m02-label">Description</label><textarea id="m02TaskDesc" class="form-control" name="description" rows="3" maxlength="800" placeholder="Notes, details or reminders"></textarea></div>
              <div class="mb-3"><label for="m02TaskDeadline" class="m02-label">Due date * <span class="m02-help">(on or before ${plan.weddingDate})</span></label><input id="m02TaskDeadline" class="form-control" name="deadline" type="date" max="<c:out value='${plan.weddingDate}'/>" required></div>
              <div class="mb-4"><label for="m02TaskPriority" class="m02-label">Priority</label><select id="m02TaskPriority" class="form-select" name="priority"><option value="HIGH">High — important</option><option value="MEDIUM" selected>Medium — normal</option><option value="LOW">Low — flexible</option></select></div>
              <button class="m02-btn w-100" type="submit"><i class="bi bi-plus-circle"></i> Add to Checklist</button>
            </form>
          </c:when>
          <c:otherwise><p class="m02-help">Task creation is unavailable for this plan.</p></c:otherwise>
        </c:choose>
      </section>

      <section class="m02-panel mt-4" style="background:#fff7f7">
        <div class="m02-kicker">Recommended for you</div>
        <h3 class="m02-heading mt-2" style="font-size:1.2rem"><i class="bi ${plan.typeIcon}"></i> ${plan.typeLabel}</h3>
        <p class="m02-lead">${plan.specialConsiderations}</p>
        <details class="mb-3"><summary class="m02-help" style="cursor:pointer">See the ${plan.recommendedTaskCount} recommended tasks</summary>
          <ul class="m02-help mt-2 ps-3 mb-0">
            <c:forEach var="r" items="${plan.recommendedChecklist}"><li><c:out value="${r.name}"/> <span class="text-muted">(${r.daysBeforeWedding} days before)</span></li></c:forEach>
          </ul>
        </details>
        <c:if test="${plan.status == 'ACTIVE'}">
          <form action="${pageContext.request.contextPath}/plans/${plan.planId}/checklist/template" method="post">
            <button class="m02-btn m02-btn-ghost w-100" type="submit"><i class="bi bi-magic"></i> Add recommended tasks</button>
          </form>
          <div class="m02-help mt-2">Tasks already in your list are skipped. Due dates fit the time left before your wedding.</div>
        </c:if>
      </section>
    </div>
  </div>

  <div id="m02TaskModal" class="m02-modal-bg" hidden>
    <section class="m02-modal" role="dialog" aria-modal="true" aria-labelledby="m02ModalTitle">
      <div class="d-flex justify-content-between align-items-center mb-3"><h2 class="m02-heading" id="m02ModalTitle">Edit checklist task</h2><button type="button" class="m02-btn m02-btn-ghost m02-btn-sm" data-m02-close aria-label="Close">×</button></div>
      <form action="${pageContext.request.contextPath}/plans/${plan.planId}/checklist/edit" method="post">
        <input type="hidden" name="taskId">
        <div class="mb-3"><label class="m02-label">Task name *</label><input class="form-control" type="text" name="taskName" required maxlength="120"></div>
        <div class="mb-3"><label class="m02-label">Description</label><textarea class="form-control" name="description" maxlength="800" rows="3"></textarea></div>
        <div class="mb-3"><label class="m02-label">Deadline *</label><input class="form-control" type="date" name="deadline" max="<c:out value='${plan.weddingDate}'/>" required></div>
        <div class="mb-4"><label class="m02-label">Priority</label><select name="priority" class="form-select"><option value="HIGH">High</option><option value="MEDIUM">Medium</option><option value="LOW">Low</option></select></div>
        <div class="d-flex justify-content-end gap-2"><button type="button" class="m02-btn m02-btn-ghost" data-m02-close>Cancel</button><button type="submit" class="m02-btn"><i class="bi bi-check2"></i> Save task</button></div>
      </form>
    </section>
  </div>
</div>
<script src="${pageContext.request.contextPath}/js/member02-planner.js?v=20261108-v7" defer></script>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
