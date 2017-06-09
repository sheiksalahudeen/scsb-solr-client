package org.recap.model.jpa;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by dharmendrag on 29/11/16.
 */
@Entity
@Table(name="roles_t",schema="recap",catalog="")
public class RoleEntity {
    @Id
    @Column(name="role_id")
    private int roleId;

    @Column(name="role_name")
    private String roleName;

    @Column(name="role_description")
    private String roleDescription;

    @ElementCollection(targetClass = PermissionEntity.class)
    @JoinTable(name="role_permission_t",joinColumns = {
            @JoinColumn(name="role_id",referencedColumnName = "role_id")},
            inverseJoinColumns = {
                    @JoinColumn(name="permission_id",referencedColumnName = "permission_id")
            })
    private Set<PermissionEntity> permissions;


    @ElementCollection(targetClass = UsersEntity.class)
    @JoinTable(name="user_role_t",joinColumns = {
           @JoinColumn(name="role_id",referencedColumnName = "role_id")},
    inverseJoinColumns = {@JoinColumn(name="user_id",referencedColumnName = "user_id")})
    private Set<UsersEntity> users;

    /**
     * Gets users.
     *
     * @return the users
     */
    public Set<UsersEntity> getUsers() {
        return users;
    }

    /**
     * Sets users.
     *
     * @param users the users
     */
    public void setUsers(Set<UsersEntity> users) {
        this.users = users;
    }

    /**
     * Gets role id.
     *
     * @return the role id
     */
    public int getRoleId() {
        return roleId;
    }

    /**
     * Sets role id.
     *
     * @param roleId the role id
     */
    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    /**
     * Gets role name.
     *
     * @return the role name
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * Sets role name.
     *
     * @param roleName the role name
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * Gets role description.
     *
     * @return the role description
     */
    public String getRoleDescription() {
        return roleDescription;
    }

    /**
     * Sets role description.
     *
     * @param roleDescription the role description
     */
    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    /**
     * Gets permissions.
     *
     * @return the permissions
     */
    public Set<PermissionEntity> getPermissions() {
        return permissions;
    }

    /**
     * Sets permissions.
     *
     * @param permissions the permissions
     */
    public void setPermissions(Set<PermissionEntity> permissions) {
        this.permissions = permissions;
    }
}
