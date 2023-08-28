package com.example.atendance_system_backend.login;





import com.example.atendance_system_backend.teacher.Teacher;
import com.example.atendance_system_backend.teacher.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    TeacherRepository teacher_db;
    @GetMapping("/teacher")
    @ResponseBody
    public  ResponseEntity<String> teacher_login(@RequestBody TeacherLoginDTO tchr_login_dto){
        Optional<Teacher> teacher = teacher_db.findTeacherById(tchr_login_dto.getId());
        System.out.println(tchr_login_dto.getId());
        if(teacher.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("FALSE");
        }
        if(teacher.get().getPassword().equals(tchr_login_dto.getPassword()) && (teacher.get().getId().equals(tchr_login_dto.getId()))){
            return ResponseEntity.status(HttpStatus.OK).body("TRUE");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("FALSE");
    }


}

