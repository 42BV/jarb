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

/**
 * Retrieves named constraint metadata based on JDBC metadata.
 * 
 * @author Jeroen van Schagen
 * @since 6-6-2011
 */
public class JdbcMetadataUniqueIndexMetadataProvider extends JdbcMetadataNamedConstraintMetadataProvider {

    /**
     * Construct a new {@link JdbcMetadataUniqueIndexMetadataProvider}.
     * @param dataSource data source reference
     */
    public JdbcMetadataUniqueIndexMetadataProvider(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<NamedConstraintMetadata> extractConstraintMetadata(DatabaseMetaData metadata) throws SQLException {
        Set<NamedConstraintMetadata> uniqueIndexMetadata = new HashSet<NamedConstraintMetadata>();
        for (String tableName : JdbcMetadataUtils.getTableNames(metadata)) {
            final ResultSet resultSet = metadata.getIndexInfo(null, null, tableName, true, true);
            while (resultSet.next()) {
                String indexName = resultSet.getString("INDEX_NAME");
                if (StringUtils.isNotBlank(indexName)) {
                    uniqueIndexMetadata.add(new NamedConstraintMetadata(indexName, NamedConstraintType.UNIQUE_INDEX));
                }
            }
        }
        return uniqueIndexMetadata;
    }

}
