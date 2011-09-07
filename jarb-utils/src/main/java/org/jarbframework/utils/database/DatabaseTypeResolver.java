package org.jarbframework.utils.database;

import javax.sql.DataSource;

public interface DatabaseTypeResolver {
    
    DatabaseType resolve(DataSource dataSource);

}
