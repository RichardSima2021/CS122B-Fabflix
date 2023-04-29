/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
// function getParameterByName(target) {
//     // Get request URL
//     let url = window.location.href;
//     // Encode target parameter name to url encoding
//     target = target.replace(/[\[\]]/g, "\\$&");
//
//     // Ues regular expression to find matched parameter value
//     let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
//         results = regex.exec(url);
//     if (!results) return null;
//     if (!results[2]) return '';
//
//     // Return the decoded parameter value
//     return decodeURIComponent(results[2].replace(/\+/g, " "));
// }

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating movie info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "movie_info"
    // let starInfoElement = jQuery("#genre_info");

    // // append two html <p> created to the h3 body, which will refresh the page
    // starInfoElement.append("<p id=\"losinfo\">" + resultData[0]["title"]
    //     + "<span style=\"font-size:20px;\">"+" (" + resultData[0]["year"] + ")" + "</span>" + "</p>");

    // starInfoElement.append("<p> " + resultData[0]["year"] + "</p>");
    console.log("handleResult: populating genre table from resultData");

    // Populate the star table
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

    // let titleInfoElement = jQuery("#title_info");
    console.log("handleResult: populating genre table from resultData");
    let titleTableBodyElement = jQuery("#title_table_body");
    for (let i = 65; i <=90; i++)
    {
        let rowHTML = "";
        rowHTML += "<div class='titleGrid'>"
            + '<a href="movie-list.html?title=' + String.fromCharCode(i) + "%"+ '">'
            + String.fromCharCode(i)
            + ' </a>'
            + "</div>";
        titleTableBodyElement.append(rowHTML);
    }

    console.log("handleResult: populating genre table from resultData");
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

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
// let genre = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/browse", // Setting request url, which is mapped by BrowseServlet in Browse.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the BrowseServlet
});