package org.jarbframework.utils.orm;

import static org.jarbframework.utils.Asserts.hasText;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * References a column in the database.
 *
 * @author Jeroen van Schagen
 * @date Aug 29, 2011
 */
public class ColumnReference {

    private String tableName;
    private String columnName;

    public ColumnReference(String tableName, String columnName) {
        this.tableName = hasText(tableName, "Table name is required");
        this.columnName = hasText(columnName, "Column name is required");
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
