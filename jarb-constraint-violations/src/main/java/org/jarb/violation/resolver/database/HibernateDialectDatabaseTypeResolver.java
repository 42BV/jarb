package org.jarb.violation.resolver.database;

import static org.jarbframework.utils.Conditions.hasText;
import static org.jarbframework.utils.Conditions.notNull;
import static org.jarbframework.utils.Conditions.state;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.dialect.Dialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resolves the database type based on a Hibernate dialect string.
 * 
 * @author Jeroen van Schagen
 * @since 13-05-2011
 */
public class HibernateDialectDatabaseTypeResolver implements DatabaseTypeResolver {
    private static final Map<Class<? extends Dialect>, DatabaseType> DIALECT_MAPPING;

    static {
        DIALECT_MAPPING = new HashMap<Class<? extends Dialect>, DatabaseType>();
        DIALECT_MAPPING.put(org.hibernate.dialect.HSQLDialect.class, DatabaseType.HSQL);
        DIALECT_MAPPING.put(org.hibernate.dialect.MySQLDialect.class, DatabaseType.MYSQL);
        DIALECT_MAPPING.put(org.hibernate.dialect.Oracle8iDialect.class, DatabaseType.ORACLE);
        DIALECT_MAPPING.put(org.hibernate.dialect.Oracle9iDialect.class, DatabaseType.ORACLE);
        DIALECT_MAPPING.put(org.hibernate.dialect.Oracle10gDialect.class, DatabaseType.ORACLE);
        DIALECT_MAPPING.put(org.hibernate.dialect.PostgreSQLDialect.class, DatabaseType.POSTGRESQL);
        DIALECT_MAPPING.put(org.hibernate.dialect.ProgressDialect.class, DatabaseType.POSTGRESQL);
    }

    private final Logger logger = LoggerFactory.getLogger(HibernateDialectDatabaseTypeResolver.class);

    /** Class reference to the dialect. **/
    private final Class<? extends Dialect> dialectClass;

    /**
     * Construct a new {@link HibernateDialectDatabaseTypeResolver}.
     * @param dialectClass class of the dialect being used, cannot be {@code null}
     */
    public HibernateDialectDatabaseTypeResolver(Class<? extends Dialect> dialectClass) {
        this.dialectClass = notNull(dialectClass, "Dialect class cannot be null.");
    }

    /**
     * Construct a new {@link HibernateDialectDatabaseTypeResolver}, based on a dialect class name.
     * @param dialectName class name of the dialect being used, has to be present inside our class path
     */
    public static HibernateDialectDatabaseTypeResolver forName(String dialectName) {
        try {
            Class<?> dialectClass = Class.forName(hasText(dialectName, "Dialect name cannot be empty."));
            state(Dialect.class.isAssignableFrom(dialectClass), "Class '" + dialectClass.getName() + "' is not a dialect.");
            return new HibernateDialectDatabaseTypeResolver((Class<? extends Dialect>) dialectClass);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Dialect '" + dialectName + "' could not be found in our class path.");
        }
    }

    @Override
    public DatabaseType resolve() {
        DatabaseType databaseType = DIALECT_MAPPING.get(dialectClass);
        if (databaseType == null) {
            logger.debug("Could not resolve database for dialect '{}', looking for recognized superclasses.", dialectClass.getName());
            databaseType = lookupDatabaseTypeWithInheritance();
        }
        if (databaseType == null) {
            throw new IllegalStateException("Could not resolve database for dialect '" + dialectClass.getName() + "'");
        }
        logger.debug("Recognized database as '{}'.", databaseType);
        return databaseType;
    }

    /**
     * Retrieve the database type from our first mapping entry with
     * a dialect that is an instance of the specified dialect.
     * @return matching database type, if any could be found
     */
    private DatabaseType lookupDatabaseTypeWithInheritance() {
        DatabaseType database = null;
        for (Map.Entry<Class<? extends Dialect>, DatabaseType> entry : DIALECT_MAPPING.entrySet()) {
            if (entry.getKey().isAssignableFrom(dialectClass)) {
                database = entry.getValue();
                break;
            }
        }
        return database;
    }

}
