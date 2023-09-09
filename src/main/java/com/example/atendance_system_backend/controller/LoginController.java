package com.example.atendance_system_backend.controller;





import com.example.atendance_system_backend.admin.Admin;
import com.example.atendance_system_backend.admin.AdminRepository;
import com.example.atendance_system_backend.student.Student;
import com.example.atendance_system_backend.student.StudentRepository;
import com.example.atendance_system_backend.teacher.Teacher;
import com.example.atendance_system_backend.teacher.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    TeacherRepository teacher_db;

    @Autowired
    StudentRepository student_db;

    @Autowired
    AdminRepository admin_db;

    @CrossOrigin
    @PostMapping("/teacher")
    @ResponseBody
    public  ResponseEntity<String> teacher_login(@RequestBody LoginDTO tchr_login_dto , HttpServletRequest hsr){
        Optional<Teacher> teacher = teacher_db.findTeacherById(tchr_login_dto.getId());
        System.out.println("Hello"+ tchr_login_dto.getId());


        HttpSession session = hsr.getSession();
        SetDefaultSession(session);
        if(teacher.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("FALSE");
        }
        if(teacher.get().getPassword().equals(tchr_login_dto.getPassword()) && (teacher.get().getId().equals(tchr_login_dto.getId()))){
            session.setAttribute("teacherid" , teacher.get().getId());
            session.setAttribute("loggedin" , true);
            return ResponseEntity.status(HttpStatus.OK).body("TRUE");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("FALSE");
    }

    @CrossOrigin
    @PostMapping("/student")
    @ResponseBody
    public  ResponseEntity<String> student_login(@RequestBody LoginDTO stdnt_login_dto, HttpServletRequest hsr){

        Optional<Student> student = student_db.findStudentById(stdnt_login_dto.getId());
        System.out.println("Hello"+ stdnt_login_dto.getId());


        HttpSession session = hsr.getSession();
        SetDefaultSession(session);
        if(student.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("FALSE");
        }
        if(student.get().getPassword().equals(stdnt_login_dto.getPassword()) && (student.get().getId().equals(stdnt_login_dto.getId()))){
            session.setAttribute("studentid" , student.get().getId());
            session.setAttribute("loggedin" , true);
            return ResponseEntity.status(HttpStatus.OK).body("TRUE");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("FALSE");
    }

    @CrossOrigin
    @PostMapping("/admin")
    @ResponseBody
    public  ResponseEntity<String> admin_login(@RequestBody LoginDTO admn_login_dto, HttpServletRequest hsr){

        Optional<Admin> admin = admin_db.findAdminById(admn_login_dto.getId());
        System.out.println("Hello"+ admn_login_dto.getId());


        HttpSession session = hsr.getSession();
        SetDefaultSession(session);
        if(admin.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("FALSE");
        }
        if(admin.get().getPassword().equals(admn_login_dto.getPassword()) && (admin.get().getId().equals(admn_login_dto.getId()))){
            session.setAttribute("adminid" , admin.get().getId());
            session.setAttribute("loggedin" , true);
            return ResponseEntity.status(HttpStatus.OK).body("TRUE");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("FALSE");
    }

    public void SetDefaultSession(HttpSession session){
        session.setAttribute("loggedin" , false);
        session.setAttribute("teacherid" , null);
        session.setAttribute("studentid" , null);
        session.setAttribute("adminid" , null);

    }




}

