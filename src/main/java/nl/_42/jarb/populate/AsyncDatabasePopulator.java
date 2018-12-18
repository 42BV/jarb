/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.populate;



/**
 * Database populator that runs asynchronously on a seperate thread.
 * This makes the application start-up faster, when population is slow.
 *
 * @author Jeroen van Schagen
 * @since Mar 4, 2014
 */
public class AsyncDatabasePopulator implements DatabasePopulator {
    
    private final Runnable task;
    
    public AsyncDatabasePopulator(DatabasePopulator populator) {
        this.task = new DatabasePopulatorRunnable(populator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        Thread thread = new Thread(task);
        thread.start();
    }
    
    private static class DatabasePopulatorRunnable implements Runnable {

        private final DatabasePopulator populator;
        
        public DatabasePopulatorRunnable(DatabasePopulator populator) {
            this.populator = populator;
        }

        @Override
        public void run() {
            populator.execute();
        }

    }
    
}
