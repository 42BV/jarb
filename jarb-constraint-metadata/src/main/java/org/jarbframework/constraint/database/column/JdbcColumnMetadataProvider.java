package org.jarbframework.constraint.database.column;

import static org.jarbframework.utils.Asserts.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.jarbframework.utils.JdbcUtils;
import org.jarbframework.utils.orm.ColumnReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retrieves column meta data using JDBC.
 * 
 * @author Jeroen van Schagen
 * @since 30-05-2011
 */
public class JdbcColumnMetadataProvider implements ColumnMetadataProvider {

    private final Logger logger = LoggerFactory.getLogger(JdbcColumnMetadataProvider.class);

    private final DataSource dataSource;
    private String catalog;
    private String schemaPattern;

    /**
     * Construct a new {@link JdbcColumnMetadataProvider}.
     * @param dataSource data source reference
     */
    public JdbcColumnMetadataProvider(DataSource dataSource) {
        this.dataSource = notNull(dataSource, "Data source cannot be null.");
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public void setSchemaPattern(String schemaPattern) {
        this.schemaPattern = schemaPattern;
    }

    @Override
    public Set<ColumnMetadata> all() {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData metadata = connection.getMetaData();
            ResultSet columnResultSet = metadata.getColumns(catalog, schemaPattern, null, null);
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
        Set<ColumnMetadata> columnMetaDataSet = new HashSet<ColumnMetadata>();
        while (resultSet.next()) {
            ColumnMetadata columnMetaData = mapToColumnMetadata(resultSet);
            logger.debug("Extracted {}.", columnMetaData);
            columnMetaDataSet.add(columnMetaData);
        }
        return columnMetaDataSet;
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
            logger.debug("Could not extract '{}' from result set", columnLabel);
            return null;
        }
    }

}
