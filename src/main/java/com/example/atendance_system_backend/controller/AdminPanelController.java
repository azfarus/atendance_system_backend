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
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

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
                                               @RequestParam Long semester , HttpServletRequest hsr){

        if(!check_session(hsr)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");


        Course new_course = new Course(null , department , courseId , count , section ,semester , null);
        courseDB.save(new_course);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("COOL");
    }


    @CrossOrigin
    @PostMapping("/course-teacher-assign")
    @ResponseBody
        private ResponseEntity<String> save_course_teacher(@RequestParam String department ,
                                                           @RequestParam Long courseCode,
                                                           @RequestParam Long teacherid,
                                                           HttpServletRequest hsr){

        if(!check_session(hsr)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");


        Optional<Course> course = courseDB.findCourseByDepartmentAndCourseId(department , courseCode);
        Optional<Teacher> teacher = teacherDB.findTeacherById(teacherid);


        if( course.isPresent()  && teacher.isPresent()){

            course.get().setTeacher(teacher.get());
            courseDB.save(course.get());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("COOL");

        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid teacher or student");
        }
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



    @PostMapping("/upload-csv")
    public String uploadFile(@RequestParam("file") MultipartFile file , @RequestParam int type) {
        try {
            // Check if the uploaded file is not empty
            if (!file.isEmpty()) {
                String fileName =  Math.round(Math.random() * 1000000000) + file.getOriginalFilename() ;
                String uploadDirectory = System.getProperty("user.dir") +"/src/main/resources/";

                // Save the file to the specified directory within the project
                file.transferTo(new File(uploadDirectory + fileName));

                switch (type){
                    case 1:
                        insert_student_csv(uploadDirectory + fileName);
                        break;
                    case 2:
                        insert_teacher_csv(uploadDirectory + fileName);
                        break;

                    case 3:
                        insert_course_csv(uploadDirectory + fileName);
                        break;

                    default:
                        return "Invalid Param";

                }
                return "File uploaded successfully!";
            } else {
                return "File is empty!";
            }
        } catch (IOException e) {
            return "File upload failed: " + e.getMessage();
        }
    }

    public  boolean check_session(HttpServletRequest hsr){


        String id = hsr.getHeader("mysession");
        System.out.println(id);
        if(id == null) return  false;
        Optional<MySession> sess= sessionDB.findById(id);

        return sess.isPresent() && sess.get().getType().equals("admin");
    }

    private boolean insert_student_csv(String filepath){
        try {
            // Create a CSVReader object with the file path
            FileReader reader = new FileReader(filepath);
            CSVReader csvReader = new CSVReader(reader);

            // Read all the rows from the CSV file
            List<String[]> records = csvReader.readAll();

            // Loop through the records and process each row
            for (String[] record : records) {
                Long id = Long.parseLong(record[0]);
                String name = record[1];
                String mail = record[2];
                String pass = record[3];
                Long semester = Long.parseLong(record[4]);
                String dept = record[5];

                Student s = new Student(id , name , mail , pass, semester , dept);
                studentDB.save(s);
                //System.out.println(); // Move to the next line for the next record
            }

            // Close the CSVReader
            csvReader.close();
            return true;
        } catch (IOException | CsvException e) {
            e.printStackTrace();
            return false;
        }

    }

    private boolean insert_teacher_csv(String filepath) {
        try {
            // Create a CSVReader object with the file path
            FileReader reader = new FileReader(filepath);
            CSVReader csvReader = new CSVReader(reader);

            // Read all the rows from the CSV file
            List<String[]> records = csvReader.readAll();

            // Loop through the records and process each row
            for (String[] record : records) {
                String name = record[0];
                Long id = Long.parseLong(record[1]);
                String pass = record[2];
                String mail = record[3];

                Random x = new Random();
                if (pass.isBlank()) {
                    pass = id.toString() + x.nextInt(1000);
                }

                Teacher t = new Teacher(id, pass, name, mail, null);
                teacherDB.save(t);
                //System.out.println(); // Move to the next line for the next record
            }

            // Close the CSVReader
            csvReader.close();
            return true;
        } catch (IOException | CsvException e) {
            e.printStackTrace();
            return false;
        }
    }
        private boolean insert_course_csv(String filepath) {
            try {
                // Create a CSVReader object with the file path
                FileReader reader = new FileReader(filepath);
                CSVReader csvReader = new CSVReader(reader);

                // Read all the rows from the CSV file
                List<String[]> records = csvReader.readAll();

                // Loop through the records and process each row
                for (String[] record : records) {


                    String dept = record[0];
                    Long code = Long.parseLong(record[1]);
                    Long sem = Long.parseLong(record[2]);
                    Character c  = record[3].charAt(0);
                    Long student_count = Long.parseLong(record[4]);



                    Course course = new Course(null , dept, code , student_count , c , sem , null);
                    courseDB.save(course);

                    //System.out.println(); // Move to the next line for the next record
                }

                // Close the CSVReader
                csvReader.close();
                return true;
            } catch (IOException | CsvException e) {
                e.printStackTrace();
                return false;
            }

    }
}
