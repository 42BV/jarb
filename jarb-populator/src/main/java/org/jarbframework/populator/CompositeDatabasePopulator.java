package org.jarbframework.populator;

import java.util.Collection;
import java.util.LinkedList;

public class CompositeDatabasePopulator implements DatabasePopulator {

    /**
     * Delegate populators, run in the sequence they were inserted.
     */
    private Collection<DatabasePopulator> populators = new LinkedList<DatabasePopulator>();
    
    public CompositeDatabasePopulator() {
        // Start with empty collection, use add method
    }
    
    public CompositeDatabasePopulator(Collection<DatabasePopulator> populators) {
        this.populators.addAll(populators);
    }
    
    @Override
    public void populate() {
        for (DatabasePopulator populator : populators) {
            populator.populate();
        }
    }
    
    public CompositeDatabasePopulator add(DatabasePopulator populator) {
        populators.add(populator);
        return this;
    }

}
