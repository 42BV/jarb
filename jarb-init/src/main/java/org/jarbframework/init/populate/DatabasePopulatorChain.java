package org.jarbframework.init.populate;

import java.util.Collection;
import java.util.LinkedList;

import org.jarbframework.init.DatabasePopulator;

/**
 * Chain of database populators, executes the population in sequence.
 *
 * @author Jeroen van Schagen
 * @since Apr 10, 2015
 */
public class DatabasePopulatorChain implements DatabasePopulator {

    /**
     * Delegate database populate elements.
     */
    private Collection<DatabasePopulator> populators = new LinkedList<DatabasePopulator>();
    
    public DatabasePopulatorChain() {
        // Do nothing
    }
    
    public DatabasePopulatorChain(DatabasePopulator populator) {
        this.populators.add(populator);
    }

    public DatabasePopulatorChain(Collection<DatabasePopulator> populators) {
        this.populators.addAll(populators);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        for (DatabasePopulator populator : populators) {
            populator.execute();
        }
    }
    
    /**
     * Add another populate to this chain.
     * 
     * @param populator the populate to add
     * @return {@code this} instance for chaining
     */
    public DatabasePopulatorChain add(DatabasePopulator populator) {
        populators.add(populator);
        return this;
    }

    /**
     * Checks if the chain is empty.
     * 
     * @return {@code true} if empty
     */
    public boolean isEmpty() {
        return populators.isEmpty();
    }

}
