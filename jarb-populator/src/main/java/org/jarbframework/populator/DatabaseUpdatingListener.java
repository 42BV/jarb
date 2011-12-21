package org.jarbframework.populator;

import java.util.concurrent.atomic.AtomicBoolean;

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
public class DatabaseUpdatingListener implements ApplicationListener<ApplicationContextEvent> {
    /** Describes whether the initializer has already been started. **/
    private final AtomicBoolean started = new AtomicBoolean();
    
    /** Executed when application context is started. **/
    private DatabaseUpdater initializer;
    /** Executed when application context is stopped. **/
    private DatabaseUpdater destroyer;
    
    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        if(event instanceof ContextRefreshedEvent && started.compareAndSet(false, true)) {
            execute(initializer);
        } else if(event instanceof ContextClosedEvent) {
            execute(destroyer);
        }
    }
    
    private void execute(DatabaseUpdater updater) {
        if(updater != null) {
            try {
                updater.update();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setInitializer(DatabaseUpdater initializer) {
        this.initializer = initializer;
    }
    
    public void setDestroyer(DatabaseUpdater destroyer) {
        this.destroyer = destroyer;
    }
}
