package com.example.atendance_system_backend.session;


import org.springframework.data.jpa.repository.JpaRepository;

public interface MySessionRepository extends JpaRepository<MySession , String> {
}
