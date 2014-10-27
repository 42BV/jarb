package org.jarbframework.migrations.liquibase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;

import org.jarbframework.migrations.DatabaseMigrator;
import org.jarbframework.utils.JdbcConnectionCallback;
import org.jarbframework.utils.JdbcUtils;

/**
 * Provides command line support for executing liquibase migrations.
 * This a simple alternative to the liquibase binary. However, this
 * executable only provides update functionality, no rollbacks.
 * 
 * @author Bas de Vos
 * @author Jeroen van Schagen
 * 
 * @since 5 September 2011
 */
public final class LiquibaseMigratorMain {
    
    private static final String DEFAULT_FILE_NAME = "liquibase.properties";

    // Property names
    
    private static final String DRIVER_CLASS = "liquibase.driverClass";
    private static final String URL = "liquibase.url";
    private static final String USER = "liquibase.user";
    private static final String PASSWORD = "liquibase.password";
    private static final String CHANGELOG_BASE_DIR = "liquibase.changeLogBaseDir";
    private static final String CHANGELOG_PATH = "liquibase.changeLogPath";
    private static final String SQL_OUTPUT_PATH = "liquibase.sqlOutputPath";
    private static final String DROP_FIRST = "liquibase.dropFirst";
    private static final String RESET_CHECK_SUM = "liquibase.resetCheckSum";
    
    // Constants

    private static final String TRUE = "true";

    /**
     * Invokes the Liquibase migrations provided.
     * 
     * @param arguments all migration arguments
     */
    public static void main(String... arguments) throws IOException {
        final PropertyAccessor properties = parseProperties(arguments);
        
        String driverClassName = properties.getRequiredProperty(DRIVER_CLASS);
        String url = properties.getRequiredProperty(URL);
        String userName = properties.getRequiredProperty(USER);
        String password = properties.getOptionalProperty(PASSWORD);
        
        final DatabaseMigrator migrator = createDatabaseMigrator(properties);

        JdbcUtils.doWithConnection(driverClassName, url, userName, password, new JdbcConnectionCallback<Void>() {
            
            @Override
            public Void doWork(Connection connection) throws SQLException {
                migrator.migrate(connection);
                connection.commit();
                return null;
            }
            
        });
    }

    private static PropertyAccessor parseProperties(String[] arguments) throws IOException {
        final String propertyPath = arguments.length > 0 ? arguments[0] : DEFAULT_FILE_NAME;

        Properties properties = new Properties();
        properties.load(new FileInputStream(new File(propertyPath)));
        return new PropertyAccessor(properties);
    }

    private static DatabaseMigrator createDatabaseMigrator(PropertyAccessor properties) {
        String changeLogBaseDir = properties.getOptionalProperty(CHANGELOG_BASE_DIR);
        String changeLogPath = properties.getOptionalProperty(CHANGELOG_PATH);
        String sqlOutputPath = properties.getOptionalProperty(SQL_OUTPUT_PATH);
        String dropFirst = properties.getOptionalProperty(DROP_FIRST);
        String resetCheckSum = properties.getOptionalProperty(RESET_CHECK_SUM);

        LiquibaseMigrator migrator = new LiquibaseMigrator(createResourceAccessor(changeLogBaseDir));
        migrator.setChangeLogPath(changeLogPath);
        migrator.setOutputFilePath(sqlOutputPath);
        migrator.setDropFirst(TRUE.equals(dropFirst));
        migrator.setResetCheckSum(TRUE.equals(resetCheckSum));
        return migrator;
    }

    private static ResourceAccessor createResourceAccessor(String changeLogBaseDir) {
        if (changeLogBaseDir == null || changeLogBaseDir.isEmpty()) {
            return new FileSystemResourceAccessor();
        } else {
            return new FileSystemResourceAccessor(changeLogBaseDir);
        }
    }
    
    /**
     * Used to access values from a property file.
     *
     * @author Jeroen van Schagen
     * @since Feb 13, 2014
     */
    private static class PropertyAccessor {
        
        private final Properties properties;
        
        public PropertyAccessor(Properties properties) {
            this.properties = properties;
        }
        
        public String getRequiredProperty(String name) {
            String value = properties.getProperty(name);
            if (value == null) {
                throw new IllegalArgumentException("Property '" + name + "' is missing.");
            }
            return value;
        }
        
        public String getOptionalProperty(String name) {
            return properties.getProperty(name, "");
        }
        
    }

}
