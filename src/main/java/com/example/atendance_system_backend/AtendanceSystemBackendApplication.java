package com.example.atendance_system_backend;

import com.example.atendance_system_backend.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;



//exclude = {SecurityAutoConfiguration.class }
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
@EnableAsync
public class AtendanceSystemBackendApplication {


	public static void main(String[] args) {
		SpringApplication.run(AtendanceSystemBackendApplication.class, args);
	}

//	@EnableWebSecurity
//	public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//		@Override
//		protected void configure(HttpSecurity http) throws Exception {
//			http
//					.sessionManagement()
//					.sessionFixation().migrateSession()
//					.sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
//			// Add other security configurations as needed
//		}
//	}

}
