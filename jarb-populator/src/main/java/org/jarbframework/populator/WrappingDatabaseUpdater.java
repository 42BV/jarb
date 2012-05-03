package org.jarbframework.populator;


/**
 * Combines two update components to both execute and revert an update.
 * We could e.g. wrap an 'insert-persons.sql' and 'delete-persons.sql'
 * SQL script updater, resulting in an updater that can both create new
 * person entries on {@link #update()} remove them on {@link #revert()}.
 * 
 * @author Jeroen van Schagen
 * @since 7 February 2011
 */
public class WrappingDatabaseUpdater implements RevertableDatabaseUpdater {

	private DatabaseUpdater updater;
	private DatabaseUpdater reverter;
	
	public WrappingDatabaseUpdater(DatabaseUpdater updater, DatabaseUpdater reverter) {
		this.updater = updater;
		this.reverter = reverter;
	}
	
	@Override
	public void update() {
		updater.update();
	}
	
	@Override
	public void revert() {
		reverter.update();
	}
	
}
