package nl._42.jarb.constraint.metadata.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import nl._42.jarb.utils.StringUtils;
import nl._42.jarb.utils.jdbc.JdbcConnectionCallback;
import nl._42.jarb.utils.jdbc.JdbcUtils;
import nl._42.jarb.utils.orm.ColumnReference;
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

    private final DataSource dataSource;

    private DatabaseIdentifierCaser identifierCaser;

    private String catalog;
    
    private String schema;
    
    public JdbcColumnMetadataRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ColumnMetadata getMetadata(final ColumnReference columnReference) {
        return JdbcUtils.doWithConnection(dataSource, new JdbcConnectionCallback<ColumnMetadata>() {
           
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
                try {
                    return mapToColumnMetadata(columnReference, resultSet);
                } finally {
                    resultSet.close(); // Always close result set, this prevents unclosed cursors in pools
                }
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
            
            Integer columnSize = getValueAsInteger(resultSet, "COLUMN_SIZE");
            if (columnSize != null && columnSize > 0) {
    			columnMetadata.setMaximumLength(columnSize);
            }
			
            Integer fractionLength = getValueAsInteger(resultSet, "DECIMAL_DIGITS");
            if (fractionLength != null) {
    			columnMetadata.setFractionLength(Math.max(fractionLength, 0));
            }
			
            columnMetadata.setRadix(getValueAsInteger(resultSet, "NUM_PREC_RADIX"));
            columnMetadata.setRequired("NO".equals(getOptionalValue(resultSet, "IS_NULLABLE")));
            columnMetadata.setAutoIncrement("YES".equals(getOptionalValue(resultSet, "IS_AUTOINCREMENT")));
        }
        return columnMetadata;
    }

    private Integer getValueAsInteger(ResultSet resultSet, String columnLabel) throws SQLException {
        Integer value = null;
        String numberAsString = resultSet.getString(columnLabel);
        if (StringUtils.isNotBlank(numberAsString)) {
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
                    identifier = identifier.toLowerCase();
                } else if (storeUpperCase) {
                    identifier = identifier.toUpperCase();
                }
            }
            return identifier;
        }

        private boolean isQuoted(String identifier) {
            boolean quoted = false;
            if (StringUtils.isNotBlank(quoteString)) {
                quoted = identifier.startsWith(quoteString) && identifier.endsWith(quoteString);
            }
            return quoted;
        }

        private String applyQuoted(String identifier) {
            int startIndex = quoteString.length();
            int endIndex = identifier.length() - startIndex;
            String unquotedIdentifier = identifier.substring(startIndex, endIndex);
            if (storeLowerCaseQuoted) {
                unquotedIdentifier = unquotedIdentifier.toLowerCase();
            } else if (storeUpperCaseQuoted) {
                unquotedIdentifier = unquotedIdentifier.toUpperCase();
            }
            return unquotedIdentifier;
        }

    }

}
