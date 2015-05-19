package org.jarbframework.init.migrate;

/**
 * Migration listener that automatically opens the HSQL
 * database manager after completing a migration. 
 *
 * @author Jeroen van Schagen
 * @since Oct 27, 2014
 */
public class HsqlOpenDatabaseManagerListener implements MigrationListener {
    
    private final String url;
    
    public HsqlOpenDatabaseManagerListener(String url) {
        this.url = url;
    }

    @Override
    public void afterMigrate() {
        org.hsqldb.util.DatabaseManagerSwing.main(new String[] { "--url", url, "--noexit" });
    }
    
}
