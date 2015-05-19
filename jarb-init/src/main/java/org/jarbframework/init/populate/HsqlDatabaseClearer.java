/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.init.populate;

import javax.sql.DataSource;

import org.jarbframework.init.DatabasePopulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Clears the entire HSQL database, while keeping the schema.
 *
 * @author Jeroen van Schagen
 * @since Apr 14, 2015
 */
public class HsqlDatabaseClearer implements DatabasePopulator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HsqlDatabaseClearer.class);

    private final JdbcTemplate jdbcTemplate;
    
    public HsqlDatabaseClearer(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        LOGGER.info("Removing all data from database...");
        jdbcTemplate.execute("TRUNCATE SCHEMA public AND COMMIT");
    }

}
