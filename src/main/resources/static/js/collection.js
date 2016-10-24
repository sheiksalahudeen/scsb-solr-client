/**
 * Created by rajeshbabuk on 13/10/16.
 */

jQuery(document).ready(function ($) {
    if ($("#barcodeFieldId").val().length == 0) {
        $("#clearBarcodeText").hide();
    }
    
    $("#barcodeFieldId").keyup(function(e) {
        if ($("#barcodeFieldId").val().length > 0) {
            $("#clearBarcodeText").show();
        } else {
            $("#clearBarcodeText").hide();
        }
    });
});

function clearBarcodeText() {
    $("#barcodeFieldId").val('');
    $("#clearBarcodeText").hide();
}


