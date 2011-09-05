package org.jarb.migrations.liquibase;

import static junit.framework.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests for the LiquibaseMigratorMain class.
 * 
 * @author BVos
 */
public class LiquibaseMigratorMainTest {

    @Test
    public void testDbMigration() throws SQLException {
        String[] migrationArguments = { "-changelogPath", "src/test/resources/changelog.groovy", "-dbPassword", "", "-dbUrl",
                "jdbc:hsqldb:mem:com.nidera.pcat", "-dbUser", "sa", "-driverClass", "org.hsqldb.jdbcDriver" };
        LiquibaseMigratorMain.main(migrationArguments);
    }

    @Test
    public void testNoArgs() throws SQLException {
        ByteArrayOutputStream baosOut = new ByteArrayOutputStream();
        ByteArrayOutputStream baosErr = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baosOut));
        System.setErr(new PrintStream(baosErr));
        try {
            LiquibaseMigratorMain.main(new String[] { "" });
            assertTrue(baosOut.toString().startsWith("Usage"));
            assertTrue(baosErr.toString().indexOf("Exception") > 0);
        } finally {
            System.setOut(System.out);
            System.setErr(System.err);
        }
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidDriverClass() throws SQLException {
        String[] args = { "-changelogPath", "src/main/db/changelog.groovy", "-dbPassword", "", "-dbUrl", "jdbc:hsqldb:mem:com.nidera.pcat", "-dbUser", "sa",
                "-driverClass", "org.hsqldb.invalidDriver" };
        LiquibaseMigratorMain.main(args);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidDbUser() throws SQLException {
        String[] args = { "-changelogPath", "src/main/db/changelog.groovy", "-dbPassword", "", "-dbUrl", "jdbc:hsqldb:mem:com.nidera.pcat", "-dbUser",
                "unknown", "-driverClass", "org.hsqldb.jdbcDriver" };
        LiquibaseMigratorMain.main(args);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidDbScript() throws SQLException {
        String[] args = { "-changelogPath", "src/test/resources/failing-changelog.groovy", "-dbPassword", "", "-dbUrl", "jdbc:hsqldb:mem:com.nidera.pcat",
                "-dbUser", "sa", "-driverClass", "org.hsqldb.jdbcDriver" };
        LiquibaseMigratorMain.main(args);
    }

    @Test
    @Ignore("Only for manual use; needs the oracle sandbox db")
    public void testDbMigrationOracle() throws SQLException {
        String[] args = { "-changelogPath", "src/main/db/changelog.groovy", "-dbPassword", "pcat3", "-dbUrl",
                "jdbc:oracle:thin:@nidrtd57.nidera.nl:1521:s01ddt", "-dbUser", "s01_pcat3", "-driverClass", "oracle.jdbc.OracleDriver", "-sqlLogPath",
                "c:/logging.txt" };
        LiquibaseMigratorMain.main(args);
    }
}
