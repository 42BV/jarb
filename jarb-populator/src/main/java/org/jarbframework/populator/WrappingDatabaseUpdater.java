package org.jarbframework.populator;

/**
 * Database updater that wraps a delegate updater.
 * @author Jeroen van Schagen
 * @since 26-12-2011
 */
public abstract class WrappingDatabaseUpdater implements RevertableDatabaseUpdater {
    private final DatabaseUpdater delegate;
    
    /**
     * Construct a new wrapping database updater.
     * @param delegate the delegate updater
     */
    public WrappingDatabaseUpdater(DatabaseUpdater delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public void update() {
        delegate.update();
    }

    @Override
    public void revert() {
        if(delegate instanceof RevertableDatabaseUpdater) {
            ((RevertableDatabaseUpdater) delegate).revert();
        }
    }
    
    protected final DatabaseUpdater getDelegate() {
        return delegate;
    }
}
