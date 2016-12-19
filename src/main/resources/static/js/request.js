/**
 * Created by rajeshbabuk on 25/10/16.
 */

jQuery(document).ready(function ($) {
    resetDefaults();
    /***Request Tab Create Request Form Show/Hide ***/
    $("#request .search-request a").click(function (e) {
        $("#request .request-main-section").show();
        $("#request .create-request-section").hide();
    });
    $("#request .backtext a").click(function () {
        $("#request .request-main-section").hide();
        $("#request .create-request-section").show();
        loadCreateRequest();
    });
});

function loadCreateRequest() {
    var $form = $('#request-form');
    var url = $form.attr('action') + "?action=loadCreateRequest";
    var request = $.ajax({
        url: url,
        type: 'post',
        data: $form.serialize(),
        success: function (response) {
            console.log("completed");
            $('#requestContentId').html(response);
            $("#request .request-main-section").hide();
            $("#request .create-request-section").show();
        }
    });
}

function searchRequests(action) {
    var $form = $('#request-form');
    var url = $form.attr('action') + "?action=" + action;
    var request = $.ajax({
        url: url,
        type: 'post',
        data: $form.serialize(),
        success: function (response) {
            console.log("completed");
            $('#requestContentId').html(response);
            $("#request .request-main-section").show();
            $("#request .create-request-section").hide();
        }
    });
}

function requestsFirstPage() {
    searchRequests('first');
}

function requestsLastPage() {
    searchRequests('last');
}

function requestsPreviousPage() {
    $('#pageNumber').val(parseInt($('#pageNumber').val()) - 1);
    searchRequests('previous');
}

function requestsNextPage() {
    $('#pageNumber').val(parseInt($('#pageNumber').val()) + 1);
    searchRequests('next');
}


function selectAllRows() {
    var selectAllFlag = $('#requestSelectAll').is(":checked");
    if (selectAllFlag) {
        $("tr.requestRow #requestSelected").prop('checked', true);
    } else {
        $("tr.requestRow #requestSelected").prop('checked', false);
    }
}

function enableRequestButtons() {
    var selectAllCheckBox = $('#requestSelectAll').is(":checked");
    var resultsCheckBox = $('tr.requestRow #requestSelected').is(":checked");
    if (selectAllCheckBox || resultsCheckBox) {
        document.getElementById("cancelRequestsButtonId").disabled = false;
    } else {
        document.getElementById("cancelRequestsButtonId").disabled = true;
    }
}

function populateItemDetails() {
    var itemBarcode = $('#itemBarcodeId').val();
    if (!isBlankValue(itemBarcode)) {
        var $form = $('#request-form');
        var url = $form.attr('action') + "?action=populateItem";
        $.ajax({
            url: url,
            type: 'post',
            data: $form.serialize(),
            success: function (response) {
                console.log("completed");
                console.log(response);
                var jsonResponse = JSON.parse(response);
                $('#itemTitleId').val(jsonResponse['itemTitle']);
                $('#itemOwningInstitutionId').val(jsonResponse['itemOwningInstitution']);
                var errorMessage = jsonResponse['errorMessage'];
                $('#itemBarcodeErrorMessage').hide();
                if (errorMessage != null && errorMessage != '') {
                    $('#itemBarcodeNotFoundErrorMessage').html(errorMessage);
                    $('#itemBarcodeNotFoundErrorMessage').show();
                } else {
                    $('#itemBarcodeNotFoundErrorMessage').html('');
                }
            }
        });
    }
}

/***Request Tab Create Request Form Selecrt EDD Section Show/Hide ***/
$(function() {
    $('#requestTypeId').change(function(){
        $('.EDDdetails-section').hide();
        $('#' + $(this).val()).show();
        if ($(this).find(':selected').val() === 'EDD' || $(this).find(':selected').val() === 'BORROW DIRECT') {
            $('#deliverylocation_request').hide();
        } else {
            $('#deliverylocation_request').show();
        }
    });
});

