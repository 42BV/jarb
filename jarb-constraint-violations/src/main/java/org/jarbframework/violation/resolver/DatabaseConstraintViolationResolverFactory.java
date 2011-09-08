package org.jarbframework.violation.resolver;

import javax.sql.DataSource;

import org.jarbframework.utils.database.DatabaseType;
import org.jarbframework.utils.database.DatabaseTypeResolver;
import org.jarbframework.utils.database.JdbcMetadataDatabaseTypeResolver;
import org.jarbframework.violation.resolver.vendor.HsqlViolationResolver;
import org.jarbframework.violation.resolver.vendor.MysqlViolationResolver;
import org.jarbframework.violation.resolver.vendor.OracleViolationResolver;
import org.jarbframework.violation.resolver.vendor.PostgresViolationResolver;

/**
 * Capable of building a default constraint violation resolver.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class DatabaseConstraintViolationResolverFactory {
    private DatabaseTypeResolver databaseTypeResolver = new JdbcMetadataDatabaseTypeResolver();

    public void setDatabaseTypeResolver(DatabaseTypeResolver databaseTypeResolver) {
        this.databaseTypeResolver = databaseTypeResolver;
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
