<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<section class="eclat-photo-hero" style="background-image:url('https://images.unsplash.com/photo-1511795409834-ef04bbd61622?auto=format&fit=crop&w=1900&q=85')" aria-label="Contracts and payments introduction">
<div class="eclat-photo-hero-copy"><span class="eclat-photo-eyebrow">ÉCLAT · FINANCIAL MANAGEMENT</span>
<h1>Every agreement, beautifully in order.</h1>
<p>Keep vendor contracts, installment payments and outstanding balances clear and organised throughout your wedding planning journey.</p></div></section>
<div class="m05-page-head d-flex justify-content-between align-items-end flex-wrap gap-3 mb-4">
    <div>
        <div class="m05-eyebrow">FINANCIAL MANAGEMENT</div>
        <h2 class="m05-page-title"><i class="bi bi-file-earmark-text me-2"></i>Vendor Contracts &amp; Payments</h2>
        <p class="m05-page-subtitle mb-0">Track service agreements, installment payments and outstanding balances in one place.</p>
    </div>
    <div class="m05-contract-count"><i class="bi bi-shield-check me-2"></i>${totalContracts} contract(s) in your account</div>
</div>

<section class="row g-3 mb-4" aria-label="Contract financial summary">
    <div class="col-12 col-sm-6 col-xl-3"><article class="m05-summary-card">
        <div class="m05-summary-icon icon-gold"><i class="bi bi-files"></i></div><div class="m05-summary-label">Total Contracts</div>
        <div class="m05-summary-value"><c:out value="${totalContracts}"/></div><div class="m05-summary-note"><c:out value="${activeContracts}"/> active agreements</div>
    </article></div>
    <div class="col-12 col-sm-6 col-xl-3"><article class="m05-summary-card">
        <div class="m05-summary-icon icon-ink"><i class="bi bi-wallet2"></i></div><div class="m05-summary-label">Total Contract Value</div>
        <div class="m05-summary-value m05-money">LKR <c:out value="${totalContractValue}"/></div><div class="m05-summary-note">Agreed vendor service value</div>
    </article></div>
    <div class="col-12 col-sm-6 col-xl-3"><article class="m05-summary-card">
        <div class="m05-summary-icon icon-green"><i class="bi bi-check2-circle"></i></div><div class="m05-summary-label">Total Collected</div>
        <div class="m05-summary-value m05-money">LKR <c:out value="${totalCollected}"/></div><div class="m05-summary-note">Recorded, non-voided payments</div>
    </article></div>
    <div class="col-12 col-sm-6 col-xl-3"><article class="m05-summary-card">
        <div class="m05-summary-icon icon-rose"><i class="bi bi-hourglass-split"></i></div><div class="m05-summary-label">Outstanding Balance</div>
        <div class="m05-summary-value m05-money">LKR <c:out value="${totalOutstanding}"/></div><div class="m05-summary-note">Remaining amount to collect</div>
    </article></div>
</section>