function createRequest() {
    var itemBarcode = $('#itemBarcodeId').val();
    var patronBarcode = $('#patronBarcodeId').val();
    var requestType = $('#requestTypeId').val();
    var deliveryLocation = $('#deliveryLocationId').val();
    var requestingInstitution = $('#requestingInstitutionId').val();

    validateEmailAddress();

    if (isBlankValue(itemBarcode)) {
        $('#itemBarcodeErrorMessage').show();
    } else {
        $('#itemBarcodeErrorMessage').hide();
    }
    if (isBlankValue(patronBarcode)) {
        $('#patronBarcodeErrorMessage').show();
    } else {
        $('#patronBarcodeErrorMessage').hide();
    }
    if (isBlankValue(requestType)) {
        $('#requestTypeErrorMessage').show();
    } else {
        if (requestType == 'EDD') {
            var startPage = $('#StartPage').val();
            var endPage = $('#EndPage').val();
            var articleTitle = $('#ArticleChapterTitle').val();

            if (isBlankValue(startPage)) {
                $('#startPageErrorMessage').show();
            } else {
                $('#startPageErrorMessage').hide();
            }
            if (isBlankValue(endPage)) {
                $('#endPageErrorMessage').show();
            } else {
                $('#endPageErrorMessage').hide();
            }
            if (isBlankValue(articleTitle)) {
                $('#articleTitleErrorMessage').show();
            } else {
                $('#articleTitleErrorMessage').hide();
            }
        }
        $('#requestTypeErrorMessage').hide();
    }
    if (isBlankValue(deliveryLocation)) {
        if (!(requestType == 'EDD' || requestType == 'BORROW DIRECT')) {
            $('#deliveryLocationErrorMessage').show();
        }
    } else {
        $('#deliveryLocationErrorMessage').hide();
    }
    if (isBlankValue(requestingInstitution)) {
        $('#requestingInstitutionErrorMessage').show();
    } else {
        $('#requestingInstitutionErrorMessage').hide();
    }
}

function isBlankValue(value) {
    if (value == null || value == '') {
        return true;
    }
    return false;
}    

function resetDefaults() {
    $('#itemBarcodeErrorMessage').hide();
    $('#patronBarcodeErrorMessage').hide();
    $('#requestTypeErrorMessage').hide();
    $('#deliveryLocationErrorMessage').hide();
    $('#requestingInstitutionErrorMessage').hide();
    $('#startPageErrorMessage').hide();
    $('#endPageErrorMessage').hide();
    $('#articleTitleErrorMessage').hide();
    $('#patronEmailIdErrorMessage').hide();
}

function toggleItemBarcodeValidation() {
    var itemBarcode = $('#itemBarcodeId').val();
    if (isBlankValue(itemBarcode)) {
        $('#itemBarcodeErrorMessage').show();
        $('#itemBarcodeNotFoundErrorMessage').hide();
    } else {
        $('#itemBarcodeErrorMessage').hide();
    }
}

function toggleRequestingInstitutionValidation() {
    var requestingInstitution = $('#requestingInstitutionId').val();
    if (isBlankValue(requestingInstitution)) {
        $('#requestingInstitutionErrorMessage').show();
    } else {
        $('#requestingInstitutionErrorMessage').hide();
    }
}

function togglePatronBarcodeValidation() {
    var patronBarcode = $('#patronBarcodeId').val();
    if (isBlankValue(patronBarcode)) {
        $('#patronBarcodeErrorMessage').show();
    } else {
        $('#patronBarcodeErrorMessage').hide();
    }
}

function toggleDeliveryLocationValidation() {
    var deliveryLocation = $('#deliveryLocationId').val();
    if (isBlankValue(deliveryLocation)) {
        $('#deliveryLocationErrorMessage').show();
    } else {
        $('#deliveryLocationErrorMessage').hide();
    }
}

function toggleStartPageValidation() {
    var startPage = $('#StartPage').val();
    if (isBlankValue(startPage)) {
        $('#startPageErrorMessage').show();
    } else {
        $('#startPageErrorMessage').hide();
    }
}

function toggleEndPageValidation() {
    var endPage = $('#EndPage').val();
    if (isBlankValue(endPage)) {
        $('#endPageErrorMessage').show();
    } else {
        $('#endPageErrorMessage').hide();
    }
}

function toggleArticleTitleValidation() {
    var articleTitle = $('#ArticleChapterTitle').val();
    if (isBlankValue(articleTitle)) {
        $('#articleTitleErrorMessage').show();
    } else {
        $('#articleTitleErrorMessage').hide();
    }
}

function validateEmailAddress() {
    var isValidEmailAddress = $('#patronEmailId').is(':valid');
    if (!isValidEmailAddress) {
        $('#patronEmailIdErrorMessage').show();
    } else {
        $('#patronEmailIdErrorMessage').hide();
    }

}