package com.wedding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Central Main Application Entry Point.
 * Configured as SpringBootServletInitializer to support both:
 * 1. Executable WAR execution: java -jar wedding-planning-system-1.0.0.war
 * 2. Deployment to an external Apache Tomcat servlet container.
 *
 * Integrates all six member modules under com.wedding package.
 *
 * @author Wedding Planning System Development Team (SE1020)
 */
@SpringBootApplication
public class WeddingApplication extends SpringBootServletInitializer {

 @Override
 protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
 return application.sources(WeddingApplication.class);
 }

 public static void main(String[] args) {
 SpringApplication.run(WeddingApplication.class, args);
 }
}

