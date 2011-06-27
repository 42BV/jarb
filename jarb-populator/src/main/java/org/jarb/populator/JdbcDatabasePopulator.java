package org.jarb.populator;

import java.sql.Connection;

import javax.sql.DataSource;

import org.jarb.utils.database.JdbcUtils;
import org.springframework.util.Assert;

/**
 * Database populator that works on the JDBC interface. Whenever
 * {@link #populate()} is invoked, a new connection is created,
 * which will get closed automatically after execution.
 * 
 * @author Jeroen van Schagen
 * @since 20-06-2011
 */
public abstract class JdbcDatabasePopulator implements DatabasePopulator {
    private DataSource dataSource;

    /**
     * Retrieve the JDBC data source on which this populator runs.
     * @return current JDBC data source
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Modify the JDBC data source on which this populator runs.
     * @param dataSource new JDBC data source
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * {@inheritDoc}
     * <p>
     * JDBC connection is opened and closed during execution.
     */
    @Override
    public final void populate() throws Exception {
        Assert.state(dataSource != null, "Data source cannot be null");
        
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            // Use connection to populate database
            populateInConnection(connection);
        } finally {
            JdbcUtils.closeQuietly(connection);
        }
    }

    /**
     * Populate the database using a JDBC connection.
     * @param connection JDBC connection to the database
     * @throws Exception whenever an unrecoverable exception occurs
     */
    protected abstract void populateInConnection(Connection connection) throws Exception;

}
