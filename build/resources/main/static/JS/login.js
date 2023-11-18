

hostaddr="atendancesystembackend-production.up.railway.app";
//atendancesystembackend-production.up.railway.app
//hostaddr="192.168.1.66";
localStorage.setItem('host', hostaddr);

$('#loginButton').click(function() {
    const userType = $('#userType').val();
    const id = $('#email').val();
    const password = $('#password').val();
    //const remember = $('#remember').prop('checked');

    // Send login request to the server using jQuery's AJAX

    const requestBody = {
        id : Number(id),
        password : password
    }

    let hash = btoa(id + ":" + password);
    $.ajax({
        type: 'POST',
        url: 'https://'+hostaddr+'/login/'+userType,
        contentType: 'application/json',
        data: JSON.stringify(requestBody),
        headers:{
            'Authorization': 'Basic ' + hash
        },


        success: function(data) {
            localStorage.setItem("mysession" , data);
            localStorage.setItem("myhash" , hash);
            alert('Login Successful ' + data);
            // Redirect to the appropriate dashboard based on the user type
            if(userType=='admin')
                window.location.href = "admin.html";
            else if(userType=='teacher')
                window.location.href = "teacher_dash.html";
            else if(userType=='student')
                window.location.href = "student_dash.html";
        },
        error: function() {
            // Handle login error
            alert('Login failed. Please check your credentials.');
        }
    });
});

// Add this script to handle the toggle functionality
$(document).ready(function () {
    // Show the login form by default
    $('#loginForm').show();
    $('#forgotPasswordForm').hide();

    // Toggle to the forgot password form
    $('#forgotPasswordLink').click(function () {
        $('#loginForm').hide();
        $('#forgotPasswordForm').show();
    });

    // Toggle back to the login form
    $('#backToLoginLink').click(function () {
        $('#forgotPasswordForm').hide();
        $('#loginForm').show();
    });

    // You can add your login and forgot password logic here
    // For example, handle the click event of the login button and reset password button
    $('#loginButton').click(function () {
        // Your login logic here
    });

    $('#resetPasswordButton').click(function () {
        // Your reset password logic here
        id=document.getElementById("forgotPasswordEmail").value;


        $.ajax({
            type: 'POST',
            url: 'https://'+hostaddr+'/forgotpass/request-change',
            contentType: 'application/x-www-form-urlencoded',
            data: {
                id:id
            },



            success: function(data) {
                alert('YES YES YES');
            },
            error: function() {
                // Handle login error
                alert('Login failed. Please check your credentials.');
            }
        });

    });
});
