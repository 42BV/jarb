package org.jarbframework.utils.orm.hibernate;

import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.internal.util.StringHelper;

/**
 * Naming strategy that describes our mapping between classes and tables.
 *
 * @author Jeroen van Schagen
 */
public class ConventionNamingStrategy extends ImprovedNamingStrategy {

    private static final String FOREIGN_KEY_SUFFIX = "_id";

    /**
     * {@inheritDoc}
     */
    @Override
    public String classToTableName(String className) {
        return ConventionNamingStrategy.addUnderscores(StringHelper.unqualify(className));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String propertyToColumnName(String propertyName) {
        return ConventionNamingStrategy.addUnderscores(StringHelper.unqualify(propertyName));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String tableName(String tableName) {
        return ConventionNamingStrategy.addUnderscores(tableName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String columnName(String columnName) {
        return ConventionNamingStrategy.addUnderscores(columnName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String foreignKeyColumnName(String propertyName, String propertyEntityName, String propertyTableName, String referencedColumnName) {
        return super.foreignKeyColumnName(propertyName, propertyEntityName, propertyTableName, referencedColumnName) + FOREIGN_KEY_SUFFIX;
    }

    protected static String addUnderscores(String name) {
        StringBuilder buffer = new StringBuilder(name.replace('.', '_'));
        for (int index = 1; index < buffer.length() - 1; index++) {
            if (isSeparator(buffer, index)) {
                buffer.insert(index++, '_');
            }
        }
        return buffer.toString().toLowerCase();
    }
    
    private static boolean isSeparator(StringBuilder buffer, int index) {
        char previous = buffer.charAt(index - 1);
        char current = buffer.charAt(index);
        char next = buffer.charAt(index + 1);
        
        return (Character.isLowerCase(previous) || Character.isDigit(previous)) && Character.isUpperCase(current)
                && (Character.isLowerCase(next) || Character.isDigit(next));
    }

}
