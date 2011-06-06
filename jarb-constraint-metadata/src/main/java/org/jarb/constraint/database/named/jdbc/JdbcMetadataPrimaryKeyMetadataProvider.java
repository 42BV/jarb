package org.jarb.constraint.database.named.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.jarb.constraint.database.named.NamedConstraintMetadata;
import org.jarb.constraint.database.named.NamedConstraintType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retrieves named constraint metadata based on JDBC metadata.
 * 
 * @author Jeroen van Schagen
 * @since 6-6-2011
 */
public class JdbcMetadataPrimaryKeyMetadataProvider extends JdbcMetadataNamedConstraintMetadataProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcMetadataPrimaryKeyMetadataProvider.class);

    /**
     * Construct a new {@link JdbcMetadataPrimaryKeyMetadataProvider}.
     * @param dataSource data source reference
     */
    public JdbcMetadataPrimaryKeyMetadataProvider(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<NamedConstraintMetadata> extractConstraintMetadata(DatabaseMetaData metadata) throws SQLException {
        Set<NamedConstraintMetadata> primaryKeyMetadata = new HashSet<NamedConstraintMetadata>();
        for (String tableName : JdbcMetadataUtils.getTableNames(metadata)) {
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

}
