package org.jarbframework.populator;

/**
 * Updater that reverts a delegate updater on execution.
 * This updater should be used to execute revert actions.
 * @author Jeroen van Schagen
 * @since 26-12-2011
 */
public class RevertingDatabaseUpdater implements DatabaseUpdater {

    /** Delegate updater to revert on execution. **/
    private final RevertableDatabaseUpdater delegate;

    /**
     * Construct a new {@link RevertingDatabaseUpdater}.
     * @param delegate the delegate updater to revert on execution
     */
    public RevertingDatabaseUpdater(RevertableDatabaseUpdater delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public void update() {
        delegate.revert();
    }
    
}
