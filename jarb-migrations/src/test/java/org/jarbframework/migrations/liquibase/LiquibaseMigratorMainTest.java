package org.jarbframework.migrations.liquibase;

import static junit.framework.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

public class LiquibaseMigratorMainTest {

    /**
     * Perform a successful migration.
     */
    @Test
    public void testDbMigration() {
        String[] args = { 
                "-changeLogPath", "src/test/resources/changelog.groovy", 
                "-dbPassword", "", 
                "-dbUrl", "jdbc:hsqldb:mem:com.nidera.pcat", 
                "-dbUser", "sa", 
                "-driverClass", "org.hsqldb.jdbcDriver", 
                "-dropFirst"
            };
        LiquibaseMigratorMain.main(args);
    }

    /**
     * Use a base directory and relative file name.
     */
    @Test
    public void testDbMigrationRelativePath() {
        String[] args = { 
                "-changeLogBaseDir", "src/test/resources", 
                "-changeLogPath", "changelog.groovy", 
                "-dbPassword", "", 
                "-dbUrl", "jdbc:hsqldb:mem:com.nidera.pcat", 
                "-dbUser", "sa", 
                "-driverClass", "org.hsqldb.jdbcDriver", 
                "-dropFirst"
            };
        LiquibaseMigratorMain.main(args);
    }

    /**
     * Specified driver class has to be known by the class loader.
     */
    @Test(expected = RuntimeException.class)
    public void testInvalidDriverClass() {
        String[] args = { 
                "-changeLogBaseDir", "src/test/resources", 
                "-changeLogPath", "changelog.groovy", 
                "-dbPassword", "", 
                "-dbUrl", "jdbc:hsqldb:mem:com.nidera.pcat", 
                "-dbUser", "sa", 
                "-driverClass", "org.invalid.Driver", 
                "-dropFirst"
            };
        LiquibaseMigratorMain.main(args);
    }

    /**
     * Database user should be valid.
     */
    @Test(expected = RuntimeException.class)
    public void testConnectionCouldNotBeOpened() {
        String[] args = { 
                "-changeLogBaseDir", "src/test/resources", 
                "-changeLogPath", "changelog.groovy", 
                "-dbPassword", "", 
                "-dbUrl", "jdbc:hsqldb:mem:com.nidera.pcat", 
                "-dbUser", "unknown", 
                "-driverClass", "org.hsqldb.jdbcDriver", 
                "-dropFirst"
            };
        LiquibaseMigratorMain.main(args);
    }

    /**
     * Specified change log should exist.
     */
    @Test(expected = RuntimeException.class)
    public void testChangeLogDoesNotExist() {
        String[] args = { 
                "-changeLogBaseDir", "src/test/resources", 
                "-changeLogPath", "non-existing-changelog.groovy", 
                "-dbPassword", "", 
                "-dbUrl", "jdbc:hsqldb:mem:com.nidera.pcat", 
                "-dbUser", "sa", 
                "-driverClass", "org.hsqldb.jdbcDriver", 
                "-dropFirst"
            };
        LiquibaseMigratorMain.main(args);
    }

    /**
     * Whenever invalid arguments are provided, show the usage.
     */
    @Test
    public void testInvalidUsage() {
        PrintStream out = System.out;
        PrintStream err = System.err;
        
        ByteArrayOutputStream tmpOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(tmpOut));
        ByteArrayOutputStream tmpErr = new ByteArrayOutputStream();
        System.setErr(new PrintStream(tmpErr));
        
        try {
            LiquibaseMigratorMain.main(new String[] { "" });
            assertTrue(tmpOut.toString().startsWith("Usage"));
            assertTrue(tmpErr.toString().indexOf("Exception") > 0);
        } finally {
            System.setOut(out);
            System.setErr(err);
        }
    }

}
