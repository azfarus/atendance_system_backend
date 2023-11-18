hostaddr=localStorage.getItem('host');

$(document).ready(function () {

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

  // Get session data and hash from local storage
  sessiondata = localStorage.getItem("mysession");
  hashdata = localStorage.getItem("myhash");

  // Function to check if the user is authenticated
  function checkAuthentication() {
    $.ajax({
      url: "http://"+hostaddr+"/session/get-session-data",
      method: "GET",
      headers: {
        'mysession': sessiondata,
        'Authorization': 'Basic ' + hashdata
      },
      success: function () {
        // User is authenticated
        fetchTeacherData();
      },
      error: function () {
        // Handle authentication error by redirecting to the login page
        window.location.href = "login.html";
      },
    });
  }

  // Function to fetch the teacher's ID from the session
  function getSessionTeacherId() {
    let teacherId = null;

    $.ajax({
      url: "http://"+hostaddr+"/session/get-session-data",
      method: "GET",
      async: false,
      headers: {
        'mysession': sessiondata,
        'Authorization': 'Basic ' + hashdata
      },
      success: function (data) {
        teacherId = data;
      },
      error: function () {
        console.error("Error fetching teacher ID");
      },
    });

    return teacherId;
  }

  // Function to fetch teacher data and populate the dashboard
  function fetchTeacherData() {
    const teacherId = getSessionTeacherId();
    const profilePicture = document.getElementById("profilePicture");
    profilePicture.src = "http://"+hostaddr+"/teacher/get-photo/" + teacherId;

    $.ajax({
      url: "http://"+hostaddr+"/teacher/info",
      method: "GET",
      headers: {
        'mysession': sessiondata,
        'Authorization': 'Basic ' + hashdata
      },
      data: {
        teacherid: teacherId
      },
      success: function (teacherData) {
        populateTeacherDashboard(teacherData);
      },
      error: function (error) {
        console.error("Error fetching teacher data:", error.responseText);
      },
    });

    $.ajax({
      url: "http://"+hostaddr+"/teacher/sheets",
      method: "GET",
      headers: {
        'mysession': sessiondata,
        'Authorization': 'Basic ' + hashdata
      },
      data: {
        teacherId: teacherId
      },
      success: function (teacherCourses) {
        renderCourseBlocks(teacherCourses);
      },
      error: function (error) {
        console.error("Error fetching teacher data:", error.responseText);
      },
    });
  }

  // Function to populate the teacher's dashboard
  function populateTeacherDashboard(teacherData) {
    $("#t_id").text(teacherData.id);
    $("#t_name").text(teacherData.name);
    $("#t_mail").text(teacherData.email);
  }

  // Function to render course blocks
  function renderCourseBlocks(courses) {
    const courseBlocks = document.querySelector(".course-blocks");

    courses.forEach(course => {
      const courseBlock = createCourseBlock(course);
      courseBlocks.appendChild(courseBlock);
    });
  }

  // Function to create a course block
function createCourseBlock(course) {
  const courseBlock = document.createElement("div");
  courseBlock.className = "course";
  courseBlock.style.position = "relative"; // Set the position to relative for absolute positioning of the icon

  const courseCode = document.createElement("div");
  courseCode.className = "course-code";
  courseCode.textContent = `${course.department} ${course.courseid} - ${course.section}`;

  const courseName = document.createElement("div");
  courseName.className = "course-name";
  courseName.textContent = `${course.coursename}`;
  const classCode = document.createElement("div");
  classCode.className = "classCode";
  classCode.textContent = `${course.code}`;
  classCode.style.color = "#fff3cf";
  classCode.style.fontSize = "small";

  const viewSheetButton = createButton("View Sheet", course.hid, redirectToSheet);
  const takeAttendanceButton = createButton("Take Attendance", course.hid, redirectToAttendance);
  const autoplay = document.createElement("a");
  autoplay.href = "autoAtt.html?hid=" + course.hid;

  const image = document.createElement("img");
  image.src = "../images/play.webp";
  image.className = "autoplay";
  autoplay.appendChild(image);

  // Create the delete icon
  const deleteIcon = document.createElement("img");
  deleteIcon.src = "../images/delicon.png";
  deleteIcon.className = "icon";

  deleteIcon.setAttribute("hid", course.hid);   //const customValue = deleteIcon.getAttribute("hid");
  deleteIcon.style.position = "absolute";
  deleteIcon.style.top = "7px";
  deleteIcon.style.right = "7px";
  deleteIcon.id = course.hid;

  deleteIcon.addEventListener("click", function() {
    const isConfirmed = window.confirm(`Unenroll from course ${course.department} ${course.courseid} - ${course.section} ?`);
    if (isConfirmed) {
      deleteCourse(course.hid);
    }
  });

  courseBlock.appendChild(courseCode);
  courseBlock.appendChild(courseName);
  courseBlock.appendChild(classCode);
  courseBlock.appendChild(viewSheetButton);
  courseBlock.appendChild(takeAttendanceButton);
  courseBlock.appendChild(autoplay);
  courseBlock.appendChild(deleteIcon);

  return courseBlock;
}

function deleteCourse(hid) {
  console.log("Deleting course with hid:", hid);
  console.log("teacher id:", getSessionTeacherId());
  $.ajax({
    url: "http://" + hostaddr + "/attendance/unenroll",
    method: "DELETE",
    data: {
      tid: getSessionTeacherId(),
      hid: hid,
    },
    headers: {
      'mysession': sessiondata,
      'Authorization': 'Basic ' + hashdata
    },
    success: function (response) {
      alert("Course unenrolled successfully");
      console.log("Course unenrolled successfully ", response);
    },
    error: function (error) {
      console.error("Error unenrolling course:", error.responseText);
    },
  });
}


  // Function to create a button element
  function createButton(label, value, clickHandler) {
    const button = document.createElement("button");
    button.textContent = label;
    button.style.fontWeight = "500";
    button.value = value;
    button.addEventListener("click", clickHandler);
    return button;
  }

  // Event listener to handle profile picture upload
  const profilePictureInput = document.getElementById("profilePictureInput");
  profilePicture.addEventListener("click", () => profilePictureInput.click());

  profilePictureInput.addEventListener("change", handleProfilePictureUpload);

  function handleProfilePictureUpload() {
    const selectedFile = profilePictureInput.files[0];

    if (selectedFile) {
      const formData = new FormData();
      formData.append("file", selectedFile);
      const uploadUrl = `http://${hostaddr}/teacher/upload-photo/${getSessionTeacherId()}`;

      $.ajax({
        url: uploadUrl,
        type: "POST",
        headers: {
          'mysession': sessiondata,
          'Authorization': 'Basic ' + hashdata
        },
        data: formData,
        processData: false,
        contentType: false,
        success: function (data) {
          if (data && data > 0) {
            alert("Photo uploaded successfully.");
          } else {
            alert("Failed to upload photo.");
          }
        },
        error: function (error) {
          console.error("Error uploading photo:", error);
        },
      });

      const reader = new FileReader();
      reader.onload = function (e) {
        profilePicture.src = e.target.result;
      };
      reader.readAsDataURL(selectedFile);
    }
  }

  function redirectToSheet(event) {
    const hid = event.target.value;
    window.location.href = "AttendanceSheet.html?hid=" + hid;
  }

  function redirectToAttendance(event) {
    const hid = event.target.value;
    window.location.href = "TakeAttendance.html?hid=" + hid;
  }

  // Check user authentication on page load
  checkAuthentication();


  $("#assignTeacherForm").submit(function (event) {
    event.preventDefault(); // Prevent the default form submission

    // Get the form values
    var id_t = getSessionTeacherId();
    var dept_t = $("#dept_teacher").val();
    var course_t = $("#courses_teacher").val();
    var section_t = $("#section_teacher").val();
    
    
    // Create a data object with the parameters
    var data = {
      
      hid: course_t,
      teacherid :id_t,
      
    };

    console.log(data);
    sessiondata = localStorage.getItem("mysession");
    hashdata = localStorage.getItem("myhash");
    // Make the AJAX request to add teacher
    $.ajax({
      url: "http://"+hostaddr+"/teacher/course-teacher-assign", // Replace with your backend API endpoint
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
  populateDepartmentDropdown();
  // Add an event listener to the department dropdown
  $("#dept_teacher").change(function () {
    var selectedDept = $(this).val();
    // Call the function to populate courses based on the selected department
    populateCoursesDropdown(selectedDept);
  });
});

function populateDepartmentDropdown() {

  const sessiondata = localStorage.getItem("mysession");
  const hashdata = localStorage.getItem("myhash");

  $.ajax({
    url: "http://"+hostaddr+"/teacher/departments",
    method: "GET",
    headers: {
      'mysession': sessiondata,
      'Authorization': 'Basic ' + hashdata
    },
    success: function (data) {
      // Populate the <select> with dynamic options
      var selectElement = $("#dept_teacher");
      // Iterate through the fetched data and create <option> elements
      for (var i = 0; i < data.length; i++) {
        var department = data[i];
        selectElement.append($('<option>', {
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

function populateCoursesDropdown(dept) {

  const sessiondata = localStorage.getItem("mysession");
  const hashdata = localStorage.getItem("myhash");

  $.ajax({
    url: "http://"+hostaddr+"/course/get-course-by-dept",
    method: "GET",
    headers: {
      'mysession': sessiondata,
      'Authorization': 'Basic ' + hashdata
    },
    data: {
      department: dept
    },
    headers: {
      'mysession': sessiondata,
      'Authorization': 'Basic ' + hashdata
    },
    success: function (data) {
      console.log("this is get course by dept data" + data[0].name);
      var selectElement = $("#courses_teacher");

      // Clear previous options
      selectElement.empty();

      // Iterate through the fetched data and create <option> elements
      for (var i = 0; i < data.length; i++) {
        var course = data[i];
        selectElement.append($('<option>', {
          value: course.hid,
          text: course.name
        }));
      }
    },
    error: function (error) {
      console.error("Error fetching courses:", error);
    }
  });
}
