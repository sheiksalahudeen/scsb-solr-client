package org.recap.security;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.userManagement.UserForm;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.recap.security.UserManagement.userAndInstitution;

/**
 * Created by dharmendrag on 21/12/16.
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService {


    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private InstitutionDetailsRepository institutionDetailsRepository;

    public UserDetailsRepository getUserDetailsRepository() {
        return userDetailsRepository;
    }

    public void setUserDetailsRepository(UserDetailsRepository userDetailsRepository) {
        this.userDetailsRepository = userDetailsRepository;
    }

    public InstitutionDetailsRepository getInstitutionDetailsRepository() {
        return institutionDetailsRepository;
    }

    public void setInstitutionDetailsRepository(InstitutionDetailsRepository institutionDetailsRepository) {
        this.institutionDetailsRepository = institutionDetailsRepository;
    }

    public UserForm doAuthentication(UsernamePasswordToken token) throws Exception {
        UserForm userForm = new UserForm();
        String[] user = userAndInstitution(token.getUsername());
        userForm.setUsername(user[0]);
        userForm.setInstitution(Integer.valueOf(user[1]));
        userForm = (getCredential(Integer.valueOf(user[1]), user[0], userForm));
        return userForm;
    }

    private UserForm getCredential(Integer institution,String username,UserForm userForm) throws Exception {
        List<InstitutionEntity> institutionEntityList= (List<InstitutionEntity>)institutionDetailsRepository.findAll();
        InstitutionEntity institutionEntity=new InstitutionEntity();
        institutionEntity.setInstitutionId(institution);
        if(institutionEntityList.get(0).getInstitutionId().equals(institution))
        {
            userForm=UserManagement.toUserForm(userDetailsRepository.findByLoginIdAndInstitutionEntity(username,institutionEntity), userForm);
            userForm.setPasswordMatcher(true);
        }else if(institutionEntityList.get(1).getInstitutionId().equals(institution))
        {
            userForm=UserManagement.toUserForm(userDetailsRepository.findByLoginIdAndInstitutionEntity(username,institutionEntity), userForm);
            userForm.setPasswordMatcher(true);

        }else if(institutionEntityList.get(2).getInstitutionId().equals(institution))
        {
            userForm=UserManagement.toUserForm(userDetailsRepository.findByLoginIdAndInstitutionEntity(username,institutionEntity), userForm);
            userForm.setPasswordMatcher(true);
        }

        return userForm;
    }

}
