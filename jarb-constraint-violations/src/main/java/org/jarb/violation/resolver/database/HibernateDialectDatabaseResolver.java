package org.jarb.violation.resolver.database;

import java.util.HashMap;
import java.util.Map;


/**
 * Resolves the database, using a hibernate dialect value.
 * 
 * @author Jeroen van Schagen
 * @since 13-05-2011
 */
public class HibernateDialectDatabaseResolver implements DatabaseResolver {
    private static final Map<String, Database> DIALECT_MAPPING;

    static {
        DIALECT_MAPPING = new HashMap<String, Database>();
        DIALECT_MAPPING.put("org.hibernate.dialect.HSQLDialect", Database.HSQL);
        DIALECT_MAPPING.put("org.hibernate.dialect.MySQLDialect", Database.MYSQL);
        DIALECT_MAPPING.put("org.hibernate.dialect.Oracle8iDialect", Database.ORACLE);
        DIALECT_MAPPING.put("org.hibernate.dialect.Oracle9Dialect", Database.ORACLE);
        DIALECT_MAPPING.put("org.hibernate.dialect.PostgreSQLDialect", Database.POSTGRESQL);
        DIALECT_MAPPING.put("org.hibernate.dialect.ProgressDialect", Database.POSTGRESQL);
    }

    /**
     * Hibernate dialect that should be used for database resolving.
     */
    private String hibernateDialect;

    public void setHibernateDialect(String hibernateDialect) {
        this.hibernateDialect = hibernateDialect;
    }

    /**
     * Resolve the database using a hibernate dialect.
     * @param hibernateDialect hibernate dialect of the database
     * @return database matching the provided dialect, if any
     */
    public static Database resolveByDialect(String hibernateDialect) {
        HibernateDialectDatabaseResolver databaseResolver = new HibernateDialectDatabaseResolver();
        databaseResolver.setHibernateDialect(hibernateDialect);
        return databaseResolver.resolve();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Database resolve() {
        return DIALECT_MAPPING.get(hibernateDialect);
    }
}
