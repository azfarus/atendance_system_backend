package com.example.atendance_system_backend.controller;


import com.example.atendance_system_backend.course.Course;
import com.example.atendance_system_backend.course.CourseRepository;
import com.example.atendance_system_backend.coursereg.StudentTakesCourse;
import com.example.atendance_system_backend.coursereg.StudentTakesCourseRepository;
import com.example.atendance_system_backend.email.GmailEmailSender;
import com.example.atendance_system_backend.session.MySession;
import com.example.atendance_system_backend.session.MySessionRepository;
import com.example.atendance_system_backend.student.Student;
import com.example.atendance_system_backend.student.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/course")
public class CourseRegistrationController {
    // an api to get the courses based on departments
    // credential checking must that i am a student
    // an api to enroll a student with a course
    // optionally send mail

    @Autowired
    CourseRepository courseDB;

    @Autowired
    StudentTakesCourseRepository regDB;

    @Autowired
    MySessionRepository sessionDB;

    @Autowired
    StudentRepository studentDB;

    @Autowired
    GmailEmailSender gmailEmailSender;

    @Autowired
    ObjectMapper mapper;

    @GetMapping("/get-course-by-dept")
    @ResponseBody
    private ResponseEntity<List<ObjectNode>> get_all_course(@RequestParam String department , HttpServletRequest hsr){

        System.out.println("Trynna get courses");
        if(!check_session(hsr)) ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        List<Course> courses = courseDB.findCoursesByDepartment(department);
        List<ObjectNode> course_names = new ArrayList<>();

       for(Course c : courses){
           System.out.println("Trynna loop");
           ObjectNode courseNode=mapper.createObjectNode();
           courseNode.put("hid" , c.getHid().toString());
           courseNode.put("name" ,c.getDepartment()+" "+c.getCourseId().toString()+" "+" "+c.getSection()+" "+ c.getCourseName());
           course_names.add(courseNode);
       }
       return ResponseEntity.status(HttpStatus.ACCEPTED).body(course_names);

    }

    @PostMapping("/register")
    @ResponseBody
    private ResponseEntity<String> register_course(@RequestParam Long stud_id ,@RequestParam String department ,@RequestParam Long course , HttpServletRequest hsr) throws Exception {

        if(!check_session(hsr)) ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        List<Course> reg_course = courseDB.findCourseByDepartmentAndCourseId(department,course);

        if(reg_course.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NO COURSE");

        Optional<Student> s = studentDB.findStudentById(stud_id);

        if(s.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid ID");

        for(Course x : reg_course){
            if(x.getCount() > 0){
                StudentTakesCourse new_reg = new StudentTakesCourse(stud_id , x.getHid());
                regDB.save(new_reg);

                x.setCount(x.getCount()-1);
                courseDB.save(x);
                String mailsubj= "Course registration successful";
                String mailbody= "You have been registered to:\n" +x.getCourseId()+" "+ x.getCourseName()+"\nSection:"+x.getSection().toString();
                gmailEmailSender.sendEmail(s.get().getMail() , mailsubj , mailbody);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Registered to "+ x.getCourseName());
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Slots filled");



    }


    public  boolean check_session(HttpServletRequest hsr){


        String id = hsr.getHeader("mysession");
        System.out.println(id);
        if(id == null) return  false;
        Optional<MySession> sess= sessionDB.findById(id);

        return sess.isPresent() && sess.get().getType().equals("student");
    }

}
