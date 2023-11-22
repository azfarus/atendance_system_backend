package com.example.atendance_system_backend.config;

import com.example.atendance_system_backend.admin.Admin;
import com.example.atendance_system_backend.admin.AdminRepository;
import com.example.atendance_system_backend.student.Student;
import com.example.atendance_system_backend.student.StudentRepository;
import com.example.atendance_system_backend.teacher.Teacher;
import com.example.atendance_system_backend.teacher.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    TeacherRepository teacherDB;

    @Autowired
    StudentRepository studentDB;

    @Autowired
    AdminRepository adminDB;

    @Autowired
    UserDetailsServiceImpl usdeetservimpl;
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Use BCryptPasswordEncoder for password hashing
        return new BCryptPasswordEncoder();
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth
//                .userDetailsService(userDetailsService())
//                .passwordEncoder(passwordEncoder());
//    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(usdeetservimpl).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/login.html").permitAll()
                .antMatchers("/login/**").permitAll()
                .antMatchers("/forgotpass/**").permitAll()
                .antMatchers("/student/get-photo/**").permitAll()// Public resources
                .antMatchers("/teacher/get-photo/**").permitAll()// Public resources
                .antMatchers("/student/**").hasAuthority("STUDENT") // Requires "USER" role
                .antMatchers("/admin/**").hasAuthority("ADMIN")
                .antMatchers("/teacher/**").hasAuthority("TEACHER")
                .antMatchers("/attendance/**").hasAuthority("TEACHER")// Requires "ADMIN" role
                .antMatchers("/attendance/get-percentage/**").permitAll()
                .antMatchers("/file/**").hasAnyAuthority("TEACHER","STUDENT")
                .anyRequest().authenticated() // Requires authentication for any other request
                .and()
                .httpBasic().and().csrf().disable();
       //http.cors(); // Enable CORS support

        // Other security configurations
    }


}
