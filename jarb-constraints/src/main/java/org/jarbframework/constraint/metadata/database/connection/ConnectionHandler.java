package org.jarbframework.constraint.metadata.database.connection;

import org.jarbframework.utils.JdbcConnectionCallback;

public interface ConnectionHandler {

    <T> T execute(JdbcConnectionCallback<T> callback);
    
}
