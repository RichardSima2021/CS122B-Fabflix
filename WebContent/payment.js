let payment_form = $("#payment_form");
console.log("called payment.js");
/**
 * Handle the data returned by PaymentServlet
 * @param resultData jsonObject
 */
function handlePaymentResult(resultData){
    console.log("handle payment response");

    let totalElement = jQuery("#payment_total_amount");

    let rowHTML = "$ " + resultData["total"];

    totalElement.append(rowHTML);
}

/**
 * Handle the data returned by SubmitPaymentServlet
 * @param resultDataString jsonObject
 */
function handleSubmitPaymentResult(resultDataString) {
    // console.log(resultDataString);
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle submit payment response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login succeeds, it will redirect the user to movie-list.html
    if (resultDataJson["status"] === "success") {
        window.location.replace("confirm.html");
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#payment_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitPaymentForm(formSubmitEvent) {
    console.log("submit payment form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/submit-payment", {
            method: "POST",
            // Serialize the payment form to the data sent by POST request
            data: payment_form.serialize(),
            success: handleSubmitPaymentResult
        }
    );
}

function loadPayment(){
    console.log("load payment");
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/payment",
        error: function(){console.log("Something went wrong")},
        success: (resultData) => handlePaymentResult(resultData)
    });
}

loadPayment();
console.log("page loaded");
payment_form.submit(submitPaymentForm);