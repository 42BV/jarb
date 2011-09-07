package org.jarbframework.violation.resolver;

import org.jarbframework.violation.resolver.database.DatabaseType;
import org.jarbframework.violation.resolver.database.DatabaseTypeAwareViolationResolver;
import org.jarbframework.violation.resolver.database.DatabaseTypeResolver;
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
public final class DatabaseConstraintViolationResolverFactory {

    /**
     * Build a default constraint violation resolver. Returned resolver instance
     * are capable of resolving constraint violation for HSQL, MySQL, Oracle and
     * PostgreSQL databases.
     * 
     * @param databaseResolver capable of determining what database type is used
     * @return new "default" constraint violation resolver
     */
    public static DatabaseConstraintViolationResolver build(DatabaseTypeResolver databaseResolver) {
        DatabaseTypeAwareViolationResolver violationResolver = new DatabaseTypeAwareViolationResolver(databaseResolver);
        violationResolver.registerResolver(DatabaseType.HSQL, new HsqlViolationResolver());
        violationResolver.registerResolver(DatabaseType.MYSQL, new MysqlViolationResolver());
        violationResolver.registerResolver(DatabaseType.ORACLE, new OracleViolationResolver());
        violationResolver.registerResolver(DatabaseType.POSTGRESQL, new PostgresViolationResolver());
        return violationResolver;
    }

    private DatabaseConstraintViolationResolverFactory() {
    }

}
