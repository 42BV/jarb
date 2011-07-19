package org.jarb.violation.resolver.database;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.dialect.Dialect;
import org.springframework.util.Assert;

/**
 * Resolves the database, using a hibernate dialect value.
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
        DIALECT_MAPPING.put(org.hibernate.dialect.PostgreSQLDialect.class, Database.POSTGRESQL);
        DIALECT_MAPPING.put(org.hibernate.dialect.ProgressDialect.class, Database.POSTGRESQL);
    }

    /**
     * Hibernate dialect that should be used for database resolving.
     */
    private final Class<?> dialectClass;

    /**
     * Construct a new {@link HibernateDialectDatabaseResolver}.
     * @param hibernateDialect the dialect being used, cannot be null and has to be in the class path
     */
    public HibernateDialectDatabaseResolver(String hibernateDialect) {
        Assert.hasText(hibernateDialect, "Dialect cannot be empty.");
        try {
            this.dialectClass = Class.forName(hibernateDialect);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Dialect '" + hibernateDialect + "' could not be found in our class path.");
        }
    }

    /**
     * Resolve the database using a hibernate dialect.
     * @param hibernateDialect hibernate dialect of the database
     * @return database matching the provided dialect, if any
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
        // No direct database dialect could be matched, look for inheritance
        if (database == null) {
            database = resolveDialectByInstanceOf();
        }
        if (database == null) {
            throw new UnsupportedOperationException("Could not resolve database for dialect '" + dialectClass.getName() + "'");
        }
        return database;
    }

    /**
     * Resolve the database using an {@code instance of} check. This
     * will also allow custom dialect subclasses to be resolved.
     * @return matched dialect, if any
     */
    private Database resolveDialectByInstanceOf() {
        Database database = null;
        for (Map.Entry<Class<? extends Dialect>, Database> dialectMappingEntry : DIALECT_MAPPING.entrySet()) {
            if (dialectMappingEntry.getKey().isAssignableFrom(dialectClass)) {
                database = dialectMappingEntry.getValue();
                break;
            }
        }
        return database;
    }

}
