package org.jarbframework.constraint.metadata.database;

import static org.apache.commons.lang3.StringUtils.endsWith;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.apache.commons.lang3.StringUtils.upperCase;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.jarbframework.constraint.metadata.database.connection.ConnectionHandler;
import org.jarbframework.constraint.metadata.database.connection.DataSourceConnectionHandler;
import org.jarbframework.utils.JdbcConnectionCallback;
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

    private final ConnectionHandler connectionHandler;

    private DatabaseIdentifierCaser identifierCaser;

    private String catalog;
    
    private String schema;
    
    public JdbcColumnMetadataRepository(DataSource dataSource) {
        this(new DataSourceConnectionHandler(dataSource));
    }
    
    public JdbcColumnMetadataRepository(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    @Override
    public ColumnMetadata getColumnMetadata(final ColumnReference columnReference) {
        return connectionHandler.execute(new JdbcConnectionCallback<ColumnMetadata>() {
           
            @Override
            public ColumnMetadata doWork(Connection connection) throws SQLException {
                DatabaseMetaData databaseMetaData = connection.getMetaData();
                if (identifierCaser == null) {
                    identifierCaser = new DatabaseIdentifierCaser(databaseMetaData);
                }
                
                String tableName = identifierCaser.apply(columnReference.getTableName());
                String columnName = identifierCaser.apply(columnReference.getColumnName());
                
                logger.debug("Querying column metadata for table: {}, column: {}.", tableName, columnName);
                ResultSet resultSet = databaseMetaData.getColumns(catalog, schema, tableName, columnName);
                
                return mapToColumnMetadata(columnReference, resultSet);
            }
            
        });
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
        Integer value = null;
        String numberAsString = resultSet.getString(columnLabel);
        if (isNotBlank(numberAsString)) {
            value = Integer.parseInt(numberAsString);
        }
        return value;
    }

    private Object getOptionalValue(ResultSet resultSet, String columnLabel) {
        Object value = null;
        try {
            value = resultSet.getObject(columnLabel);
        } catch (SQLException e) {
            logger.trace("Column '" + columnLabel + "'  value could not be extracted from result set", e);
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

        private final String quoteString;

        private final boolean storeUpperCase;
        private final boolean storeUpperCaseQuoted;

        private final boolean storeLowerCase;
        private final boolean storeLowerCaseQuoted;

        public DatabaseIdentifierCaser(DatabaseMetaData databaseMetaData) throws SQLException {
            quoteString = databaseMetaData.getIdentifierQuoteString();
            storeUpperCase = databaseMetaData.storesUpperCaseIdentifiers();
            storeUpperCaseQuoted = databaseMetaData.storesUpperCaseQuotedIdentifiers();
            storeLowerCase = databaseMetaData.storesLowerCaseIdentifiers();
            storeLowerCaseQuoted = databaseMetaData.storesLowerCaseQuotedIdentifiers();
        }

        public String apply(String identifier) {
            if (isQuoted(identifier)) {
                identifier = applyQuoted(identifier);
            } else {
                if (storeLowerCase) {
                    identifier = lowerCase(identifier);
                } else if (storeUpperCase) {
                    identifier = upperCase(identifier);
                }
            }
            return identifier;
        }

        private boolean isQuoted(String identifier) {
            boolean quoted = false;
            if (isNotBlank(quoteString)) {
                quoted = startsWith(identifier, quoteString) && endsWith(identifier, quoteString);
            }
            return quoted;
        }

        private String applyQuoted(String identifier) {
            String unquotedIdentifier = substringBetween(identifier, quoteString);
            if (storeLowerCaseQuoted) {
                unquotedIdentifier = lowerCase(unquotedIdentifier);
            } else if (storeUpperCaseQuoted) {
                unquotedIdentifier = upperCase(unquotedIdentifier);
            }
            return unquotedIdentifier;
        }

    }

}
