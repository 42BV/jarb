package org.jarb.constraint.database.named;

import java.util.Set;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Retrieves named constraint information from a database table (view).
 * 
 * @author Jeroen van Schagen
 * @since 6-6-2011
 */
public class JdbcNamedConstraintMetadataProvider implements NamedConstraintMetadataProvider {
    private final JdbcTemplate jdbcTemplate;

    public JdbcNamedConstraintMetadataProvider(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<NamedConstraintMetadata> all() {
        return null; // TODO
    }

}
