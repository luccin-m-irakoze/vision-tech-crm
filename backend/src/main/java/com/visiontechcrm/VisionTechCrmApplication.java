package com.visiontechcrm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Vision Technologies CRM backend.
 *
 * <p>The application exposes a JSON REST API protected by JWT and persists data
 * in MySQL through Spring Data JPA. It follows the MVC pattern: REST
 * controllers (View boundary) delegate to services (Controller / business
 * logic), which collaborate with JPA repositories over entity Models.
 */
@SpringBootApplication
public class VisionTechCrmApplication {

    public static void main(String[] args) {
        SpringApplication.run(VisionTechCrmApplication.class, args);
    }
}
