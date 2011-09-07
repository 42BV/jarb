package org.jarb.violation.resolver.database;

import static org.jarb.utils.Conditions.notNull;

import java.util.HashMap;
import java.util.Map;

import org.jarb.violation.DatabaseConstraintViolation;
import org.jarb.violation.resolver.DatabaseConstraintViolationResolver;

/**
 * Constraint violation resolver that delegates to database specific resolvers.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class DatabaseTypeAwareViolationResolver implements DatabaseConstraintViolationResolver {
    /** Delegating resolvers for each type of database **/
    private final Map<DatabaseType, DatabaseConstraintViolationResolver> violationResolvers;
    /** Used to resolve the type of database **/
    private final DatabaseTypeResolver databaseTypeResolver;

    /**
     * Construct a new {@link DatabaseTypeAwareViolationResolver}.
     * @param databaseTypeResolver resolves the type of database
     */
    public DatabaseTypeAwareViolationResolver(DatabaseTypeResolver databaseTypeResolver) {
        this.databaseTypeResolver = notNull(databaseTypeResolver, "Database type resolver cannot be null.");
        violationResolvers = new HashMap<DatabaseType, DatabaseConstraintViolationResolver>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatabaseConstraintViolation resolve(Throwable throwable) {
        final DatabaseType databaseType = databaseTypeResolver.resolve();
        DatabaseConstraintViolationResolver violationResolver = violationResolvers.get(databaseType);
        if (violationResolver == null) {
            throw new IllegalStateException("No violation resolver has been registered for a '" + databaseType + "' database.");
        }
        return violationResolver.resolve(throwable);
    }

    /**
     * Register a database specific constraint violation resolver.
     * @param databaseType type of database that the resolver works on
     * @param violationResolver reference to the violation resolver
     * @return this instance to enable method chaining
     */
    public DatabaseTypeAwareViolationResolver registerResolver(DatabaseType databaseType, DatabaseConstraintViolationResolver violationResolver) {
        violationResolvers.put(databaseType, violationResolver);
        return this;
    }
}
