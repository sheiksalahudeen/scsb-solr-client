

jQuery(document).ready(function ($) {
    $("#userRoles .users-main-section").show();
    resetDefaults();
    /***Request Tab Create Request Form Show/Hide ***/
    $("#userRoles .usersbacklink a").click(function (e) {
        $("#userRoles .users-main-section").show();
        $("#userRoles .user-create-section").hide();
    });
    $("#userRoles .user-create a").click(function () {
        $("#userRoles .users-main-section").hide();
        $("#userRoles .user-create-section").show();
    });
});

function userRolesFirstPage() {
    searchUserRoles('first','#userRoles .users-main-section','#userRoles .user-create-section');
}

function userRolesLastPage() {
    searchUserRoles('last','#userRoles .users-main-section','#userRoles .user-create-section');
}

function userRolesPreviousPage() {
    $('#pageNumber').val(parseInt($('#pageNumber').val()) - 1);
    searchUserRoles('previous','#userRoles .users-main-section','#userRoles .user-create-section');
}

function userRolesNextPage() {
    $('#pageNumber').val(parseInt($('#pageNumber').val()) + 1);
    searchUserRoles('next','#userRoles .users-main-section','#userRoles .user-create-section');
}


function searchUserRoles(action,showId,hideId) {
    var $form = $('#userRole-form');
    var url = $form.attr('action') + "?action=" + action;
    var request = $.ajax({
        url: url,
        type: 'post',
        data: $form.serialize(),
        success: function (response) {
            $('#userRolesContentId').html(response);
            $(showId).show();
            $(hideId).hide();
        }
    });
}

function submitForm(){
    var action='createUser';
    var $form = $('#userRole-form');
    var url = $form.attr('action') + "?action=" + action;
    var request = $.ajax({
        url: url,
        type: 'post',
        data: $form.serialize(),
        success: function (response) {
            $('#userRolesContentId').html(response);
            $(showId).show();
            $(hideId).hide();
        }
    });
}
function resetDefaults(){
    $('#networkLoginIdErrMsg').hide();
    $('#institutionIdErrMsg').hide();
    $('#roleIdErrMsg').hide();
}

function validateForm(){
    resetDefaults();
    var newNetworkLoginId=$('#networkloginid');
    var institutionId=$('#institutionId');
    var roleId=$('#roleId');

    if(newNetworkLoginId == null || newNetworkLoginId.trim().val().length==0)
    {
        $('#networkLoginIdErrMsg').show();
        return false;
    }

    if(institutionId == null || institutionId.val()<1)
    {
        $('#institutionIdErrMsg').show();
        return false;
    }

    if(roleId == null || roleId.val().length<1)
    {
        $('#roleIdErrMsg').show();
        return false;
    }
}





