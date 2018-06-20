/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.utils.orm.hibernate.type;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.io.Serializable;
import java.sql.*;
import java.time.LocalDateTime;

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
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        Timestamp timestamp = rs.getTimestamp(names[0]);
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        LocalDateTime time = (LocalDateTime) value;
        if (time != null) {
            st.setTimestamp(index, Timestamp.valueOf(time));
        } else {
            st.setNull(index, Types.TIMESTAMP);
        }
    }

}
