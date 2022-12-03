package guru.springframework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

// https://stackoverflow.com/questions/48212761/how-to-log-all-active-properties-of-a-spring-boot-application-before-the-beans-i
public class PropertiesLogger implements ApplicationListener<ApplicationPreparedEvent> {
    private static final Logger log = LoggerFactory.getLogger(PropertiesLogger.class);

    private ConfigurableEnvironment environment;
    private boolean printed = false;

    public static ConfigurableApplicationContext runWithPropertiesLogger(Class<?> primarySource, String... args) {
        SpringApplication springApplication = new SpringApplication(new Class[]{primarySource});
        springApplication.addListeners(new PropertiesLogger());
        return springApplication.run(args);
    }

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        if (printed) {
            return;
        }
        environment = event.getApplicationContext().getEnvironment();
        printProperties();
        printed = true;
    }

    public void printProperties() {
        log.info("======= PRINT PROPERTIES: start =======");
        for (EnumerablePropertySource propertySource : findPropertiesPropertySources()) {
            log.info("******* " + propertySource.getName() + " *******");
            String[] propertyNames = propertySource.getPropertyNames();
            Arrays.sort(propertyNames);
            for (String propertyName : propertyNames) {
                String resolvedProperty = environment.getProperty(propertyName);
                String sourceProperty = propertySource.getProperty(propertyName).toString();
                if(resolvedProperty.equals(sourceProperty)) {
                    log.info("{}={}", propertyName, resolvedProperty);
                }else {
                    log.info("{}={} OVERRIDDEN to {}", propertyName, sourceProperty, resolvedProperty);
                }
            }
        }
        log.info("======= PRINT PROPERTIES: end =======");
    }

    private List<EnumerablePropertySource> findPropertiesPropertySources() {
        List<EnumerablePropertySource> propertiesPropertySources = new LinkedList<>();
        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            if (propertySource instanceof EnumerablePropertySource) {
                propertiesPropertySources.add((EnumerablePropertySource) propertySource);
            }
        }
        return propertiesPropertySources;
    }
}