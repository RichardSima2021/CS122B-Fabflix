let add_movie_form = $("#new_movie_form");

function handleAddMovieResult(resultData){

    if(resultData["status"] === "Inserted"){
        let resultMessage = "Added new movie, movieID: " + resultData["movieID"] + ".";
        if(resultData["newStarEntry"]){
            resultMessage += " New Star added, starID: " + resultData["starID"] + ".";
        }
        if(resultData["newGenreEntry"]){
            resultMessage += " New Genre added, genreID: " + resultData["genreID"] + ".";
        }
        $("#add_movie_result_message").text(resultMessage);
    }
    else{
        $("#add_movie_result_message").text("Movie Exists");
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