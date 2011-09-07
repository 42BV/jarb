package org.jarbframework.constraint.database.column;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.jarbframework.utils.orm.ColumnReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;

/**
 * Retrieves column meta data using JDBC.
 * 
 * @author Jeroen van Schagen
 * @since 30-05-2011
 */
public class JdbcColumnMetadataProvider implements ColumnMetadataProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcColumnMetadataProvider.class);
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
            JdbcUtils.closeConnection(connection);
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
        String tableName = resultSet.getString("TABLE_NAME");
        String columnName = resultSet.getString("COLUMN_NAME");
        ColumnReference columnReference = new ColumnReference(tableName, columnName);
        ColumnMetadata columnMetadata = new ColumnMetadata(columnReference);
        columnMetadata.setDefaultValue(resultSet.getString("COLUMN_DEF"));
        columnMetadata.setMaximumLength(getValueAsInteger(resultSet, "COLUMN_SIZE"));
        columnMetadata.setFractionLength(getValueAsInteger(resultSet, "DECIMAL_DIGITS"));
        columnMetadata.setRadix(getValueAsInteger(resultSet, "NUM_PREC_RADIX"));
        columnMetadata.setRequired("NO".equals(getValueSafely(resultSet, "IS_NULLABLE")));
        columnMetadata.setAutoIncrement("YES".equals(getValueSafely(resultSet, "IS_AUTOINCREMENT")));
        return columnMetadata;
    }

    private Integer getValueAsInteger(ResultSet resultSet, String columnLabel) throws SQLException {
        String numberAsString = resultSet.getString(columnLabel);
        if (StringUtils.isBlank(numberAsString)) {
            return null;
        }
        return Integer.parseInt(numberAsString);
    }

    public Object getValueSafely(ResultSet resultSet, String columnLabel) {
        try {
            return resultSet.getObject(columnLabel);
        } catch (SQLException e) {
            LOGGER.debug("Could not extract '" + columnLabel + "' from result set");
            return null;
        }
    }

}
