package org.jarbframework.populator;

import static org.jarbframework.utils.Asserts.notNull;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Application listener that is capable of updating the database
 * on context initialization and destruction.
 * @author Jeroen van Schagen
 * @since 02-11-2011
 */
public class DatabaseUpdatingListener implements ApplicationListener<ApplicationContextEvent> {

    /** Describes whether the initializer has already been started. **/
    private final AtomicBoolean started = new AtomicBoolean();

    /** Executed when application context is started. **/
    private final DatabaseUpdater initializer;
    /** Executed when application context is stopped. **/
    private final DatabaseUpdater destroyer;

    public DatabaseUpdatingListener(DatabaseUpdater initializer) {
    	this(initializer, null);
    }
    
    public DatabaseUpdatingListener(DatabaseUpdater initializer, DatabaseUpdater destroyer) {
    	this.initializer = initializer;
    	this.destroyer = destroyer;
    }
    
    public static DatabaseUpdatingListener initializeAndRevert(RevertableDatabaseUpdater updater) {
    	notNull(updater, "Updater cannot be null.");
    	return new DatabaseUpdatingListener(updater, new RevertingDatabaseUpdater(updater));
    }
    
    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        if (event instanceof ContextRefreshedEvent && started.compareAndSet(false, true)) {
            execute(initializer);
        } else if (event instanceof ContextClosedEvent) {
            execute(destroyer);
        }
    }

    private void execute(DatabaseUpdater updater) {
        if (updater != null) {
            updater.update();
        }
    }
}
