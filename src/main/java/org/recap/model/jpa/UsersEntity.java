package org.recap.model.jpa;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by dharmendrag on 29/11/16.
 */
@Entity
@Table(name="user_t",schema="recap",catalog="")
public class UsersEntity implements Serializable{
    @Id
    @Column(name="user_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer userId;

    @Column(name="login_id")
    private String loginId;

    @Column(name="user_institution")
    private Integer institutionId;

    @Column(name="user_description")
    private String userDescription;

    @Column(name="user_emailid")
    private String emailId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED_DATE")
    private Date lastUpdatedDate;

    @Column(name = "LAST_UPDATED_BY")
    private String lastUpdatedBy;


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="user_role_t", joinColumns = {
            @JoinColumn(name="user_id",referencedColumnName = "user_id")},
            inverseJoinColumns = {
                    @JoinColumn(name="role_id",referencedColumnName = "role_id")
            })
    private List<RoleEntity> userRole;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "user_institution", insertable = false, updatable = false)
    private InstitutionEntity institutionEntity;


    /**
     * Gets user id.
     *
     * @return the user id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * Sets user id.
     *
     * @param userId the user id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * Gets login id.
     *
     * @return the login id
     */
    public String getLoginId() {
        return loginId;
    }

    /**
     * Sets login id.
     *
     * @param loginId the login id
     */
    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    /**
     * Gets user role.
     *
     * @return the user role
     */
    public List<RoleEntity> getUserRole() {
        return userRole;
    }

    /**
     * Sets user role.
     *
     * @param userRole the user role
     */
    public void setUserRole(List<RoleEntity> userRole) {
        this.userRole = userRole;
    }

    /**
     * Gets institution entity.
     *
     * @return the institution entity
     */
    public InstitutionEntity getInstitutionEntity() {
        return institutionEntity;
    }

    /**
     * Sets institution entity.
     *
     * @param institutionEntity the institution entity
     */
    public void setInstitutionEntity(InstitutionEntity institutionEntity) {
        this.institutionEntity = institutionEntity;
    }

    /**
     * Gets institution id.
     *
     * @return the institution id
     */
    public Integer getInstitutionId() {
        return institutionId;
    }

    /**
     * Sets institution id.
     *
     * @param institutionId the institution id
     */
    public void setInstitutionId(Integer institutionId) {
        this.institutionId = institutionId;
    }

    /**
     * Gets user description.
     *
     * @return the user description
     */
    public String getUserDescription() {
        return userDescription;
    }

    /**
     * Sets user description.
     *
     * @param userDescription the user description
     */
    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }

    /**
     * Gets email id.
     *
     * @return the email id
     */
    public String getEmailId() {
        return emailId;
    }

    /**
     * Sets email id.
     *
     * @param emailId the email id
     */
    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    /**
     * Gets created date.
     *
     * @return the created date
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets created date.
     *
     * @param createdDate the created date
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Gets created by.
     *
     * @return the created by
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets created by.
     *
     * @param createdBy the created by
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Gets last updated date.
     *
     * @return the last updated date
     */
    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    /**
     * Sets last updated date.
     *
     * @param lastUpdatedDate the last updated date
     */
    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    /**
     * Gets last updated by.
     *
     * @return the last updated by
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * Sets last updated by.
     *
     * @param lastUpdatedBy the last updated by
     */
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }
}