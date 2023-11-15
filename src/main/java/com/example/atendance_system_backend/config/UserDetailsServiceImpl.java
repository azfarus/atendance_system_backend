package com.example.atendance_system_backend.config;

import com.example.atendance_system_backend.admin.Admin;
import com.example.atendance_system_backend.admin.AdminRepository;
import com.example.atendance_system_backend.student.Student;
import com.example.atendance_system_backend.student.StudentRepository;
import com.example.atendance_system_backend.teacher.Teacher;
import com.example.atendance_system_backend.teacher.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.management.relation.Role;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    StudentRepository student_db;

    @Autowired
    TeacherRepository teacherDB;

    @Autowired
    AdminRepository admin_db;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Admin> admin = admin_db.findAdminById(Long.parseLong(username));
        Optional<Teacher> teacher = teacherDB.findTeacherById(Long.parseLong(username));
        Optional<Student> student = student_db.findStudentById(Long.parseLong(username));

        System.out.println("usrdts");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


        if(admin.isPresent()){
            System.out.println("admin");
            Set<String> rolegula = new HashSet<>();
            rolegula.add("ADMIN");
            return new org.springframework.security.core.userdetails.User(
                    admin.get().getId().toString(), encoder.encode(admin.get().getPassword()), true, true, true, true,
                    getAuthorities("ADMIN"));
        }
        else if(teacher.isPresent()){
            System.out.println("tchr");
            Set<String> rolegula = new HashSet<>();
            rolegula.add("TEACHER");
            return new org.springframework.security.core.userdetails.User(
                    teacher.get().getId().toString(), encoder.encode(teacher.get().getPassword()), true, true, true, true,
                    getAuthorities("TEACHER"));
        }
        else if(student.isPresent()){
            System.out.println("stdnt");
            Set<String> rolegula = new HashSet<>();
            rolegula.add("STUDENT");
            return new org.springframework.security.core.userdetails.User(
                    student.get().getId().toString(), encoder.encode(student.get().getPassword()), true, true, true, true,
                    getAuthorities("STUDENT"));
        }
        else{
            throw new UsernameNotFoundException("User not found with username: ");
        }

         // Implement getAuthorities() method
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String roles) {

        List<SimpleGrantedAuthority> x = new ArrayList<>();
        x.add(new SimpleGrantedAuthority(roles));
        return x;
    }
}