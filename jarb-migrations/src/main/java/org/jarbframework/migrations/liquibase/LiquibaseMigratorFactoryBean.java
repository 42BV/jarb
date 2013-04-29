package org.jarbframework.migrations.liquibase;

import org.jarbframework.utils.spring.SingletonFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

/**
 * Creates a liquibase migrator bean, based on a base- and change log path.
 * @author Jeroen van Schagen
 */
public class LiquibaseMigratorFactoryBean extends SingletonFactoryBean<LiquibaseMigrator> {

    private String basePath;
    
    private String changeLogPath;
    
    @Autowired
    private ResourceLoader resourceLoader;
    
    @Override
    protected LiquibaseMigrator createObject() throws Exception {
        SpringResourceAccessor resourceAccessor = createResourceAccessor();
        
        LiquibaseMigrator migrator = new LiquibaseMigrator(resourceAccessor);
        migrator.setChangeLogPath(changeLogPath);
        return migrator;
    }

    private SpringResourceAccessor createResourceAccessor() {
        SpringResourceAccessor resourceAccessor = new SpringResourceAccessor();
        resourceAccessor.setResourceLoader(resourceLoader);
        resourceAccessor.setBasePath(basePath);
        return resourceAccessor;
    }
    
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }
    
    public void setChangeLogPath(String changeLogPath) {
        this.changeLogPath = changeLogPath;
    }

}
