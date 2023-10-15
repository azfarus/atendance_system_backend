package com.example.atendance_system_backend.controller;

import com.example.atendance_system_backend.department.Department;
import com.example.atendance_system_backend.department.DepartmentRepository;
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
import java.util.ArrayList;
import java.util.List;
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

    @Autowired
    DepartmentRepository departmentDB;

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

    @CrossOrigin
    @GetMapping("/departments")
    @ResponseBody
    private ResponseEntity<List<String>> get_depts(HttpServletRequest hsr){

        //if(!check_session(hsr)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        List<Department> all_depts = departmentDB.findAll();
        List<String> dept_names = new ArrayList<String>();

        for(int i = 0 ; i < all_depts.size() ; i++){
            dept_names.add(all_depts.get(i).getName());
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(dept_names);
    }

    public  boolean check_session(HttpServletRequest hsr){


        String id = hsr.getHeader("mysession");
        System.out.println(id);
        if(id == null) return  false;
        Optional<MySession> sess= sessionDB.findById(id);

        return sess.isPresent() && sess.get().getType().equals("student");
    }




}