/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.populator.listener;

import org.jarbframework.populator.AsyncDatabasePopulator;
import org.jarbframework.populator.DatabasePopulator;
import org.jarbframework.populator.DatabasePopulatorChain;

/**
 * Builds a database populating listener. 
 *
 * @author Jeroen van Schagen
 * @since Feb 12, 2014
 */
public class PopulateApplicationListenerBuilder {
    
    /**
     * The listener being build.
     */
    private final PopulateApplicationListener listener = new PopulateApplicationListener();
    
    /**
     * Configure the initializer.
     * 
     * @return the initialize appender
     */
    public PopulatorAppendCommand initializer() {
        return new PopulatorAppendCommand() {
            
            @Override
            protected void handle(DatabasePopulator initializer) {
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
            protected void handle(DatabasePopulator destroyer) {
                listener.setDestroyer(destroyer);
            }
            
        };
    }
    
    /**
     * Return the created populating listener.
     * 
     * @return the created listener
     */
    public PopulateApplicationListener build() {
        return listener;
    }
    
    /**
     * Appends the populator to the listener.
     *
     * @author Jeroen van Schagen
     * @since Feb 12, 2014
     */
    public abstract class PopulatorAppendCommand {
        
        private DatabasePopulatorChain chain = new DatabasePopulatorChain();
        
        private DatabasePopulatorChain current = new DatabasePopulatorChain();
        
        private boolean async = false;

        /**
         * Add a populator to the listener.
         * 
         * @param populator the populator to add
         * @return this builder, for chaining
         */
        public PopulatorAppendCommand add(DatabasePopulator populator) {
            current.add(populator);
            return this;
        }
        
        /**
         * Create an asynchronous task for populating the database. To add synchronous
         * populators again, invoke {@link PopulatorAppendCommand#current()}.
         * 
         * @return this builder, for chaining
         */
        public PopulatorAppendCommand task() {
            return this.async(true);
        }
        
        /**
         * Mark all next populators as synchronous. To add an asynchronous
         * populator task, invoke {@link PopulatorAppendCommand#task()}.
         * 
         * @return this builder, for chaining
         */
        public PopulatorAppendCommand current() {
            return this.async(false);
        }
        
        /**
         * Switch between synchronous and asynchronous mode. During this
         * process we add all currently configured populators to the chain.
         * 
         * @param async {@code true} when asynchronous
         * @return this builder, for chaining
         */
        private PopulatorAppendCommand async(boolean async) {
            addToChain();
            this.async = async;

            return this;
        }
        
        private DatabasePopulator addToChain() {
            if (!current.isEmpty()) {
                if (async) {
                    chain.add(new AsyncDatabasePopulator(current));
                } else {
                    chain.add(current);
                }
                current = new DatabasePopulatorChain();
            }
            return chain;
        }

        /**
         * Finish appending populators and return to the listener builder.
         * 
         * @return this builder, for chaining
         */
        public PopulateApplicationListenerBuilder done() {
            handle(addToChain());
            return PopulateApplicationListenerBuilder.this;
        }

        /**
         * Finish appending populators and build the listener.
         * 
         * @return the listener
         */
        public PopulateApplicationListener build() {
            done();
            return PopulateApplicationListenerBuilder.this.build();
        }

        /**
         * Place the populator in our listener.
         * 
         * @param populator the populator to use
         */
        protected abstract void handle(DatabasePopulator populator);

    }

}
