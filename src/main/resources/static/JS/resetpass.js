var url = new URL(window.location.href);
var token = url.searchParams.get('token');

hostaddr=url.searchParams.get('server');


document.getElementById("resetpass").addEventListener("click" , ()=>{
    newpass=document.getElementById("newPassword").value;
    newpassconfirm=document.getElementById("confirmPassword").value;
    
        if(newpass===newpassconfirm){
            
            $.ajax({
                type: 'POST',
                url: 'http://'+hostaddr+'/forgotpass/exec-change',
                contentType: 'application/x-www-form-urlencoded',
                data: {
                    password:newpass,
                    token:token
                },
                
                success: function(data) {
                    alert('Password changed successfully. You can now log in.');
                    window.location.replace('http://'+hostaddr+':5501/login.html');
                },
                error: function() {
                    alert('Login failed. Please check your credentials.');
                }
            });
        }
    });
