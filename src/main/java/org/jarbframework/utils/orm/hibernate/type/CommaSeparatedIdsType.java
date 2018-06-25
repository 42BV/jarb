/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.utils.orm.hibernate.type;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.springframework.util.StringUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Stores a collection of long identifiers as a string.
 *
 * @author Jeroen van Schagen
 * @since Jun 4, 2015
 */
public class CommaSeparatedIdsType extends UserTypeSupport {

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] sqlTypes() {
        return new int[] { Types.VARCHAR };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> returnedClass() {
        return Set.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        String values = rs.getString(names[0]);
        Set<Long> result = new HashSet<Long>();
        for (String value : StringUtils.commaDelimitedListToSet(values)) {
            result.add(Long.valueOf(value));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value != null) {
            Collection<?> collection = (Collection<?>) value;
            String argument = StringUtils.collectionToDelimitedString(collection, ",");
            st.setString(index, argument);
        } else {
            st.setNull(index, Types.VARCHAR);
        }
    }
    
}
