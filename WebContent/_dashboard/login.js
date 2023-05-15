let login_form = $("#employee_login_form");
function handleLoginResult(resultData){
    if(resultData["status"] === "success"){
        console.log("login success");
        window.location.replace("dashboard.html");
    }
    else{
        console.log("login fail");
        console.log(resultData["message"]);
        $("#login_error_message").text(resultData["message"]);
    }
}

function submitLoginForm(formSubmitEvent){
    console.log("submit login form");

    formSubmitEvent.preventDefault();

    $.ajax(

        "../api/dashboard_login",
        {
            contentType:"appliation/json",
            dataType:"json",
            method:"POST",
            data:login_form.serialize(),
            success: (resultData) => handleLoginResult(resultData)
        }
    );
}

login_form.submit(submitLoginForm);