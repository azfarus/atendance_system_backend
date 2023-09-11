package com.example.atendance_system_backend.controller;

import com.example.atendance_system_backend.course.Course;
import com.example.atendance_system_backend.course.CourseRepository;
import com.example.atendance_system_backend.department.Department;
import com.example.atendance_system_backend.department.DepartmentRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminPanelController {

    @Autowired
    TeacherRepository teacherDB;

    @Autowired
    CourseRepository courseDB;

    @Autowired
    DepartmentRepository departmentDB;

    @Autowired
    StudentRepository studentDB;

    @Autowired
    MySessionRepository sessionDB;
    @CrossOrigin
    @PostMapping("/teacher")
    @ResponseBody
    private ResponseEntity<String> save_teacher(@RequestParam Long id ,
                                                @RequestParam String password ,
                                                @RequestParam String name ,
                                                @RequestParam String email , HttpServletRequest hsr){

        if(!check_session(hsr)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        Teacher new_teacher = new Teacher(id , password , name , email , null);
        teacherDB.save(new_teacher);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("COOL");
    }

    @CrossOrigin
    @PostMapping("/course")
    @ResponseBody
    private ResponseEntity<String> save_course(@RequestParam String department ,
                                               @RequestParam Long courseId ,
                                               @RequestParam Long count ,
                                               @RequestParam Character section ,
                                               @RequestParam Long teacherId,
                                               @RequestParam Long semester , HttpServletRequest hsr){

        if(!check_session(hsr)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");

        Optional<Teacher> responsible_teacher = teacherDB.findTeacherById(teacherId);
        if(responsible_teacher.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Teacher Not Found");
        }
        Course new_course = new Course(null , department , courseId , count , section ,semester , responsible_teacher.get());
        courseDB.save(new_course);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("COOL");
    }

    @CrossOrigin
    @PostMapping("/student")
    @ResponseBody
    private ResponseEntity<String> save_student(@RequestParam String name ,
                                               @RequestParam Long id ,
                                               @RequestParam String password ,
                                               @RequestParam String email ,
                                               @RequestParam String guardianEmail,
                                                @RequestParam String department ,
                                               @RequestParam Long semester , HttpServletRequest hsr){

        if(!check_session(hsr)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");


        Student new_student = new Student(id , name , email , password , semester , department);
        studentDB.save(new_student);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("COOL");
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

        return sess.isPresent() && sess.get().getType().equals("admin");
    }



}
