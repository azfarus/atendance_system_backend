package com.example.atendance_system_backend.controller;

import com.example.atendance_system_backend.attendance.Attendance;
import com.example.atendance_system_backend.attendance.AttendanceRepository;
import com.example.atendance_system_backend.course.Course;
import com.example.atendance_system_backend.course.CourseRepository;
import com.example.atendance_system_backend.coursereg.StudentTakesCourse;
import com.example.atendance_system_backend.coursereg.StudentTakesCourseRepository;
import com.example.atendance_system_backend.session.MySession;
import com.example.atendance_system_backend.session.MySessionRepository;
import com.example.atendance_system_backend.student.Student;
import com.example.atendance_system_backend.student.StudentRepository;
import com.example.atendance_system_backend.teacher.Teacher;
import com.example.atendance_system_backend.teacher.TeacherRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/attendance")
public class AttendanceController {


    @Autowired
    TeacherRepository teacherDB;

    @Autowired
    StudentRepository studentDB;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    CourseRepository courseDB;

    @Autowired
    StudentTakesCourseRepository courseregDB;

    @Autowired
    AttendanceRepository attendanceDB;




    @GetMapping("/get-students")
    @ResponseBody
    private ResponseEntity<ObjectNode> get_students(@RequestParam Long hid){

        List<StudentTakesCourse> studentids = courseregDB.findAllByCourseHid(hid);

        ObjectNode res_student = mapper.createObjectNode();

        for(StudentTakesCourse x : studentids){
            Optional<Student> s = studentDB.findStudentById(x.getStudentId());
            if(s.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);



            res_student.put(s.get().getId().toString() , s.get().getName() );


        }

        return ResponseEntity.status(HttpStatus.OK).body(res_student);
    }


    @PostMapping("/submit-attendance/{hid}")
    @ResponseBody
    private ResponseEntity<String> submit_attendance(@RequestBody Map<String , String> attendanceMap , @PathVariable Long hid){

        if(!courseDB.existsById(hid)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("eheh");

        LocalDate d = LocalDate.now();

        System.out.println(hid);
        for(Map.Entry<String,String> x : attendanceMap.entrySet()){
            System.out.println(x.getKey() + " : " + x.getValue());
            Attendance atd = new Attendance(Long.parseLong(x.getKey()) , d , x.getValue() , hid);
            attendanceDB.save(atd);
        }
        return ResponseEntity.status(HttpStatus.OK).body("HEHE");
    }


    @GetMapping("/prev-attendance/{hid}")
    @ResponseBody
    private ResponseEntity<ObjectNode> prev_attendance( @PathVariable Long hid) throws JsonProcessingException {

        if(!courseDB.existsById(hid)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

        List<StudentTakesCourse> studentids = courseregDB.findAllByCourseHid(hid);
        if(studentids.size() <= 0) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

        ObjectNode attendanceofstudents = mapper.createObjectNode();
        ArrayNode startinfo = mapper.createArrayNode(); // array of dates

        List<Attendance> studentlist = attendanceDB.findAttendanceByStudentIdAndCourseHid(studentids.get(0).getStudentId() , hid );
        startinfo.add("Name");
        for(Attendance a : studentlist){
            startinfo.add(a.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }
        attendanceofstudents.set("start" , startinfo);

        for(StudentTakesCourse x : studentids){
            studentlist = attendanceDB.findAttendanceByStudentIdAndCourseHid(x.getStudentId() , hid); // attendances from the attendancedb
            Optional<Student> s = studentDB.findStudentById(x.getStudentId()); // to get the student name


            Collections.sort(studentlist); // sort according to date

            ArrayNode info = mapper.createArrayNode(); // array of P A L

            info.add(s.get().getName());



            for(Attendance y : studentlist){
                info.add(y.getStatus());
            }

            attendanceofstudents.set(x.getStudentId().toString()  , info );
        }

        return ResponseEntity.status(HttpStatus.OK).body(attendanceofstudents);
    }

}