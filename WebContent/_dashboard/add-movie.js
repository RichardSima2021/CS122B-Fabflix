let add_movie_form = $("#new_movie_form");

function handleAddMovieResult(resultData){

    if(resultData["status"] === "success"){
        $("#add_movie_result_message").text("Successfully added new movie, ID: " + resultData["newID"]);
    }
    else{
        $("#add_movie_result_message").text("Add movie failed: " + resultData["errorMessage"]);
    }
}

function handleError(){
    console.log("something went wrong");
}

function submitAddMovieForm(formSubmitEvent){
    formSubmitEvent.preventDefault();

    $.ajax(
        "../api/add_movie",
        {
            contentType:"appliation/json",
            dataType:"json",
            method:"GET",
            data:add_movie_form.serialize(),
            error: handleError,
            success:(resultData) => handleAddMovieResult(resultData)
        }
    );
}

add_movie_form.submit(submitAddMovieForm);