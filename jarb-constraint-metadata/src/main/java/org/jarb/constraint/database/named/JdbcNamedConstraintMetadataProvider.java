package org.jarb.constraint.database.named;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.jarb.utils.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class JdbcNamedConstraintMetadataProvider implements NamedConstraintMetadataProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcNamedConstraintMetadataProvider.class);
    private final DataSource dataSource;

    /**
     * Construct a new {@link JdbcNamedConstraintMetadataProvider}.
     * @param dataSource data source reference
     */
    public JdbcNamedConstraintMetadataProvider(DataSource dataSource) {
        Assert.notNull(dataSource, "Property 'data source' cannot be null.");
        this.dataSource = dataSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<NamedConstraintMetadata> all() {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData metadata = connection.getMetaData();
            Set<NamedConstraintMetadata> namedConstraintMetadata = new HashSet<NamedConstraintMetadata>();
            namedConstraintMetadata.addAll(getPrimaryKeyMetadata(metadata));
            namedConstraintMetadata.addAll(getCheckMetadata(metadata));
            namedConstraintMetadata.addAll(getUniqueIndexMetadata(metadata));
            return namedConstraintMetadata;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JdbcUtils.closeQuietly(connection);
        }
    }

    private Set<String> getTableNames(DatabaseMetaData metadata) throws SQLException {
        Set<String> tableNames = new HashSet<String>();
        ResultSet resultSet = metadata.getTables(null, null, null, null);
        while (resultSet.next()) {
            tableNames.add(resultSet.getString("TABLE_NAME"));
        }
        return tableNames;
    }

    private Set<NamedConstraintMetadata> getPrimaryKeyMetadata(DatabaseMetaData metadata) throws SQLException {
        Set<NamedConstraintMetadata> primaryKeyMetadata = new HashSet<NamedConstraintMetadata>();
        for (String tableName : getTableNames(metadata)) {
            final ResultSet resultSet = metadata.getPrimaryKeys(null, null, tableName);
            while (resultSet.next()) {
                String primaryKeyName = resultSet.getString("PK_NAME");
                if (StringUtils.isNotBlank(primaryKeyName)) {
                    primaryKeyMetadata.add(new NamedConstraintMetadata(primaryKeyName, NamedConstraintType.PRIMARY_KEY));
                } else {
                    LOGGER.info("The primary key for '{}' has no name, and thus will not be loaded.", tableName);
                }
            }
        }
        return primaryKeyMetadata;
    }

    private Set<NamedConstraintMetadata> getCheckMetadata(DatabaseMetaData metadata) {
        // TODO: Figure out how this works
        // metadata.getFunctions(catalog, schemaPattern, functionNamePattern)
        return Collections.emptySet();
    }

    private Set<NamedConstraintMetadata> getUniqueIndexMetadata(DatabaseMetaData metadata) throws SQLException {
        Set<NamedConstraintMetadata> uniqueIndexMetadata = new HashSet<NamedConstraintMetadata>();
        for (String tableName : getTableNames(metadata)) {
            final ResultSet resultSet = metadata.getIndexInfo(null, null, tableName, true, true);
            while (resultSet.next()) {
                String indexName = resultSet.getString("INDEX_NAME");
                if (StringUtils.isNotBlank(indexName)) {
                    uniqueIndexMetadata.add(new NamedConstraintMetadata(indexName, NamedConstraintType.UNIQUE_INDEX));
                } else {
                    System.out.println(tableName);
                }
            }
        }
        return uniqueIndexMetadata;
    }

}
