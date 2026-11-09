<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h3 class="fw-bold" style="color: #8d6a22;"><i class="bi bi-shield-check"></i> System Administration</h3>
        <p class="text-secondary small mb-0">Manage customer accounts, system administrators, and role permissions</p>
    </div>
</div>

<!-- Stats Row -->
<div class="row g-3 mb-4">
    <div class="col-md-4">
        <div class="card card-custom p-3 border-start border-4 border-primary shadow-sm">
            <div class="text-secondary small">Total Customers</div>
            <div class="fs-2 fw-bold text-primary">${totalCustomers}</div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card card-custom p-3 border-start border-4 border-success shadow-sm">
            <div class="text-secondary small">Active Customers</div>
            <div class="fs-2 fw-bold text-success">${activeCustomers}</div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card card-custom p-3 border-start border-4 border-warning shadow-sm">
            <div class="text-secondary small">System Administrators</div>
            <div class="fs-2 fw-bold text-warning">${totalAdmins}</div>
        </div>
    </div>
</div>

<!-- Customer Management Table -->
<div class="card card-custom p-4 shadow-sm mb-4">
    <h5 class="fw-bold mb-3" style="color: #8d6a22;"><i class="bi bi-people"></i> Registered Customers</h5>
    <div class="table-responsive">
        <table class="table table-hover align-middle">
            <thead class="table-custom">
                <tr>
                    <th>User ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Wedding Date</th>
                    <th>Status</th>
                    <th class="text-end">Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="c" items="${customers}">
                    <tr>
                        <td class="fw-semibold">${c.userId}</td>
                        <td>${c.name}</td>
                        <td>${c.email}</td>
                        <td>${c.phone}</td>
                        <td>${empty c.weddingDate ? 'Not Set' : c.weddingDate}</td>
                        <td>
                            <c:if test="${c.active}"><span class="badge badge-active">Active</span></c:if>
                            <c:if test="${!c.active}"><span class="badge badge-cancelled">Deactivated</span></c:if>
                        </td>
                        <td class="text-end">
                            <form action="${pageContext.request.contextPath}/admin/customer/toggle-active" method="post" style="display:inline;">
                                <input type="hidden" name="customerId" value="${c.userId}">
                                <input type="hidden" name="active" value="${c.active}">
                                <button type="submit" class="btn btn-sm ${c.active ? 'btn-outline-danger' : 'btn-outline-success'}">
                                    <i class="bi ${c.active ? 'bi-person-x' : 'bi-person-check'}"></i>
                                    ${c.active ? 'Deactivate' : 'Activate'}
                                </button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty customers}">
                    <tr><td colspan="7" class="text-center text-muted py-4">No customers registered yet.</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<!-- Administrators List -->
<div class="card card-custom p-4 shadow-sm">
    <h5 class="fw-bold mb-3" style="color: #8d6a22;"><i class="bi bi-shield-lock"></i> System Administrators</h5>
    <div class="table-responsive">
        <table class="table table-hover align-middle">
            <thead class="table-custom">
                <tr>
                    <th>Admin ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Level</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="a" items="${admins}">
                    <tr>
                        <td class="fw-semibold">${a.userId}</td>
                        <td>${a.name}</td>
                        <td>${a.email}</td>
                        <td>${a.phone}</td>
                        <td><span class="badge bg-purple">${a.adminLevel}</span></td>
                        <td><span class="badge badge-active">Active</span></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />

