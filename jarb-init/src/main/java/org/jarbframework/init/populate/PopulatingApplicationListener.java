package org.jarbframework.init.populate;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Application listener that is capable of updating the database
 * on context initialization and destruction.
 * 
 * @author Jeroen van Schagen
 * @since 02-11-2011
 */
public class PopulatingApplicationListener implements ApplicationListener<ApplicationContextEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PopulatingApplicationListener.class);

    /** Describes whether the initializer has already been started. **/
    private final AtomicBoolean initialized = new AtomicBoolean();

    /** Executed when application context is started. **/
    private final DatabasePopulator initializer;
    
    /** Executed when application context is stopped. **/
    private final DatabasePopulator destroyer;
    
    /**
     * Construct a new {@link PopulatingApplicationListener}.
     * 
     * @param initializer the initializer
     */
    public PopulatingApplicationListener(DatabasePopulator initializer) {
        this(initializer, null);
    }
    
    /**
     * Construct a new {@link PopulatingApplicationListener}.
     * 
     * @param initializer the initializer
     * @param destroyer the destroyer
     */
    public PopulatingApplicationListener(DatabasePopulator initializer, DatabasePopulator destroyer) {
        this.initializer = initializer;
        this.destroyer = destroyer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        if (event instanceof ContextRefreshedEvent && hasNotBeenInitializedYet()) {
            LOGGER.info("Populating database...");
            execute(initializer);
        } else if (event instanceof ContextClosedEvent) {
            LOGGER.info("Cleaning up database...");
            execute(destroyer);
        }
    }

    private boolean hasNotBeenInitializedYet() {
        return initialized.compareAndSet(false, true);
    }

    private void execute(DatabasePopulator populator) {
        if (populator != null) {
            populator.execute();
        }
    }

}
