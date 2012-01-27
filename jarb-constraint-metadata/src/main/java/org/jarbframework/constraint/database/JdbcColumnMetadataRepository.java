package org.jarbframework.constraint.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.jarbframework.utils.Asserts;
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
public class JdbcColumnMetadataRepository implements ColumnMetadataRepository {

    private final Logger logger = LoggerFactory.getLogger(JdbcColumnMetadataRepository.class);

    /** Provides access to our database using JDBC. **/
    private final DataSource dataSource;

    /** Applies the correct casing on identifiers. **/
    private DatabaseIdentifierCaser databaseIdentifierCaser;

    /** Database catalog name, can be {@code null} **/
    private String catalog;

    /** Database schema name, can be {@code null} **/
    private String schema;

    /**
     * Construct a new {@link JdbcColumnMetadataRepository}.
     * @param dataSource data source reference
     */
    public JdbcColumnMetadataRepository(DataSource dataSource) {
        this.dataSource = Asserts.notNull(dataSource, "Data source cannot be null.");
    }

    @Override
    public ColumnMetadata getColumnMetadata(ColumnReference columnReference) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            if (databaseIdentifierCaser == null) {
                databaseIdentifierCaser = new DatabaseIdentifierCaser(databaseMetaData);
            }
            String tableName = databaseIdentifierCaser.caseIdentifier(columnReference.getTableName());
            String columnName = databaseIdentifierCaser.caseIdentifier(columnReference.getColumnName());
            logger.debug("Querying column metadata for table: {}, column: {}.", tableName, columnName);
            ResultSet resultSet = databaseMetaData.getColumns(catalog, schema, tableName, columnName);
            return mapToColumnMetadata(columnReference, resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JdbcUtils.closeQuietly(connection);
        }
    }

    /**
     * Convert a row inside the result set to column meta data.
     * @param columnReference the original column reference used during querying
     * @param resultSet the result set containing our information
     * @throws SQLException if any exception occurs
     */
    private ColumnMetadata mapToColumnMetadata(ColumnReference columnReference, ResultSet resultSet) throws SQLException {
        ColumnMetadata columnMetadata = null;
        if (resultSet.next()) {
            columnMetadata = new ColumnMetadata(columnReference);
            columnMetadata.setDefaultValue(resultSet.getString("COLUMN_DEF"));
            columnMetadata.setMaximumLength(getValueAsInteger(resultSet, "COLUMN_SIZE"));
            columnMetadata.setFractionLength(getValueAsInteger(resultSet, "DECIMAL_DIGITS"));
            columnMetadata.setRadix(getValueAsInteger(resultSet, "NUM_PREC_RADIX"));
            columnMetadata.setRequired("NO".equals(getOptionalValue(resultSet, "IS_NULLABLE")));
            columnMetadata.setAutoIncrement("YES".equals(getOptionalValue(resultSet, "IS_AUTOINCREMENT")));
        }
        return columnMetadata;
    }

    private Integer getValueAsInteger(ResultSet resultSet, String columnLabel) throws SQLException {
        String numberAsString = resultSet.getString(columnLabel);
        if (StringUtils.isBlank(numberAsString)) {
            return null;
        }
        return Integer.parseInt(numberAsString);
    }

    private Object getOptionalValue(ResultSet resultSet, String columnLabel) {
        Object value = null;
        try {
            value = resultSet.getObject(columnLabel);
        } catch (SQLException e) {
            logger.debug("Column '" + columnLabel + "'  value could not be extracted from result set", e);
        }
        return value;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    private static class DatabaseIdentifierCaser {

        /** String used to quote an identifier **/
        private final String identifierQuoteString;

        private final boolean storesUpperCaseIdentifiers;
        private final boolean storesUpperCaseQuotedIdentifiers;

        private final boolean storesLowerCaseIdentifiers;
        private final boolean storesLowerCaseQuotedIdentifiers;

        public DatabaseIdentifierCaser(DatabaseMetaData databaseMetaData) throws SQLException {
            identifierQuoteString = databaseMetaData.getIdentifierQuoteString();
            storesUpperCaseIdentifiers = databaseMetaData.storesUpperCaseIdentifiers();
            storesUpperCaseQuotedIdentifiers = databaseMetaData.storesUpperCaseQuotedIdentifiers();
            storesLowerCaseIdentifiers = databaseMetaData.storesLowerCaseIdentifiers();
            storesLowerCaseQuotedIdentifiers = databaseMetaData.storesLowerCaseQuotedIdentifiers();
        }

        public String caseIdentifier(String identifier) {
            if (isQuoted(identifier)) {
                identifier = caseQuotedIdentifier(identifier);
            } else {
                if (storesLowerCaseIdentifiers) {
                    identifier = StringUtils.lowerCase(identifier);
                } else if (storesUpperCaseIdentifiers) {
                    identifier = StringUtils.upperCase(identifier);
                }
            }
            return identifier;
        }

        private boolean isQuoted(String identifier) {
            boolean quoted = false;
            if (StringUtils.isNotBlank(identifierQuoteString)) {
                quoted = StringUtils.startsWith(identifier, identifierQuoteString) && StringUtils.endsWith(identifier, identifierQuoteString);
            }
            return quoted;
        }

        private String caseQuotedIdentifier(String identifier) {
            String unquotedIdentifier = StringUtils.substringBetween(identifier, identifierQuoteString);
            if (storesLowerCaseQuotedIdentifiers) {
                unquotedIdentifier = StringUtils.lowerCase(unquotedIdentifier);
            } else if (storesUpperCaseQuotedIdentifiers) {
                unquotedIdentifier = StringUtils.upperCase(unquotedIdentifier);
            }
            return unquotedIdentifier;
        }

    }

}
