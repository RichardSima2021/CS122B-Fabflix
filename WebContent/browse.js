/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */

let search_form = $("#search_form")

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating genre table from resultData");

    // Populate the genre table
    // Find the empty table body by id "movie_table_body"
    let genreTableBodyElement = jQuery("#genre_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData['genres'].length; i++) {
        let rowHTML = "";
        rowHTML += "<div class='genreGrid'>"
            + '<a href="movie-list.html?genre=' + resultData['genres'][i] + '">'
            + resultData['genres'][i]
            + ' </a>'
            + "</div>";

        // Append the row created to the table body, which will refresh the page
        genreTableBodyElement.append(rowHTML);
    }

    // Populate Browse by Title Char
    let titleTableBodyElement = jQuery("#title_table_body");
    for (let i = 65; i <=90; i++)
    {
        let rowHTML = "";
        rowHTML += "<div class='titleGrid'>"
            + '<a href="movie-list.html?title=' + String.fromCharCode(i) + '">'
            + String.fromCharCode(i)
            + ' </a>'
            + "</div>";
        titleTableBodyElement.append(rowHTML);
    }

    // Populate Browse by Digits
    let digittitleTableBodyElement = jQuery("#digit_title_table_body");
    for (let i = 0; i <=9; i++)
    {
        let rowHTML = "";
        rowHTML += "<div class='titleGrid'>"
            + '<a href="movie-list.html?title=' + i + '">'
            + i
            + ' </a>'
            + "</div>";
        digittitleTableBodyElement.append(rowHTML);
    }
    let rowHTML = "";
    rowHTML += "<div class='titleGrid'>"
        + '<a href="movie-list.html?title=' + "*" + '">'
        + "*"
        + ' </a>'
        + "</div>";
    digittitleTableBodyElement.append(rowHTML);

}

function submitSearchForm(formSubmitEvent){
    formSubmitEvent.preventDefault();

    let searchURL = "movie-list.html?filter=search";
    let searchByTitle = "&searchByTitle=" + document.querySelector("#searchTitle").value;
    let searchByYear = "&searchByYear=" + document.querySelector("#searchYear").value;
    let searchByDirector = "&searchByDirector=" + document.querySelector("#searchDirector").value;
    let searchByStar = "&searchByStar=" + document.querySelector("#searchStar").value;

    searchURL = searchURL + searchByTitle + searchByYear + searchByDirector + searchByStar;
    console.log(searchURL);
    window.location.replace(searchURL);
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

function loadPage(){
    // Makes the HTTP GET request and registers on success callback function handleResult
    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "api/browse", // Setting request url, which is mapped by BrowseServlet in Browse.java
        success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the BrowseServlet
    });
}

loadPage();


search_form.submit(submitSearchForm);