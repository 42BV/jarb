/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.init.populate;


/**
 * Builds a database populating listener. 
 *
 * @author Jeroen van Schagen
 * @since Feb 12, 2014
 */
public class PopulatorApplicationListenerBuilder {
    
    private DatabasePopulator initializer;

    private DatabasePopulator destroyer;
    
    /**
     * Configure the initializer.
     * 
     * @return the append command
     */
    public DatabasePopulateAppender initializer() {
        return new DatabasePopulateAppender() {
            
            /**
             * {@inheritDoc}
             */
            @Override
            protected void handle(DatabasePopulator initializer) {
                PopulatorApplicationListenerBuilder.this.initializer = initializer;
            }
            
        };
    }
    
    /**
     * Configure the destroyer.
     * 
     * @return the append command
     */
    public DatabasePopulateAppender destroyer() {
        return new DatabasePopulateAppender() {
            
            /**
             * {@inheritDoc}
             */
            @Override
            protected void handle(DatabasePopulator destroyer) {
                PopulatorApplicationListenerBuilder.this.destroyer = destroyer;
            }
            
        };
    }
    
    /**
     * Return the created populating listener.
     * 
     * @return the created listener
     */
    public PopulatorApplicationListener build() {
        return new PopulatorApplicationListener(initializer, destroyer);
    }
    
    /**
     * Appends the populator to the listener.
     *
     * @author Jeroen van Schagen
     * @since Feb 12, 2014
     */
    public abstract class DatabasePopulateAppender {
        
        private DatabasePopulatorChain chain = new DatabasePopulatorChain();
        
        private DatabasePopulatorChain current = new DatabasePopulatorChain();
        
        private boolean async = false;

        /**
         * Add a populator to the listener.
         * 
         * @param populator the populator to add
         * @return this builder, for chaining
         */
        public DatabasePopulateAppender add(DatabasePopulator populator) {
            current.add(populator);
            return this;
        }
        
        /**
         * Create an asynchronous task for populating the database. To add synchronous
         * populators again, invoke {@link DatabasePopulateAppender#current()}.
         * 
         * @return this builder, for chaining
         */
        public DatabasePopulateAppender task() {
            return this.async(true);
        }
        
        /**
         * Mark all next populators as synchronous. To add an asynchronous
         * populator task, invoke {@link DatabasePopulateAppender#task()}.
         * 
         * @return this builder, for chaining
         */
        public DatabasePopulateAppender current() {
            return this.async(false);
        }
        
        /**
         * Switch between synchronous and asynchronous mode. During this
         * process we add all currently configured populators to the chain.
         * 
         * @param async {@code true} when asynchronous
         * @return this builder, for chaining
         */
        private DatabasePopulateAppender async(boolean async) {
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
        public PopulatorApplicationListenerBuilder and() {
            handle(addToChain());
            return PopulatorApplicationListenerBuilder.this;
        }

        /**
         * Finish appending populators and build the listener.
         * 
         * @return the listener
         */
        public PopulatorApplicationListener build() {
            and();
            return PopulatorApplicationListenerBuilder.this.build();
        }

        /**
         * Place the populator in our listener.
         * 
         * @param populator the populator to use
         */
        protected abstract void handle(DatabasePopulator populator);

    }

}
