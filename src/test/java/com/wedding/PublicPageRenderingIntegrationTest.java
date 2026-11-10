package com.wedding;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Runs the public JSP routes through the embedded servlet container.
 * MockMvc view-name tests do not compile/render JSPs, so this catches real
 * Jasper/Tomcat failures such as a login page returning HTTP 500.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "app.demo-data.enabled=false")
class PublicPageRenderingIntegrationTest {

    @Autowired
    private TestRestTemplate http;

    @Test
    void loginPageRendersThroughEmbeddedTomcat() {
        ResponseEntity<String> response = http.getForEntity("/login", String.class);
        assertEquals(200, response.getStatusCode().value(),
                "GET /login must render the login JSP, not return a server error.");
        assertTrue(response.getBody() != null && response.getBody().contains("Sign in to your account"),
                "The rendered login page should contain the sign-in form.");
    }

    @Test
    void registrationPageRendersThroughEmbeddedTomcat() {
        ResponseEntity<String> response = http.getForEntity("/register", String.class);
        assertEquals(200, response.getStatusCode().value(),
                "GET /register must render the registration JSP.");
        assertTrue(response.getBody() != null && response.getBody().contains("Create your Éclat account"));
    }

    @Test
    void publicVendorDirectoryAndVenueFilterRender() {
        ResponseEntity<String> directory = http.getForEntity("/vendors", String.class);
        assertEquals(200, directory.getStatusCode().value(),
                "The public vendor directory must render for visitors.");

        ResponseEntity<String> venues = http.getForEntity("/vendors?category=VENUES", String.class);
        assertEquals(200, venues.getStatusCode().value(),
                "The venue category filter must render for visitors.");
    }
}
