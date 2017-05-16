package org.jarbframework.utils.orm.hibernate;

import org.hibernate.StatelessSession;

/**
 * Extension of the {@link StatelessSession} which is able to flush the JDBC statements to the database.
 *
 * @author Willem Dekker
 */
public interface FlushableStatelessSession extends StatelessSession {

    /**
     * Flush all pending JDBC statements to the database.
     */
    void flush();

}
