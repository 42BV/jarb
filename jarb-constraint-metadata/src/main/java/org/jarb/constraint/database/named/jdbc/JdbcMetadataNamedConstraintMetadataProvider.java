package org.jarb.constraint.database.named.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Set;

import javax.sql.DataSource;

import org.jarb.constraint.database.named.NamedConstraintMetadata;
import org.jarb.constraint.database.named.NamedConstraintMetadataProvider;
import org.jarb.utils.JdbcUtils;
import org.springframework.util.Assert;

/**
 * Template for JDBC metadata oriented named constraint providers.
 * 
 * @author Jeroen van Schagen
 * @since 6-6-2011
 */
public abstract class JdbcMetadataNamedConstraintMetadataProvider implements NamedConstraintMetadataProvider {
    private final DataSource dataSource;

    /**
     * Construct a new {@link JdbcMetadataNamedConstraintMetadataProvider}.
     * @param dataSource data source reference
     */
    public JdbcMetadataNamedConstraintMetadataProvider(DataSource dataSource) {
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
            return extractConstraintMetadata(connection.getMetaData());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JdbcUtils.closeQuietly(connection);
        }
    }

    /**
     * Extract constraint metadata from the JDBC metadata.
     * @param metadata JDBC metadata
     * @return extracted constraint metadata
     */
    protected abstract Set<NamedConstraintMetadata> extractConstraintMetadata(DatabaseMetaData metadata) throws SQLException;

}
