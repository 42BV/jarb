package org.jarb.migrations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:liquibase-context.xml")
public class MigratingDataSourceTest {

    @Autowired
    @Qualifier("migratingDataSource")
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void buildTemplate() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Verifies that the 'persons' table, as described below, is created using our 
     * migrating data source. We assert this by creating a new person record and
     * again retrieving it.
     * 
     * <p>
     * 
     * Migration script (changelog.groovy):
     * <pre>
     *  createTable(tableName: "persons") {
     *      column(autoIncrement: true, name: "id", type: "BIGINT") {
     *          constraints(nullable: false, primaryKey: true)
     *      }
     *      column(name: "name", type: "VARCHAR(255)", defaultValue: "henk")
     *  }
     * </pre>
     */
    @Test
    public void testMigrateAndFeed() {
        jdbcTemplate.execute("INSERT INTO persons (id) values (1)");
        assertEquals("henk", jdbcTemplate.queryForObject("SELECT name FROM persons WHERE id = 1", String.class));
    }

    // Mocked test scenarios

    /**
     * Assert that a runtime exception is thrown, whenever a connection could not be opened.
     * @throws SQLException the connection failure exception
     */
    public void testMigrateWithoutConnection() throws SQLException {
        DataSource dataSourceMock = EasyMock.createMock(DataSource.class);
        MigratingDataSource migratingDataSource = new MigratingDataSource();
        migratingDataSource.setDelegate(dataSourceMock);

        // Enforce our data source to throw an exception during connection retrieval
        final Throwable invalidConnectionException = new SQLException("No connection");
        EasyMock.expect(dataSourceMock.getConnection()).andThrow(invalidConnectionException);
        EasyMock.replay(dataSourceMock);

        try {
            migratingDataSource.migrate();
            fail("Expection a runtime exception, whenever the connection could not be opened");
        } catch (RuntimeException e) {
            assertEquals(invalidConnectionException, e.getCause());
        }

        EasyMock.verify(dataSourceMock);
    }

    /**
     * Assert that a runtime exception is thrown, whenever a connection could not be closed.
     * @throws SQLException the connection failure exception
     */
    @Test
    public void testMigrateWithUnclosableConnection() throws SQLException {
        DataSource dataSourceMock = EasyMock.createMock(DataSource.class);
        DatabaseMigrator migratorMock = EasyMock.createMock(DatabaseMigrator.class);
        MigratingDataSource migratingDataSource = new MigratingDataSource();
        migratingDataSource.setDelegate(dataSourceMock);
        migratingDataSource.setMigrator(migratorMock);

        // Enforce our connection to throw an exception during closure
        Connection connectionMock = EasyMock.createMock(Connection.class);
        EasyMock.expect(dataSourceMock.getConnection()).andReturn(connectionMock);
        migratorMock.migrate(connectionMock);
        EasyMock.expectLastCall();
        connectionMock.close();
        final Throwable unclosableConnectionException = new SQLException("Stubborn connection");
        EasyMock.expectLastCall().andThrow(unclosableConnectionException);
        EasyMock.replay(dataSourceMock, migratorMock, connectionMock);

        try {
            migratingDataSource.migrate();
            fail("Expecting a runtime exception, whenever the connection could not be closed");
        } catch (RuntimeException e) {
            assertEquals(unclosableConnectionException, e.getCause());
        }

        EasyMock.verify(dataSourceMock, migratorMock, connectionMock);
    }
}
