package org.jarbframework.populator;

/**
 * Database updater that run from a delegate updater.
 * @author Jeroen van Schagen
 * @since 27-1-2012
 */
public abstract class DelegatingDatabaseUpdater implements RevertableDatabaseUpdater {

    protected abstract DatabaseUpdater getDelegate();
	
    @Override
    public void update() {
        DatabaseUpdater delegate = getDelegate();
        if(delegate != null) {
        	delegate.update();
        }
    }

    @Override
    public void revert() {
        DatabaseUpdater delegate = getDelegate();
        if (delegate instanceof RevertableDatabaseUpdater) {
            ((RevertableDatabaseUpdater) delegate).revert();
        }
    }

}
