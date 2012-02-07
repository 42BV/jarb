package org.jarbframework.populator;

import static org.jarbframework.utils.Asserts.state;

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
		state(!(updater instanceof RevertingDatabaseUpdater), "Wrapped database updater cannot be revertable.");
		state(!(reverter instanceof RevertingDatabaseUpdater), "Wrapped database reverter cannot be revertable.");

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
