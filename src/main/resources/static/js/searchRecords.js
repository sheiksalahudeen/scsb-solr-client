/**
 * Created by rajeshbabuk on 2/8/16.
 */

jQuery(document).ready(function ($) {
    $("#searchResults").tablesorter();
});

function showItems(resultRowIndex) {
    $('#searchResults-' + resultRowIndex).after($("tr.row" + resultRowIndex));
}

function sortHeader() {
    $("tr.childrow").removeClass('in');
}

