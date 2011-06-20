package org.jarb.populator;

import javax.annotation.PostConstruct;

import org.springframework.util.Assert;

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
     * Construct a new {@link DatabasePopulatorExecutor}.
     * @param populator database populator being invoked
     */
    public DatabasePopulatorExecutor(DatabasePopulator populator) {
        Assert.notNull(populator, "Database populator cannot be null");
        this.populator = populator;
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
