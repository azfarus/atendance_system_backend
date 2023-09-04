package com.example.atendance_system_backend.department;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface DepartmentRepository extends JpaRepository<Department , String> {

}
