<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h3 class="fw-bold" style="color: #8d6a22;"><i class="bi bi-file-earmark-text"></i> Vendor Contracts &amp; Payments</h3>
        <p class="text-secondary small mb-0">Manage service agreements, recorded advance/partial/final payments, and balance tracking</p>
    </div>
</div>

<div class="card card-custom p-4 shadow-sm">
    <div class="table-responsive">
        <table class="table table-hover align-middle">
            <thead class="table-custom">
                <tr>
                    <th>Contract ID</th>
                    <th>Booking ID</th>
                    <th>Contract Amount</th>
                    <th>Total Paid</th>
                    <th>Outstanding Balance</th>
                    <th>Payment Status</th>
                    <th>Contract Status</th>
                    <th class="text-end">Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="c" items="${contracts}">
                    <tr>
                        <td class="fw-semibold">${c.contractId}</td>
                        <td>${c.bookingId}</td>
                        <td class="fw-bold">LKR ${c.contractAmount}</td>
                        <td class="fw-bold text-success">LKR ${c.totalPaid}</td>
                        <td class="fw-bold ${c.outstandingBalance > 0 ? 'text-danger' : 'text-success'}">
                            LKR ${c.outstandingBalance}
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${c.paymentStatus == 'FULLY_PAID'}"><span class="badge badge-fully-paid">Fully Paid</span></c:when>
                                <c:when test="${c.paymentStatus == 'PARTIALLY_PAID'}"><span class="badge badge-partially-paid">Partially Paid</span></c:when>
                                <c:otherwise><span class="badge badge-unpaid">Unpaid</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${c.status == 'ACTIVE'}"><span class="badge badge-active">Active</span></c:when>
                                <c:when test="${c.status == 'COMPLETED'}"><span class="badge badge-completed">Completed</span></c:when>
                                <c:otherwise><span class="badge badge-cancelled">${c.status}</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td class="text-end">
                            <a href="${pageContext.request.contextPath}/contracts/view/${c.contractId}" class="btn btn-sm btn-purple">
                                <i class="bi bi-wallet2 me-1"></i> Manage Payments
                            </a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty contracts}">
                    <tr><td colspan="8" class="text-center text-muted py-5">No vendor contracts generated yet.</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />

