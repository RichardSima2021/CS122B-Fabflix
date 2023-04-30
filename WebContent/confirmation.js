console.log("Loaded confirmation.js")

function loadConfirmationPage(){
    var transactionListString = localStorage.getItem("transactionListString");
    // console.log(transactionListString);
    transactionList = JSON.parse(transactionListString);
    // console.log(transactionList);
    let transactionTableBodyElement = jQuery("#transaction_table_body")
    let subtotal = 0;
    for(let i = 0; i < transactionList.length; i++){
        let saleID = transactionList[i]["id"];
        let title = transactionList[i]["title"];
        let quantity = transactionList[i]["count"];
        let price = transactionList[i]["pricePerUnit"];
        let total = price * quantity;
        let movieId = transactionList[i]["movieId"];

        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<td>" + saleID + "</td>"
        rowHTML +=
            "<td>" +
            '<a href="single-movie.html?id=' + movieId + '">'
            + title + "</a></td>";
        rowHTML += "<td>" + quantity + "</td>";
        rowHTML += "<td>" + price + "</td>";
        rowHTML += "<td>" + total + "</td>"
        console.log(rowHTML);
        transactionTableBodyElement.append(rowHTML);
        subtotal += total;
    }

    let totalAmountElement = jQuery("#total_amount");
    totalAmountElement.append(subtotal);

}

loadConfirmationPage();