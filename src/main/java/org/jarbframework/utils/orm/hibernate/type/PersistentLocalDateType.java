/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.utils.orm.hibernate.type;

import java.io.Serializable;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;

/**
 * 
 *
 * @author Jeroen van Schagen
 * @since Oct 10, 2014
 */
public class PersistentLocalDateType extends UserTypeSupport implements Serializable {

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] sqlTypes() {
        return new int[] { Types.DATE };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> returnedClass() {
        return LocalDate.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        Date date = rs.getDate(names[0]);
        return date != null ? date.toLocalDate() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        LocalDate date = (LocalDate) value;
        if (date != null) {
            st.setDate(index, Date.valueOf(date));
        } else {
            st.setNull(index, Types.DATE);
        }
    }

}
