package nl._42.jarb.utils.orm;

import static nl._42.jarb.utils.Asserts.hasText;

/**
 * References a column in the database.
 *
 * @author Jeroen van Schagen
 * @since Aug 29, 2011
 */
public class ColumnReference {

    private String tableName;
    
    private String columnName;

    /**
     * Create a new database column reference.
     * @param tableName name of the table
     * @param columnName name of the column
     */
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
        if (!(obj instanceof ColumnReference)) {
            return false;
        }
        ColumnReference other = (ColumnReference) obj;
        return other.getTableName().equals(tableName) && other.getColumnName().equals(columnName);
    }

    @Override
    public int hashCode() {
        return tableName.hashCode() * columnName.hashCode();
    }

    @Override
    public String toString() {
        return tableName + "." + columnName;
    }
}
