/**
 * Created by SheikS on 6/20/2016.
 */
var intervalID;

jQuery(document).ready(function ($) {
    $("#fullIndex-form").submit(function (event) {
        event.preventDefault();
        fullIndex();
    });
});


function refresh() {
    var autoRefresh = $('#autoRefresh').is(':checked');
    if(autoRefresh) {
        intervalID= setInterval(function () {
            updateStatus();
        }, 5000);
    } else {
        clearInterval(intervalID);
    }
}

function fullIndex() {
    var $form = $('#fullIndex-form');
    $("#submit").attr('disabled', 'disabled');
    $.ajax({
        url: $form.attr('action'),
        type: 'post',
        data: $form.serialize(),
        success: function (response) {
            console.log("completed");
            $("#submit").removeAttr('disabled');
        }
    });
    setTimeout(function(){
    }, 2000);
    updateStatus();
}


function updateStatus() {
    var request = $.ajax({
        url: "solrIndexer/report",
        type: "GET",
        contentType: "application/json"
    });
    request.done(function (msg) {
        document.getElementById("fullIndexingStatus").value = msg;
    });
}
