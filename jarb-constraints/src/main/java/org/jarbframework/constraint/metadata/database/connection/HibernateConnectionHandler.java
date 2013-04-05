package org.jarbframework.constraint.metadata.database.connection;

import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.Session;
import org.hibernate.jdbc.ReturningWork;
import org.jarbframework.utils.Asserts;
import org.jarbframework.utils.JdbcConnectionCallback;

public class HibernateConnectionHandler implements ConnectionHandler {

    private final Session session;
    
    public HibernateConnectionHandler(Session session) {
        this.session = Asserts.notNull(session, "Session cannot be null.");
    }
    
    @Override
    public <T> T execute(final JdbcConnectionCallback<T> callback) {
        return session.doReturningWork(new ReturningWork<T>() {
            
            @Override
            public T execute(Connection connection) throws SQLException {
                return callback.doWork(connection);
            }
            
        });
    }
    
}
