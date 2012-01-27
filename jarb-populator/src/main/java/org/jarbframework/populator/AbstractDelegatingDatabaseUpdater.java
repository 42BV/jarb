package org.jarbframework.populator;

/**
 * Database updater that run from a delegate updater.
 * @author Jeroen van Schagen
 * @since 27-1-2012
 */
public abstract class AbstractDelegatingDatabaseUpdater implements RevertableDatabaseUpdater {

    @Override
    public void update() {
    	getDelegate().update();
    }

    @Override
    public void revert() {
    	DatabaseUpdater delegate = getDelegate();
        if(delegate instanceof RevertableDatabaseUpdater) {
            ((RevertableDatabaseUpdater) delegate).revert();
        }
    }
    
    protected abstract DatabaseUpdater getDelegate();
	
}
