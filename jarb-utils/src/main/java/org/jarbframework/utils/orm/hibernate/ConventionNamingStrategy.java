package org.jarbframework.utils.orm.hibernate;

import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.internal.util.StringHelper;
import org.jarbframework.utils.orm.MappingUtils;

/**
 * Naming strategy that describes our mapping between classes and tables.
 *
 * @author Jeroen van Schagen
 */
public class ConventionNamingStrategy extends ImprovedNamingStrategy {

    private static final String FOREIGN_KEY_SUFFIX = "_id";

    @Override
    public String propertyToColumnName(String propertyName) {
        String simplePropertyName = StringHelper.unqualify(propertyName);
        return MappingUtils.lowerCaseWithUnderscores(simplePropertyName);
    }

    @Override
    public String tableName(String tableName) {
        return MappingUtils.lowerCaseWithUnderscores(tableName);
    }

    @Override
    public String columnName(String columnName) {
        return MappingUtils.lowerCaseWithUnderscores(columnName);
    }

    @Override
    public String foreignKeyColumnName(String propertyName, String propertyEntityName, String propertyTableName, String referencedColumnName) {
        return super.foreignKeyColumnName(propertyName, propertyEntityName, propertyTableName, referencedColumnName) + FOREIGN_KEY_SUFFIX;
    }

}