<section class="m05-panel">
    <div class="m05-panel-head"><div><h3 class="m05-section-title">Contract register</h3><p class="m05-section-caption mb-0">Review contract values, payment progress and agreement status.</p></div>
        <span class="m05-mini-stat"><i class="bi bi-check-circle-fill me-1"></i><c:out value="${completedContracts}"/> completed</span>
    </div>
    <div class="m05-toolbar">
        <div class="m05-search-wrap"><i class="bi bi-search"></i><input id="contractSearch" type="search" class="form-control" placeholder="Search contract ID or booking ID..." aria-label="Search contracts"></div>
        <div class="d-flex flex-wrap gap-2">
            <select id="paymentFilter" class="form-select m05-filter" aria-label="Filter by payment status">
                <option value="">All payment statuses</option><option value="UNPAID">Unpaid</option><option value="PARTIALLY_PAID">Partially paid</option><option value="FULLY_PAID">Fully paid</option>
            </select>
            <select id="contractFilter" class="form-select m05-filter" aria-label="Filter by contract status">
                <option value="">All contract statuses</option><option value="ACTIVE">Active</option><option value="COMPLETED">Completed</option><option value="CANCELLED">Cancelled</option>
            </select>
        </div>
    </div>
    <div class="table-responsive m05-table-wrap">
        <table class="table align-middle m05-contract-table" id="contractsTable"><thead><tr>
            <th><button type="button" class="m05-sort-btn" data-sort-col="0">Contract / Booking <i class="bi bi-arrow-down-up ms-1"></i></button></th><th><button type="button" class="m05-sort-btn" data-sort-col="1">Contract Amount <i class="bi bi-arrow-down-up ms-1"></i></button></th><th><button type="button" class="m05-sort-btn" data-sort-col="2">Total Paid <i class="bi bi-arrow-down-up ms-1"></i></button></th><th><button type="button" class="m05-sort-btn" data-sort-col="3">Outstanding <i class="bi bi-arrow-down-up ms-1"></i></button></th><th><button type="button" class="m05-sort-btn" data-sort-col="4">Payment Status <i class="bi bi-arrow-down-up ms-1"></i></button></th><th><button type="button" class="m05-sort-btn" data-sort-col="5">Contract Status <i class="bi bi-arrow-down-up ms-1"></i></button></th><th class="text-end">Action</th>
        </tr></thead><tbody>
        <c:forEach var="contract" items="${contracts}">
            <tr data-search="${contract.contractId} ${contract.bookingId}" data-payment="${contract.paymentStatus}" data-contract="${contract.status}">
                <td><div class="m05-primary-cell"><c:out value="${contract.contractId}"/></div><div class="m05-secondary-cell">Booking <c:out value="${contract.bookingId}"/></div></td>
                <td class="m05-amount">LKR <c:out value="${contract.contractAmount}"/></td>
                <td class="m05-amount text-success">LKR <c:out value="${contract.totalPaid}"/></td>
                <td class="m05-amount ${contract.outstandingBalance > 0 ? 'text-danger' : 'text-success'}">LKR <c:out value="${contract.outstandingBalance}"/></td>
                <td><c:choose>
                    <c:when test="${contract.paymentStatus == 'FULLY_PAID'}"><span class="m05-status m05-paid">Fully paid</span></c:when>
                    <c:when test="${contract.paymentStatus == 'PARTIALLY_PAID'}"><span class="m05-status m05-partial">Partially paid</span></c:when>
                    <c:otherwise><span class="m05-status m05-unpaid">Unpaid</span></c:otherwise>
                </c:choose></td>
                <td><c:choose>
                    <c:when test="${contract.status == 'ACTIVE'}"><span class="m05-status m05-active">Active</span></c:when>
                    <c:when test="${contract.status == 'COMPLETED'}"><span class="m05-status m05-completed">Completed</span></c:when>
                    <c:otherwise><span class="m05-status m05-cancelled"><c:out value="${contract.status}"/></span></c:otherwise>
                </c:choose></td>
                <td class="text-end"><a href="${pageContext.request.contextPath}/contracts/view/${contract.contractId}" class="btn m05-manage-btn"><i class="bi bi-arrow-up-right-square me-1"></i> View details</a></td>
            </tr>
        </c:forEach>
        <c:if test="${empty contracts}"><tr><td colspan="7" class="text-center py-5"><div class="m05-empty-icon"><i class="bi bi-file-earmark-plus"></i></div><div class="fw-semibold mt-2">No vendor contracts yet</div><div class="text-secondary small">Contracts appear here after an administrator generates one from a confirmed booking.</div></td></tr></c:if>
        </tbody></table>
        <div id="contractNoResults" class="m05-no-results d-none">No contracts match your search or selected filters.</div>
        <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 p-2 border-top"><label class="small text-secondary">Rows per page <select id="contractPageSize" class="form-select form-select-sm d-inline-block" style="width:auto"><option>5</option><option selected>10</option><option>25</option><option>50</option></select></label><div class="btn-group btn-group-sm"><button type="button" class="btn btn-outline-secondary" id="contractPrev">Previous</button><span class="btn btn-light disabled" id="contractPageLabel">Page 1</span><button type="button" class="btn btn-outline-secondary" id="contractNext">Next</button></div></div>
    </div>
    <div class="m05-table-foot"><span id="contractResultCount"><c:out value="${totalContracts}"/> contract(s)</span><span><i class="bi bi-lock me-1"></i>Payment records are retained for audit history.</span></div>
</section>

