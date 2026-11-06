<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />
<div class="row justify-content-center">
  <div class="col-lg-8">
    <a href="${pageContext.request.contextPath}/contracts/view/${contract.contractId}" class="text-decoration-none text-secondary small">← Back to contract</a>
    <div class="card card-custom p-4 shadow-sm mt-3">
      <p class="eyebrow">CONTRACT MANAGEMENT</p>
      <h2 class="fw-bold mb-1" style="color:#8d6a22;">Edit Contract</h2>
      <p class="text-secondary">The contract amount cannot be lower than payments already recorded. The event date must remain within the contract period.</p>
      <form action="${pageContext.request.contextPath}/contracts/edit/${contract.contractId}" method="post">
        <div class="row g-3">
          <div class="col-md-6">
            <label class="form-label fw-semibold">Start Date</label>
            <input type="date" name="startDate" class="form-control" value="${contract.startDate}" required>
          </div>
          <div class="col-md-6">
            <label class="form-label fw-semibold">End Date</label>
            <input type="date" name="endDate" class="form-control" value="${contract.endDate}" required>
          </div>
          <div class="col-12">
            <label class="form-label fw-semibold">Contract Amount (LKR)</label>
            <input type="number" name="contractAmount" class="form-control" min="${contract.totalPaid}" step="0.01" value="${contract.contractAmount}" required>
          </div>
          <div class="col-12">
            <label class="form-label fw-semibold">Terms & Conditions</label>
            <textarea name="terms" class="form-control" rows="5">${contract.terms}</textarea>
          </div>
        </div>
        <div class="d-flex gap-2 mt-4">
          <button type="submit" class="btn btn-purple">Save Contract</button>
          <a href="${pageContext.request.contextPath}/contracts/view/${contract.contractId}" class="btn btn-outline-secondary">Cancel</a>
        </div>
      </form>
    </div>
  </div>
</div>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
