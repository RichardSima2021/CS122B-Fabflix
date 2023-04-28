/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */

let search_form = $("#search_form");
/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");
    console.log("Looking for " + target + " in " + url);
    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}
function handleError(){
    console.log("Failed");
}
function handleMovieResult(resultData) {

    console.log("handleMovieListResult: populating movie table from resultData");

    // Populate the movie table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#movie_table_body");
    starTableBodyElement.empty();
    // Iterate through resultData
    for (let i = 0; i < resultData.length; i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<td>" +
            // Add a link to single-movie.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]['movieID'] + '">'
            + resultData[i]["title"] +     // display title for the link text
            '</a>' +
            "</td>";
        rowHTML += "<td>" + resultData[i]["year"] + "</td>";
        rowHTML += "<td>" + resultData[i]["director"] + "</td>";
        // rowHTML += "<td>" + resultData[i]["genres"] + "</td>";
        rowHTML += "<td>";
        for(let c = 0; c < resultData[i]["genres"].length-1; c++){
            rowHTML += "<a href = movie-list.html?genre=" + resultData[i]["genres"][c] + ">" + resultData[i]["genres"][c] + "</a>, ";
        }
        rowHTML += "<a href = movie-list.html?genre=" + resultData[i]["genres"][resultData[i]["genres"].length-1] + ">" + resultData[i]["genres"][resultData[i]["genres"].length-1] + "</a>";
        rowHTML += "</td>";

        rowHTML += "<td>";
        for(let c = 0; c < resultData[i]["stars_name"].length-1; c++){
            rowHTML += "<a href = single-star.html?id=" + resultData[i]["stars_id"][c] +">" + resultData[i]["stars_name"][c] + "</a>, ";
        }
        rowHTML += "<a href = single-star.html?id=" + resultData[i]["stars_id"][resultData[i]["stars_name"].length-1] +">" + resultData[i]["stars_name"][resultData[i]["stars_name"].length-1] + "</a>";
        rowHTML += "</td>";


        rowHTML += "<td>" + "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-star\" viewBox=\"0 0 16 16\">\n" +
            "  <path d=\"M2.866 14.85c-.078.444.36.791.746.593l4.39-2.256 4.389 2.256c.386.198.824-.149.746-.592l-.83-4.73 3.522-3.356c.33-.314.16-.888-.282-.95l-4.898-.696L8.465.792a.513.513 0 0 0-.927 0L5.354 5.12l-4.898.696c-.441.062-.612.636-.283.95l3.523 3.356-.83 4.73zm4.905-2.767-3.686 1.894.694-3.957a.565.565 0 0 0-.163-.505L1.71 6.745l4.052-.576a.525.525 0 0 0 .393-.288L8 2.223l1.847 3.658a.525.525 0 0 0 .393.288l4.052.575-2.906 2.77a.565.565 0 0 0-.163.506l.694 3.957-3.686-1.894a.503.503 0 0 0-.461 0z\"/>\n" +
            "</svg> " + resultData[i]["rating"] + "</td>";

        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}

let movieGenre = getParameterByName('genre');
let movieTitle = getParameterByName('title');
let sortOrder = "TITLE ASC, RATING ASC "; // This is a default value
let perPage = 10; // This is a default value


var updateButton = document.getElementById("updateButton");
updateButton.addEventListener("click", function() {
    sortOrder = document.getElementById("sort").value;
    perPage = document.getElementById("perPage").value;

    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/movie-list", // Setting request url, which is mapped by MovieListServlet in MovieListServlet.java
        data:{
            "browseByGenre": movieGenre,
            "browseByTitle": movieTitle,
            "sortOrder": sortOrder,
            "perPage": perPage
        },
        error: handleError(),
        success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the MovieListServlet

    });
});

function submitSearchForm(formSubmitEvent) {
    console.log("submit search form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax({
            url :"api/movie-list",
            method: "GET",
            // Serialize the login form to the data sent by get request
            data: search_form.serialize(),
        // NEED TO FIND SOME WAY TO SEND THIS DATA ACROSS
            success: (resultData) => handleMovieResult(resultData)
        }
    );
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */
// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movie-list", // Setting request url, which is mapped by MovieListServlet in MovieListServlet.java
    data:{
        "browseByGenre": movieGenre,
        "browseByTitle": movieTitle,
        "sortOrder": sortOrder,
        "perPage": perPage
    },
    error: handleError(),
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the MovieListServlet

});

search_form.submit(submitSearchForm);