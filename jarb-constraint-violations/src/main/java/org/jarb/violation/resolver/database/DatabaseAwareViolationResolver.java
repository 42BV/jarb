package org.jarb.violation.resolver.database;

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
public class DatabaseAwareViolationResolver implements DatabaseConstraintViolationResolver {
    /** Delegating resolvers for each type of database **/
    private final Map<Database, DatabaseConstraintViolationResolver> violationResolvers;
    /** Used to resolve the type of database **/
    private final DatabaseResolver databaseResolver;

    /**
     * Construct a new {@link DatabaseAwareViolationResolver}.
     * @param databaseResolver resolves the type of database
     */
    public DatabaseAwareViolationResolver(DatabaseResolver databaseResolver) {
        this.databaseResolver = databaseResolver;
        violationResolvers = new HashMap<Database, DatabaseConstraintViolationResolver>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatabaseConstraintViolation resolve(Throwable throwable) {
        final Database database = databaseResolver.resolve();
        DatabaseConstraintViolationResolver violationResolver = violationResolvers.get(database);
        if (violationResolver == null) {
            throw new IllegalStateException("No violation resolver has been registered for a '" + database + "' database.");
        }
        return violationResolver.resolve(throwable);
    }

    /**
     * Register a database specific constraint violation resolver.
     * @param database type of database that the resolver works on
     * @param violationResolver reference to the violation resolver
     * @return this instance to enable method chaining
     */
    public DatabaseAwareViolationResolver registerResolver(Database database, DatabaseConstraintViolationResolver violationResolver) {
        violationResolvers.put(database, violationResolver);
        return this;
    }
}
