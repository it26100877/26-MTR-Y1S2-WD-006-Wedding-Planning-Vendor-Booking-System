<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<div class="row justify-content-center">
    <div class="col-md-8">
        <div class="card card-custom p-4 shadow">
            <h4 class="fw-bold mb-4" style="color: #8d6a22;">
                <i class="bi bi-calendar-heart"></i> ${isEdit ? "Edit Wedding Plan" : "Create New Wedding Plan"}
            </h4>

            <form action="${isEdit ? pageContext.request.contextPath.concat('/plans/edit/').concat(plan.planId) : pageContext.request.contextPath.concat('/plans/new')}" method="post">
                <div class="row g-3">
                    <div class="col-md-12">
                        <label class="form-label fw-semibold">Plan Name *</label>
                        <input type="text" name="planName" class="form-control" placeholder="e.g. Dream Summer Wedding" value="${plan != null ? plan.planName : ''}" required>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Wedding Event Date *</label>
                        <input type="date" name="weddingDate" class="form-control" value="${plan != null ? plan.weddingDate : ''}" required>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Overall Budget (USD) *</label>
                        <div class="input-group">
                            <span class="input-group-text">$</span>
                            <input type="number" step="0.01" name="totalBudget" class="form-control" placeholder="15000.00" value="${plan != null ? plan.totalBudget : ''}" required>
                        </div>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Venue / Location *</label>
                        <input type="text" name="venue" class="form-control" placeholder="Grand Ballroom, Cinnamon Grand" value="${plan != null ? plan.venue : ''}" required>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Wedding Theme / Style</label>
                        <input type="text" name="theme" class="form-control" placeholder="Rustic Boho / Floral Elegance" value="${plan != null ? plan.theme : ''}">
                    </div>

                    <div class="col-12">
                        <label class="form-label fw-semibold">Description / Notes</label>
                        <textarea name="description" class="form-control" rows="3" placeholder="Brief outline of your vision, guest expectations, and special requests...">${plan != null ? plan.description : ''}</textarea>
                    </div>

                    <div class="col-12 mt-4 d-flex justify-content-between">
                        <a href="${pageContext.request.contextPath}/plans" class="btn btn-outline-secondary">Cancel</a>
                        <button type="submit" class="btn btn-purple px-4 fw-bold">${isEdit ? "Update Plan" : "Save Wedding Plan"}</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />

