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

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    public Set<PermissionEntity> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<PermissionEntity> permissions) {
        this.permissions = permissions;
    }
}