<section class="m05-panel mt-4" aria-label="Contract payment overview">
 <div class="m05-panel-head"><div><h3 class="m05-section-title">Payment overview</h3><p class="m05-section-caption mb-0">Recorded contract value and collection progress.</p></div></div>
 <div class="row g-3 align-items-center"><div class="col-md-4"><div class="m05-chart-wrap"><canvas id="contractPaymentChart" height="190" aria-label="Collected and outstanding contract value"></canvas></div></div>
 <div class="col-md-8"><div class="row g-3"><div class="col-sm-4"><div class="m05-chart-stat"><span>Contract value</span><strong>LKR <c:out value="${totalContractValue}"/></strong></div></div><div class="col-sm-4"><div class="m05-chart-stat"><span>Collected</span><strong class="text-success">LKR <c:out value="${totalCollected}"/></strong></div></div><div class="col-sm-4"><div class="m05-chart-stat"><span>Outstanding</span><strong class="text-danger">LKR <c:out value="${totalOutstanding}"/></strong></div></div></div></div></div>
</section>

<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.7/dist/chart.umd.min.js"></script>
<script>
(function () {
    const search = document.getElementById('contractSearch');
    const payment = document.getElementById('paymentFilter');
    const status = document.getElementById('contractFilter');
    let rows = Array.from(document.querySelectorAll('#contractsTable tbody tr[data-search]'));
    const noResults = document.getElementById('contractNoResults');
    const count = document.getElementById('contractResultCount');
    const pageSize = document.getElementById('contractPageSize');
    const pageLabel = document.getElementById('contractPageLabel');
    const prev = document.getElementById('contractPrev');
    const next = document.getElementById('contractNext');
    let page = 1;
    function applyFilters() {
        const term = (search.value || '').trim().toLowerCase();
        const filtered = rows.filter(row => row.dataset.search.toLowerCase().includes(term)
            && (!payment.value || row.dataset.payment === payment.value)
            && (!status.value || row.dataset.contract === status.value));
        const size = Number(pageSize.value) || 10;
        const pages = Math.max(1, Math.ceil(filtered.length / size));
        page = Math.min(page, pages);
        rows.forEach(row => row.classList.add('d-none'));
        filtered.slice((page - 1) * size, page * size).forEach(row => row.classList.remove('d-none'));
        noResults.classList.toggle('d-none', filtered.length !== 0 || rows.length === 0);
        count.textContent = filtered.length + ' matching contract(s)';
        pageLabel.textContent = 'Page ' + page + ' of ' + pages;
        prev.disabled = page <= 1; next.disabled = page >= pages;
    }
    [search, payment, status].forEach(control => control.addEventListener('input', () => { page = 1; applyFilters(); }));
    [payment, status, pageSize].forEach(control => control.addEventListener('change', () => { page = 1; applyFilters(); }));
    prev.addEventListener('click', () => { page = Math.max(1, page - 1); applyFilters(); });
    next.addEventListener('click', () => { page++; applyFilters(); });
    let sortColumn = -1;
    let sortDirection = 1;
    document.querySelectorAll('#contractsTable [data-sort-col]').forEach(button => {
        button.addEventListener('click', () => {
            const column = Number(button.dataset.sortCol);
            sortDirection = sortColumn === column ? -sortDirection : 1;
            sortColumn = column;
            rows.sort((left, right) => {
                const a = (left.cells[column]?.textContent || '').trim();
                const b = (right.cells[column]?.textContent || '').trim();
                if (column >= 1 && column <= 3) {
                    const av = Number(a.replace(/[^0-9.-]/g, '')) || 0;
                    const bv = Number(b.replace(/[^0-9.-]/g, '')) || 0;
                    return (av - bv) * sortDirection;
                }
                return a.localeCompare(b, undefined, {numeric:true, sensitivity:'base'}) * sortDirection;
            });
            rows.forEach(row => row.parentElement.appendChild(row));
            document.querySelectorAll('#contractsTable [data-sort-col]').forEach(item => {
                item.setAttribute('aria-pressed', item === button ? 'true' : 'false');
            });
            applyFilters();
        });
    });
    applyFilters();
    const chartCanvas = document.getElementById('contractPaymentChart');
    if (chartCanvas && window.Chart) {
      new Chart(chartCanvas, {type:'doughnut',data:{labels:['Collected','Outstanding'],datasets:[{data:[Number('${totalCollected}'),Number('${totalOutstanding}')]}]},options:{responsive:true,maintainAspectRatio:false,plugins:{legend:{position:'bottom'}}}});
    }
})();
</script>
<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
