let add_star_form = $("#new_star_form");

function handleAddStarResult(resultData){
    // console.log("Hello");
    if(resultData["status"] === "success"){
        $("#add_star_result_message").text("Successfully added new star, ID: " + resultData["newID"]);
    }
    else{
        $("#add_star_result_message").text("Add star failed: " + resultData["errorMessage"]);
    }
}

function handleError(){
    console.log("something went wrong");
}

function submitAddStarForm(formSubmitEvent){
    formSubmitEvent.preventDefault();

    $.ajax(
        "../api/add_star",
        {
            contentType:"appliation/json",
            dataType:"json",
            method:"GET",
            data:add_star_form.serialize(),
            error: handleError,
            success:(resultData) => handleAddStarResult(resultData)
        }
    );
}

add_star_form.submit(submitAddStarForm);