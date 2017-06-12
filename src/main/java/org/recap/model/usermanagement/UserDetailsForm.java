package org.recap.model.usermanagement;

/**
 * Created by dharmendrag on 28/12/16.
 */
public class UserDetailsForm {

    private Integer loginInstitutionId;

    private boolean superAdmin;

    private boolean recapUser;

    /**
     * Gets login institution id.
     *
     * @return the login institution id
     */
    public Integer getLoginInstitutionId() {
        return loginInstitutionId;
    }

    /**
     * Sets login institution id.
     *
     * @param loginInstitutionId the login institution id
     */
    public void setLoginInstitutionId(Integer loginInstitutionId) {
        this.loginInstitutionId = loginInstitutionId;
    }

    /**
     * Is super admin boolean.
     *
     * @return the boolean
     */
    public boolean isSuperAdmin() {
        return superAdmin;
    }

    /**
     * Sets super admin.
     *
     * @param superAdmin the super admin
     */
    public void setSuperAdmin(boolean superAdmin) {
        this.superAdmin = superAdmin;
    }

    /**
     * Is recap user boolean.
     *
     * @return the boolean
     */
    public boolean isRecapUser() {
        return recapUser;
    }

    /**
     * Sets recap user.
     *
     * @param recapUser the recap user
     */
    public void setRecapUser(boolean recapUser) {
        this.recapUser = recapUser;
    }
}
