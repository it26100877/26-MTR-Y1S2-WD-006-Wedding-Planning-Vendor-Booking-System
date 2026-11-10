<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<style>
  .eclat-otp-input{letter-spacing:.6em;text-align:center;font-size:1.6rem;font-weight:500;padding-left:1.2rem!important}
  .eclat-otp-resend{display:flex;justify-content:center;align-items:center;gap:.5rem;margin-top:1.25rem;font-size:.95rem}
  .eclat-otp-resend button{background:none;border:0;padding:0;color:inherit;text-decoration:underline;cursor:pointer;font:inherit}
  .eclat-otp-resend button:disabled{opacity:.5;cursor:not-allowed;text-decoration:none}
</style>

<section class="eclat-auth-wrap">
  <div class="eclat-auth-visual">
    <div class="eclat-auth-visual-shade"></div>
    <div class="eclat-auth-visual-copy">
      <span class="eclat-eyebrow">ONE QUICK CHECK</span>
      <h1>Let&rsquo;s make sure<br>it&rsquo;s <em>really</em> you.</h1>
      <p>We use a one-time code to verify your phone when you create your account.</p>
      <div class="eclat-auth-caption">ÉCLAT · WEDDING PLANNING &amp; VENDOR DIRECTORY</div>
    </div>
  </div>
  <div class="eclat-auth-panel">
    <div class="eclat-auth-card">
      <form action="${pageContext.request.contextPath}/login/otp/cancel" method="post" class="mb-3">
        <button type="submit" class="eclat-auth-back border-0 bg-transparent p-0"><i class="bi bi-arrow-left"></i> Use a different account</button>
      </form>
      <div class="eclat-auth-brand">Éclat<span>.</span></div>
      <span class="eclat-eyebrow"><c:choose><c:when test="${registrationVerification}">COMPLETE YOUR REGISTRATION</c:when><c:otherwise>VERIFY YOUR PHONE</c:otherwise></c:choose></span>
      <h2>Enter your 6-digit code</h2>
      <p class="eclat-auth-intro"><c:choose><c:when test="${registrationVerification}">Verify your phone number <strong><c:out value="${maskedPhone}"/></strong> to activate your new account.</c:when><c:otherwise>We sent a code to <strong><c:out value="${maskedPhone}"/></strong>. It expires in 5 minutes.</c:otherwise></c:choose> The code expires in 5 minutes.</p>

      <form action="${pageContext.request.contextPath}/login/otp" method="post">
        <div class="eclat-form-field">
          <label for="otpCode">Verification code</label>
          <div class="eclat-input-wrap">
            <input id="otpCode" class="eclat-otp-input" type="text" name="code" inputmode="numeric"
                   autocomplete="one-time-code" pattern="[0-9]{6}" maxlength="6" placeholder="••••••" required autofocus>
          </div>
        </div>
        <button type="submit" class="eclat-primary-btn w-100">Verify phone and continue <i class="bi bi-arrow-right"></i></button>
      </form>

      <form class="eclat-otp-resend" action="${pageContext.request.contextPath}/login/otp/resend" method="post">
        <span>Didn&rsquo;t get it?</span>
        <button type="submit" id="resendBtn" data-wait="${resendIn}">Resend code</button>
      </form>

      <div class="eclat-auth-foot"><i class="bi bi-shield-check"></i> Never share this code with anyone, including Éclat staff.</div>
    </div>
  </div>
</section>

<script>
  (function () {
    var btn = document.getElementById('resendBtn');
    var wait = parseInt(btn.getAttribute('data-wait'), 10) || 0;
    function tick() {
      if (wait > 0) { btn.disabled = true; btn.textContent = 'Resend in ' + wait + 's'; wait--; setTimeout(tick, 1000); }
      else { btn.disabled = false; btn.textContent = 'Resend code'; }
    }
    tick();
    document.getElementById('otpCode').addEventListener('input', function () {
      this.value = this.value.replace(/\D/g, '').slice(0, 6);
    });
  })();
</script>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
