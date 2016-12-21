package org.recap.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.Ini;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.recap.config.ApacheShiroConfig;
import org.recap.model.userManagement.UserForm;
import org.recap.repository.jpa.UserDetailsRepository;
import org.recap.security.UserService;
import org.recap.security.realm.SimpleAuthorizationRealm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Created by dharmendrag on 13/12/16.
 */
public class LoginControllerUT extends BaseControllerUT {
    protected DefaultSecurityManager securityManager = null;
    protected AuthorizingRealm realm;
    protected final String username = "jhon";
    protected final String plainTextPassword = "welcome";
    protected final String testRole = "testRole";
    protected final String testPermissionString = "testDomain:testTarget:testAction";

    // Maps keyed on test method name so setup/teardown can manage per test resources
    protected HashMap<String, SimpleAuthorizationRealm> realmMap = new HashMap<String, SimpleAuthorizationRealm>();

    @Mock
    Model model;

    @Mock
    BindingResult bindingResult;

    @Mock
    HttpServletRequest request;

    @Autowired
    ApacheShiroConfig apacheShiroConfig;

    @Autowired
    SimpleAuthorizationRealm simpleAuthorizationRealm ;

    @Autowired
    LoginController loginController;

    @Autowired
    UserDetailsRepository userRepo;

    @Mock
    UserService userService;


    @Rule
    public TestName name = new TestName();

    LoginController loginControl=new LoginController();



    private UserForm getUserForm(){
        UserForm user=new UserForm();
        user.setUsername("jhon");
        user.setPassword("admin1234");
        user.setInstitution("PUL");
        return user;
    }


    @Before
    public void setup() {
        ThreadContext.remove();
        MockitoAnnotations.initMocks(this);


        Ini config = new Ini();
        config.setSectionProperty("main", "myRealm", "org.recap.security.realm.SimpleAuthorizationRealm");
        config.setSectionProperty("main", "myRealmCredentialsMatcher", "org.apache.shiro.authc.credential.SimpleCredentialsMatcher");
        config.setSectionProperty("main", "myRealm.credentialsMatcher", "$myRealmCredentialsMatcher");
        config.setSectionProperty("main", "securityManager.sessionManager.sessionValidationSchedulerEnabled", "false");

        IniSecurityManagerFactory factory = new IniSecurityManagerFactory(config);
        securityManager = (DefaultSecurityManager) factory.createInstance();
        SecurityUtils.setSecurityManager(securityManager);

        // Create a realm for the test
        createRealm(name.getMethodName());
        this.mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
    }


    @Test
    public void loginScreen()throws Exception{
        MvcResult mvcResult = this.mockMvc.perform(get("/")).andReturn();
        String reponse = mvcResult.getResponse().getContentAsString();
        assertNotNull(reponse);
        int status = mvcResult.getResponse().getStatus();
        assertTrue(status == 200);
    }

    @Test
    public void testUserLoginSuccess() throws Exception {
        String view = loginController.createSession(getUserForm(),request,model,bindingResult);
        assertNotNull(view);
        assertEquals("/search",view);
    }


    @After
    public void tearDown() {
        final String testName = name.getMethodName();
        shutDown(testName);
        SecurityUtils.setSecurityManager(null);
        securityManager.destroy();
        ThreadContext.remove();
    }



    @Test
    public void testUserWrongPassword() throws Exception {
        String testMethodName = name.getMethodName();
        SimpleAuthorizationRealm realm = realmMap.get(testMethodName);

        Subject.Builder builder = new Subject.Builder(securityManager);
        Subject currentUser = builder.buildSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, "passwrd");
        try {
            currentUser.login(token);
        } catch (IncorrectCredentialsException ex) {
            // Expected
        }
    }


    /**
     * Creates a realm for a test method and puts it in the realMap.
     */
    protected void createRealm(String testMethodName) {
        SimpleAuthorizationRealm realm = (SimpleAuthorizationRealm) securityManager.getRealms().iterator().next();
        realmMap.put(testMethodName, realm);
    }

    /**
     * Shuts down the database and removes the realm from the realm map.
     */
    protected void shutDown(String testName) {

        realmMap.remove(testName);

    }


}
