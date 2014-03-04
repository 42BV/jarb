package org.jarbframework.populator;

import java.util.Collection;
import java.util.LinkedList;

public class DatabasePopulatorChain implements DatabasePopulator {

    /**
     * Delegate database populate elements.
     */
    private Collection<DatabasePopulator> populators = new LinkedList<DatabasePopulator>();
    
    public DatabasePopulatorChain() {
    }
    
    public DatabasePopulatorChain(DatabasePopulator populator) {
        this.populators.add(populator);
    }

    public DatabasePopulatorChain(Collection<DatabasePopulator> populators) {
        this.populators.addAll(populators);
    }
    
    @Override
    public void populate() {
        for (DatabasePopulator populator : populators) {
            populator.populate();
        }
    }
    
    /**
     * Add another populate to this chain.
     * @param populator the populate to add
     * @return {@code this} instance for chaining
     */
    public DatabasePopulatorChain add(DatabasePopulator populator) {
        populators.add(populator);
        return this;
    }

    public boolean isEmpty() {
        return populators.isEmpty();
    }

}
