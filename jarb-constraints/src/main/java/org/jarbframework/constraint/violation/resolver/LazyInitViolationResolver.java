package org.jarbframework.constraint.violation.resolver;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;

/**
 * Database constraint violation resolver that gets constructed
 * when first used. This prevents us from doing heavy operations,
 * such as determining the database product, during initialization.
 * 
 * @author Jeroen van Schagen
 */
public abstract class LazyInitViolationResolver implements DatabaseConstraintViolationResolver {

	private DatabaseConstraintViolationResolver delegate;
	
	@Override
	public DatabaseConstraintViolation resolve(Throwable throwable) {
		return getDelegate().resolve(throwable);
	}
	
	private DatabaseConstraintViolationResolver getDelegate() {
		if (delegate == null) {
			delegate = init();
		}
		return delegate;
	}
	
	protected abstract DatabaseConstraintViolationResolver init();

}
