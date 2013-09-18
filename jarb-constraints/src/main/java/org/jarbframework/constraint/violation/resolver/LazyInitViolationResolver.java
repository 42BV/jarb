package org.jarbframework.constraint.violation.resolver;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;

/**
 * Database constraint violation resolver that is build on
 * first usage rather than on initialization. This prevents
 * us from doing heavy operations during initialization.
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
	
	/**
	 * Build the delegate violation resolver that should be used.
	 * @return the delegate violation resolver
	 */
	protected abstract DatabaseConstraintViolationResolver init();

}
