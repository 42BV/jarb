package org.jarbframework.populator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class ClassNameDatabaseUpdater extends AbstractDelegatingDatabaseUpdater {
	
	/** Name of the database updater that should run. **/
	private final String className;
	
	/** Application context used to create the updater. **/
	@Autowired private ApplicationContext applicationContext;

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
		Class<?> updaterClass = getUpdaterClass(); // Ensures the class is valid before accessing factory
		return (DatabaseUpdater) applicationContext.getAutowireCapableBeanFactory().createBean(updaterClass);
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

}
