package org.jarbframework.populator;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class ClassNameDatabaseUpdater extends DelegatingDatabaseUpdater {

    /** Full name of the database updater class that should run. **/
    private final String className;

    @Autowired
    private ApplicationContext applicationContext;

    /** Delegate that runs the updates. **/
    private DatabaseUpdater delegate;

    public ClassNameDatabaseUpdater(String className) {
        this.className = className;
    }

    protected DatabaseUpdater getDelegate() {
        if (delegate == null) {
            delegate = instantiate();
        }
        return delegate;
    }

    private DatabaseUpdater instantiate() {
    	DatabaseUpdater updater = null;
    	if(isNotBlank(className)) {
            Class<? extends DatabaseUpdater> updaterClass = resolveUpdaterClass();
            updater = applicationContext.getAutowireCapableBeanFactory().createBean(updaterClass);
    	}
        return updater;
    }

    private Class<? extends DatabaseUpdater> resolveUpdaterClass() {
        Class<? extends DatabaseUpdater> updaterClass;
        try {
            updaterClass = Class.forName(className).asSubclass(DatabaseUpdater.class);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return updaterClass;
    }

}
