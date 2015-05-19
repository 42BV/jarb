package org.jarbframework.init.populate.listener;

import java.util.concurrent.atomic.AtomicBoolean;

import org.jarbframework.init.populate.DatabasePopulator;
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
public class PopulateApplicationListener implements ApplicationListener<ApplicationContextEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PopulateApplicationListener.class);

    /** Describes whether the initializer has already been started. **/
    private final AtomicBoolean initialized = new AtomicBoolean();

    /** Executed when application context is started. **/
    private DatabasePopulator initializer;
    
    /** Executed when application context is stopped. **/
    private DatabasePopulator destroyer;

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
    
    public void setInitializer(DatabasePopulator initializer) {
        this.initializer = initializer;
    }
    
    public void setDestroyer(DatabasePopulator destroyer) {
        this.destroyer = destroyer;
    }
    
}
