package org.jarb.constraint.database.column;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.jarb.utils.database.JdbcUtils;
import org.springframework.util.Assert;

/**
 * Retrieves column meta data using JDBC.
 * 
 * @author Jeroen van Schagen
 * @since 30-05-2011
 */
public class JdbcColumnMetadataProvider implements ColumnMetadataProvider {
    private final DataSource dataSource;

    /**
     * Construct a new {@link JdbcColumnMetadataProvider}.
     * @param dataSource data source reference
     */
    public JdbcColumnMetadataProvider(DataSource dataSource) {
        Assert.notNull(dataSource, "Property 'data source' cannot be null.");
        this.dataSource = dataSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<ColumnMetadata> all() {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData metadata = connection.getMetaData();
            ResultSet columnResultSet = metadata.getColumns(null, null, null, null);
            return extractColumnInfo(columnResultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JdbcUtils.closeQuietly(connection);
        }
    }

    /**
     * Extract column constraint information from our result set.
     * @param resultSet the result set containing our information
     * @return list of meta data extracted from the result set
     * @throws SQLException if any exception occurs
     */
    private Set<ColumnMetadata> extractColumnInfo(ResultSet resultSet) throws SQLException {
        Set<ColumnMetadata> columnMetaData = new HashSet<ColumnMetadata>();
        while (resultSet.next()) {
            columnMetaData.add(mapToColumnMetadata(resultSet));
        }
        return columnMetaData;
    }

    /**
     * Convert a row inside the result set to column meta data.
     * @param resultSet the result set containing our information
     * @return row content as meta data
     * @throws SQLException if any exception occurs
     */
    private ColumnMetadata mapToColumnMetadata(ResultSet resultSet) throws SQLException {
        String schemaName = resultSet.getString("TABLE_SCHEM");
        String tableName = resultSet.getString("TABLE_NAME");
        String columnName = resultSet.getString("COLUMN_NAME");
        ColumnReference columnReference = new ColumnReference(schemaName, tableName, columnName);
        ColumnMetadata columnMetadata = new ColumnMetadata(columnReference);
        columnMetadata.setDefaultValue(resultSet.getString("COLUMN_DEF"));
        columnMetadata.setMaximumLength(getValueAsInteger(resultSet, "COLUMN_SIZE"));
        columnMetadata.setFractionLength(getValueAsInteger(resultSet, "DECIMAL_DIGITS"));
        columnMetadata.setRadix(getValueAsInteger(resultSet, "NUM_PREC_RADIX"));
        columnMetadata.setRequired(resultSet.getString("IS_NULLABLE").equals("NO"));
        columnMetadata.setAutoIncrement((resultSet.getString("IS_AUTOINCREMENT").equals("YES")));
        return columnMetadata;
    }

    /**
     * Retrieve a column from the result set, as integer. Whenever the column
     * value is {@code null}, it will be returned as {@code null}.
     * @param resultSet the result set containing our information
     * @param columnName name of the numeric column
     * @return column value as integer
     * @throws SQLException if any exception occurs
     */
    public Integer getValueAsInteger(ResultSet resultSet, String columnName) throws SQLException {
        String numberAsString = resultSet.getString(columnName);
        if (StringUtils.isBlank(numberAsString)) {
            return null;
        }
        return Integer.parseInt(numberAsString);
    }
}
