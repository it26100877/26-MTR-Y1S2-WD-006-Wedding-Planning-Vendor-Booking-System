package lk.sliit.group006;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"lk.sliit.group006", "com.wedding"})
public class WeddingPlanningApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeddingPlanningApplication.class, args);
    }
}
