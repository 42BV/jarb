/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.init;

import org.jarbframework.init.migrate.HsqlOpenDatabaseManagerListener;
import org.jarbframework.init.migrate.MigratingDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * Template configuration for creating a local Hypersonic database.
 *
 * @see LocalDatabaseConfig
 * @author Jeroen van Schagen
 * @since Jun 11, 2015
 */
public abstract class LocalHsqlDatabaseConfig extends LocalDatabaseConfig {
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure(MigratingDatabaseBuilder builder) {
        builder.setType(EmbeddedDatabaseType.HSQL).setName("dev");
        if ("swing".equals(System.getProperty("dbms"))) {
            builder.addListener(new HsqlOpenDatabaseManagerListener("jdbc:hsqldb:mem:dev"));
        }
    }

}
