/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.utils.orm.hibernate.type;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;

/**
 * 
 *
 * @author Jeroen van Schagen
 * @since Oct 10, 2014
 */
public class PersistentLocalDateTimeType extends UserTypeSupport implements Serializable {

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] sqlTypes() {
        return new int[] { Types.TIMESTAMP };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> returnedClass() {
        return LocalDateTime.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        Timestamp timestamp = rs.getTimestamp(names[0]);
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        LocalDateTime time = (LocalDateTime) value;
        if (time != null) {
            st.setTimestamp(index, Timestamp.valueOf(time));
        } else {
            st.setNull(index, Types.TIMESTAMP);
        }
    }

}
