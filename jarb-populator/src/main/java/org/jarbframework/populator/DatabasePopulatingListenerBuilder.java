/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.populator;

/**
 * Builds a database populating listener. 
 *
 * @author jeroen
 * @since Feb 12, 2014
 */
public class DatabasePopulatingListenerBuilder {
    
    private final DatabasePopulatingListener listener = new DatabasePopulatingListener();
    
    /**
     * Configure the initializer.
     * 
     * @return the initialize appender
     */
    public PopulatorAppendCommand initializer() {
        return new PopulatorAppendCommand() {
            
            @Override
            protected void handle(DatabasePopulatorChain initializer) {
                listener.setInitializer(initializer);
            }
            
        };
    }
    
    /**
     * Configure the destroyer.
     * 
     * @return the destroy appender
     */
    public PopulatorAppendCommand destroyer() {
        return new PopulatorAppendCommand() {
            
            @Override
            protected void handle(DatabasePopulatorChain destroyer) {
                listener.setDestroyer(destroyer);
            }
            
        };
    }
    
    /**
     * Return the created populating listener.
     * 
     * @return the created listener
     */
    public DatabasePopulatingListener build() {
        return listener;
    }
    
    /**
     * Appends the populator to the listener.
     *
     * @author Jeroen van Schagen
     * @since Feb 12, 2014
     */
    public abstract class PopulatorAppendCommand {
        
        private DatabasePopulatorChain populators = new DatabasePopulatorChain();

        public PopulatorAppendCommand add(DatabasePopulator populator) {
            populators.add(populator);
            return this;
        }

        /**
         * Finish appending populators and return to the listener builder.
         * 
         * @return the listener builder
         */
        public DatabasePopulatingListenerBuilder done() {
            handle(populators);
            return DatabasePopulatingListenerBuilder.this;
        }
        
        /**
         * Finish appending populators and build the listener.
         * 
         * @return the listener
         */
        public DatabasePopulatingListener build() {
            return DatabasePopulatingListenerBuilder.this.build();
        }

        protected abstract void handle(DatabasePopulatorChain populator);

    }

}
