// attendance.js
hostaddr=localStorage.getItem('host')

var dataFromBackend = {};
var percentage_input = 85;

// Get the current URL
var url = new URL(window.location.href);
// Get the value of the 'hid' query parameter
var hid = url.searchParams.get('hid');
var globalcourse;

function getSessionTeacherId() {
    var teacherid = null;

    sessiondata = localStorage.getItem("mysession");
    hashdata = localStorage.getItem("myhash");
    // Make an AJAX GET request to fetch the teacher's ID from the session
    $.ajax({
        url: "https://"+hostaddr+"/session/get-session-data", // Replace with your backend API endpoint
        method: "GET",
        async: false, // Synchronous request to wait for the response
        headers: {
            'mysession': sessiondata,
            'Authorization': 'Basic ' + hashdata
        },
        success: function (data) {
            teacherid = data; // Store the teacher's ID
        },
        error: function () {
            console.error("Error fetching teacher ID");
        },
    });
    return teacherid;
}

var tid = getSessionTeacherId();
console.log(tid);
// Make an AJAX request to fetch the teacher's data
$.ajax({
    url: "https://"+hostaddr+"/teacher/sheets", // Replace with your backend API endpoint
    method: "GET",
    async: false, // Synchronous request to wait for the response
    headers: {
        'mysession': sessiondata,
        'Authorization': 'Basic ' + hashdata
    },
    data: {
        teacherId: tid
    },
    success: function (teacherCourses) {
        console.log(teacherCourses);
        const hidToMatch = hid;
        for (const course of teacherCourses) {
            if (course.hid == hidToMatch) {
                globalcourse = course.department + course.courseid + course.section;
                document.getElementById('courseCode').textContent = course.department +" "+ course.courseid +" "+course.section;
                document.getElementById('courseName').textContent = course.coursename;
                document.getElementById('count').textContent = course.count;
                document.getElementById('percentage').textContent = course.percentage;
                break;
            }
        }
    },
    error: function (error) {
        // Handle any errors here
        console.error("Error fetching teacher data:", error.responseText);
    },
});

document.getElementById("att_percentage").addEventListener("change",()=>{
    document.getElementById("studentList").innerHTML="";
    percentage_input = document.getElementById("att_percentage").value;

    if(percentage_input > 100 || percentage_input < 0) alert("Invalid Percentage");
    else fetchStudentIds(percentage_input);

});

function loadPrevAttendance() {
    const table = document.getElementById("attendance-table");
    const students = Object.keys(dataFromBackend);

    // Get the existing table row by its id
    const headerRow = document.getElementById("start-row");
    const headerCell = document.createElement("th");
    headerCell.textContent = "Student ID";
    headerRow.appendChild(headerCell);

    const headers = dataFromBackend["start"];

    // Populate the table header row with headers
    headers.forEach(headerText => {
        const headerCell = document.createElement("th");
        headerCell.textContent = headerText;
        headerRow.appendChild(headerCell);
    });

    const selectElement = document.getElementById("selectID");
    students.filter(student => student !== "start").forEach((student) => {
        const option = document.createElement("option");
        option.value = student;
        option.textContent = student;
        selectElement.appendChild(option);


        const row = table.insertRow();
        row.insertCell(0).textContent = student;

        const attendanceData = dataFromBackend[student];
        for (let i = 0; i < attendanceData.length ; i++) {
            const cell = row.insertCell(i + 1);
            cell.textContent = attendanceData[i];
            cell.style.fontWeight = "600";
            if(attendanceData[i]=="A"){
                cell.style.backgroundColor = "rgba(255, 0, 0, 0.224)";
            }
            else if(attendanceData[i]=="L"){
                cell.style.backgroundColor = "rgba(255, 238, 0, 0.384)";
            }
        }
    });
}



var studentIDs = [];

document.addEventListener("DOMContentLoaded", function () {
    $.ajax({
        url: "https://"+hostaddr+"/attendance/prev-attendance/"+hid, // Replace with your backend API endpoint
        method: "GET",
        async: false, // Synchronous request to wait for the response
        headers: {
            'mysession': sessiondata,
            'Authorization': 'Basic ' + hashdata
        },
        data: {
            teacherId: tid
        },
        success: function (response) {
            dataFromBackend=response;
            console.log(dataFromBackend);
            loadPrevAttendance();
        },
        error: function (error) {
            // Handle any errors here
            console.error("Error fetching teacher data:", error.responseText);
        },
    });

    document.getElementById("studentList").innerHTML="";
    fetchStudentIds(85);
});

