/**
 * Created by hemalathas on 22/12/16.
 */
jQuery(document).ready(function ($) {
    resetDefaultsRoles()

    /***Request Tab Create Request Form Show/Hide ***/
    $("#recaprole .roleCreateNewlink a").click(function (e) {
        $("#recaprole .create-role-section").show();
        $("#recaprole .roles-main-section").hide();
        $("#recaprole .delete-role-section").hide();
        $("#recaprole .edit-role-section").hide();
        populatePermissionNames();
    });
    $("#recaprole .backtext a").click(function () {
        resetDefaultsRoles()
        $("#recaprole .create-role-section").hide();
        $("#recaprole .roles-main-section").show();
        $("#recaprole .delete-role-section").hide();
        $("#recaprole .edit-role-section").hide();
        $('#errorMessageId').hide();
        $("#recaprole .errorMessage").hide();
        //loadCreateRole()
    });

    $('#permissionNameId').multiselect();
    $('#deletePermissionNameId').multiselect();
    $('#editPermissionNameId').multiselect();
});

function loadCreateRole() {
    
    var $form = $('#roles-form');
    var url = $form.attr('action') + "?action=loadCreateRole";
    var role = $.ajax({
        url: url,
        type: 'post',
        data: $form.serialize(),
        success: function (response) {
            console.log("completed");
            $('#rolesContentId').html(response);
            $("#recaprole .create-role-section").show();
            $("#recaprole .roles-main-section").hide();
            $("#recaprole .delete-role-section").hide();
            $("#recaprole .edit-role-section").hide();
        }
    });
}

function populatePermissionNames(){
    var $form = $('#roles-form');
    var url = $form.attr('action') + "?action=populatePermissionName";
    var role = $.ajax({
        url: url,
        type: 'post',
        data: $form.serialize(),
        success: function (response) {
            console.log("completed");
            $('#rolesContentId').html(response);
            $("#recaprole .create-role-section").show();
            $("#recaprole .roles-main-section").hide();
            $("#recaprole .delete-role-section").hide();
            $("#recaprole .edit-role-section").hide();
        }
    });

}

function resetDefaultsRoles() {
    $('#roleNameErrorMessage').hide();
    $('#roleDescriptionErrorMessage').hide();
    $('#permissionNamesErrorMessage').hide();
    $('#editRoleNameErrorMessage').hide();
    $('#editRoleDescriptionErrorMessage').hide();
    $('#editPermissionNamesErrorMessage').hide();
}

function editResetDefault() {
    var $form = $('#roles-form');
    var url = $form.attr('action') + "?action=clearPage";
    var role = $.ajax({
        url: url,
        type: 'post',
        data: $form.serialize(),
        success: function (response) {
            console.log("completed");
            $('#rolesContentId').html(response);
            $('#roleNameErrorMessage').hide();
            $('#roleDescriptionErrorMessage').hide();
            $('#permissionNamesErrorMessage').hide();
            $('#editRoleNameErrorMessage').hide();
            $('#editRoleDescriptionErrorMessage').hide();
            $('#editPermissionNamesErrorMessage').hide();
            $("#recaprole .edit-role-section").show();
            $("#recaprole .create-role-section").hide();
            $("#recaprole .roles-main-section").hide();
            $("#recaprole .delete-role-section").hide();
            $('#editPermissionNameId').show();
            $('#editPermissionNameId').multiselect();
        }
    });
}

function createResetDefault() {
    var $form = $('#roles-form');
    var url = $form.attr('action') + "?action=clearPage";
    var role = $.ajax({
        url: url,
        type: 'post',
        data: $form.serialize(),
        success: function (response) {
            console.log("completed");
            $('#rolesContentId').html(response);
            $('#roleNameErrorMessage').hide();
            $('#roleDescriptionErrorMessage').hide();
            $('#permissionNamesErrorMessage').hide();
            $("#recaprole .edit-role-section").hide();
            $("#recaprole .create-role-section").show();
            $("#recaprole .roles-main-section").hide();
            $("#recaprole .delete-role-section").hide();

        }
    });
}

