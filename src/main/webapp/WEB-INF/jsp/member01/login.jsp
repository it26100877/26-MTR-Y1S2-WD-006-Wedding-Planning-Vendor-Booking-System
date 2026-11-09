<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<section class="eclat-auth-wrap">

    <div class="eclat-auth-visual">
        <div class="eclat-auth-visual-shade"></div>

        <div class="eclat-auth-visual-copy">
            <span class="eclat-eyebrow">THE ART OF A BEAUTIFUL BEGINNING</span>

            <h1>
                Every love story
                <br>
                <em>deserves</em> a beautiful day.
            </h1>

            <p>
                Bring every detail of your celebration together with Éclat.
            </p>

            <div class="eclat-auth-caption">
                ÉCLAT · WEDDING PLANNING &amp; VENDOR DIRECTORY
            </div>
        </div>
    </div>

    <div class="eclat-auth-panel">
        <div class="eclat-auth-card">

            <a class="eclat-auth-back"
               href="${pageContext.request.contextPath}/">
                <i class="bi bi-arrow-left"></i> Back to explore
            </a>

            <div class="eclat-auth-brand">
                Éclat<span>.</span>
            </div>

            <span class="eclat-eyebrow">WELCOME BACK</span>

            <h2>Sign in to your account</h2>

            <p class="eclat-auth-intro">
                Continue planning your celebration, one beautiful detail at a time.
            </p>

            <!-- Login Role Tabs -->
            <ul class="nav nav-pills eclat-auth-tabs nav-fill mb-4"
                id="loginTab" role="tablist">

                <li class="nav-item" role="presentation">
                    <button class="nav-link active"
                            id="customer-tab"
                            data-bs-toggle="pill"
                            data-bs-target="#customerPane"
                            type="button"
                            role="tab">
                        Customer
                    </button>
                </li>

                <li class="nav-item" role="presentation">
                    <button class="nav-link"
                            id="admin-tab"
                            data-bs-toggle="pill"
                            data-bs-target="#adminPane"
                            type="button"
                            role="tab">
                        Administrator
                    </button>
                </li>

            </ul>

            <div class="tab-content" id="loginTabContent">

                <!-- Customer Login -->
                <div class="tab-pane fade show active"
                     id="customerPane" role="tabpanel">

                    <form action="${pageContext.request.contextPath}/login"
                          method="post">

                        <input type="hidden" name="role" value="CUSTOMER">

                        <div class="eclat-form-field">
                            <label for="customerEmail">Email address</label>

                            <div class="eclat-input-wrap">
                                <i class="bi bi-envelope"></i>

                                <input id="customerEmail"
                                       type="email"
                                       name="email"
                                       placeholder="name@example.com"
                                       autocomplete="username" autocapitalize="none" spellcheck="false"
                                       required>
                            </div>
                        </div>

                        <div class="eclat-form-field">
                            <label for="customerPassword">Password</label>

                            <div class="eclat-input-wrap">
                                <i class="bi bi-lock"></i>

                                <input id="customerPassword"
                                       type="password"
                                       name="password"
                                       placeholder="Enter your password"
                                       autocomplete="current-password"
                                       required>
                            </div>
                        </div>

                        <button type="submit" class="eclat-primary-btn w-100">
                            Sign in <i class="bi bi-arrow-right"></i>
                        </button>

                    </form>

                    <p class="eclat-auth-switch">
                        New to Éclat?
                        <a href="${pageContext.request.contextPath}/register">
                            Create an account
                        </a>
                    </p>

                </div>

                <!-- Administrator Login -->
                <div class="tab-pane fade" id="adminPane" role="tabpanel">

                    <form action="${pageContext.request.contextPath}/login"
                          method="post">

                        <input type="hidden" name="role" value="ADMIN">

                        <div class="eclat-form-field">
                            <label for="adminEmail">Administrator email</label>

                            <div class="eclat-input-wrap">
                                <i class="bi bi-shield-lock"></i>

                                <input id="adminEmail"
                                       type="email"
                                       name="email"
                                       placeholder="Enter administrator email"
                                       autocomplete="username" autocapitalize="none" spellcheck="false"
                                       required>
                            </div>
                        </div>

                        <div class="eclat-form-field">
                            <label for="adminPassword">Password</label>

                            <div class="eclat-input-wrap">
                                <i class="bi bi-key"></i>

                                <input id="adminPassword"
                                       type="password"
                                       name="password"
                                       placeholder="Enter administrator password"
                                       autocomplete="current-password"
                                       required>
                            </div>
                        </div>

                        <button type="submit" class="eclat-primary-btn w-100">
                            Sign in as administrator
                            <i class="bi bi-arrow-right"></i>
                        </button>

                    </form>

                </div>

            </div>

            <div class="eclat-auth-foot">
                <i class="bi bi-shield-check"></i>
                Your account and wedding details stay protected.
            </div>

        </div>
    </div>

</section>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
