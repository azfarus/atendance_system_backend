package com.example.atendance_system_backend.controller;


import com.example.atendance_system_backend.session.MySession;
import com.example.atendance_system_backend.session.MySessionRepository;
import com.example.atendance_system_backend.teacher.Teacher;
import com.example.atendance_system_backend.teacher.TeacherRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/teacher")
public class TeacherController {
    //check if request is a teaacher then send teacher information

    @Autowired
    MySessionRepository sessionDB;

    @Autowired
    TeacherRepository teacherDB;

    @Autowired
    ObjectMapper mapper;



    @GetMapping("/info")
    @ResponseBody
    private ResponseEntity<ObjectNode> info(@RequestParam(name = "teacherid") Long id , HttpServletRequest hsr){
        if(!check_session(hsr)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        Optional<Teacher> teach_info = teacherDB.findTeacherById(id);

        if(teach_info.isEmpty()) ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);


        ObjectNode teacher = mapper.createObjectNode();
        teacher.put("id" , teach_info.get().getId());
        teacher.put("name" , teach_info.get().getName());
        teacher.put("email" , teach_info.get().getEmail());

        return ResponseEntity.status(HttpStatus.OK).body(teacher);

    }

    public  boolean check_session(HttpServletRequest hsr){


        String id = hsr.getHeader("mysession");
        System.out.println(id);
        if(id == null) return  false;
        Optional<MySession> sess= sessionDB.findById(id);

        return sess.isPresent() && sess.get().getType().equals("teacher");
    }
}
