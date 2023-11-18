hostaddr=localStorage.getItem('host');

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
      //window.location.href = "login.html";
    },
  });
});

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

  // Function to get the Student's ID from the session
function getSessionStudentId() {
  var Studentid = null;
  
  sessiondata = localStorage.getItem("mysession");
  hashdata = localStorage.getItem("myhash");
  // Make an AJAX GET request to fetch the Student's ID from the session
  $.ajax({
    url: "http://"+hostaddr+"/session/get-session-data", // Replace with your backend API endpoint
    method: "GET",
    async: false, // Synchronous request to wait for the response
    headers: {
      'mysession': sessiondata,
      'Authorization': 'Basic ' + hashdata
    },
    success: function (data) {
      Studentid = data; // Store the Student's ID
    },
    error: function () {
      console.error("Error fetching Student ID");
    },
  });
  
  return Studentid;
}


// Function to fetch Student's data using AJAX
function fetchStudentData() {
  // Get the Student's ID from the session (you might need to update this part)
  var sid = getSessionStudentId(); // Implement this function to retrieve the Student ID from the session
  console.log(sid);
  // Make an AJAX request to fetch the Student's data
  sessiondata = localStorage.getItem("mysession");
  hashdata = localStorage.getItem("myhash");
  $.ajax({
    url: "http://"+hostaddr+"/student/info", // Replace with your backend API endpoint
    method: "GET",
    headers: {
      'mysession': sessiondata,
      'Authorization': 'Basic ' + hashdata
    },
    data: {
      studentid: sid
    },
    success: function (StudentData) {
      // Handle the successful response here
      console.log(StudentData);
      // Populate the Student's dashboard with the retrieved data
      populateStudentDashboard(StudentData);
    },
    error: function (error) {
      // Handle any errors here
      console.error("Error fetching Student data:", error.responseText);
    },
  });

  
const studentId = sid;

$.ajax({
    url: `http://`+hostaddr+`/student/get-student-data/${studentId}`,
    method: "GET",
    headers: {
      'mysession': sessiondata,
      'Authorization': 'Basic ' + hashdata
    },
    dataType: "json",
    success: function (data) {
        console.log("Student Data:", data);

        const phoneNumber = '0' + data.phoneNumber;
        const email = data.email;
        const address = data.address;

        $("#phone_student").val(phoneNumber);
        $("#email_student").val(email);
        $("#address_student").val(address);
    },
    error: function (error) {
        console.error("Error fetching student data:", error);
    }
});

}

// Function to populate the Student's dashboard with data
function populateStudentDashboard(StudentData) {
  $("#s_id").text(StudentData.id);
  $("#s_name").text(StudentData.name);
  $("#s_mail").text(StudentData.email);

  sessiondata = localStorage.getItem("mysession");
  hashdata = localStorage.getItem("myhash");
      const profilePicture = document.getElementById('profilePicture');
      profilePicture.src = "http://"+hostaddr+"/student/get-photo/" + StudentData.id;
}


$('#updateStudentForm').submit(function(event) {
  event.preventDefault(); // Prevent the default form submission
  var sid = getSessionStudentId(); // Implement this function to retrieve the Student ID from the session
  
  var phn = $('#phone_student').val();
        phn = phn.replace(/[^0-9+]/g, "");
        // Remove any prefix like "+88" or "+sth"
        if (phn.startsWith("+88")) {
            phn = phn.slice(3,14);
            console.log(phn);
          }
          // Limit the length to 11 digits
          phn = phn.slice(0, 11);
          console.log(phn);


  // Collect form data
  var formData = {
    phonenumber: phn,
    email: $('#email_student').val(),
    address: $('#address_student').val()
  };
  
  // Create a new FormData object
  var form = new FormData();
  
  // Append form fields to FormData
  for (var key in formData) {
    form.append(key, formData[key]);
  }

  // Append the image file
  form.append('file', $('#profile_image')[0].files[0]);
  console.log(formData);

  // Make an AJAX POST request
  $.ajax({
      url: 'http://'+hostaddr+'/student/update-data/'+sid, // Replace with your backend API endpoint
      type: 'POST',
      headers: {
        'mysession': sessiondata,
        'Authorization': 'Basic ' + hashdata
      },
      data: form,
      processData: false, // Prevent jQuery from processing the data
      contentType: false, // Prevent jQuery from setting the content type
      success: function(response) {
          // Handle the success response from the backen
          alert("Data updated successfully");
          console.log('Data sent successfully:', response);
          window.location.href = "student_dash.html";
      },
      error: function(error) {
          // Handle errors
          console.error('Error sending data:', error);
      }
  });
});



