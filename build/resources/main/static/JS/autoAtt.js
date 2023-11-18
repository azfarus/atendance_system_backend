
hostaddr=localStorage.getItem('host');


sessiondata = localStorage.getItem("mysession");
hashdata = localStorage.getItem("myhash");
const stdIDs = [];
const attendanceData = {}; // Initialize an object to store attendance data
var attendanceDate ;
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
        const months = ['JAN', 'FEB', 'MAR', 'APR', 'MAY', 'JUN', 'JUL', 'AUG', 'SEP', 'OCT', 'NOV', 'DEC'];
        const formattedDate = currentDate.getDate() + ' ' + months[currentDate.getMonth()] + ' ' + currentDate.getFullYear();

        console.log(teacherCourses);
        const hidToMatch = hid;
        for (const course of teacherCourses) {
            if (course.hid == hidToMatch) {
                document.getElementById('courseCode').textContent = course.department +" "+ course.courseid +" "+course.section;
                document.getElementById('courseName').textContent = course.coursename;
                document.getElementById("courseDate").textContent = formattedDate;
                break;
            }
        }
    },
    error: function (error) {
        // Handle any errors here
        console.error("Error fetching teacher data:", error.responseText);
    },
});

const currentDate = new Date();
const options = { timeZone: 'Asia/Dhaka', year: 'numeric', month: '2-digit', day: '2-digit' };
const formattedDateParts = currentDate.toLocaleDateString('en-US', options).split('/');
const formattedDate = `${formattedDateParts[2]}-${formattedDateParts[0].padStart(2, '0')}-${formattedDateParts[1].padStart(2, '0')}`;

$.ajax({
    url: 'https://'+hostaddr+'/attendance/get-students',
    method: 'GET',
    dataType: 'json',
    headers: {
        'mysession': sessiondata,
        'Authorization': 'Basic ' + hashdata
    },
    data: {
        hid: hid,
        attendanceDate: formattedDate,
    },
    success: function (data) {
        console.log(data);
        dataFromBackend = data; // Populate dataFromBackend with the fetched data
        displayAttendanceData(); // Call a function to display data after it's fetched
    },
    error: function (error) {
        // Handle any errors
        console.error('Error fetching data:', error);
    }
});

function displayAttendanceData() {
    const table = document.getElementById("attendance-table");

    students = Object.keys(dataFromBackend);


    students.forEach((studentId) => {

        stdIDs.push(studentId);
        const row = table.insertRow();

        // Display Student ID in the first column
        row.insertCell(0).textContent = studentId;

        const studentName = dataFromBackend[studentId].name;

        // Display Student Name in the second column
        row.insertCell(1).textContent = studentName;

        // Create the third column with a single button for P, A, L
        const attendanceCell = row.insertCell(2);
        const div = document.createElement("div");
        div.textContent = "A"; // Set the initial value to "A"
        div.classList.add("A");
        div.value = studentId;

        attendanceCell.appendChild(div);

        // Initialize the attendance data object with "A" for each student
        attendanceData[studentId] = "A";
    });

    // Function to submit attendance data
    function submitAttendanceData() {

        const currentDate = new Date();
        const options = { timeZone: 'Asia/Dhaka', year: 'numeric', month: '2-digit', day: '2-digit' };
        const formattedDateParts = currentDate.toLocaleDateString('en-US', options).split('/');
        const formattedDate = `${formattedDateParts[2]}-${formattedDateParts[0].padStart(2, '0')}-${formattedDateParts[1].padStart(2, '0')}`;
        console.log(formattedDate);

        $.ajax({
            url: 'https://'+hostaddr+'/attendance/submit-attendance/'+hid + '?attendanceDate=' + formattedDate, // Replace with your API endpoint
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
}

const audioContext = new (window.AudioContext || window.webkitAudioContext)();
const beepFrequency = 500; // Frequency of the beep sound in hertz
const beepDuration = 100; // Duration of the beep sound in milliseconds
const beepVolume = 1; // Maximum volume (1.0 is maximum, 0.0 is muted)

function playBeepSound() {
    const oscillator = audioContext.createOscillator();
    const gainNode = audioContext.createGain();

    oscillator.type = 'sine';
    oscillator.frequency.setValueAtTime(beepFrequency, audioContext.currentTime);

    gainNode.gain.setValueAtTime(beepVolume, audioContext.currentTime);

    oscillator.connect(gainNode);
    gainNode.connect(audioContext.destination);

    oscillator.start();
    oscillator.stop(audioContext.currentTime + beepDuration / 1000);
}

document.getElementById('connectButton').addEventListener('click', async () => {
    try {
        const port = await navigator.serial.requestPort();
        await port.open({ baudRate: 115200 });

        // Function to read data from the serial port
        async function readData() {
            const reader = port.readable.getReader();
            while (true) {
                const { value, done } = await reader.read();
                if (done) {
                    reader.releaseLock();
                    break;
                }
                const scannedId = new TextDecoder().decode(value);
                const parsedId = parseInt(scannedId, 10); // Parse the scanned data as an integer

                if (!isNaN(parsedId)) {
                    document.getElementById('output').textContent = "Scanned ID : " + parsedId;
                    if (stdIDs.includes(parsedId.toString())) {
                        console.log("found id");
                        // Check if parsedId is in the stdIDs array
                        const rows = document.querySelectorAll('tr');
                        for (let i = 1; i < rows.length; i++) {
                            const studentId = rows[i].cells[0].textContent;
                            if (parseInt(studentId) == parsedId) {
                                playBeepSound();
                                rows[i].cells[2].querySelector('div').textContent = 'P';
                                rows[i].cells[2].querySelector('div').classList.remove('A');
                                rows[i].cells[2].querySelector('div').classList.add('P');
                                attendanceData[studentId] = "P";
                            }
                        }
                    }
                } else {
                    console.log('Invalid data received:', scannedId);
                }
            }
        }

        // Function to write data to the serial port
        async function writeData(data) {
            const writer = port.writable.getWriter();
            await writer.write(new TextEncoder().encode(data));
            writer.releaseLock();
        }

        // Read and display data from the device
        readData();

        // Example: Write data to the device
        writeData('Hello, USB CDC Device!\n');

    } catch (error) {
        console.error('Error:', error);
    }
});
