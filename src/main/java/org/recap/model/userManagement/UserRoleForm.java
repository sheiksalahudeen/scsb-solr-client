package org.recap.model.userManagement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dharmendrag on 23/12/16.
 */
public class UserRoleForm {

    private int userId;

    private String searchNetworkId;

    private String networkLoginId;

    private int institutionId;

    private int[] roleId;

    private String roleName;

    private String totalRecordsCount = "0";

    private Integer pageNumber = 0;

    private Integer pageSize = 10;

    private Integer totalPageCount = 0;

    private String institutionName;

    private String message;

    private boolean allowCreateEdit;

    private List<Object> roles=new ArrayList<Object>();

    private List<Object> institutions=new ArrayList<Object>();

    private String sectionName;

    private String buttonName;

    private String editNetworkId;

    private int[] editRoleId;

    private int editInstitutionId;

    private int editUserId;

    private boolean isCreatedRequest;

    public boolean isCreatedRequest() {
        return isCreatedRequest;
    }

    public void setCreatedRequest(boolean createdRequest) {
        isCreatedRequest = createdRequest;
    }

    public String getSearchNetworkId() {
        return searchNetworkId;
    }

    public void setSearchNetworkId(String searchNetworkId) {
        this.searchNetworkId = searchNetworkId;
    }

    public String getEditNetworkId() {
        return editNetworkId;
    }

    public void setEditNetworkId(String editNetworkId) {
        this.editNetworkId = editNetworkId;
    }

    public int[] getEditRoleId() {
        return editRoleId;
    }

    public void setEditRoleId(int[] editRoleId) {
        this.editRoleId = editRoleId;
    }

    public int getEditInstitutionId() {
        return editInstitutionId;
    }

    public void setEditInstitutionId(int editInstitutionId) {
        this.editInstitutionId = editInstitutionId;
    }

    public List<Object> getRoles() {
        return roles;
    }

    public void setRoles(List<Object> roles) {
        this.roles = roles;
    }

    public List<Object> getInstitutions() {
        return institutions;
    }

    public void setInstitutions(List<Object> institutions) {
        this.institutions = institutions;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getButtonName() {
        return buttonName;
    }

    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    public boolean isAllowCreateEdit() {
        return allowCreateEdit;
    }

    public void setAllowCreateEdit(boolean allowCreateEdit) {
        this.allowCreateEdit = allowCreateEdit;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    private boolean selected;

    private boolean submitted ;
    private boolean showResults ;

    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    public boolean isShowResults() {
        return showResults;
    }

    public void setShowResults(boolean showResults) {
        this.showResults = showResults;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    private List<UserRoleForm> userRoleFormList=new ArrayList<UserRoleForm>();

    public List<UserRoleForm> getUserRoleFormList() {
        return userRoleFormList;
    }

    public void setUserRoleFormList(List<UserRoleForm> userRoleFormList) {
        this.userRoleFormList = userRoleFormList;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEditUserId() {
        return editUserId;
    }

    public void setEditUserId(int editUserId) {
        this.editUserId = editUserId;
    }

    public String getNetworkLoginId() {
        return networkLoginId;
    }

    public void setNetworkLoginId(String networkLoginId) {
        this.networkLoginId = networkLoginId;
    }

    public int getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(int institutionId) {
        this.institutionId = institutionId;
    }

    public int[] getRoleId() {
        return roleId;
    }

    public void setRoleId(int[] roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getTotalRecordsCount() {
        return totalRecordsCount;
    }

    public void setTotalRecordsCount(String totalRecordsCount) {
        this.totalRecordsCount = totalRecordsCount;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalPageCount() {
        return totalPageCount;
    }

    public void setTotalPageCount(Integer totalPageCount) {
        this.totalPageCount = totalPageCount;
    }

    public void resetPageNumber(){this.pageNumber=0;}
}
