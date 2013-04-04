package org.jarbframework.constraint.violation.resolver;

import java.util.Map;

import javax.sql.DataSource;

import org.jarbframework.constraint.violation.resolver.vendor.H2ViolationResolver;
import org.jarbframework.constraint.violation.resolver.vendor.HsqlViolationResolver;
import org.jarbframework.utils.database.DatabaseType;
import org.jarbframework.utils.database.DatabaseTypeResolver;
import org.jarbframework.utils.database.JdbcMetadataDatabaseTypeResolver;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/**
 * Capable of building a default constraint violation resolver.
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class DatabaseConstraintViolationResolverFactory {
    
    private final DatabaseTypeResolver databaseTypeResolver;
    
    private final Map<DatabaseType, MessageBasedViolationResolver> violationMessageResolvers;

    public DatabaseConstraintViolationResolverFactory() {
        this(new JdbcMetadataDatabaseTypeResolver());
    }

    public DatabaseConstraintViolationResolverFactory(DatabaseTypeResolver databaseTypeResolver) {
        this.databaseTypeResolver = Preconditions.checkNotNull(databaseTypeResolver, "Database type resolver cannot be null.");
        
        violationMessageResolvers = Maps.newHashMap();
        violationMessageResolvers.put(DatabaseType.HSQL, new HsqlViolationResolver());
        violationMessageResolvers.put(DatabaseType.H2, new H2ViolationResolver());
        violationMessageResolvers.put(DatabaseType.MYSQL, new HsqlViolationResolver());
        violationMessageResolvers.put(DatabaseType.ORACLE, new HsqlViolationResolver());
        violationMessageResolvers.put(DatabaseType.POSTGRESQL, new HsqlViolationResolver());
    }

    /**
     * Build a default constraint violation resolver. Returned resolver instance
     * are capable of resolving constraint violation for HSQL, MySQL, Oracle and
     * PostgreSQL databases. Whenever the database specific resolver cannot handle
     * our exception we fallback to the hibernate resolver (when on classpath).
     * 
     * @param dataSource the data source for which we build a resolver
     * @return new "default" constraint violation resolver
     */
    public DatabaseConstraintViolationResolver build(DataSource dataSource) {
        ViolationResolverChain resolverChain = new ViolationResolverChain();
        resolverChain.addToChain(buildResolverForDataSource(dataSource));
        resolverChain.addToChain(new HibernateViolationResolver());
        return resolverChain;
    }

    private DatabaseConstraintViolationResolver buildResolverForDataSource(DataSource dataSource) {
        DatabaseType databaseType = databaseTypeResolver.resolveDatabaseType(dataSource);
        MessageBasedViolationResolver violationMessageResolver = violationMessageResolvers.get(databaseType);
        if (violationMessageResolver == null) {
            throw new UnsupportedOperationException("No violation message resolver registered for database: " + databaseType);
        }
        return new RootCauseMessageViolationResolver(violationMessageResolver);
    }

}
