package org.jarbframework.populator;

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
public class DatabasePopulatingListener implements ApplicationListener<ApplicationContextEvent> {
    private final Logger logger = LoggerFactory.getLogger(DatabasePopulatingListener.class);

    /** Describes whether the initializer has already been started. **/
    private final AtomicBoolean initializerStarted = new AtomicBoolean();
    
    /** Executed when application context is started. **/
    private DatabasePopulator initializer;
    /** Executed when application context is stopped. **/
    private DatabasePopulator destroyer;
    
    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        if(event instanceof ContextRefreshedEvent && initializerStarted.compareAndSet(false, true)) {
            execute(initializer);
        } else if(event instanceof ContextClosedEvent) {
            execute(destroyer);
        }
    }
    
    private void execute(DatabasePopulator populator) {
        if(populator != null) {
            try {
                populator.populate();
            } catch (Exception e) {
                logger.error("Could not execute '" + populator + "'.", e);
            }
        }
    }

    public void setInitializer(DatabasePopulator initializer) {
        this.initializer = initializer;
    }
    
    public void setDestroyer(DatabasePopulator destroyer) {
        this.destroyer = destroyer;
    }
}
