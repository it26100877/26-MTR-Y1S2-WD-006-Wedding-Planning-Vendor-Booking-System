<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/member02-planner.css?v=20261108-v7">
<div class="m02 container-fluid px-0">
  <jsp:include page="/WEB-INF/jsp/member02/_plan-nav.jsp"><jsp:param name="tab" value="guests"/><jsp:param name="title" value="Guest list"/></jsp:include>

  <div class="row g-3 mb-3">
    <div class="col-6 col-xl-3"><div class="m02-stat"><div class="m02-stat-icon"><i class="bi bi-people"></i></div><div class="label">Total invited</div><div class="value">${stats.totalHeadcount}</div><div class="sub">${stats.entries} guest entries</div></div></div>
    <div class="col-6 col-xl-3"><div class="m02-stat"><div class="m02-stat-icon"><i class="bi bi-check-circle"></i></div><div class="label">Confirmed</div><div class="value">${stats.confirmedHeadcount}</div></div></div>
    <div class="col-6 col-xl-3"><div class="m02-stat"><div class="m02-stat-icon"><i class="bi bi-hourglass-split"></i></div><div class="label">Awaiting reply</div><div class="value">${stats.pendingHeadcount}</div></div></div>
    <div class="col-6 col-xl-3"><div class="m02-stat"><div class="m02-stat-icon"><i class="bi bi-x-circle"></i></div><div class="label">Declined</div><div class="value">${stats.declinedHeadcount}</div></div></div>
  </div>

  <c:if test="${stats.overExpected}"><div class="m02-warn mb-3"><i class="bi bi-exclamation-triangle"></i> Your guest list (${stats.attendingEstimate} attending) is larger than the expected guest count of ${stats.expectedGuests}. Check your venue capacity and budget.</div></c:if>

  <div class="row g-4">
    <div class="col-xl-8">
      <section class="m02-panel mb-4">
        <div class="row g-4 align-items-center">
          <div class="col-md-6">
            <h2 class="m02-heading mb-1">RSVP status</h2>
            <p class="m02-lead">${stats.responseRate}% of invited guests have replied.</p>
            <div class="m02-track mb-3"><span style="width:${stats.responseRate}%"></span></div>
            <div class="m02-help">Expected guests in plan: <strong>${plan.guestCount}</strong> &nbsp;•&nbsp; Attending estimate: <strong>${stats.attendingEstimate}</strong></div>
            <c:if test="${plan.status == 'ACTIVE' && stats.attendingEstimate > 0 && stats.attendingEstimate != plan.guestCount}">
              <form class="mt-3" action="${pageContext.request.contextPath}/plans/${plan.planId}/guests/sync" method="post"><button class="m02-btn m02-btn-ghost m02-btn-sm" type="submit"><i class="bi bi-arrow-repeat"></i> Set expected guests to ${stats.attendingEstimate}</button></form>
            </c:if>
          </div>
          <div class="col-md-6">
            <div data-m02-donut data-aria="Guests by RSVP"
                 data-values="${stats.confirmedHeadcount},${stats.pendingHeadcount},${stats.declinedHeadcount}"
                 data-labels="Confirmed|Awaiting reply|Declined" data-colors="#4c9a6b,#e0a458,#c46a7a"
                 data-center="${stats.totalHeadcount}" data-center-label="PEOPLE" data-none="No guests yet"></div>
          </div>
        </div>
      </section>

      <section class="m02-panel">
        <h2 class="m02-heading mb-3">Guest list</h2>
        <c:if test="${empty guests}"><div class="m02-empty"><i class="bi bi-people"></i><h3>Your guest list is empty</h3><p>Add family, friends and colleagues, then track who has replied.</p></div></c:if>
        <c:if test="${not empty guests}">
          <div data-m02-filterbar data-target="[data-m02-guest]" data-empty="#m02NoGuest">
            <div class="m02-filter-tabs">
              <button type="button" class="active" data-filter="ALL">All</button>
              <button type="button" data-filter="INVITED">Awaiting reply</button>
              <button type="button" data-filter="CONFIRMED">Confirmed</button>
              <button type="button" data-filter="DECLINED">Declined</button>
            </div>
            <div class="mb-3"><label class="visually-hidden" for="m02GuestSearch">Search guests</label><input id="m02GuestSearch" class="form-control" data-m02-q placeholder="Search by name, group or contact"></div>
          </div>
          <div id="m02NoGuest" class="m02-help mb-3" hidden>No guests match this filter.</div>
          <div class="m02-table-wrap"><table class="m02-table">
            <thead><tr><th>Guest</th><th>Group</th><th class="num">Party</th><th>RSVP</th><c:if test="${plan.status == 'ACTIVE'}"><th></th></c:if></tr></thead>
            <tbody>
              <c:forEach var="g" items="${guests}">
                <tr data-m02-guest data-status="${g.rsvpStatus}">
                  <td><strong><c:out value="${g.guestName}"/></strong><c:if test="${not empty g.contact}"><div class="m02-help"><c:out value="${g.contact}"/></div></c:if><c:if test="${not empty g.note}"><div class="m02-help"><c:out value="${g.note}"/></div></c:if></td>
                  <td>${g.group.label}</td>
                  <td class="num">${g.partySize}</td>
                  <td>
                    <c:choose>
                      <c:when test="${plan.status == 'ACTIVE'}">
                        <form action="${pageContext.request.contextPath}/plans/${plan.planId}/guests/rsvp" method="post" class="m-0">
                          <input type="hidden" name="guestId" value="<c:out value='${g.guestId}'/>">
                          <label class="visually-hidden" for="rsvp-${g.guestId}">RSVP for <c:out value="${g.guestName}"/></label>
                          <select id="rsvp-${g.guestId}" name="rsvpStatus" class="m02-rsvp" data-m02-autosubmit>
                            <option value="INVITED" ${g.rsvpStatus == 'INVITED' ? 'selected' : ''}>Awaiting reply</option>
                            <option value="CONFIRMED" ${g.rsvpStatus == 'CONFIRMED' ? 'selected' : ''}>Confirmed</option>
                            <option value="DECLINED" ${g.rsvpStatus == 'DECLINED' ? 'selected' : ''}>Declined</option>
                          </select>
                        </form>
                      </c:when>
                      <c:otherwise><span class="m02-chip ${g.rsvpStatus.toLowerCase()}">${g.rsvpStatus}</span></c:otherwise>
                    </c:choose>
                  </td>
                  <c:if test="${plan.status == 'ACTIVE'}">
                    <td class="num">
                      <div class="d-inline-flex gap-1">
                        <button type="button" class="m02-btn m02-btn-ghost m02-btn-sm" title="Edit guest" data-m02-modal-open="m02GuestModal"
                                data-f-guestid="<c:out value='${g.guestId}'/>" data-f-guestname="<c:out value='${g.guestName}'/>" data-f-group="${g.group.code}"
                                data-f-contact="<c:out value='${g.contact}'/>" data-f-partysize="${g.partySize}" data-f-rsvpstatus="${g.rsvpStatus}"
                                data-f-note="<c:out value='${g.note}'/>"><i class="bi bi-pencil"></i></button>
                        <form action="${pageContext.request.contextPath}/plans/${plan.planId}/guests/delete" method="post" data-m02-confirm="Remove this guest?"><input type="hidden" name="guestId" value="<c:out value='${g.guestId}'/>"><button class="m02-btn m02-btn-danger m02-btn-sm" title="Remove guest"><i class="bi bi-trash"></i></button></form>
                      </div>
                    </td>
                  </c:if>
                </tr>
              </c:forEach>
            </tbody>
          </table></div>
        </c:if>
      </section>
    </div>

    <div class="col-xl-4">
      <section class="m02-panel">
        <h2 class="m02-heading mb-1">Add a guest</h2>
        <p class="m02-lead">A party can include family members who come together.</p>
        <c:choose>
          <c:when test="${plan.status == 'ACTIVE'}">
            <form action="${pageContext.request.contextPath}/plans/${plan.planId}/guests/add" method="post">
              <div class="mb-3"><label for="m02GName" class="m02-label">Name *</label><input id="m02GName" name="guestName" class="form-control" required minlength="2" maxlength="80" placeholder="e.g. Perera family"></div>
              <div class="row g-3 mb-3"><div class="col-7"><label for="m02GGroup" class="m02-label">Group</label><select id="m02GGroup" name="group" class="form-select"><c:forEach var="grp" items="${groups}"><option value="${grp.code}">${grp.label}</option></c:forEach></select></div><div class="col-5"><label for="m02GParty" class="m02-label">Party size</label><input id="m02GParty" name="partySize" class="form-control" type="number" min="1" max="10" value="1"></div></div>
              <div class="mb-3"><label for="m02GContact" class="m02-label">Contact</label><input id="m02GContact" name="contact" class="form-control" maxlength="80" placeholder="Phone or email (optional)"></div>
              <div class="mb-3"><label for="m02GRsvp" class="m02-label">RSVP</label><select id="m02GRsvp" name="rsvpStatus" class="form-select"><option value="INVITED">Awaiting reply</option><option value="CONFIRMED">Confirmed</option><option value="DECLINED">Declined</option></select></div>
              <div class="mb-4"><label for="m02GNote" class="m02-label">Note</label><input id="m02GNote" name="note" class="form-control" maxlength="200" placeholder="Dietary needs, table preference..."></div>
              <button class="m02-btn w-100" type="submit"><i class="bi bi-person-plus"></i> Add guest</button>
            </form>
          </c:when>
          <c:otherwise><p class="m02-help">The guest list cannot be changed for this plan.</p></c:otherwise>
        </c:choose>
      </section>
    </div>
  </div>

  <div id="m02GuestModal" class="m02-modal-bg" hidden>
    <section class="m02-modal" role="dialog" aria-modal="true" aria-labelledby="m02GuestModalTitle">
      <div class="d-flex justify-content-between align-items-center mb-3"><h2 class="m02-heading" id="m02GuestModalTitle">Edit guest</h2><button type="button" class="m02-btn m02-btn-ghost m02-btn-sm" data-m02-close aria-label="Close">×</button></div>
      <form action="${pageContext.request.contextPath}/plans/${plan.planId}/guests/edit" method="post">
        <input type="hidden" name="guestId">
        <div class="mb-3"><label class="m02-label">Name *</label><input class="form-control" type="text" name="guestName" required minlength="2" maxlength="80"></div>
        <div class="row g-3 mb-3"><div class="col-7"><label class="m02-label">Group</label><select name="group" class="form-select"><c:forEach var="grp" items="${groups}"><option value="${grp.code}">${grp.label}</option></c:forEach></select></div><div class="col-5"><label class="m02-label">Party size</label><input class="form-control" type="number" min="1" max="10" name="partySize"></div></div>
        <div class="mb-3"><label class="m02-label">Contact</label><input class="form-control" name="contact" maxlength="80"></div>
        <div class="mb-3"><label class="m02-label">RSVP</label><select name="rsvpStatus" class="form-select"><option value="INVITED">Awaiting reply</option><option value="CONFIRMED">Confirmed</option><option value="DECLINED">Declined</option></select></div>
        <div class="mb-4"><label class="m02-label">Note</label><input class="form-control" name="note" maxlength="200"></div>
        <div class="d-flex justify-content-end gap-2"><button type="button" class="m02-btn m02-btn-ghost" data-m02-close>Cancel</button><button type="submit" class="m02-btn"><i class="bi bi-check2"></i> Save guest</button></div>
      </form>
    </section>
  </div>
</div>
<script src="${pageContext.request.contextPath}/js/member02-planner.js?v=20261108-v7" defer></script>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
