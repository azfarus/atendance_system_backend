package com.example.atendance_system_backend.controller;

import com.example.atendance_system_backend.admin.Admin;
import com.example.atendance_system_backend.admin.AdminRepository;
import com.example.atendance_system_backend.email.GmailEmailSender;
import com.example.atendance_system_backend.email.GmailService;
import com.example.atendance_system_backend.forgotpassword.TokenTable;
import com.example.atendance_system_backend.forgotpassword.TokenTableRepository;
import com.example.atendance_system_backend.hasher.StringHasher;
import com.example.atendance_system_backend.session.MySession;
import com.example.atendance_system_backend.session.MySessionRepository;
import com.example.atendance_system_backend.student.Student;
import com.example.atendance_system_backend.student.StudentRepository;
import com.example.atendance_system_backend.teacher.Teacher;
import com.example.atendance_system_backend.teacher.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping("/forgotpass")
public class ForgotPassController {

    @Autowired
    TeacherRepository teacher_db;

    @Autowired
    StudentRepository student_db;

    @Autowired
    AdminRepository admin_db;

    @Autowired
    MySessionRepository sessionDB;


    @Autowired
    GmailEmailSender gmailEmailSender;

    @Autowired
    TokenTableRepository tk_DB;


    String reset_endpoint="localhost:5501";
    String server_endpoint="localhost";

    @CrossOrigin
    @PostMapping("/request-change")
    @ResponseBody
    public ResponseEntity<String> request_change(@RequestParam Long id ) throws Exception {
        Optional<Student> stdnt = student_db.findStudentById(id);
        Optional<Teacher> tchr = teacher_db.findTeacherById(id);


        String token=StringHasher.hashString(id.toString() + Instant.now().toString());
        String emailBody = "Click here to change your password: http://" + reset_endpoint + "/reset.html?token=" + token + "&server=" + server_endpoint;

        tk_DB.save(new TokenTable(id , token , Instant.now()));

        if(stdnt.isPresent() && tchr.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INVALID PARAMS");
        }
        else if(stdnt.isPresent()){


            gmailEmailSender.sendEmail(stdnt.get().getMail() ,"Reset Password" ,emailBody);
        }
        else if (tchr.isPresent()) {
            gmailEmailSender.sendEmail(tchr.get().getEmail() ,"Reset Password" ,emailBody);
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INVALID PARAMS");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Check Email");
    }

//    @CrossOrigin
//    @PostMapping("/exec-change")
//    @ResponseBody
//    public  ResponseEntity<String> exec_change(@RequestBody LoginDTO stdnt_login_dto, HttpServletRequest hsr){
//
//
//    }

    @CrossOrigin
    @PostMapping("/exec-change")
    @ResponseBody
    public ResponseEntity<String> exec_change(@RequestParam String password , @RequestParam String token ) throws Exception {
        System.out.println(token);

        Optional<TokenTable> tktb = tk_DB.findTokenTableByToken(token);

        if(tktb.isPresent()){
            Optional<Student> stdnt = student_db.findStudentById(tktb.get().getUserId());
            Optional<Teacher> tchr = teacher_db.findTeacherById(tktb.get().getUserId());
            if(stdnt.isPresent()){

                stdnt.get().setPassword(password);
                student_db.save(stdnt.get());
                return ResponseEntity.status(HttpStatus.OK).body("PASS CHANGED");

            }
            else if (tchr.isPresent()) {
                tchr.get().setPassword(password);
                teacher_db.save(tchr.get());
                return ResponseEntity.status(HttpStatus.OK).body("PASS CHANGED");
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INVALID PARAMS");
            }

        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Problem");
    }

}
