package org.recap.model.jpa;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by dharmendrag on 29/11/16.
 */

@Entity
@Table(name="users_t",schema="recap",catalog="")
public class UsersEntity implements Serializable{
    @Id
    @Column(name="user_id")
    private Integer userId;

    @Column(name="login_id")
    private String loginId;

    @Column(name="user_password")
    private String passWord;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="user_role_t", joinColumns = {
            @JoinColumn(name="user_id",referencedColumnName = "user_id")},
            inverseJoinColumns = {
                    @JoinColumn(name="role_id",referencedColumnName = "role_id")
            })
    private List<RoleEntity> userRole;

    /*@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "institution", insertable = false, updatable = false)
    private InstitutionEntity institutionEntity;*/


    @Column(name="user_institution")
    private String institutionId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public List<RoleEntity> getUserRole() {
        return userRole;
    }

    public void setUserRole(List<RoleEntity> userRole) {
        this.userRole = userRole;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }







}
