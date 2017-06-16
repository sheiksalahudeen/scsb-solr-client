package org.recap.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by sheiks on 26/05/17.
 */
@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    private static ApplicationContext context;

    private static ApplicationContextProvider applicationContextProvider;

    public static ApplicationContextProvider getInstance(){
        if(null == applicationContextProvider){
            applicationContextProvider = new ApplicationContextProvider();
        }
        return applicationContextProvider;
    }

    private ApplicationContextProvider(){

    }

    public ApplicationContext getApplicationContext() {
        return context;
    }

    public void setApplicationContext(ApplicationContext ac)
            throws BeansException {
        context = ac;
    }
}
