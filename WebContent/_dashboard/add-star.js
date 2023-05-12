let add_star_form = $("#new_star_form");

function handleAddStarResult(resultData){

}

function submitAddStarForm(formSubmitEvent){
    formSubmitEvent.preventDefault();

    $.ajax(
        "../api/add_star",
        {
            dataType:"json",
            method:"GET",
            data:add_star_form.serialize(),
            success:(resultData) => handleAddStarResult(resultData)
        }
    );
}

add_star_form.submit(submitAddStarForm);