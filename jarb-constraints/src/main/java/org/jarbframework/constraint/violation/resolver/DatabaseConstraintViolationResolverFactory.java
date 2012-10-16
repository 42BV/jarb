package org.jarbframework.constraint.violation.resolver;

import static org.jarbframework.utils.Asserts.notNull;

import javax.sql.DataSource;

import org.jarbframework.constraint.violation.resolver.vendor.HsqlViolationResolver;
import org.jarbframework.constraint.violation.resolver.vendor.MysqlViolationResolver;
import org.jarbframework.constraint.violation.resolver.vendor.OracleViolationResolver;
import org.jarbframework.constraint.violation.resolver.vendor.PostgresViolationResolver;
import org.jarbframework.utils.database.DatabaseType;
import org.jarbframework.utils.database.DatabaseTypeResolver;
import org.jarbframework.utils.database.JdbcMetadataDatabaseTypeResolver;

/**
 * Capable of building a default constraint violation resolver.
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class DatabaseConstraintViolationResolverFactory {
    
    private DatabaseTypeResolver databaseTypeResolver;

    public DatabaseConstraintViolationResolverFactory() {
        this(new JdbcMetadataDatabaseTypeResolver());
    }

    public DatabaseConstraintViolationResolverFactory(DatabaseTypeResolver databaseTypeResolver) {
        this.databaseTypeResolver = notNull(databaseTypeResolver, "Database type resolver cannot be null.");
    }

    /**
     * Build a default constraint violation resolver. Returned resolver instance
     * are capable of resolving constraint violation for HSQL, MySQL, Oracle and
     * PostgreSQL databases.
     * 
     * @param dataSource the data source for which we build a resolver
     * @return new "default" constraint violation resolver
     */
    public DatabaseConstraintViolationResolver build(DataSource dataSource) {
        DatabaseConstraintViolationResolver resolver = null;
        DatabaseType databaseType = databaseTypeResolver.resolve(dataSource);
        switch (databaseType) {
        case HSQL:
            resolver = new HsqlViolationResolver();
            break;
        case MYSQL:
            resolver = new MysqlViolationResolver();
            break;
        case ORACLE:
            resolver = new OracleViolationResolver();
            break;
        case POSTGRESQL:
            resolver = new PostgresViolationResolver();
            break;
        }
        return resolver;
    }

}
