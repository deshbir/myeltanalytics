package myeltanalytics.service;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service(value="applicationContextProvider")
public class ApplicationContextProvider implements ApplicationContextAware {
    private static ApplicationContext appContext;

    public void setApplicationContext(ApplicationContext context) throws BeansException {
        appContext = context;
    }
    
    public static ApplicationContext getApplicationContext() {
      return appContext;
    }
}