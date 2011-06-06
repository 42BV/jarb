package org.jarb.violation.resolver;

import org.jarb.violation.resolver.database.Database;
import org.jarb.violation.resolver.database.DatabaseResolver;
import org.jarb.violation.resolver.database.DatabaseSpecificConstraintViolationResolver;
import org.jarb.violation.resolver.vendor.HsqlConstraintViolationResolver;
import org.jarb.violation.resolver.vendor.MysqlConstraintViolationResolver;
import org.jarb.violation.resolver.vendor.OracleConstraintViolationResolver;
import org.jarb.violation.resolver.vendor.PostgresConstraintViolationResolver;

/**
 * Capable of building a default constraint violation resolver.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class ConstraintViolationResolverFactory {

    /**
     * Build a default constraint violation resolver. Returned resolver isntances
     * are capable of resolving constraint violation for HSQL, MySQL, Oracle and
     * PostgreSQL databases.
     * 
     * @param databaseResolver capable of determining what database type is used
     * @return new "default" constraint violation resolver
     */
    public static ConstraintViolationResolver build(DatabaseResolver databaseResolver) {
        DatabaseSpecificConstraintViolationResolver violationResolver = new DatabaseSpecificConstraintViolationResolver();
        violationResolver.register(Database.HSQL, new HsqlConstraintViolationResolver());
        violationResolver.register(Database.MYSQL, new MysqlConstraintViolationResolver());
        violationResolver.register(Database.ORACLE, new OracleConstraintViolationResolver());
        violationResolver.register(Database.POSTGRESQL, new PostgresConstraintViolationResolver());
        violationResolver.setDatabaseResolver(databaseResolver);
        return violationResolver;
    }

}
