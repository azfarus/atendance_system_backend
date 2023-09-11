package com.example.atendance_system_backend.controller;


import com.example.atendance_system_backend.course.Course;
import com.example.atendance_system_backend.course.CourseRepository;
import com.example.atendance_system_backend.session.MySession;
import com.example.atendance_system_backend.session.MySessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/course")
public class CourseRegistrationController {
    // an api to get the courses based on departments
    // credential checking must that i am a student
    // an api to enroll a student with a course
    // optionally send mail

    @Autowired
    CourseRepository courseDB;

    @Autowired
    MySessionRepository sessionDB;
    @GetMapping("/get-course-by-dept")
    @ResponseBody
    private ResponseEntity<List<Long>> get_all_course(@RequestParam String department , HttpServletRequest hsr){

        if(!check_session(hsr)) ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        List<Course> courses = courseDB.findCoursesByDepartment(department);
        List<Long> course_names = new ArrayList<>();

       for(Course c : courses){
           course_names.add( c.getCourseId());
       }
       return ResponseEntity.status(HttpStatus.ACCEPTED).body(course_names);

    }

    @PostMapping("/register")
    @ResponseBody
    private ResponseEntity<String> register_course(@RequestParam String department ,Long course , HttpServletRequest hsr){

        if(!check_session(hsr)) ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        Optional<Course> reg_course = courseDB.findCourseByDepartmentAndCourseId(department,course);

        if(reg_course.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NO COURSE");



        return ResponseEntity.status(HttpStatus.ACCEPTED).body("ok");

    }


    public  boolean check_session(HttpServletRequest hsr){


        String id = hsr.getHeader("mysession");
        System.out.println(id);
        if(id == null) return  false;
        Optional<MySession> sess= sessionDB.findById(id);

        return sess.isPresent() && sess.get().getType().equals("student");
    }

}