function populateStudentIDs() {
    const studentList = document.getElementById("studentList");
    studentList.innerHTML = "";
    const EmailBtn = document.getElementById("emailBtn");

    // Store the original button styles only if they haven't been stored before
    if (!EmailBtn.originalStyles) {
        EmailBtn.originalStyles = {
            backgroundColor: EmailBtn.style.backgroundColor,
            cursor: EmailBtn.style.cursor,
            color: EmailBtn.style.color,
            transform: EmailBtn.style.transform,
            textContent: EmailBtn.textContent,
            disabled: EmailBtn.disabled
        };
    }

    // Check if the studentIDs array is empty
    if (studentIDs == null || studentIDs.length === 0) {
        // Change button styles when no students are present
        EmailBtn.style.backgroundColor = "#cecece8b";
        EmailBtn.style.cursor = "not-allowed";
        EmailBtn.style.color = "#0000008b";
        EmailBtn.style.transform = "scale(1.0)";
        EmailBtn.textContent = "No students";
        EmailBtn.disabled = true;
    } else {
        // Revert back to original button styles
        if (EmailBtn.originalStyles) {
            EmailBtn.style.backgroundColor = EmailBtn.originalStyles.backgroundColor;
            EmailBtn.style.cursor = EmailBtn.originalStyles.cursor;
            EmailBtn.style.color = EmailBtn.originalStyles.color;
            EmailBtn.style.transform = EmailBtn.originalStyles.transform;
            EmailBtn.textContent = EmailBtn.originalStyles.textContent;
            EmailBtn.disabled = EmailBtn.originalStyles.disabled;
        }

        for (let i = 0; i < studentIDs.length; i++) {
            // Create individual student ID elements
            const studentID = document.createElement("div");
            studentID.className = "student-id";
            studentID.textContent = studentIDs[i];

            studentList.appendChild(studentID);
        }
    }
}



function fetchStudentIds(percentage){
    sessiondata = localStorage.getItem("mysession");
    hashdata = localStorage.getItem("myhash");

    studentIDs=[];

    $.ajax({
        type: "GET",
        url: "https://"+hostaddr+"/attendance/get-defaulters/"+hid+"/"+percentage,
        headers: {
            'mysession': sessiondata,
            'Authorization': 'Basic ' + hashdata
        },
        success: function(response) {

            studentIDs = response;
            populateStudentIDs()
            //alert("got the defaulters.");
        },
        error: function(xhr, status, error) {
            console.error("Error:", error);
            alert("Error fetching defaulters");
        }
    });
}

function emailBtnfunc() {
    sessiondata = localStorage.getItem("mysession");
    hashdata = localStorage.getItem("myhash");
    $.ajax({
        type: "POST",
        url: "https://"+hostaddr+"/attendance/send-warning/"+hid,
        headers: {
            'mysession': sessiondata,
            'Authorization': 'Basic ' + hashdata,
            'Content-Type':'application/json'
        },

        data: JSON.stringify(studentIDs),
        success: function(response) {
            alert("Warning emails sent successfully.");
        },
        error: function(xhr, status, error) {
            console.error("Error:", error);
            alert("Error sending warning emails.");
        }
    });
}

function sheetSubmitBtn() {
    const table = document.getElementById("attendance-table");
    const rows = table.querySelectorAll("tr");
    let csvContent = "";

    // Iterate through table rows and cells to extract data
    rows.forEach(function (row) {
        const cols = row.querySelectorAll("td");
        let rowData = [];
        cols.forEach(function (col) {
            rowData.push(col.textContent);
        });
        if (rowData.length > 0) {
            csvContent += rowData.join(",") + "\n";
        }
    });

    // Create a Blob with the CSV data
    const blob = new Blob([csvContent], { type: "text/csv" });

    // Create a download link for the Blob
    const link = document.createElement("a");
    link.href = window.URL.createObjectURL(blob);
    const dlink = "attendance-data-" + globalcourse + ".csv";
    link.download = dlink;

    // Trigger a click event on the download link
    link.click();
}

