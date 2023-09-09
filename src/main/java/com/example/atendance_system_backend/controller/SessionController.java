package com.example.atendance_system_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;


@RestController
@RequestMapping("/session")
public class SessionController {


    @CrossOrigin
    @GetMapping("/get-teacher-session-data")
    @ResponseBody
    private ResponseEntity<Long> teacher_session_attribute_retrieval(HttpServletRequest hsr){

        HttpSession session = hsr.getSession();
        if(session == null || session.isNew()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body((long)-2);
        }

        Long teacherid = (Long) session.getAttribute("teacherid");
        if(teacherid == null){

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body((long)-1);
        }
        else return ResponseEntity.status(HttpStatus.OK).body(teacherid);


    }

    @CrossOrigin
    @GetMapping("/get-student-session-data")
    @ResponseBody
    private ResponseEntity<Long> student_session_attribute_retrieval(HttpServletRequest hsr){

        HttpSession session = hsr.getSession();
        if(session == null || session.isNew()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body((long)-2);
        }

        Long studentid = (Long) session.getAttribute("studentid");
        if(studentid == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        else return ResponseEntity.status(HttpStatus.OK).body(studentid);


    }

    @CrossOrigin
    @GetMapping("/get-admin-session-data")
    @ResponseBody
    private ResponseEntity<Long> admin_session_attribute_retrieval(HttpServletRequest hsr){

        HttpSession session = hsr.getSession();
        if(session == null || session.isNew()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body((long)-2);
        }

        Long adminid = (Long) session.getAttribute("adminid");
        if(adminid == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        else return ResponseEntity.status(HttpStatus.OK).body(adminid);


    }
}
