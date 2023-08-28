package com.example.atendance_system_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })

public class AtendanceSystemBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AtendanceSystemBackendApplication.class, args);
	}

}
