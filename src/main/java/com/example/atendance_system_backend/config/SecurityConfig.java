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
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Use BCryptPasswordEncoder for password hashing
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/login/**").permitAll() // Public resources
                .antMatchers("/student/**").hasRole("STUDENT") // Requires "USER" role
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/teacher/**").hasRole("TEACHER")// Requires "ADMIN" role
                .anyRequest().authenticated() // Requires authentication for any other request
                .and()
                .httpBasic().and().csrf().disable();
        http.cors(); // Enable CORS support

        // Other security configurations
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://127.0.0.1:5501"); // Allow requests from any origin (not recommended for production)
        configuration.addAllowedMethod("*"); // Allow all HTTP methods
        configuration.addAllowedHeader("*"); // Allow all headers
        configuration.setAllowCredentials(true); // Allow credentials (e.g., cookies)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public UserDetailsService userDetailsService() {

        List<UserDetails> allusers = new ArrayList<>();
        List<Student> students = studentDB.findAll();

        for(Student x : students){

            UserDetails user = User.builder()
                    .username(x.getId().toString())
                    .password(passwordEncoder().encode(x.getPassword())) // Encode the password
                    .roles("STUDENT")
                    .build();

            allusers.add(user);

        }

        List<Teacher> teachers = teacherDB.findAll();

        for(Teacher x : teachers){

            UserDetails user = User.builder()
                    .username(x.getId().toString())
                    .password(passwordEncoder().encode(x.getPassword())) // Encode the password
                    .roles("TEACHER")
                    .build();

            allusers.add(user);

        }

        List<Admin> admins = adminDB.findAll();

        for(Admin x : admins){

            UserDetails user = User.builder()
                    .username(x.getId().toString())
                    .password(passwordEncoder().encode(x.getPassword())) // Encode the password
                    .roles("ADMIN")
                    .build();

            allusers.add(user);

        }



        return new InMemoryUserDetailsManager(allusers);
    }
}
