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
    // console.log(window.location.href);
    // Populate the movie table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#movie_table_body");
    starTableBodyElement.empty();
    // Iterate through resultData
    for (let i = 0; i < resultData.length-1; i++) {

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

        if(resultData[i]["rating"] === "N/A"){
            rowHTML += "<td>$" + 5.0 + "</td>";
        }
        else{
            rowHTML += "<td>$" + resultData[i]["rating"]*2 + "</td>";
        }

        rowHTML += "<td>" + "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-star\" viewBox=\"0 0 16 16\">\n" +
            "  <path d=\"M2.866 14.85c-.078.444.36.791.746.593l4.39-2.256 4.389 2.256c.386.198.824-.149.746-.592l-.83-4.73 3.522-3.356c.33-.314.16-.888-.282-.95l-4.898-.696L8.465.792a.513.513 0 0 0-.927 0L5.354 5.12l-4.898.696c-.441.062-.612.636-.283.95l3.523 3.356-.83 4.73zm4.905-2.767-3.686 1.894.694-3.957a.565.565 0 0 0-.163-.505L1.71 6.745l4.052-.576a.525.525 0 0 0 .393-.288L8 2.223l1.847 3.658a.525.525 0 0 0 .393.288l4.052.575-2.906 2.77a.565.565 0 0 0-.163.506l.694 3.957-3.686-1.894a.503.503 0 0 0-.461 0z\"/>\n" +
            "</svg> " + resultData[i]["rating"] + "</td>";

        rowHTML += "<td>" +"<button name= \"cart_plus\" type=\"button\" class=\"btn btn-outline-secondary btn-sm cart_plus\" style=\"background-color: #BDCDD6\"> <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-cart-plus\" viewBox=\"0 0 16 16\">\n" +
            "  <path name =\"plusSymbol\" d=\"M9 5.5a.5.5 0 0 0-1 0V7H6.5a.5.5 0 0 0 0 1H8v1.5a.5.5 0 0 0 1 0V8h1.5a.5.5 0 0 0 0-1H9V5.5z\"/>\n" +
            "  <path name=\"cartSymbol\" d=\"M.5 1a.5.5 0 0 0 0 1h1.11l.401 1.607 1.498 7.985A.5.5 0 0 0 4 12h1a2 2 0 1 0 0 4 2 2 0 0 0 0-4h7a2 2 0 1 0 0 4 2 2 0 0 0 0-4h1a.5.5 0 0 0 .491-.408l1.5-8A.5.5 0 0 0 14.5 3H2.89l-.405-1.621A.5.5 0 0 0 2 1H.5zm3.915 10L3.102 4h10.796l-1.313 7h-8.17zM6 14a1 1 0 1 1-2 0 1 1 0 0 1 2 0zm7 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0z\"/>\n" +
            "</svg> </button>\n" + "</td>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
    document.getElementById("pageNumber").innerHTML = resultData[resultData.length-1];

    var AddToCartButtons = document.getElementsByName("cart_plus");
    console.log("Number of cart plus buttons: " + AddToCartButtons.length);
    for (var i = 0; i < AddToCartButtons.length; i++){
        AddToCartButtons[i].addEventListener("click",AddToCartButtonsClicked);
    }
}

let filter = "search";
if (getParameterByName("filter") === null){
    filter = "browse";
}
let movieGenre = getParameterByName('genre');
let movieTitle = getParameterByName('title');
let sortOrder = null //"TITLE ASC, RATING ASC "; // This is a default value
let perPage = null //10; // This is a default value



var updateButton = document.getElementById("updateButton");
updateButton.addEventListener("click", function() {
    sortOrder = document.getElementById("sort").value;
    perPage = document.getElementById("perPage").value;

    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/movie-list", // Setting request url, which is mapped by MovieListServlet in MovieListServlet.java
        data:{
            "filter" : "update",
            "sortOrder": sortOrder,
            "perPage": perPage,
            "pageNum" : 1
        },
        error: handleError(),
        success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the MovieListServlet

    });
});

var prevButton = document.getElementById("previousButton");
prevButton.addEventListener("click",prevPageClicked);
function noPrev(){

}
function prevPageClicked(){
    console.log("prev button clicked");
    let currentPage = parseInt(document.getElementById("pageNumber").textContent);
    console.log(currentPage);
    if(currentPage === 1){
        console.log("Already page 1");
        window.alert("Already on Page 1");
    }
    else{
        console.log("Going to previous page")
        document.getElementById("pageNumber").innerHTML = currentPage-1
        jQuery.ajax({
            dataType:"json",
            url:"api/movie-list",
            method: "GET",
            data:{
                "filter" : "update",
                "sortOrder" : document.getElementById("sort").value,
                "perPage" : document.getElementById("perPage").value,
                "pageNum" : currentPage-1
            },
            error: noNext(),
            success: (resultData) => handleMovieResult(resultData)
        })
    }
}
var nextButton = document.getElementById("nextButton");
nextButton.addEventListener("click",nextPageClicked);
function noNext(){
    window.alert("No next page");
}
function nextPageClicked(){
    console.log("next button clicked");
    let currentPage = parseInt(document.getElementById("pageNumber").textContent);
    console.log(currentPage);
    // Check if there is a next page to go to:
    // How do I do this

    jQuery.ajax({
        dataType:"json",
        url:"api/movie-list",
        method: "GET",
        data:{
            "filter" : "update",
            "sortOrder" : document.getElementById("sort").value,
            "perPage" : document.getElementById("perPage").value,
            "pageNum" : currentPage+1
        },
        error: noNext(),
        success: (resultData) => handleMovieResult(resultData)
    })
    console.log("Go to next page");
    // document.getElementById("pageNumber").innerHTML = currentPage+1;
    // pageNumberElement.empty();
    // pageNumberElement.append(currentPage+1)
}


function submitSearchForm(formSubmitEvent) {
    console.log("submit search form");
    formSubmitEvent.preventDefault();

    jQuery.ajax({
            dataType: "json",
            url :"api/movie-list",
            method: "GET",
            data:
                {
                    "filter" : "search",
                    "searchByTitle" : document.querySelector("#searchTitle").value,
                    "searchByYear" : document.querySelector("#searchYear").value,
                    "searchByDirector" : document.querySelector("#searchDirector").value,
                    "searchByStar" : document.querySelector("#searchStar").value,
                    "sortOrder" : document.getElementById("sort").value,
                    "perPage" : document.getElementById("perPage").value,
                    "pageNum" : 1
                },
            success: (resultData) => handleMovieResult(resultData)
        }
    );
}


function AddToCartButtonsClicked(event){
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
    let price = parseFloat(cells[5].innerText.slice(1));
    console.log(title + " costs " + price);
    console.log(price);

    jQuery.ajax(
        {
            method: "GET",
            url: "api/add-to-cart",
            data: {
                    "title": title,
                    "price": price
                },
            error: function(){window.alert("add to cart failed"); console.log("Add to cart failure")},
            success: function(){window.alert("add to cart success")}
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
        "filter" : filter,
        "browseByGenre": movieGenre,
        "browseByTitle": movieTitle,
        "searchByTitle" : getParameterByName("searchByTitle"),
        "searchByYear" : getParameterByName("searchByYear"),
        "searchByDirector" : getParameterByName("searchByDirector"),
        "searchByStar" : getParameterByName("searchByStar"),
        "sortOrder": sortOrder,
        "perPage": perPage
    },
    error: handleError(),
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the MovieListServlet

});

// var AddToCartButtons;
// var AddtoCartButtons = document.getElementsByClassName("cart_plus");


search_form.submit(submitSearchForm);