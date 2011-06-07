package org.jarb.constraint.database.named;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * Retrieves named constraint information from a database table (view).
 * 
 * @author Jeroen van Schagen
 * @since 6-6-2011
 */
public class JdbcNamedConstraintMetadataProvider implements NamedConstraintMetadataProvider {
    /** Maps result set instances to named constraint metadata **/
    private static final RowMapper<NamedConstraintMetadata> ROW_MAPPER = new NamedConstraintMetadataRowMapper();
    /** Allows us to query the database. **/
    private final JdbcTemplate jdbcTemplate;

    /**
     * Construct a new {@link JdbcNamedConstraintMetadataProvider}.
     * @param dataSource provides access to our database
     */
    public JdbcNamedConstraintMetadataProvider(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<NamedConstraintMetadata> all() {
        return new HashSet<NamedConstraintMetadata>(jdbcTemplate.query("SELECT * FROM named_constraint_metadata", ROW_MAPPER));
    }

    /**
     * Row mapper for named constraint metadata.
     */
    private static class NamedConstraintMetadataRowMapper implements RowMapper<NamedConstraintMetadata> {

        /**
         * {@inheritDoc}
         */
        @Override
        public NamedConstraintMetadata mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            String name = resultSet.getString("name");
            NamedConstraintType type = typeFor(resultSet.getString("type"));
            return new NamedConstraintMetadata(name, type);
        }

        /**
         * Retrieve the constraint type as enumeration constant.
         * @param typeName string representation of our type
         * @return enumeration constant, if any
         */
        private NamedConstraintType typeFor(String typeName) {
            // Enumeration constants are always in uppercase
            return NamedConstraintType.valueOf(typeName.toUpperCase());
        }

    }

}