// Call the function to fetch Student's data when the page loads
$(document).ready(function () {
  fetchStudentData();
  
  sessiondata = localStorage.getItem("mysession");
  hashdata = localStorage.getItem("myhash");
  const apiUrl = "http://"+hostaddr+"/student/";
  
// Step 1: Fetch all course information
$.ajax({
  url: apiUrl + "courses_info",
  type: "GET",
  headers: {
    'mysession': sessiondata,
    'Authorization': 'Basic ' + hashdata
  },
  dataType: "json",
  success: function (allCourses) {
    
    studentId = getSessionStudentId(); // Replace with the actual student ID
    const enrolledCourses = [];
    const getAttendancePromises = [];

    // Step 2: Fetch the courses a student is enrolled in
    $.ajax({
      url: apiUrl + "courses/" + studentId,
      type: "GET",
      headers: {
        'mysession': sessiondata,
        'Authorization': 'Basic ' + hashdata
      },
      dataType: "json",
      success: function (studentCourses) {
        // Handle the response data here
        console.log("Student Courses Data:", studentCourses);

        // Compare and find matching courses
        for (const course of allCourses) {
          if (studentCourses.includes(course.hid)) {
            const promise = getAttendancePercentage(studentId, course.hid);
            getAttendancePromises.push(promise);

            enrolledCourses.push({
              hid: course.hid,
              department: course.department,
              courseid: course.courseid,
              courseName: course.courseName,
            });
          }
        }

        Promise.all(getAttendancePromises)
      .then(attendancePercentages => {
        // Extract labels and data from enrolledCourses
        const labels = enrolledCourses.map(course => `${course.department} ${course.courseid}`);
        const data = attendancePercentages.map(percentage => parseFloat(percentage));
        console.log("Attendance Percentages:", data);
        
        // Define your chart data
        var chartData = {
          labels: labels,
          datasets: [
            {
              label: "Attendance Perecentage",
              data: data,
              backgroundColor: "rgba(75, 192, 192, 0.2)", // Fill color
              borderColor: "rgba(75, 192, 192, 1)", // Border color
              borderWidth: 2,
            },
          ],
        };

        var ctx = document.getElementById("myChart").getContext("2d");

        // Create the chart
        var myChart = new Chart(ctx, {
          type: 'line',
          data: chartData,
          options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
              y: {
                stepSize: 10, // Set the y-axis step size
                suggestedMax: 100, // Set the maximum value for the y-axis
                beginAtZero: true, // Do not start the y-axis at zero
              },
            },
          },
        });
        displayCourses(enrolledCourses);
      })
      .catch(error => {
        console.error("Error fetching attendance percentages:", error);
      });
      },
    });
  },
  error: function (error) {
    console.error("Error fetching all courses:", error);
  },
});

});

// Function to display courses in the list
function displayCourses(courses) {
  const courseList = document.getElementById('courseList');
  courseList.innerHTML = '';
  if (courses.length > 0) {
    let counter = 1; // Initialize counter
    courses.forEach(course => {
      const listItem = document.createElement('li');
      listItem.className = 'course-item';

      const courseInfo = document.createElement('div');
      courseInfo.className = 'course-info';
      courseInfo.textContent = `${counter} .   ${course.department}  ${course.courseid}    -    ${course.courseName}`;
      listItem.appendChild(courseInfo);
      courseList.appendChild(listItem);
      counter++;
    });
  } else {
    const message = document.createElement('p');
    message.textContent = 'No courses found for the student.';
    courseList.appendChild(message);
  }
}





function getAttendancePercentage(studentId, hid) {
  const sessiondata = localStorage.getItem("mysession");
  const hashdata = localStorage.getItem("myhash");

  return new Promise((resolve, reject) => {
    $.ajax({
      url: `http://`+hostaddr+`/student/get-percentage/${hid}/${studentId}`,
      type: "GET",
      headers: {
        mysession: sessiondata,
        Authorization: 'Basic ' + hashdata,
      },
      dataType: "json",
      success: function (data) {
        if (data < 0) {
          data = 0;
        }
        resolve((data * 100).toFixed(2)); // Round to two decimal places
      }
      ,
      error: function (error) {
        reject(error);
      },
    });
  });
}

// Function to handle code enrollment
function codeEnroll(sheetCode, studentId) {
  $.ajax({
    url: `http://${hostaddr}/student/code-enroll`,
    method: "POST",
    headers: {
      'mysession': sessiondata,
      'Authorization': 'Basic ' + hashdata
    },
    data: {
      sheetcode: sheetCode,
      stud_id: studentId
    },
    success: function (response) {
      alert('Enrollment successful!');
      window.location.reload();
    },
    error: function (error) {
      console.error('Error:', error);
      alert('Failed to enroll. Please try again.');
    }
  });
}

// Event listener for the enrollment form
$('#enrollmentSubmit').on('click', function () {
  const sheetCode = $('#enrollmentInput').val();
  codeEnroll(sheetCode, studentId);
});

