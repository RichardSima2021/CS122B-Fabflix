

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

console.log("called shopping-cart.js");

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
function handleCartResult(resultData) {

    console.log("populating cart table");
    // console.log(window.location.href);
    // Populate the movie table
    // Find the empty table body by id "star_table_body"
    let ShopCartTableBodyElement = jQuery("#shopping_cart_table_body");
    ShopCartTableBodyElement.empty();
    // Iterate through resultData
    for (let i = 0; i < resultData["items"].length; i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        // TODO: Add title element
        rowHTML += "<td>" + resultData["items"][i][0] + "</td>\n";
        rowHTML +=
            "            <td style=\"display: flex; justify-content: center\">\n" +
            "                <div class=\"item_counter\" id = \"item_counter\">\n" +
            "\n" +
            "                    <a><button name = \"minus_button\" type=\"button\" class=\"btn btn-outline-secondary btn-sm\" style=\"display: flex\">\n" +
            "                        <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-dash\" viewBox=\"0 0 16 16\">\n" +
            "                            <path d=\"M4 8a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7A.5.5 0 0 1 4 8z\"/>\n" +
            "                        </svg>\n" +
            "                    </button></a>\n";
        rowHTML +=
            "                    <div class=\"count\">" + resultData["items"][i][1] + "</div>\n";
        rowHTML +=
            "                    <a><button name = \"plus_button\" type=\"button\" class=\"btn btn-outline-secondary btn-sm\" style=\"display: flex\">\n" +
            "                        <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-plus\" viewBox=\"0 0 16 16\">\n" +
            "                            <path d=\"M8 4a.5.5 0 0 1 .5.5v3h3a.5.5 0 0 1 0 1h-3v3a.5.5 0 0 1-1 0v-3h-3a.5.5 0 0 1 0-1h3v-3A.5.5 0 0 1 8 4z\"/>\n" +
            "                        </svg>\n" +
            "                    </button></a>\n" +
            "                </div>\n" +
            "            </td>\n";
        rowHTML +=
            // price of item
            "            <td>" + resultData["items"][i][2] + "</td>\n";
            // Subtotal of quantity * item
        rowHTML +=
            "            <td>" + resultData["items"][i][3] + "</td>\n";
        rowHTML +=
            "            <td style=\"display: flex; justify-content: center\">" +
            "                        <a><button  name = \"remove_button\" type=\"button\" class=\"btn btn-outline-secondary btn-sm\" style=\"background-color: #BDCDD6; display: flex\">\n" +
            "                            <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-trash3\" viewBox=\"0 0 16 16\">\n" +
            "                                <path d=\"M6.5 1h3a.5.5 0 0 1 .5.5v1H6v-1a.5.5 0 0 1 .5-.5ZM11 2.5v-1A1.5 1.5 0 0 0 9.5 0h-3A1.5 1.5 0 0 0 5 1.5v1H2.506a.58.58 0 0 0-.01 0H1.5a.5.5 0 0 0 0 1h.538l.853 10.66A2 2 0 0 0 4.885 16h6.23a2 2 0 0 0 1.994-1.84l.853-10.66h.538a.5.5 0 0 0 0-1h-.995a.59.59 0 0 0-.01 0H11Zm1.958 1-.846 10.58a1 1 0 0 1-.997.92h-6.23a1 1 0 0 1-.997-.92L3.042 3.5h9.916Zm-7.487 1a.5.5 0 0 1 .528.47l.5 8.5a.5.5 0 0 1-.998.06L5 5.03a.5.5 0 0 1 .47-.53Zm5.058 0a.5.5 0 0 1 .47.53l-.5 8.5a.5.5 0 1 1-.998-.06l.5-8.5a.5.5 0 0 1 .528-.47ZM8 4.5a.5.5 0 0 1 .5.5v8.5a.5.5 0 0 1-1 0V5a.5.5 0 0 1 .5-.5Z\"/>\n" +
            "                            </svg>\n" +
            "                        </button></a>" +
            "            </td>";

        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        ShopCartTableBodyElement.append(rowHTML);
    }

    var minusButtons = document.getElementsByName("minus_button");
    // console.log("Number of minus buttons: " + minusButtons.length);

    var plusButtons = document.getElementsByName("plus_button");
    // console.log("Number of plus buttons: " + plusButtons.length);

    var removeButtons = document.getElementsByName("remove_button");
    // console.log("Number of remove buttons: " + removeButtons.length);

    for (var i = 0; i < minusButtons.length; i++){
        minusButtons[i].addEventListener("click",minusButtonClicked);
        plusButtons[i].addEventListener("click",plusButtonClicked);
        removeButtons[i].addEventListener("click",removeButtonClicked);
    }

    let subtotalBody = jQuery("#total_amount");
    subtotalBody.empty()
    subtotalBody.append("$" + resultData["total"]);
}

// var minusButton = document.getElementById('minus_counter');
// minusButton.addEventListener("click", minusButtonClicked);
function minusButtonClicked(event){

    /*
        make ajax request with info of which movie to remove
        servlet updates session cart
        js calls the populate table function

     */
    let title = "";
    let row;
    if(event.target.tagName === "BUTTON"){
        row = event.target.parentNode.parentNode.parentNode.parentNode;
    }
    else{
        row = event.target.parentNode.parentNode.parentNode.parentNode.parentNode;
    }
    // console.log(row);
    let cells = row.getElementsByTagName("td");
    title = cells[0].innerText;
    console.log("Minus one of " + title);
    jQuery.ajax({
        method:"GET",
        url:"api/modify-cart",
        data:{
            "modify" : "minus",
            "title" : title
        },
        error: handleModifyError(),
        success: () => populateTable()
    });
}

function plusButtonClicked(event){
    let title = "";
    let row;
    if(event.target.tagName === "BUTTON"){
        row = event.target.parentNode.parentNode.parentNode.parentNode;
    }
    else{
        row = event.target.parentNode.parentNode.parentNode.parentNode.parentNode;
    }
    // console.log(row);
    let cells = row.getElementsByTagName("td");
    title = cells[0].innerText;
    console.log("Add one of " + title);
    jQuery.ajax({
        method:"GET",
        url:"api/modify-cart",
        data:{
            "modify" : "add",
            "title" : title
        },
        error: handleModifyError(),
        success: () => populateTable()
    });
}

// var removeButton = document.getElementById('remove_button');
// removeButton.addEventListener("click", removeButtonClicked);
function removeButtonClicked(event){
    let title = "";
    let row;
    if(event.target.tagName === "BUTTON"){
        row = event.target.parentNode.parentNode.parentNode.parentNode;
    }
    else{
        row = event.target.parentNode.parentNode.parentNode.parentNode.parentNode;
    }
    // console.log(row);
    let cells = row.getElementsByTagName("td");
    title = cells[0].innerText;
    console.log("Remove " + title);
    jQuery.ajax({
        method:"GET",
        url:"api/modify-cart",
        data:{
            "modify" : "remove",
            "title" : title
        },
        error: handleModifyError(),
        success: () => populateTable()
    });
}

function handleModifyError(){

}

function populateTable(){
    console.log("load shopping cart");
    jQuery.ajax({
        dataType:"json",
        method: "GET",// Setting request method
        url: "api/shopping-cart" ,
        error: function(){console.log("Something went wrong")},
        success: (resultData) => handleCartResult(resultData)
    });
}
// populate the cart initially
populateTable()