package org.jarbframework.constraint.violation.resolver;

import javax.sql.DataSource;

import org.jarbframework.constraint.violation.resolver.vendor.HsqlViolationResolver;
import org.jarbframework.constraint.violation.resolver.vendor.MysqlViolationResolver;
import org.jarbframework.constraint.violation.resolver.vendor.OracleViolationResolver;
import org.jarbframework.constraint.violation.resolver.vendor.PostgresViolationResolver;
import org.jarbframework.utils.database.DatabaseType;
import org.jarbframework.utils.database.DatabaseTypeResolver;
import org.jarbframework.utils.database.JdbcMetadataDatabaseTypeResolver;

import com.google.common.base.Preconditions;

/**
 * Capable of building a default constraint violation resolver.
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class DatabaseConstraintViolationResolverFactory {
    
    private final DatabaseTypeResolver databaseTypeResolver;

    public DatabaseConstraintViolationResolverFactory() {
        this(new JdbcMetadataDatabaseTypeResolver());
    }

    public DatabaseConstraintViolationResolverFactory(DatabaseTypeResolver databaseTypeResolver) {
        this.databaseTypeResolver = Preconditions.checkNotNull(databaseTypeResolver, "Database type resolver cannot be null.");
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
        DatabaseConstraintViolationResolver violationResolver = null;
        DatabaseType databaseType = databaseTypeResolver.resolveDatabaseType(dataSource);
        ViolationMessageResolver messageResolver = getVendorSpecificMessageResolver(databaseType);
        if (messageResolver != null) {
            violationResolver = new RootCauseMessageViolationResolver(messageResolver);
        }
        return violationResolver;
    }
    
    private ViolationMessageResolver getVendorSpecificMessageResolver(DatabaseType databaseType) {
        ViolationMessageResolver messageResolver = null;
        switch (databaseType) {
        case HSQL:
            messageResolver = new HsqlViolationResolver();
            break;
        case MYSQL:
            messageResolver = new MysqlViolationResolver();
            break;
        case ORACLE:
            messageResolver = new OracleViolationResolver();
            break;
        case POSTGRESQL:
            messageResolver = new PostgresViolationResolver();
            break;
        }
        return messageResolver;
    }

}
