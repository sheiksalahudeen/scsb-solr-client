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
    $("tr.childRow").removeClass('in');
    $('[name=showItemsInput]').val("false");
    $('[name=showItemsIcon]').removeClass("fa-minus-circle");
    $('[name=showItemsIcon]').addClass("fa-plus-circle");
}

function selectAllParentRows() {
    var selectAllFlag = $('#selectAll').is(":checked");
    if (selectAllFlag) {
        $("tr.parentRow #selected").prop('checked', true);
    } else {
        $("tr.parentRow #selected").prop('checked', false);
    }
}

function selectAllChildRows(childRowIndex) {
    var selectAllFlag = $('#selectAllItems-' + childRowIndex).is(":checked");
    if (selectAllFlag) {
        $("tr.row" + childRowIndex + " #selectedItem").prop('checked', true);
    } else {
        $("tr.row" + childRowIndex + " #selectedItem").prop('checked', false);
    }
}

jQuery(document).keypress(function (e) {
    if (e.which == 13) {
        $("#search").click();
    }
});

