/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "movie_info"
    let starInfoElement = jQuery("#movie_info");

    // append two html <p> created to the h3 body, which will refresh the page
    starInfoElement.append("<p id=\"losinfo\">" + resultData[0]["title"]
        + "<span style=\"font-size:20px;\">"+" (" + "</span>"
        + "<span style=\"font-size:20px;\">"+ resultData[0]["year"] + "</span>"
        + "<span style=\"font-size:20px;\">"+")" + "</span>"
        + "</p>");
    // starInfoElement.append("<p> " + resultData[0]["year"] + "</p>");
    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    let rowHTML = "";
    rowHTML += "<tr>";
    rowHTML += "<th>" + resultData[0]["director"] + "</th>";
    rowHTML += "<th>" + resultData[0]["rating"] + "</th>";
    rowHTML += "<th>" + resultData[0]["genres"] + "</th>";
    rowHTML += "<th>";

    for(var i = 0; i < resultData[1]["stars_names"].length; i++){
        rowHTML +=  "<a href =" +
            "single-star.html?id=" + resultData[2]['stars_ids'][i] + ">" + resultData[1]['stars_names'][i] + "</a>" + ", ";
    }

    rowHTML += "</th>";

    // rowHTML += "<th>" + resultData[i]["genres"] + "</th>";
    // rowHTML += "<th>" + resultData[i]["stars"] + "</th>";
    rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});