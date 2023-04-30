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
    // rowHTML += "<td>" + resultData[0]["genres"] + "</td>";

    rowHTML += "<td>";

    for(var i = 0; i < resultData[1]["genres_names"].length-1; i++){
        rowHTML +=  "<a href =" +
            "movie-list.html?genre=" + resultData[1]['genres_names'][i] + ">" + resultData[1]['genres_names'][i] + "</a>" + ", ";
    }
    rowHTML +=  "<a href =" +
        "movie-list.html?genre=" + resultData[1]['genres_names'][resultData[1]['genres_names'].length-1] + ">" + resultData[1]['genres_names'][resultData[1]['genres_names'].length-1] + "</a>";

    rowHTML += "</td>";

    rowHTML += "<td>";

    for(var i = 0; i < resultData[3]["stars_names"].length-1; i++){
        rowHTML +=  "<a href =" +
            "single-star.html?id=" + resultData[4]['stars_ids'][i] + ">" + resultData[3]['stars_names'][i] + "</a>" + ", ";
    }
    rowHTML +=  "<a href =" +
        "single-star.html?id=" + resultData[4]['stars_ids'][resultData[4]['stars_ids'].length-1] + ">" + resultData[3]['stars_names'][resultData[3]['stars_names'].length-1] + "</a>" ;

    rowHTML += "</td>";

    rowHTML += "<td>" +"<button name=\"cart_plus\" type=\"button\" class=\"btn btn-outline-secondary btn-sm cart_plus\" style=\"background-color: #BDCDD6\"> <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-cart-plus\" viewBox=\"0 0 16 16\">\n" +
        "  <path name =\"plusSymbol\" d=\"M9 5.5a.5.5 0 0 0-1 0V7H6.5a.5.5 0 0 0 0 1H8v1.5a.5.5 0 0 0 1 0V8h1.5a.5.5 0 0 0 0-1H9V5.5z\"/>\n" +
        "  <path name=\"cartSymbol\" d=\"M.5 1a.5.5 0 0 0 0 1h1.11l.401 1.607 1.498 7.985A.5.5 0 0 0 4 12h1a2 2 0 1 0 0 4 2 2 0 0 0 0-4h7a2 2 0 1 0 0 4 2 2 0 0 0 0-4h1a.5.5 0 0 0 .491-.408l1.5-8A.5.5 0 0 0 14.5 3H2.89l-.405-1.621A.5.5 0 0 0 2 1H.5zm3.915 10L3.102 4h10.796l-1.313 7h-8.17zM6 14a1 1 0 1 1-2 0 1 1 0 0 1 2 0zm7 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0z\"/>\n" +
        "</svg> </button>\n" + "</td>";
    rowHTML += "</tr>";

    // rowHTML += "<td>" + resultData[i]["genres"] + "</td>";
    // rowHTML += "<td>" + resultData[i]["stars"] + "</td>";
    // rowHTML += "</tr>";


    //     // Append the row created to the table body, which will refresh the page
    movieTableBodyElement.append(rowHTML);

    var AddToCartButton = document.getElementsByName("cart_plus")[0];
    // console.log("Number of cart plus buttons: " + AddToCartButton.length);
    AddToCartButton.addEventListener("click",AddToCartButtonClicked);

    if(resultData[0]["rating"] === "N/A"){
        moviePrice = 0;
    }
    else{
        moviePrice = parseFloat(resultData[0]["rating"])*2;
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');
let moviePrice;

function AddToCartButtonClicked(event){
    console.log("cart_plus button clicked");
    let row;
    if (event.target.tagName === "BUTTON") {
        row = event.target.parentNode.parentNode;
    }
    else if(event.target.tagName === "svg") {
        row = event.target.parentNode.parentNode.parentNode;
    }
    else{
        row = event.target.parentNode.parentNode.parentNode.parentNode;
    }

    // let row = event.target.parentNode.parentNode;
    let cells = row.getElementsByTagName("td");
    console.log(row);
    console.log(cells);

    let title = cells[0].innerText;
    // let price = parseFloat(cells[5].innerText.slice(1));
    console.log(title + " costs " + moviePrice);
    // console.log(price);

    jQuery.ajax(
        {
            method: "GET",
            url: "api/add-to-cart",
            data: {
                "title": title,
                "price": moviePrice
            },
            error: function(){window.alert("add to cart failed"); console.log("Add to cart failure")},
            success: function(){window.alert("add to cart success")}
        }
    );
}

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});