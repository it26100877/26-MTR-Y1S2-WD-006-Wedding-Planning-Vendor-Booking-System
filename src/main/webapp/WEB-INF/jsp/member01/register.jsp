<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<section class="eclat-register-wrap">
  <div class="eclat-register-heading">
    <span class="eclat-eyebrow">YOUR NEXT CHAPTER STARTS HERE</span>
    <h1>Create your Éclat account</h1>
    <p>One thoughtful place to plan your wedding and discover trusted professionals across Sri Lanka.</p>
  </div>
  <div class="eclat-register-card">
    <div class="eclat-register-aside">
      <div class="eclat-register-monogram">É.</div>
      <h2>A day that feels<br><em>like you.</em></h2>
      <p>Save your plans, explore vendors, request bookings and keep every detail beautifully organised.</p>
      <div class="eclat-register-aside-bottom">ÉCLAT · SRI LANKA WEDDING DIRECTORY</div>
    </div>
    <div class="eclat-register-form">
      <h2>Your details</h2>
      <p class="eclat-auth-intro">Create your customer account to get started.</p>
      <form action="${pageContext.request.contextPath}/register" method="post">
        <div class="row g-3">
          <div class="col-12 eclat-form-field"><label for="regName">Full name *</label><input id="regName" type="text" name="name" placeholder="Your full name" required></div>
          <div class="col-md-6 eclat-form-field"><label for="regEmail">Email address *</label><input id="regEmail" type="email" name="email" placeholder="name@example.com" required></div>
          <div class="col-md-6 eclat-form-field"><label for="regPhone">Phone number *</label><input id="regPhone" type="text" name="phone" placeholder="+94 77 123 4567" required></div>
          <div class="col-md-6 eclat-form-field"><label for="regPassword">Password *</label><input id="regPassword" type="password" name="password" placeholder="At least 8 characters" minlength="8" autocomplete="new-password" required></div>
          <div class="col-md-6 eclat-form-field"><label for="regConfirm">Confirm password *</label><input id="regConfirm" type="password" name="confirmPassword" placeholder="Re-enter password" autocomplete="new-password" required></div>
          <div class="col-md-6 eclat-form-field"><label for="regDate">Wedding date</label><input id="regDate" type="date" name="weddingDate"></div>
          <div class="col-md-6 eclat-form-field"><label for="regAddress">Address / district</label><input id="regAddress" type="text" name="address" placeholder="e.g. Matara, Sri Lanka"></div>
          <div class="col-12"><button type="submit" class="eclat-primary-btn w-100">Create my account <i class="bi bi-arrow-right"></i></button></div>
        </div>
      </form>
      <p class="eclat-auth-switch">Already have an account? <a href="${pageContext.request.contextPath}/login">Sign in</a></p>
    </div>
  </div>
</section>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
