package com.example.atendance_system_backend.controller;

import com.example.atendance_system_backend.attendance.Attendance;
import com.example.atendance_system_backend.attendance.AttendanceRepository;
import com.example.atendance_system_backend.course.Course;
import com.example.atendance_system_backend.course.CourseRepository;
import com.example.atendance_system_backend.coursereg.StudentCourseIdClass;
import com.example.atendance_system_backend.coursereg.StudentTakesCourse;
import com.example.atendance_system_backend.coursereg.StudentTakesCourseRepository;
import com.example.atendance_system_backend.email.GmailEmailSender;
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
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.text.DecimalFormat;

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

    @Autowired
    GmailEmailSender gmailEmailSender;




    @GetMapping("/get-students")
    @ResponseBody
    private ResponseEntity<ObjectNode> get_students(@RequestParam Long hid ,
                                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate attendanceDate){

        List<StudentTakesCourse> studentids = courseregDB.findAllByCourseHid(hid);

        ObjectNode res_student = mapper.createObjectNode();

        for(StudentTakesCourse x : studentids){
            Optional<Student> s = studentDB.findStudentById(x.getStudentId());
            if(s.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);



            Optional<Attendance> prev_attendance_status = attendanceDB.findAttendanceByStudentIdAndCourseHidAndDate(x.getStudentId() , hid , attendanceDate);
            ObjectNode student_deets= mapper.createObjectNode();
            student_deets.put("name" , s.get().getName());
            student_deets.put("status", "P");
            prev_attendance_status.ifPresent(attendance -> student_deets.put("status", attendance.getStatus()));



            res_student.set(s.get().getId().toString() , student_deets );


        }

        return ResponseEntity.status(HttpStatus.OK).body(res_student);
    }


    @PostMapping("/submit-attendance/{hid}")
    @ResponseBody
    private ResponseEntity<String> submit_attendance(
            @RequestBody Map<String, String> attendanceMap,
            @PathVariable Long hid,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate attendanceDate
    ) {
        if (!courseDB.existsById(hid)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Course not found");
        }

        System.out.println(hid);
        for (Map.Entry<String, String> entry : attendanceMap.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
            Attendance atd = new Attendance(
                    Long.parseLong(entry.getKey()),
                    attendanceDate,
                    entry.getValue(),
                    hid
            );
            attendanceDB.save(atd);
        }

        return ResponseEntity.status(HttpStatus.OK).body("Attendance submitted successfully");
    }


    @PostMapping("/send-warning/{hid}")
    @ResponseBody
    private ResponseEntity<String> warning_sender(@RequestBody List<Long> defaulters, @PathVariable Long hid) throws Exception {

        Optional<Course> c = courseDB.findById(hid);

        if(c.isEmpty() ) return null;


        String topic = "Low Attendance Percentage";
        String message= "Your attendance percentage in your course : "+c.get().getCourseId()+" "+c.get().getCourseName()+" is below the required limit.";

        for(Long x : defaulters){
            System.out.println(x);
            Optional<Student> s = studentDB.findStudentById(x);
            if(s.isEmpty()) continue;
            gmailEmailSender.sendEmail(s.get().getMail() , topic , message);
        }
        return ResponseEntity.status(HttpStatus.OK).body("Succees");
    }


    @GetMapping("/get-defaulters/{hid}/{percentage}")
    @ResponseBody
    private ResponseEntity<List<Long>> get_defaulters( @PathVariable Long hid , @PathVariable Double percentage){

        percentage/=100.0;


        if(!courseDB.existsById(hid)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

        List<StudentTakesCourse> studentsInCourse = courseregDB.findAllByCourseHid(hid);
        List<Long> defaulters = new ArrayList<>();
        for(StudentTakesCourse k :  studentsInCourse){
            Double percentVal = get_percentage_func(hid , k.getStudentId());
            if( percentVal>= 0 && percentVal < percentage )defaulters.add(k.getStudentId());
            System.out.println("ID "+ k.getStudentId() +" Percentage "+percentVal);
        }

        return ResponseEntity.status(HttpStatus.OK).body(defaulters);

    }

    @GetMapping("/get-percentage/{hid}/{sid}")
    @ResponseBody
    private ResponseEntity<Double> get_percentage(@PathVariable Long hid , @PathVariable Long sid){

        return ResponseEntity.status(HttpStatus.OK).body(get_percentage_func(hid , sid));


    }

    private Double get_percentage_func(Long hid , Long sid){
        List<Attendance> attendanceListofStudentId = attendanceDB.findAttendanceByStudentIdAndCourseHid(sid , hid);

        double tot=0;
        double present = 0;

        for (Attendance x : attendanceListofStudentId){
            if (x.getStatus().equals("P") || x.getStatus().equals("L")){
                present+=1;
            }
            tot+=1;
        }


        if(tot == 0 ) return (double)0;
        return Math.round((present / tot) * 1000.0) / 1000.0;



    }

    @DeleteMapping("/unenroll")
    @ResponseBody
    private  ResponseEntity<String> unenroll_teacher(@RequestParam Long tid , @RequestParam Long hid){


        Optional<Teacher> tchr = teacherDB.findTeacherById(tid);
        Optional<Course>  course = courseDB.findById(hid);

        if(tchr.isPresent() && course.isPresent()){
            tchr.get().getCourse().remove(course.get());
            teacherDB.save(tchr.get());
            course.get().getTeacher().remove(tchr.get());
            courseDB.save(course.get());
            return ResponseEntity.status(HttpStatus.OK).body("UNENROLLED");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("FAILED");
    }

    @DeleteMapping("/delete-student")
    @ResponseBody
    private ResponseEntity<String> delete_stud(@RequestParam Long hid, @RequestParam Long stud_id) {
        try {
            if (stud_id == 0) {
                // If stud_id is 0, delete all students for the given course
                courseregDB.deleteAll(courseregDB.findAllByCourseHid(hid));
                attendanceDB.deleteAll(attendanceDB.findAttendanceByCourseHid(hid));
                return ResponseEntity.status(HttpStatus.OK).body("DELETED_ALL");
            } else {
                // Delete the specified student for the given course
                courseregDB.deleteById(new StudentCourseIdClass(stud_id, hid));
                attendanceDB.deleteAll(attendanceDB.findAttendanceByStudentIdAndCourseHid(stud_id , hid));
                return ResponseEntity.status(HttpStatus.OK).body("DELETED_SINGLE");
            }
        } catch (Exception e) {
            // Log the exception for debugging purposes
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR");
        }
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
        startinfo.add("Percentage");


        Map<LocalDate , Integer> dateToIndex = new HashMap<>();
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        for(StudentTakesCourse x : studentids){
            studentlist = attendanceDB.findAttendanceByStudentIdAndCourseHid(x.getStudentId() , hid); // attendances from the attendancedb
            Optional<Student> s = studentDB.findStudentById(x.getStudentId()); // to get the student name


            Collections.sort(studentlist); // sort according to date

            ArrayNode info = mapper.createArrayNode();// array of P A L


            info.add(s.get().getName());
            info.add(   decimalFormat.format(get_percentage_func(hid , x.getStudentId())*100 )+ "%");


            for(Attendance y : studentlist){

                if(!dateToIndex.containsKey(y.getDate())){
                    dateToIndex.put(y.getDate() , dateToIndex.size()+2);
                }
                Integer place = dateToIndex.get(y.getDate());

                while(info.size() <= place){
                    info.add("");
                }

                while(startinfo.size() <= place){
                    startinfo.add("");
                }

                startinfo.set(place,y.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                info.set(place , y.getStatus());



            }

            System.out.println(info);



            attendanceofstudents.set(x.getStudentId().toString()  , info );
        }




        attendanceofstudents.set("start" , startinfo);
        return ResponseEntity.status(HttpStatus.OK).body(attendanceofstudents);
    }

}
