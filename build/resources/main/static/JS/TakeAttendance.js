// AttendanceSheet.js

hostaddr=localStorage.getItem('host');


sessiondata = localStorage.getItem("mysession");
hashdata = localStorage.getItem("myhash");

// Get the current URL
var url = new URL(window.location.href);
// Get the value of the 'hid' query parameter
var hid = url.searchParams.get('hid');
console.log(hid);

let dataFromBackend = {}; // Initialize as an empty object

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

        const currentDate = new Date();
        const options = { timeZone: 'Asia/Dhaka', year: 'numeric', month: '2-digit', day: '2-digit' };

        console.log(teacherCourses);
        const hidToMatch = hid;
        for (const course of teacherCourses) {
            if (course.hid == hidToMatch) {
                document.getElementById('courseCode').textContent = course.department + " " + course.courseid + " " + course.section;
                document.getElementById('courseName').textContent = course.coursename;

                // Format the date with the correct time zone
                const formattedDateParts = currentDate.toLocaleDateString('en-US', options).split('/');
                const formattedDate = `${formattedDateParts[2]}-${formattedDateParts[0].padStart(2, '0')}-${formattedDateParts[1].padStart(2, '0')}`;
                document.getElementById("attDate").value = formattedDate;
                break;
            }
        }




    },
    error: function (error) {
        // Handle any errors here
        console.error("Error fetching teacher data:", error.responseText);
    },
});


const attendanceData = {};


refreshAttendanceList();

document.getElementById("attDate").addEventListener("change" , refreshAttendanceList);


function refreshAttendanceList() {
    const attendanceDate = document.getElementById("attDate").value;
    $.ajax({
        url: 'https://' + hostaddr + '/attendance/get-students',
        method: 'GET',
        dataType: 'json',
        headers: {
            'mysession': sessiondata,
            'Authorization': 'Basic ' + hashdata
        },
        data: {
            hid: hid,
            attendanceDate: attendanceDate
        },
        success: function (data) {
            console.log(data);
            dataFromBackend = data; // Populate dataFromBackend with the fetched data
            console.log(data);
            displayAttendanceData(); // Call a function to display data after it's fetched

            // After the first AJAX call is completed, simulate the clicks on toggleImagesButton
            const toggleImagesButton = document.getElementById('toggleImages');
            toggleImagesButton.dispatchEvent(new Event('click')); // Simulate a click on the toggleImagesButton
            toggleImagesButton.dispatchEvent(new Event('click')); // Simulate a second click
        },
        error: function (error) {
            // Handle any errors
            console.error('Error fetching data:', error);
        }
    });
}


function displayAttendanceData() {
    const table = document.getElementById("attendance-table");
    var rowcount = table.rows.length;

    for (var i = rowcount - 1; i > 0; i--) {
        table.deleteRow(i);
    }

    students = Object.keys(dataFromBackend);

    // Initialize an object to store attendance data

    students.forEach((studentId) => {
        const row = table.insertRow();
        row.setAttribute("data-student-id", studentId);

        // Display Student ID in the first column
        row.insertCell(0).textContent = studentId;

        const studentName = dataFromBackend[studentId].name;
        const atd_status=dataFromBackend[studentId].status

        // Display Student Name in the second column
        row.insertCell(1).textContent = studentName;

        const imgCell = row.insertCell(2);
        const img = document.createElement("img");
        img.src = "https://"+hostaddr+"/student/get-photo/" + studentId;
        img.classList.add("student-image");
        imgCell.appendChild(img);

        // Create the third column with a single button for P, A, L
        const attendanceCell = row.insertCell(3);
        const button = document.createElement("button");
        button.textContent = atd_status; // Set the initial value to "P"
        button.classList.add(atd_status);
        button.value = studentId;
        button.addEventListener("click", toggleAttendance);
        attendanceCell.appendChild(button);

        // Initialize the attendance data object with "P" for each student
        attendanceData[studentId] = atd_status;
    });

}

