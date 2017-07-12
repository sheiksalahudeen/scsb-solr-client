package org.recap.spring;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 12/7/17.
 */
public class ApplicationContextProviderUT extends BaseTestCase{

    @Autowired
    ApplicationContextProvider applicationContextProvider;

    @Autowired
    private ApplicationContext context;

    @Test
    public void testApplicationContextProvider(){
        applicationContextProvider.getInstance();
        applicationContextProvider.setApplicationContext(context);
        ApplicationContext context = applicationContextProvider.getApplicationContext();
        assertNotNull(context);
    }

}