document.getElementById("reportBtn").addEventListener("click", () => reportBtnfunc(percentage_input));
function reportBtnfunc(attPercentage) {
    console.log(attPercentage);
    const table = document.getElementById("attendance-table");
    const rows = table.querySelectorAll("tr");
    let csvContent = "";

    // Iterate through table rows and cells to extract data
    rows.forEach(function (row) {
        const cols = row.querySelectorAll("td");
        let rowData = [];

        // Skip header row
        if (cols.length > 0) {
            cols.forEach(function (col, index) {
                // Check if it's the attendance percentage column (index 2)
                if (index === 2) {
                    const percentage = parseFloat(col.textContent);
                    if (percentage < attPercentage) {
                        // Include Student ID, Student Name, and Attendance Percentage
                        rowData.push(row.cells[0].textContent);
                        rowData.push(row.cells[1].textContent);
                        rowData.push(percentage);
                    }
                }
            });

            // If rowData is not empty, add it to the CSV content
            if (rowData.length > 0) {
                csvContent += rowData.join(",") + "\n";
            }
        }
    });

    // Create a Blob with the CSV data
    const blob = new Blob([csvContent], { type: "text/csv" });

    // Create a download link for the Blob
    const link = document.createElement("a");
    link.href = window.URL.createObjectURL(blob);
    const dlink = "Below-attendance-" + globalcourse + ".csv";
    link.download = dlink;

    // Trigger a click event on the download link
    link.click();
}

function registerStudents(studentId , failed) {
    $.ajax({
        url: 'https://'+hostaddr+'/teacher/course-register'//samnun url
        ,
        method: 'POST',
        //dataType: 'application/x-www-form-urlencoded',
        headers: {
            'mysession': sessiondata,
            'Authorization': 'Basic ' + hashdata
        },
        data: {
            stud_id: studentId,
            hid: hid,
        },
        success: function(data) {

        },
        error: function(data) {
            alert("Couldnt register "+ JSON.stringify(data));
        }

    });
}

function csvSubmit() {
    const fileInput = document.createElement('input');
    fileInput.type = 'file';
    fileInput.accept = '.csv';

    // Trigger a click on the file input to open the file dialog
    fileInput.click();

    // Handle file selection
    fileInput.addEventListener('change', handleFileSelect);
}

function handleFileSelect(event) {
    const file = event.target.files[0];

    if (file) {
        // Read the CSV file
        const reader = new FileReader();

        reader.onload = function (e) {
            const csvData = e.target.result;

            // Process CSV data
            processData(csvData);
        };

        // Read the file as text
        reader.readAsText(file);
    }
}

function processData(csvData) {
    // Split CSV data into rows
    const rows = csvData.split('\n');
    var failed;
    // Loop through each entry in the CSV
    for (let i = 0; i < rows.length; i++) { // Assuming the first row is header
        const columns = rows[i].split(',');
        console.log(columns[0]);

        // Assuming the CSV columns are in the order: studentId, attendanceDate
        const studentId = columns[0].trim();
        //const attendanceDate = columns[1].trim();

        // Make the API call for each entry
        registerStudents(studentId , failed);
    }
}



$('#deleteBtn').on('click', function () {
    const selectedStudentID = $('#selectID').val();
    console.log(selectedStudentID);
    console.log(hid);
    var isConfirmed

    if(selectedStudentID == 0){
        isConfirmed = window.confirm(`Are you sure you want to delete All Students?` );
    }
    else
        isConfirmed = window.confirm(`Are you sure you want to delete ${selectedStudentID} ?` );

    if (isConfirmed) {
        $.ajax({
            type: 'DELETE',
            url: 'https://'+hostaddr+'/attendance/delete-student',
            headers: {
                'mysession': sessiondata,
                'Authorization': 'Basic ' + hashdata
            },
            data: {
                hid: hid,
                stud_id: selectedStudentID
            },
            success: function (response) {
                alert('Student(s) unenrolled successfully!');
                window.location.reload();
            },
            error: function (error) {
                console.error('Error:', error);
                alert('Failed to unenroll student(s)');
            }
        });
    }
});
