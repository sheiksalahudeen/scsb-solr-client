package org.recap.service.userManagement;

import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.RoleEntity;
import org.recap.model.jpa.UsersEntity;
import org.recap.model.userManagement.UserRoleForm;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.RolesDetailsRepositorty;
import org.recap.repository.jpa.UserDetailsRepository;
import org.recap.security.UserManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dharmendrag on 23/12/16.
 */
@Service
public class UserRoleServiceImpl implements UserRoleService{

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private RolesDetailsRepositorty rolesDetailsRepositorty;

    @Autowired
    private InstitutionDetailsRepository institutionDetailsRepository;

    public Page<UsersEntity> searchUsers(UserRoleForm userRoleForm,boolean superAdmin)
    {
        Pageable pageable=new PageRequest(userRoleForm.getPageNumber(), userRoleForm.getPageSize(), Sort.Direction.ASC, UserManagement.USER_ID);
        if(superAdmin)
        {
            return userDetailsRepository.findAll(pageable);
        }else{
            InstitutionEntity institutionEntity=new InstitutionEntity();
            institutionEntity.setInstitutionId(userRoleForm.getInstitutionId());
            return userDetailsRepository.findByInstitutionEntity(institutionEntity,pageable);
        }

    }

    public Page<UsersEntity> searchByNetworkId(UserRoleForm userRoleForm,boolean superAdmin)
    {
        Pageable pageable=new PageRequest(userRoleForm.getPageNumber(), userRoleForm.getPageSize(), Sort.Direction.ASC, UserManagement.USER_ID);
        if(superAdmin)
        {
            return userDetailsRepository.findByLoginId(userRoleForm.getSearchNetworkId(),pageable);
        }else
        {
            InstitutionEntity institutionEntity=new InstitutionEntity();
            institutionEntity.setInstitutionId(userRoleForm.getInstitutionId());
            return userDetailsRepository.findByLoginIdAndInstitutionEntity(userRoleForm.getSearchNetworkId(),institutionEntity,pageable);
        }
    }

    public Page<RoleEntity> searchByRoleName(UserRoleForm userRoleForm,boolean superAdmin)
    {
        Pageable pageable=new PageRequest(userRoleForm.getPageNumber(), userRoleForm.getPageSize(), Sort.Direction.ASC, UserManagement.ROLE_ID);
        return rolesDetailsRepositorty.findByRoleName(userRoleForm.getRoleName(),pageable);
    }

    public List<Object> getRoles(Integer superAdminRole)
    {
        List<Object> rolesList=new ArrayList<Object>();

        List<RoleEntity> roleEntities=rolesDetailsRepositorty.findAll();

        for(RoleEntity roleEntity:roleEntities)
        {
            if(!superAdminRole.equals(roleEntity.getRoleId())) {
                Object[] role = new Object[2];
                role[0] = roleEntity.getRoleId();
                role[1] = roleEntity.getRoleName();
                rolesList.add(role);
            }
        }

        return rolesList;
    }

    public List<Object> getInstitutions(boolean isSuperAdmin,Integer loginInstitutionId)
    {
        List<Object> institutions=new ArrayList<Object>();

        Iterable<InstitutionEntity> institutionsList=institutionDetailsRepository.findAll();

        for(InstitutionEntity institutionEntity:institutionsList)
        {
            if(isSuperAdmin || loginInstitutionId.equals(institutionEntity.getInstitutionId())) {
                Object[] inst = new Object[2];
                inst[0] = institutionEntity.getInstitutionId();
                inst[1] = institutionEntity.getInstitutionCode();
                institutions.add(inst);
            }
        }

        return institutions;

    }
}
