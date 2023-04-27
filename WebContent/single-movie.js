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

    console.log("handleResult: populating movie info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "movie_info"
    let starInfoElement = jQuery("#movie_info");

    // append two html <p> created to the h3 body, which will refresh the page
    starInfoElement.append("<p id=\"losinfo\">" + resultData[0]["title"]
        + "<span style=\"font-size:20px;\">"+" (" + resultData[0]["year"] + ")" + "</span>" + "</p>");
    // starInfoElement.append("<p> " + resultData[0]["year"] + "</p>");
    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    let rowHTML = "";
    rowHTML += "<tr>";
    rowHTML += "<td>" + resultData[0]["director"] + "</td>";
    rowHTML += "<td>" + "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-star\" viewBox=\"0 0 16 16\">\n" +
        "  <path d=\"M2.866 14.85c-.078.444.36.791.746.593l4.39-2.256 4.389 2.256c.386.198.824-.149.746-.592l-.83-4.73 3.522-3.356c.33-.314.16-.888-.282-.95l-4.898-.696L8.465.792a.513.513 0 0 0-.927 0L5.354 5.12l-4.898.696c-.441.062-.612.636-.283.95l3.523 3.356-.83 4.73zm4.905-2.767-3.686 1.894.694-3.957a.565.565 0 0 0-.163-.505L1.71 6.745l4.052-.576a.525.525 0 0 0 .393-.288L8 2.223l1.847 3.658a.525.525 0 0 0 .393.288l4.052.575-2.906 2.77a.565.565 0 0 0-.163.506l.694 3.957-3.686-1.894a.503.503 0 0 0-.461 0z\"/>\n" +
        "</svg> " + resultData[0]["rating"] + "</td>";
    rowHTML += "<td>" + resultData[0]["genres"] + "</td>";
    rowHTML += "<td>";

    for(var i = 0; i < resultData[1]["stars_names"].length; i++){
        rowHTML +=  "<a href =" +
            "single-star.html?id=" + resultData[2]['stars_ids'][i] + ">" + resultData[1]['stars_names'][i] + "</a>" + ", ";
    }

    rowHTML += "</td>";

    // rowHTML += "<td>" + resultData[i]["genres"] + "</td>";
    // rowHTML += "<td>" + resultData[i]["stars"] + "</td>";
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