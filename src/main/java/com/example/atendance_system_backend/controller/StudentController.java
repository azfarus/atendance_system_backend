package com.example.atendance_system_backend.controller;

import com.example.atendance_system_backend.session.MySession;
import com.example.atendance_system_backend.session.MySessionRepository;
import com.example.atendance_system_backend.student.Student;
import com.example.atendance_system_backend.student.StudentRepository;
import com.example.atendance_system_backend.teacher.Teacher;
import com.example.atendance_system_backend.teacher.TeacherRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/student")
public class StudentController {
    //check if request is a teaacher then send teacher information

    @Autowired
    MySessionRepository sessionDB;

    @Autowired
    StudentRepository studentDB;

    @Autowired
    ObjectMapper mapper;



    @GetMapping("/info")
    @ResponseBody
    private ResponseEntity<ObjectNode> info(@RequestParam(name = "studentid") Long id , HttpServletRequest hsr){
        if(!check_session(hsr)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        Optional<Student> student_info = studentDB.findStudentById(id);

        if(student_info.isEmpty()) ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);


        ObjectNode student = mapper.createObjectNode();
        student.put("id" , student_info.get().getId());
        student.put("name" , student_info.get().getName());
        student.put("email" , student_info.get().getMail());

        return ResponseEntity.status(HttpStatus.OK).body(student);

    }

    public  boolean check_session(HttpServletRequest hsr){


        String id = hsr.getHeader("mysession");
        System.out.println(id);
        if(id == null) return  false;
        Optional<MySession> sess= sessionDB.findById(id);

        return sess.isPresent() && sess.get().getType().equals("student");
    }
}