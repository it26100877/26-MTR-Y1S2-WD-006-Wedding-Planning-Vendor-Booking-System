<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<div class="row justify-content-center">
    <div class="col-md-7">
        <div class="card card-custom p-4 shadow">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h4 class="fw-bold mb-0" style="color: #8d6a22;"><i class="bi bi-person-circle"></i> Customer Profile</h4>
                <span class="badge bg-purple px-3 py-2">ID: ${customer.userId}</span>
            </div>

            <form action="${pageContext.request.contextPath}/profile" method="post">
                <div class="row g-3">
                    <div class="col-md-12">
                        <label class="form-label fw-semibold">Full Name</label>
                        <input type="text" name="name" class="form-control" value="${customer.name}" required>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Email Address (Read-only)</label>
                        <input type="email" class="form-control bg-light" value="${customer.email}" readonly>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Phone Number</label>
                        <input type="text" name="phone" class="form-control" value="${customer.phone}" required>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Planned Wedding Date</label>
                        <input type="date" name="weddingDate" class="form-control" value="${customer.weddingDate}">
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Address / City</label>
                        <input type="text" name="address" class="form-control" value="${customer.address}">
                    </div>

                    <div class="col-12 mt-4 d-flex justify-content-between">
                        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-outline-secondary">Back to Dashboard</a>
                        <button type="submit" class="btn btn-purple px-4 fw-bold">Update Profile</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />

