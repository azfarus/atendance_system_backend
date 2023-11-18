hostaddr=localStorage.getItem('host')

//get session


$(document).ready(function () {
  sessiondata = localStorage.getItem("mysession");
  hashdata = localStorage.getItem("myhash");
  $.ajax({
    url: "http://"+hostaddr+"/session/get-session-data",
    method: "GET",
    headers: {
      'mysession': sessiondata,
      'Authorization': 'Basic ' + hashdata
    },
    success: function (response) {

    },
    error: function (error) {
      // Handle any errors here
      window.location.href = "login.html";
    },
  });
});

//frontend js
const sidebarItems = document.querySelectorAll(".sidebar li");
const sections = document.querySelectorAll(".section");

sidebarItems.forEach((item) => {
  item.addEventListener("click", () => {
    // Remove active class from all sidebar items
    sidebarItems.forEach((item) => item.classList.remove("active"));
    // Add active class to clicked item
    item.classList.add("active");
    const target = item.getAttribute("data-target");
    // Hide all sections
    sections.forEach((section) => section.classList.remove("active"));
    // Show target section
    document.querySelector(target).classList.add("active");
  });
});

// When the form is submitted
$("#insertTeacherForm").submit(function (event) {
  event.preventDefault(); // Prevent the default form submission
  
  // Get the form values
  var name_t = $("#name_teacher").val();
  var id_t = $("#id_teacher").val();
  var password_t = $("#password_teacher").val();
  var email_t = $("#mail_teacher").val();
  
  // Create a data object with the parameters
  var data = {
    name: name_t,
    id: id_t,
    password: password_t,
    email: email_t,
  };
  
  sessiondata = localStorage.getItem("mysession");
  hashdata = localStorage.getItem("myhash");
  
  // Make the AJAX request to add teacher
  $.ajax({
    url: "http://"+hostaddr+"/admin/teacher", // Replace with your backend API endpoint
    method: "POST",
    data: jQuery.param(data),
    contentType: "application/x-www-form-urlencoded; charset=UTF-8",
    headers: {
      'mysession': sessiondata,
      'Authorization': 'Basic ' + hashdata
    },
    success: function (response) {
      // Handle the successful response here
      alert("Teacher added successfully");
      console.log("Teacher added successfully:", response);
    },
    error: function (error) {
      // Handle any errors here
      console.error("Error adding teacher:", error.responseText);
    },
  });
});



// When the form is submitted
$("#assignTeacherForm").submit(function (event) {
  event.preventDefault(); // Prevent the default form submission
  
  // Get the form values
  var id_t = $("#teacherIdInput").val();
  var dept_t = $("#dept_teacher").val();
  var course_t = $("#courses_teacher").val();
  var section_t = $("#section_teacher").val();
  
  
  // Create a data object with the parameters
  var data = {
    department: dept_t,
    courseCode: course_t,
    teacherid :id_t,
    section : section_t
  };
  
  sessiondata = localStorage.getItem("mysession");
  hashdata = localStorage.getItem("myhash");
  
  // Make the AJAX request to add teacher
  $.ajax({
    url: "http://"+hostaddr+"/admin/course-teacher-assign", // Replace with your backend API endpoint
    method: "POST",
    data: jQuery.param(data),
    contentType: "application/x-www-form-urlencoded; charset=UTF-8",
    headers: {
      'mysession': sessiondata,
      'Authorization': 'Basic ' + hashdata
    },
    success: function (response) {
      // Handle the successful response here
      alert("Course assigned successfully");
      console.log("Course assigned successfully ", response);
    },
    error: function (error) {
      // Handle any errors here
      console.error("Error registering course:", error.responseText);
    },
  });
});

sessiondata = localStorage.getItem("mysession");
hashdata = localStorage.getItem("myhash");

//teacher csv upload
$('#TeacherCSVForm').submit(function (event) {
  event.preventDefault();
  const formData = new FormData();
  const fileInput = document.getElementById('csv_file_teacher');
  formData.append('file', fileInput.files[0]);
  formData.append('type', 2);
  $.ajax({
      type: 'POST',
      url: 'http://'+hostaddr+'/admin/upload-csv', // Replace with your backend API endpoint
      data: formData,
      headers: {
        'mysession': sessiondata,
        'Authorization': 'Basic ' + hashdata
      },
      processData: false, // Don't process the data
      contentType: false, // Don't set content type (it will be set automatically)
      success: function (response) {
          // Handle success response from the backend
          console.log('File uploaded successfully:', response);
          alert('File uploaded successfully.');
      },
      error: function (error) {
          // Handle error response from the backend
          console.error('Error uploading file:', error);
          alert('Error uploading file. Please try again.');
      }
  });
});

// When the form is submitted
$("#insertCourseForm").submit(function (event) {
  event.preventDefault(); // Prevent the default form submission
  
  // Get the form values
  var department = $("#dept_course").val();
  var code = $("#code_course").val();
  var course_name = $("#course_name").val();
  var semester = $("#semester_course").val();
  var section = $("#section_course").val();
  var studentCountLimit = $("#student_count_limit").val();

  // Create a data object with the parameters
  var data = {
    department: department,
    courseId: code,
    semester: semester,
    count: studentCountLimit,
    section: section,
    courseName: course_name
  };
  
  sessiondata = localStorage.getItem("mysession");
  hashdata = localStorage.getItem("myhash");
  
  // Make the AJAX request to add course
  $.ajax({
    url: "http://"+hostaddr+"/admin/course",
    method: "POST",
    data: jQuery.param(data),
    contentType: "application/x-www-form-urlencoded; charset=UTF-8",
    headers: {
      'mysession': sessiondata,
      'Authorization': 'Basic ' + hashdata
    },
    success: function (response) {
      // Handle the successful response here
      alert("Course added successfully");
      console.log("Course added successfully:", response);
    },
    error: function (error) {
      // Handle any errors here
      console.error("Error adding course:", error.responseText);
    },
  });
});


sessiondata = localStorage.getItem("mysession");
hashdata = localStorage.getItem("myhash");

//Course csv upload
$('#CourseCSVForm').submit(function (event) {
  event.preventDefault();
  const formData = new FormData();
  const fileInput = document.getElementById('csv_file_course');
  formData.append('file', fileInput.files[0]);
  formData.append('type', 3);
  $.ajax({
      type: 'POST',
      url: 'http://'+hostaddr+'/admin/upload-csv', // Replace with your backend API endpoint
      data: formData,
      headers: {
        'mysession': sessiondata,
        'Authorization': 'Basic ' + hashdata
      },
      processData: false, // Don't process the data
      contentType: false, // Don't set content type (it will be set automatically)
      success: function (response) {
          // Handle success response from the backend
          console.log('File uploaded successfully:', response);
          alert('File uploaded successfully.');
      },
      error: function (error) {
          // Handle error response from the backend
          console.error('Error uploading file:', error);
          alert('Error uploading file. Please try again.');
      }
  });
});

// When the form is submitted
$("#insertStudentForm").submit(function (event) {
  event.preventDefault(); // Prevent the default form submission
  
  // Get the form values
  var name = $("#name_student").val();
  var id = $("#id_student").val();
  var email = $("#mail_student").val();
  var guardianEmail = $("#mail_guardian").val();
  var password = $("#password_student").val();
  var department = $("#dept_student").val();
  var semester = $("#semester_student").val();

  // Create a data object with the parameters
  var data = {
    name: name,
    id: id,
    password: password,
    email: email,
    guardianEmail: guardianEmail,
    department: department,
    semester: semester,
  };
  
  sessiondata = localStorage.getItem("mysession");
  hashdata = localStorage.getItem("myhash");
  // Make the AJAX request to add student
  $.ajax({
    url: "http://"+hostaddr+"/admin/student", // Replace with your backend API endpoint
    method: "POST",
    data: jQuery.param(data),
    contentType: "application/x-www-form-urlencoded; charset=UTF-8",
    headers: {
      'mysession': sessiondata,
      'Authorization': 'Basic ' + hashdata
    },
    success: function (response) {
      // Handle the successful response here
      alert("Student added successfully");
      console.log("Student added successfully:", response);
    },
    error: function (error) {
      // Handle any errors here
      console.error("Error adding student:", error.responseText);
    },
  });
});


sessiondata = localStorage.getItem("mysession");
hashdata = localStorage.getItem("myhash");

//student csv upload
$('#StudentCSVForm').submit(function (event) {
    event.preventDefault();
    const formData = new FormData();
    const fileInput = document.getElementById('csv_file_student');
    formData.append('file', fileInput.files[0]);
    formData.append('type', 1);
    $.ajax({
        type: 'POST',
        url: 'http://'+hostaddr+'/admin/upload-csv', // Replace with your backend API endpoint
        data: formData,
        headers: {
          'mysession': sessiondata,
          'Authorization': 'Basic ' + hashdata
        },
        processData: false, // Don't process the data
        contentType: false, // Don't set content type (it will be set automatically)
        success: function (response) {
            // Handle success response from the backend
            console.log('File uploaded successfully:', response);
            alert('File uploaded successfully.');
        },
        error: function (error) {
            // Handle error response from the backend
            console.error('Error uploading file:', error);
            alert('Error uploading file. Please try again.');
        }
    });
});

sessiondata = localStorage.getItem("mysession");
hashdata = localStorage.getItem("myhash");

// Function to populate the department dropdown
function populateDepartmentDropdown() {
  $.ajax({
    url: "http://"+hostaddr+"/admin/departments",
    method: "GET",
    headers: {
      'mysession': sessiondata,
      'Authorization': 'Basic ' + hashdata
    },
    success: function (data) {
      // Populate the <select> with dynamic options
      var selectElement = $("#dept_course");
      var selectElement2 = $("#dept_student");
      
      
      // Iterate through the fetched data and create <option> elements
      for (var i = 0; i < data.length; i++) {
        var department = data[i];
        selectElement.append($('<option>', {
          value: department,
          text: department
        }));
        selectElement2.append($('<option>', {
          value: department,
          text: department
        }));
      }
    },
    error: function (error) {
      // Handle errors
      console.error("Error fetching departments:", error);
    }
  });
}

// Populate the department dropdown initially
populateDepartmentDropdown();