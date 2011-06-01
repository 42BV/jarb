package org.jarb.violation.resolver.database;

import java.util.HashMap;
import java.util.Map;

import org.jarb.violation.ConstraintViolation;
import org.jarb.violation.resolver.ConstraintViolationResolver;

/**
 * Constraint violation resolver which delegates to database
 * specific resolvers.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class DatabaseSpecificConstraintViolationResolver implements ConstraintViolationResolver {
    private final Map<Database, ConstraintViolationResolver> violationResolvers;
    private DatabaseResolver databaseResolver;

    /**
     * Construct a new {@link DatabaseSpecificConstraintViolationResolver}.
     */
    public DatabaseSpecificConstraintViolationResolver() {
        violationResolvers = new HashMap<Database, ConstraintViolationResolver>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConstraintViolation resolve(Throwable throwable) {
        final Database database = databaseResolver.resolve();
        ConstraintViolationResolver violationResolver = violationResolvers.get(database);
        if (violationResolver == null) {
            throw new IllegalStateException("No violation resolver has been registered for a '" + database + "' database.");
        }
        return violationResolver.resolve(throwable);
    }

    /**
     * Register a database specific constraint violation resolver.
     * @param database type of database that the resolver works on
     * @param violationResolver reference to the violation resolver
     */
    public void register(Database database, ConstraintViolationResolver violationResolver) {
        violationResolvers.put(database, violationResolver);
    }

    public void setDatabaseResolver(DatabaseResolver databaseResolver) {
        this.databaseResolver = databaseResolver;
    }
}
