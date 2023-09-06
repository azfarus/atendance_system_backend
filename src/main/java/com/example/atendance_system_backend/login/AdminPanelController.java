package com.example.atendance_system_backend.login;

import com.example.atendance_system_backend.course.Course;
import com.example.atendance_system_backend.course.CourseRepository;
import com.example.atendance_system_backend.department.Department;
import com.example.atendance_system_backend.department.DepartmentRepository;
import com.example.atendance_system_backend.teacher.Teacher;
import com.example.atendance_system_backend.teacher.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @CrossOrigin
    @PostMapping("/teacher")
    @ResponseBody
    private ResponseEntity<String> save_teacher(@RequestParam Long id , @RequestParam String password , @RequestParam String name , @RequestParam String email){
        Teacher new_teacher = new Teacher(id , password , name , email , null);
        teacherDB.save(new_teacher);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("COOL");
    }

    @CrossOrigin
    @PostMapping("/course")
    @ResponseBody
    private ResponseEntity<String> save_course(@RequestParam String department , @RequestParam Long courseId , @RequestParam Long count , @RequestParam Character section , @RequestParam Long teacherId){

        Optional<Teacher> responsible_teacher = teacherDB.findTeacherById(teacherId);
        if(responsible_teacher.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Teacher Not Found");
        }
        Course new_course = new Course(null , department , courseId , count , section , responsible_teacher.get());
        courseDB.save(new_course);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("COOL");
    }

    @CrossOrigin
    @GetMapping("/departments")
    @ResponseBody
    private ResponseEntity<List<String>> get_depts(){

        List<Department> all_depts = departmentDB.findAll();
        List<String> dept_names = new ArrayList<String>();

        for(int i = 0 ; i < all_depts.size() ; i++){
            dept_names.add(all_depts.get(i).getName());
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(dept_names);
    }

}
