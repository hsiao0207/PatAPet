/**
 * 檔案用途：Spring Boot 應用程式的啟動類別 (Entry point)。
 * 互動關係：
 * - 功能：開啟 @EnableJpaAuditing 讓 BaseEntity 的時間欄位自動更新
 * - 影響範圍：啟動整個 Spring Boot 容器與各個 Component
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
