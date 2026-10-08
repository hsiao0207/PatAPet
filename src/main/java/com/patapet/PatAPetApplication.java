/**
 * Purpose: The entry point class for the Spring Boot application.
 * Interactions:
 * - Function: Enables @EnableJpaAuditing for automatic updating of time fields in BaseEntity
 * - Scope: Starts the entire Spring Boot container and all Components
 */
package com.patapet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // 核心重點：啟用 Spring Data JPA 時間自動審計功能
public class PatAPetApplication {

    public static void main(String[] args) {
        SpringApplication.run(PatAPetApplication.class, args);
    }
}
