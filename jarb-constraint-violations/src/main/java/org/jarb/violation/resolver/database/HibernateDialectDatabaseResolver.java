package org.jarb.violation.resolver.database;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.dialect.Dialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resolves the database by {@link Dialect}.
 * 
 * @author Jeroen van Schagen
 * @since 13-05-2011
 */
public class HibernateDialectDatabaseResolver implements DatabaseResolver {
    private static final Map<Class<? extends Dialect>, Database> DIALECT_MAPPING;
    static {
        DIALECT_MAPPING = new HashMap<Class<? extends Dialect>, Database>();
        DIALECT_MAPPING.put(org.hibernate.dialect.HSQLDialect.class, Database.HSQL);
        DIALECT_MAPPING.put(org.hibernate.dialect.MySQLDialect.class, Database.MYSQL);
        DIALECT_MAPPING.put(org.hibernate.dialect.Oracle8iDialect.class, Database.ORACLE);
        DIALECT_MAPPING.put(org.hibernate.dialect.Oracle9iDialect.class, Database.ORACLE);
        DIALECT_MAPPING.put(org.hibernate.dialect.Oracle10gDialect.class, Database.ORACLE);
        DIALECT_MAPPING.put(org.hibernate.dialect.PostgreSQLDialect.class, Database.POSTGRESQL);
        DIALECT_MAPPING.put(org.hibernate.dialect.ProgressDialect.class, Database.POSTGRESQL);
    }

    private final Logger logger = LoggerFactory.getLogger(HibernateDialectDatabaseResolver.class);
    private final Class<?> dialectClass;

    /**
     * Construct a new {@link HibernateDialectDatabaseResolver}.
     * @param hibernateDialect the dialect being used, cannot be null and has to be in the class path
     */
    public HibernateDialectDatabaseResolver(String hibernateDialect) {
        try {
            this.dialectClass = Class.forName(hibernateDialect);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Dialect '" + hibernateDialect + "' could not be found in our class path.");
        }
    }

    /**
     * Resolve our database type by dialect class name.
     * @param hibernateDialect class name of the dialect
     * @return database matching the provided dialect
     */
    public static Database resolveByDialect(String hibernateDialect) {
        return new HibernateDialectDatabaseResolver(hibernateDialect).resolve();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Database resolve() {
        Database database = DIALECT_MAPPING.get(dialectClass);
        if (database == null) {
            logger.debug("Could not resolve database for dialect '{}', looking for recognized superclasses.", dialectClass.getName());
            database = determineDatabaseBySuperclasses();
        }
        if (database == null) {
            throw new UnsupportedOperationException("Could not resolve database for dialect '" + dialectClass.getName() + "'");
        }
        logger.debug("Recognized database as '{}'.", database);
        return database;
    }

    private Database determineDatabaseBySuperclasses() {
        Database database = null;
        for (Map.Entry<Class<? extends Dialect>, Database> entry : DIALECT_MAPPING.entrySet()) {
            if (entry.getKey().isAssignableFrom(dialectClass)) {
                database = entry.getValue();
                break;
            }
        }
        return database;
    }

}
