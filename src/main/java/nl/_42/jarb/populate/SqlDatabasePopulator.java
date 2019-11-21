package nl._42.jarb.populate;

import nl._42.jarb.utils.jdbc.JdbcUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.io.File;
import java.io.InputStream;

import static java.lang.String.format;
import static nl._42.jarb.utils.Asserts.notNull;

/**
 * Update the database by executing an SQL resource.
 * 
 * @author Jeroen van Schagen
 * @since 01-06-2011
 */
public class SqlDatabasePopulator implements DatabasePopulator {

    /** Data source being populated. **/
    private final DataSource dataSource;
    
    /** Resource to use. **/
    private final Resource resource;
    
    /** If we should fail if the resource does not exist. **/
    private boolean failIfNotExists = true;

    public SqlDatabasePopulator(DataSource dataSource, InputStream inputStream) {
        this(dataSource, new InputStreamResource(inputStream));
    }
    
    public SqlDatabasePopulator(DataSource dataSource, File file) {
        this(dataSource, new FileSystemResource(file));
    }
    
    public SqlDatabasePopulator(DataSource dataSource, String path) {
        this(dataSource, new File(path));
    }

    public SqlDatabasePopulator(DataSource dataSource, Resource resource) {
        this.dataSource = notNull(dataSource, "Data source cannot be null.");
        this.resource = notNull(resource, "Resource cannot be null.");
    }

    public static SqlDatabasePopulator fromSql(DataSource dataSource, String sql) {
        return new SqlDatabasePopulator(dataSource, new ByteArrayResource(sql.getBytes()));
    }
    
    public SqlDatabasePopulator ifExists() {
        failIfNotExists = false;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        if (resource.exists()) {
            final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(resource);
            
            JdbcUtils.doWithConnection(dataSource, connection -> {
                boolean autoCommit  = connection.getAutoCommit();
                try {
                    connection.setAutoCommit(true);
                    populator.populate(connection);
                } finally {
                    connection.setAutoCommit(autoCommit);
                }
                return null;
            });
        } else if (failIfNotExists) {
            throw new IllegalStateException(
              format("Resource '%s' does not exist.", resource.getFilename()));
        }
    }

}
