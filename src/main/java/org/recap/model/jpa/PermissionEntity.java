package org.recap.model.jpa;



import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by dharmendrag on 29/11/16.
 */
@Cacheable(true)
@Entity
@Table(name="permissions_t",schema="recap",catalog="")
public class PermissionEntity implements Serializable{
    @Id
    @Column(name="permission_id")
    private int permissionId;

    @Column(name="permission_name")
    private String permissionName;

    @Column(name="permission_description")
    private String permissionDesc;

    @ManyToMany(mappedBy ="permissions")
    private List<RoleEntity> roleEntityList;

    /**
     * Gets permission id.
     *
     * @return the permission id
     */
    public int getPermissionId() {
        return permissionId;
    }

    /**
     * Sets permission id.
     *
     * @param permissionId the permission id
     */
    public void setPermissionId(int permissionId) {
        this.permissionId = permissionId;
    }

    /**
     * Gets permission name.
     *
     * @return the permission name
     */
    public String getPermissionName() {
        return permissionName;
    }

    /**
     * Sets permission name.
     *
     * @param permissionName the permission name
     */
    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    /**
     * Gets permission desc.
     *
     * @return the permission desc
     */
    public String getPermissionDesc() {
        return permissionDesc;
    }

    /**
     * Sets permission desc.
     *
     * @param permissionDesc the permission desc
     */
    public void setPermissionDesc(String permissionDesc) {
        this.permissionDesc = permissionDesc;
    }

    /**
     * Gets role entity list.
     *
     * @return the role entity list
     */
    public List<RoleEntity> getRoleEntityList() {
        return roleEntityList;
    }

    /**
     * Sets role entity list.
     *
     * @param roleEntityList the role entity list
     */
    public void setRoleEntityList(List<RoleEntity> roleEntityList) {
        this.roleEntityList = roleEntityList;
    }





}
