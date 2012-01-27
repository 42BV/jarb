package org.jarbframework.populator;

/**
 * Database updater that wraps a delegate updater.
 * @author Jeroen van Schagen
 * @since 26-12-2011
 */
public class DelegatingDatabaseUpdater extends AbstractDelegatingDatabaseUpdater {
	
	/** Delegate that runs the updates. **/
    private final DatabaseUpdater delegate;
    
    /**
     * Construct a new wrapping database updater.
     * @param delegate the delegate updater
     */
    public DelegatingDatabaseUpdater(DatabaseUpdater delegate) {
        this.delegate = delegate;
    }
    
    protected final DatabaseUpdater getDelegate() {
        return delegate;
    }
    
}
