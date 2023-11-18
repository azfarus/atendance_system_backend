package com.example.atendance_system_backend.controller;

import com.example.atendance_system_backend.session.MySession;
import com.example.atendance_system_backend.session.MySessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/session")
@CrossOrigin("*")
public class SessionController {


    @Autowired
    MySessionRepository sessionDB;

    @CrossOrigin
    @GetMapping("/get-session-data")
    @ResponseBody
    private ResponseEntity<Long> teacher_session_attribute_retrieval(HttpServletRequest hsr){

        if(check_session(hsr)){
            return ResponseEntity.status(HttpStatus.OK).body(get_session_id(hsr));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body((long)-1);

    }



    public  boolean check_session(HttpServletRequest hsr){


        String id = hsr.getHeader("mysession");
        System.out.println(id);
        if(id == null) return  false;
        Optional<MySession> sess= sessionDB.findById(id);

        return sess.isPresent();
    }

    public  Long get_session_id(HttpServletRequest hsr){


        String id = hsr.getHeader("mysession");
        System.out.println(id);
        if(id == null) return  null;
        Optional<MySession> sess= sessionDB.findById(id);
        if(sess.isPresent())
            return sess.get().getId();
        else return  null;
    }
}
