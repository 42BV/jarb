package org.jarbframework.populator;

import static org.jarbframework.utils.Asserts.notNull;

import java.util.Collection;

import javax.annotation.PostConstruct;

/**
 * Perform database populating post construct.
 * 
 * @see DatabasePopulator
 * 
 * @author Jeroen van Schagen
 * @since 20-06-2011
 */
public final class DatabasePopulatorExecutor {
    /** Database populator being invoked. **/
    private final DatabasePopulator populator;
    
    /**
     * Construct a new {@link DatabasePopulatorExecutor} that executes one populator.
     * @param populator database populator being invoked
     */
    public DatabasePopulatorExecutor(DatabasePopulator populator) {
        this.populator = notNull(populator, "Populator cannot be null");
    }
    
    /**
     * Construct a new {@link DatabasePopulatorExecutor} that executes a collection of populators.
     * @param populators database popoulators being invoked
     */
    public DatabasePopulatorExecutor(Collection<DatabasePopulator> populators) {
        this(new CompoundDatabasePopulator(populators));
    }
    
    /**
     * Perform the actual database populating.
     */
    @PostConstruct
    public void execute() {
        try {
            populator.populate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
