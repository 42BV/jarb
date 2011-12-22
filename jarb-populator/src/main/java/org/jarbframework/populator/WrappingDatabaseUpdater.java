package org.jarbframework.populator;

public abstract class WrappingDatabaseUpdater implements DatabaseUpdater, Revertable {
    private DatabaseUpdater delegate;
    
    public WrappingDatabaseUpdater(DatabaseUpdater delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public void update() throws Exception {
        delegate.update();
    }

    @Override
    public void revert() throws Exception {
        if(delegate instanceof Revertable) {
            ((Revertable) delegate).revert();
        }
    }
    
    protected final DatabaseUpdater getDelegate() {
        return delegate;
    }
}
