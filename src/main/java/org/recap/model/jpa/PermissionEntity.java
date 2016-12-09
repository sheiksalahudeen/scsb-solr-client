package org.recap.model.jpa;



import javax.persistence.*;
import java.io.Serializable;

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

    public int getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(int permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getPermissionDesc() {
        return permissionDesc;
    }

    public void setPermissionDesc(String permissionDesc) {
        this.permissionDesc = permissionDesc;
    }

    @Column(name="permission_description")

    private String permissionDesc;



}
