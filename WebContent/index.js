/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleMovieResult(resultData) {

    console.log("handleStarResult: populating movie table from resultData");

    // Populate the movie table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#movie_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-movie.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]['movieID'] + '">'
            + resultData[i]["title"] +     // display title for the link text
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
        rowHTML += "<th>" + resultData[i]["genres"] + "</th>";
        rowHTML +=
            "<th>" +
                "<a href =" +
            "single-star.html?id=" + resultData[i]['star1_id'] + ">" + resultData[i]['star1_name'] + "</a>" + ", "
        + "<a href =" +
            "single-star.html?id=" + resultData[i]['star2_id'] + ">" + resultData[i]['star2_name'] + "</a>" + ", "
        + "<a href =" +
            "single-star.html?id=" + resultData[i]['star3_id'] + ">" + resultData[i]['star3_name'] + "</a>" + " "
        +"</th>";



        // rowHTML +=
        //     "<th>" +
        //     // Add a link to single-star.html with id passed with GET url parameter
        //     '<a href="single-star.html?id=' + resultData[i]['star1_id'] + '">'
        //     + resultData[i]["star1_name"] +     // display star_name for the link text
        //     '</a>' +
        //     "</th>";
        // try{
        //     rowHTML+=
        //         "<th>" +
        //         // Add a link to single-star.html with id passed with GET url parameter
        //         '<a href="single-star.html?id=' + resultData[i]['star2_id'] + '">'
        //         + resultData[i]["star2_name"] +     // display star_name for the link text
        //         '</a>' +
        //         "</th>";
        // }
        // catch(err){
        //
        // }
        // try{
        //     rowHTML+=
        //         "<th>" +
        //         // Add a link to single-star.html with id passed with GET url parameter
        //         '<a href="single-star.html?id=' + resultData[i]['star3_id'] + '">'
        //         + resultData[i]["star3_name"] +     // display star_name for the link text
        //         '</a>' +
        //         "</th>";
        // }
        // catch(err){
        //
        // }
        // rowHTML +=
        //     "<th>" +
        //     // Add a link to single-star.html with id passed with GET url parameter
        //     '<a href="single-star.html?id=' + resultData[i]['star_id'] + '">'
        //     + resultData[i]["star_name"] +     // display star_name for the link text
        //     '</a>' +
        //     "</th>";
        // rowHTML += "<th>" + resultData[i]["year"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movie-list", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});