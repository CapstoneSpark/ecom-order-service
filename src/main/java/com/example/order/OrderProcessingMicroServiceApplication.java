package com.example.order;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderProcessingMicroServiceApplication {

    public static void main(String[] args) {
        // Load .env before Spring starts
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        System.setProperty("DB_USER", dotenv.get("DB_USER"));
        System.setProperty("DB_PASS", dotenv.get("DB_PASS"));
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
        System.setProperty("JWT_EXPIRATION", dotenv.get("JWT_EXPIRATION"));

        SpringApplication.run(OrderProcessingMicroServiceApplication.class, args);
    }

}
