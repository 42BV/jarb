package org.jarb.violation.resolver;

import org.jarb.violation.resolver.database.DatabaseType;
import org.jarb.violation.resolver.database.DatabaseTypeResolver;
import org.jarb.violation.resolver.database.DatabaseTypeAwareViolationResolver;
import org.jarb.violation.resolver.vendor.HsqlViolationResolver;
import org.jarb.violation.resolver.vendor.MysqlViolationResolver;
import org.jarb.violation.resolver.vendor.OracleViolationResolver;
import org.jarb.violation.resolver.vendor.PostgresViolationResolver;

/**
 * Capable of building a default constraint violation resolver.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public final class DatabaseConstraintViolationResolverFactory {

    /**
     * Build a default constraint violation resolver. Returned resolver isntances
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
