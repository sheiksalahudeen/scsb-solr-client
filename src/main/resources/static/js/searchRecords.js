/**
 * Created by rajeshbabuk on 2/8/16.
 */

jQuery(document).ready(function ($) {
    $("#searchResults").tablesorter();
});

function showItems(resultRowIndex) {
    toggleIcon(resultRowIndex);
    $('#searchResults-' + resultRowIndex).after($("tr.row" + resultRowIndex));
}

function toggleIcon(resultRowIndex) {
    var showItemsInputValue = $('#showItemsInput-' + resultRowIndex).val();
    if (showItemsInputValue == "false") {
        $('#showItemsInput-' + resultRowIndex).val("true");
        $('#showItemsIcon-' + resultRowIndex).removeClass("fa-plus-circle");
        $('#showItemsIcon-' + resultRowIndex).addClass("fa-minus-circle");
    } else {
        $('#showItemsInput-' + resultRowIndex).val("false");
        $('#showItemsIcon-' + resultRowIndex).removeClass("fa-minus-circle");
        $('#showItemsIcon-' + resultRowIndex).addClass("fa-plus-circle");
    }
}

function sortHeader() {
    $("tr.childrow").removeClass('in');
}

