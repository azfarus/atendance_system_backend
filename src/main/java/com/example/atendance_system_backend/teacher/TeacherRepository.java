package com.example.atendance_system_backend.teacher;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher , Long> {

    Optional<Teacher> findTeacherById(Long id);


}
