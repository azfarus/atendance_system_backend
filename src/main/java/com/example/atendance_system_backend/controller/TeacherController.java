package com.example.atendance_system_backend.controller;


import com.example.atendance_system_backend.course.Course;
import com.example.atendance_system_backend.course.CourseRepository;
import com.example.atendance_system_backend.department.Department;
import com.example.atendance_system_backend.department.DepartmentRepository;
import com.example.atendance_system_backend.file.File;
import com.example.atendance_system_backend.file.FileStorageService;
import com.example.atendance_system_backend.session.MySession;
import com.example.atendance_system_backend.session.MySessionRepository;
import com.example.atendance_system_backend.student.Student;
import com.example.atendance_system_backend.teacher.Teacher;
import com.example.atendance_system_backend.teacher.TeacherRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    @Autowired
    CourseRepository courseDB;

    @Autowired
    FileStorageService fileStorageService;


    @Autowired
    DepartmentRepository departmentDB;

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


    @CrossOrigin
    @PostMapping("/course-teacher-assign")
    @ResponseBody
    private ResponseEntity<String> save_course_teacher(@RequestParam String department ,
                                                       @RequestParam Long courseCode,
                                                       @RequestParam Long teacherid,
                                                       @RequestParam String section,
                                                       HttpServletRequest hsr){

        System.out.println(section.charAt(0));
        Optional<Course> course = courseDB.findCourseByDepartmentAndCourseIdAndSection(department , courseCode , Character.toUpperCase(section.charAt(0)));
        Optional<Teacher> teacher = teacherDB.findTeacherById(teacherid);


        if( course.isPresent()  && teacher.isPresent()){


            if(course.get().getTeacher() != null)  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Course already taken");
            course.get().setTeacher(teacher.get());
            courseDB.save(course.get());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("COOL");

        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid teacher or student");
        }
    }

    @GetMapping("/sheets")
    @ResponseBody
    private ResponseEntity<List<ObjectNode>> get_sheets(@RequestParam Long teacherId){

        Optional<Teacher> reqTeacher = teacherDB.findTeacherById(teacherId);

        if(reqTeacher.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);


        List<Course> courses = courseDB.findCoursesByTeacher(reqTeacher.get());

        List<ObjectNode> result = new ArrayList<>();

        for( Course x : courses){
            ObjectNode teacher = mapper.createObjectNode();
            teacher.put("hid" , x.getHid());
            teacher.put("coursename" , x.getCourseName());
            teacher.put("courseid" , x.getCourseId());
            teacher.put("department" , x.getDepartment());
            teacher.put("section" , x.getSection().toString());

            result.add(teacher);
        }
        return  ResponseEntity.status(HttpStatus.ACCEPTED).body(result);
    }

    @CrossOrigin
    @PostMapping("/upload-photo/{teachid}")
    @ResponseBody
    private ResponseEntity<Long> upload_photo(@RequestParam MultipartFile file , @PathVariable Long teachid ) throws IOException {

        Long fileid = fileStorageService.store(file);

        if(fileid < 0 )return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

        Optional<Teacher> t = teacherDB.findTeacherById(teachid);

        if(t.isEmpty())return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

        t.get().setFileId(fileid);

        teacherDB.save(t.get());

        return ResponseEntity.status(HttpStatus.OK).body(fileid);


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


    @CrossOrigin
    @GetMapping("/get-photo/{teachid}")
    @ResponseBody
    private ResponseEntity<byte[]> get_photo( @PathVariable Long teachid ) throws IOException {

        Optional<Teacher> s = teacherDB.findTeacherById(teachid);

        if(s.isEmpty()) ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

        File fileDB = fileStorageService.getFile(s.get().getFileId());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getFilename() + "\"")
                .body(fileDB.getData());


    }
    public  boolean check_session(HttpServletRequest hsr){


        String id = hsr.getHeader("mysession");
        System.out.println(id);
        if(id == null) return  false;
        Optional<MySession> sess= sessionDB.findById(id);

        return sess.isPresent() && sess.get().getType().equals("teacher");
    }
}
