// Calls generateTable on each SQL Table and populates dashboard.html
function populatePage(resultData){
    let sqlTablesElement = jQuery("#sqltables_table")
    for(let i = 0; i < resultData.length; i++){
        sqlTablesElement.append(generateTable(resultData[i]));
        // console.log(resultData[i]);
    }

}

// Generates the table for each SQL table
function generateTable(tableData){

    let tableHTML = "<h1>";

    for(var tableName in tableData){
        // console.log(tableName);
        tableHTML += tableName + "</h1>";
        tableHTML += "<table>";
        tableHTML += "<thead>" +
                        "<tr>" +
                            "<th>" + "Attribute" + "</th>" +
                            "<th>" + "Type" + "</th>" +
                        "</tr>" +
                    "</thead>" +
                    "<tbody>"
        for(var columnName in tableData[tableName]){
            tableHTML += "<tr>" +
                "<td>" + columnName + "</td>" +
                "<td>" + tableData[tableName][columnName] + "</td>";
            // console.log(columnName);
            // console.log(tableData[tableName][columnName]);
            tableHTML += "</tr>";
        }
        tableHTML += "</tbody>" +
                    "</table>";
    }
    return tableHTML;
}

function handleError(errorResponse){
    console.log(errorResponse["errorMessage"]);
}

function getPageData(){
    jQuery.ajax({
        dataType:"json",
        method:"GET",
        url:"../api/get-database-info",
        error: (errorResponse) => handleError(errorResponse),
        success:(resultData) => populatePage(resultData) //resultData is an Array of JsonObjects, each representing a table
    });
}

getPageData();