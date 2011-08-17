/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarb.utils.orm;

/**
 * Maps bean (properties) to database tables and columns.
 *
 * @author Jeroen van Schagen
 * @date Aug 16, 2011
 */
public interface SchemaMapper {

    /**
     * Retrieve the table name of a bean.
     * @param beanClass bean class
     * @return table name, if any
     */
    String table(Class<?> beanClass);

    /**
     * Retrieve the column of a bean property.
     * @param beanClass bean class
     * @param propertyName property name
     * @return column name, if any
     */
    String column(Class<?> beanClass, String propertyName);

}
