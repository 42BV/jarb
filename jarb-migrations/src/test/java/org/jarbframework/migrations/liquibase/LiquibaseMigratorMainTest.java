package org.jarbframework.migrations.liquibase;

import java.io.IOException;

import org.junit.Test;

public class LiquibaseMigratorMainTest {

    /**
     * Perform a successful migration.
     */
    @Test
    public void testDbMigration() throws IOException {
        LiquibaseMigratorMain.main("src/test/resources/liquibase-correct.properties");
    }

    /**
     * Use a base directory and relative file name.
     */
    @Test
    public void testDbMigrationRelativePath() throws IOException {
        LiquibaseMigratorMain.main("src/test/resources/liquibase-relative.properties");
    }

    /**
     * Specified driver class has to be known by the class loader.
     */
    @Test(expected = RuntimeException.class)
    public void testInvalidDriverClass() throws IOException {
        LiquibaseMigratorMain.main("src/test/resources/liquibase-invalid-driver.properties");
    }

    /**
     * Database user should be valid.
     */
    @Test(expected = RuntimeException.class)
    public void testConnectionCouldNotBeOpened() throws IOException {
        LiquibaseMigratorMain.main("src/test/resources/liquibase-invalid-user.properties");
    }

    /**
     * Specified change log should exist.
     */
    @Test(expected = RuntimeException.class)
    public void testChangeLogDoesNotExist() throws IOException {
        LiquibaseMigratorMain.main("src/test/resources/liquibase-unknown-changelog.properties");
    }

}