function createRole(){
    if(isValidRole()){
        loadCreateRole();
    }
}

function editRole(roleId,roleNames,roleDescription,permissionNames){

    //$('#permissionNameId').multiselect();
    console.log('Role Name  : ' + roleNames);
    console.log('Role Desc  : ' + roleDescription);
    console.log('Permission Name  : ' + permissionNames);

    var $form = $('#roles-form');
    var roleName = $('#recaprole .roleDetails').val();
    var url = $form.attr('action') + "?action=editRole";
    var role = $.ajax({
        url: url,
        type: 'post',
        data: {roleId:roleId,roleName:roleNames,roleDescription:roleDescription,permissionName:permissionNames},
        success: function (response) {
            $('#rolesContentId').html(response);
            console.log(response);
            $("#recaprole .edit-role-section").show();
            $("#recaprole .create-role-section").hide();
            $("#recaprole .roles-main-section").hide();
            $("#recaprole .delete-role-section").hide();




        }
    });
    
}

function saveEditedRole(){
    var $form = $('#roles-form');
    var url = $form.attr('action') + "?action=saveEditedRole";
    var role = $.ajax({
        url: url,
        type: 'post',
        data: $form.serialize(),
        success: function (response) {
            console.log("completed");
            $('#rolesContentId').html(response);
            $("#recaprole .edit-role-section").show();
            $("#recaprole .create-role-section").hide();
            $("#recaprole .roles-main-section").hide();
            $("#recaprole .delete-role-section").hide();
        }
    });
}

function deleteFromDb(){
    var $form = $('#roles-form');
    var url = $form.attr('action') + "?action=delete";
    var role = $.ajax({
        url: url,
        type: 'post',
        data: $form.serialize(),
        success: function (response) {
            console.log("completed");
            $('#rolesContentId').html(response);
            $("#recaprole .edit-role-section").hide();
            $("#recaprole .create-role-section").hide();
            $("#recaprole .roles-main-section").hide();
            $("#recaprole .delete-role-section").show();
        }
    });
}

function deleteRole(roleId,roleNames,roleDescription,permissionNames) {
    

    console.log('Role Name  : ' + roleNames);
    console.log('Role Desc  : ' + roleDescription);
    console.log('Permission Name  : ' + permissionNames);
    var $form = $('#roles-form');
    var roleName = $('#recaprole .roleDetails').val();
    var url = $form.attr('action') + "?action=deleteRole";
    var role = $.ajax({
        url: url,
        type: 'post',
        data: {roleId:roleId,roleName:roleNames,roleDescription:roleDescription,permissionName:permissionNames},
        success: function (response) {
            $('#rolesContentId').html(response);
            $("#recaprole .delete-role-section").show();
            $("#recaprole .create-role-section").hide();
            $("#recaprole .roles-main-section").hide();
            $("#recaprole .edit-role-section").hide();
        }
    });
    
}

function isValidRole() {
    var isValid = true;
    var roleName = $('#roleNameId').val();
    var roleDescription = $('#roleDescriptionId').val();
    var permissionName = $('#permissionNameId').val();
    console.log(permissionName);

    if (isBlankValue(roleName)) {
        $('#roleNameErrorMessage').show();
        isValid = false;
    } else {
        $('#roleNameErrorMessage').hide();
    }
    if (isBlankValue(roleDescription)) {
        $('#roleDescriptionErrorMessage').show();
        isValid = false;
    } else {
        $('#roleDescriptionErrorMessage').hide();
    }
    if (isBlankValue(permissionName)) {
        $('#permissionNamesErrorMessage').show();
        isValid = false;
    } else {
        $('#permissionNamesErrorMessage').hide();
    }
    return isValid;

   
}

function isBlankValue(value) {
    if (value == null || value == '') {
        return true;
    }
    return false;
}