// refreshes table after toggleImagesButton is clicked
function refreshTable() {
    const table = document.getElementById("attendance-table");

    students.forEach((studentId) => {
        // Check if the row for the student already exists
        let row = table.querySelector(`tr[data-student-id="${studentId}"]`);

        // If the row doesn't exist, create a new one
        if (!row) {
            row = table.insertRow();
            row.setAttribute("data-student-id", studentId);
        } else {
            // Clear the existing content of the row
            row.innerHTML = "";
        }

        // Display Student ID in the first column
        row.insertCell(0).textContent = studentId;

        const studentName = dataFromBackend[studentId].name;

        // Display Student Name in the second column
        row.insertCell(1).textContent = studentName;

        const imgCell = row.insertCell(2);
        const img = document.createElement("img");
        img.src = "https://" + hostaddr + "/student/get-photo/" + studentId;
        img.classList.add("student-image");
        imgCell.appendChild(img);

        // Create the third column with a single button for P, A, L
        const attendanceCell = row.insertCell(3);
        const button = document.createElement("button");
        button.textContent = attendanceData[studentId]; // Set the initial value to "P"
        button.classList.add(attendanceData[studentId]);
        button.value = studentId;
        button.addEventListener("click", toggleAttendance);
        attendanceCell.appendChild(button);
    });
}




function toggleAttendance(event) {
    const button = event.target;
    const attendanceStates = ["P", "A", "L"];
    let currentIndex = attendanceStates.indexOf(button.textContent);
    button.classList.remove(attendanceStates[currentIndex]);
    currentIndex = (currentIndex + 1) % attendanceStates.length;
    button.textContent = attendanceStates[currentIndex];
    button.classList.add(attendanceStates[currentIndex]);
    // Update the attendance data object with the new status
    const studentId = button.value;
    attendanceData[studentId] = attendanceStates[currentIndex];
}

// Function to submit attendance data
function submitAttendanceData() {
    const attendanceDate = document.getElementById("attDate").value;
    console.log(attendanceDate);

    $.ajax({
        url: 'https://' + hostaddr + '/attendance/submit-attendance/' + hid + '?attendanceDate=' + attendanceDate,
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(attendanceData),
        headers: {
            'mysession': sessiondata,
            'Authorization': 'Basic ' + hashdata
        },
        success: function (response) {
            console.log(attendanceData);
            alert('Attendance Taken Successfully:', response);
            window.location.href = "teacher_dash.html";
        },
        error: function (error) {
            console.error('Error submitting attendance data:', error);
        }
    });
}

// Attach a click event handler to a submit button
document.getElementById("sheetSubmitBtn").addEventListener("click", submitAttendanceData);


const toggleImagesButton = document.getElementById('toggleImages');
let cardsVisible = false;
let tableVisible = true; // Default to showing the table

toggleImagesButton.addEventListener('click', function () {
    console.log('Toggle images button clicked');

    const container = document.getElementById("attendance-container");
    const table = document.getElementById("table-container");

    if (tableVisible) {
        // Hide the table and show the cards
        table.style.display = "none";
        container.innerHTML = ""; // Remove existing cards

        students.forEach((studentId) => {
            const card = document.createElement("div");
            card.classList.add("student-card");

            // Create an image element
            const img = document.createElement("img");
            img.src = "https://" + hostaddr + "/student/get-photo/" + studentId;
            img.classList.add("student-image");
            card.appendChild(img);

            // Create a div for student ID
            const idDiv = document.createElement("div");
            const fullName = dataFromBackend[studentId].name;
            const nameParts = fullName.split(' ');
            const firstName = nameParts[0];

            idDiv.innerHTML = studentId + '<br>' + firstName;

            idDiv.classList.add("student-id");
            card.appendChild(idDiv);

            // Create a button for attendance
            const button = document.createElement("button");
            button.textContent = attendanceData[studentId];
            button.classList.add("attendance-button", attendanceData[studentId]);
            button.value = studentId;
            button.addEventListener("click", toggleAttendance);
            card.appendChild(button);

            // Append the card to the container
            container.appendChild(card);
        });

        cardsVisible = true;
        tableVisible = false;
    } else {
        // Hide the cards and show the table
        table.style.display = "block";
        refreshTable(); // Refresh the table content
        container.innerHTML = ""; // Remove existing cards

        cardsVisible = false;
        tableVisible = true;
    }
});

