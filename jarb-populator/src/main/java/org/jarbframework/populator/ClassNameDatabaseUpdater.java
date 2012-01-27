package org.jarbframework.populator;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ClassNameDatabaseUpdater extends AbstractDelegatingDatabaseUpdater implements ApplicationContextAware {
	
	/** Name of the database updater that should run. **/
	private final String className;
	
	/** Application context used to create the updater. **/
	private ApplicationContext applicationContext;

	/** Delegate that runs the updates. **/
	private DatabaseUpdater delegate;

	public ClassNameDatabaseUpdater(String className) {
		this.className = className;
	}
	
	protected DatabaseUpdater getDelegate() {
		if(delegate == null) {
			delegate = instantiate();
		}
		return delegate;
	}
	
	private DatabaseUpdater instantiate() {
		return (DatabaseUpdater) applicationContext.getAutowireCapableBeanFactory().createBean(getUpdaterClass());
	}

	private Class<?> getUpdaterClass() {
		Class<?> updaterClass;
		try {
			updaterClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return updaterClass;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
