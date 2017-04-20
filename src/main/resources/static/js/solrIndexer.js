/**
 * Created by SheikS on 6/20/2016.
 */
var intervalID;

jQuery(document).ready(function ($) {
    $("#fullIndex-form").submit(function (event) {
        event.preventDefault();
        fullIndex();
    });

    $("#reports-form").submit(function (event) {
        event.preventDefault();
        generateReport();
    });

    $('#dateFrom').datetimepicker({
        format: "dd-mm-yyyy hh:ii"
    });

    $('#createdDate').datepicker({
        format: "yyyy/mm/dd"
    });

    $('#todate').datepicker({
        format: "yyyy/mm/dd"
    });
    
    $('#matchingAlgoDate').datepicker({
        format: "yyyy/mm/dd"
    });

    $('#fromDate').datepicker({
        format: "yyyy/mm/dd"
    });

    showDateField();
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
            $("#submit").removeAttr('disabled');
            document.getElementById("fullIndexingStatus").value = response;
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

function saveReport() {
    $("#saveReport").attr('disabled', 'disabled');
    document.getElementById("matchingAlgorithmStatus").value = '';
    var criteria = $('#matchingCriteria').val();
    var matchingAlgoDate = $('#matchingAlgoDate').val();
    var url = '';
    if(criteria === 'ALL') {
        url = "/matchingAlgorithm/full";
    } else if (criteria === 'Reports') {
        url = "/matchingAlgorithm/reports";
    } else if (criteria === 'UpdateCGDInDB') {
        url = "/matchingAlgorithm/updateCGDInDB";
    } else if (criteria === 'UpdateCGDInSolr') {
        url = "/matchingAlgorithm/updateCGDInSolr?matchingAlgoDate="+matchingAlgoDate;
    } else if (criteria === 'PopulateDataForDataDump') {
        url = "/matchingAlgorithm/populateDataForDataDump";
    }
    if(url !== '') {
        var request = $.ajax({
            url: url,
            type: 'post'
        });
        request.done(function (msg) {
            document.getElementById("matchingAlgorithmStatus").value = msg;
            $("#saveReport").removeAttr('disabled');
        })
    }
}

function generateReport() {
    var $form = $('#reports-form');
    $("#report").attr('disabled', 'disabled');
    document.getElementById("reportStatus").value = '';
    var criteria = $('#matchingCriteriaForReports').val();
    var processType = $('#processType').val();
    var url = '';
    if(processType === 'SolrIndex' || processType === 'DeAccession_Summary_Report' || processType ==='Accession'  || processType ==='SubmitCollection') {
        url = "/reportGeneration/generateReports";
    } else {
        if(criteria === 'ALL') {
            url = "/matchingAlgorithm/generateReports/full";
        } else if(criteria === 'OCLC') {
            url = "/matchingAlgorithm/generateReports/oclc";
        } else if(criteria === 'ISBN') {
            url = "/matchingAlgorithm/generateReports/isbn";
        } else if(criteria === 'ISSN') {
            url = "/matchingAlgorithm/generateReports/issn";
        } else if(criteria === 'LCCN') {
            url = "/matchingAlgorithm/generateReports/lccn";
        }
    }
    if(url !== '') {
        var request = $.ajax({
            url: url,
            type: 'post',
            data: $form.serialize(),
            success: function (response) {
                $("#report").removeAttr('disabled');
            }
        });
        request.done(function (msg) {
            document.getElementById("reportStatus").value = msg;
        })
    }
}

function showDateField() {
    var criteria = $('#matchingCriteria').val();
    if(criteria === 'UpdateCGDInSolr') {
        $('#matchingAlgoDateDiv').show();
    } else {
        $('#matchingAlgoDateDiv').hide();
    }
}
