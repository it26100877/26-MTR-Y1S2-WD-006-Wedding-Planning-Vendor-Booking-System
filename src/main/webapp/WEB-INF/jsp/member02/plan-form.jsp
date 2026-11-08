<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/member02-planner.css?v=20261108-v7">
<%-- Values come from the flash attribute "form" after a validation error, otherwise from the plan being edited. --%>
<c:set var="fName" value="${not empty form ? form.planName : plan.planName}"/>
<c:set var="fDate" value="${not empty form ? form.weddingDate : plan.weddingDate}"/>
<c:set var="fBudget" value="${not empty form ? form.totalBudget : plan.totalBudget}"/>
<c:set var="fVenue" value="${not empty form ? form.venue : plan.venue}"/>
<c:set var="fGuests" value="${not empty form ? form.guestCount : (isEdit ? plan.guestCount : 0)}"/>
<c:set var="fTheme" value="${not empty form ? form.theme : plan.theme}"/>
<c:set var="fDesc" value="${not empty form ? form.description : plan.description}"/>
<c:set var="fType" value="${not empty form ? form.planType : (isEdit ? plan.type.code : 'STANDARD')}"/>
<c:set var="fAuto" value="${not empty form ? form.autoChecklist == 'on' : true}"/>
<div class="m02 container-fluid px-0">
  <a href="${pageContext.request.contextPath}/plans" class="text-decoration-none d-inline-block mb-3" style="color:#9b8051"><i class="bi bi-arrow-left"></i> Back to My Wedding Plans</a>
  <div class="m02-hero m02-hero--form mb-4"><div><div class="m02-eyebrow">Éclat • Wedding details</div><h1 class="m02-hero-title">${isEdit ? 'Refine your wedding plan' : 'Let’s plan something magical.'}</h1><p>Capture the details that make your celebration uniquely yours.</p></div></div>
  <div class="row g-4 align-items-start">
    <div class="col-lg-8">
      <section class="m02-panel">
        <h2 class="m02-heading mb-1">${isEdit ? 'Edit Wedding Plan' : 'Create Wedding Plan'}</h2>
        <p class="m02-lead mb-4">Fields marked * are required. Budget is entered in Sri Lankan Rupees (LKR).</p>
        <form action="${isEdit ? pageContext.request.contextPath.concat('/plans/edit/').concat(plan.planId) : pageContext.request.contextPath.concat('/plans/new')}" method="post">
          <div class="row g-3">
            <div class="col-12">
              <span class="m02-label" id="m02TypeLabel">Type of wedding</span>
              <div class="m02-type-grid" role="radiogroup" aria-labelledby="m02TypeLabel">
                <c:forEach var="t" items="${types}">
                  <input type="radio" name="planType" id="m02Type${t.code}" value="${t.code}" ${fType == t.code ? 'checked' : ''}>
                  <label for="m02Type${t.code}"><i class="bi ${t.icon}"></i>${t.label}</label>
                </c:forEach>
              </div>
              <p class="m02-help mt-2 mb-0"><i class="bi bi-image"></i> Select a wedding style to see a matching photo on the right.</p>
            </div>
            <div class="col-12"><label class="m02-label" for="m02PlanName">Wedding plan name *</label><input id="m02PlanName" name="planName" class="form-control" type="text" minlength="3" maxlength="100" placeholder="e.g. Our Dream Wedding" value="<c:out value='${fName}'/>" required></div>
            <div class="col-md-6"><label class="m02-label" for="m02Date">Wedding date *</label><input id="m02Date" name="weddingDate" class="form-control" type="date" ${isEdit ? '' : 'min="'.concat(minDate).concat('"')} value="<c:out value='${fDate}'/>" required></div>
            <div class="col-md-6"><label class="m02-label" for="m02Budget">Estimated budget (LKR) *</label><input id="m02Budget" name="totalBudget" class="form-control" type="number" step="0.01" min="0" max="999999999999" placeholder="1500000" value="<c:out value='${fBudget}'/>" required></div>
            <div class="col-md-8"><label class="m02-label" for="m02Venue">Venue / location *</label><input id="m02Venue" name="venue" class="form-control" maxlength="140" placeholder="e.g. Grand Ballroom, Colombo" value="<c:out value='${fVenue}'/>" required></div>
            <div class="col-md-4"><label class="m02-label" for="m02Guests">Estimated guest count</label><input id="m02Guests" name="guestCount" class="form-control" type="number" min="0" max="5000" value="<c:out value='${fGuests}'/>" required></div>
            <div class="col-12"><label class="m02-label" for="m02Theme">Wedding theme / style</label><input id="m02Theme" name="theme" class="form-control" maxlength="100" placeholder="Floral elegance, Traditional, Garden celebration..." value="<c:out value='${fTheme}'/>"></div>
            <div class="col-12"><label class="m02-label" for="m02Desc">Your inspiration & notes</label><textarea id="m02Desc" name="description" class="form-control" rows="5" maxlength="1200" placeholder="Tell us about the details, traditions, atmosphere, and special requests you envision..."><c:out value="${fDesc}"/></textarea></div>
            <c:if test="${!isEdit}">
              <div class="col-12"><div class="form-check"><input class="form-check-input" type="checkbox" name="autoChecklist" id="m02Auto" ${fAuto ? 'checked' : ''}><label class="form-check-label" for="m02Auto">Add the recommended checklist for this type of wedding</label></div></div>
            </c:if>
            <div class="col-12 d-flex gap-2 flex-wrap align-items-center justify-content-end mt-4"><a class="m02-btn m02-btn-ghost" href="${pageContext.request.contextPath}/plans">Cancel</a><button class="m02-btn" type="submit"><i class="bi bi-check2-circle"></i> ${isEdit ? 'Save Changes' : 'Create Wedding Plan'}</button></div>
          </div>
        </form>
      </section>
    </div>
    <div class="col-lg-4">
      <section class="m02-panel mb-4">
        <div class="m02-kicker mb-2">Live estimate</div>
        <div id="m02Estimate" class="m02-estimate" data-url="${pageContext.request.contextPath}/plans/estimate">
          <div class="m02-help" data-est-label>Choose a wedding type</div>
          <div class="amount" data-est-amount>LKR 0</div>
          <div class="m02-help" data-est-tasks></div>
          <p class="m02-help mt-2 mb-0" data-est-advice>The estimate uses the cost model of the selected wedding type and your guest count.</p>
          <div data-est-compare></div>
        </div>
      </section>
      <%-- Preview reacts to the chosen WeddingType without altering backend/CRUD behavior. --%>
      <div class="m02-form-visual m02-type-preview" id="m02TypePreview"
           data-image-base="${pageContext.request.contextPath}/images/member02/">
        <img id="m02PreviewImage"
             src="${pageContext.request.contextPath}/images/member02/wedding-classic.jpg"
             alt="Classic wedding ceremony with elegant floral arch"
             loading="eager" decoding="async" width="800" height="1000">
        <div class="m02-preview-chip"><i class="bi bi-stars"></i> Wedding inspiration</div>
        <div class="m02-visual-copy" aria-live="polite" aria-atomic="true">
          <div class="m02-eyebrow" id="m02PreviewEyebrow">CLASSIC / CUSTOM WEDDING</div>
          <h3 id="m02PreviewTitle">Classic elegance.</h3>
          <p class="mb-0" id="m02PreviewText">A timeless celebration with beautiful floral details and personal touches.</p>
        </div>
      </div>
    </div>
  </div>
</div>
<script src="${pageContext.request.contextPath}/js/member02-planner.js?v=20261108-v7" defer></script>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
