package com.wedding.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Session-backed synchronizer-token CSRF protection plus baseline browser
 * security headers for this session-authenticated JSP application.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class WebSecurityHeadersAndCsrfFilter extends OncePerRequestFilter {
    public static final String SESSION_TOKEN = "ECLAT_CSRF_TOKEN";
    public static final String REQUEST_TOKEN = "eclatCsrfToken";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        addSecurityHeaders(request, response);

        HttpSession session = isStaticAsset(request) ? request.getSession(false)
                : request.getSession(request.getMethod().equalsIgnoreCase("GET"));
        String token = session == null ? null : (String) session.getAttribute(SESSION_TOKEN);
        if (token == null && session != null && !isStaticAsset(request)) {
            token = UUID.randomUUID().toString();
            session.setAttribute(SESSION_TOKEN, token);
        }
        request.setAttribute(REQUEST_TOKEN, token);

        if (isStateChanging(request.getMethod())) {
            String supplied = request.getParameter("_csrf");
            if (supplied == null || supplied.isBlank()) supplied = request.getHeader("X-CSRF-TOKEN");
            if (token == null || supplied == null || !constantTimeEquals(token, supplied)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token.");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private static boolean isStaticAsset(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith(request.getContextPath() + "/css/")
                || path.startsWith(request.getContextPath() + "/js/")
                || path.startsWith(request.getContextPath() + "/images/")
                || path.startsWith(request.getContextPath() + "/webjars/")
                || path.endsWith(".ico") || path.endsWith(".png") || path.endsWith(".jpg")
                || path.endsWith(".jpeg") || path.endsWith(".svg") || path.endsWith(".woff2")
                || path.endsWith(".css") || path.endsWith(".js");
    }

    private static boolean isStateChanging(String method) {
        return !("GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method)
                || "OPTIONS".equalsIgnoreCase(method) || "TRACE".equalsIgnoreCase(method));
    }

    private static boolean constantTimeEquals(String expected, String supplied) {
        return java.security.MessageDigest.isEqual(
                expected.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                supplied.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    private static void addSecurityHeaders(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        response.setHeader("Permissions-Policy", "camera=(), microphone=(), geolocation=()");
        response.setHeader("Content-Security-Policy",
                "default-src 'self'; base-uri 'self'; object-src 'none'; frame-ancestors 'none'; "
                + "form-action 'self'; img-src 'self' data: https://images.unsplash.com https:; "
                + "font-src 'self' data: https://fonts.gstatic.com https://cdn.jsdelivr.net; "
                + "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://fonts.googleapis.com; "
                + "script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net; connect-src 'self'");
        if (request.isSecure()) {
            response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        }
    }
}
