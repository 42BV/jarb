/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.utils.orm.hibernate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConventionNamingStrategyTest {
    
    private ConventionNamingStrategy namingStrategy;
    
    @Before
    public void setUp() {
        namingStrategy = new ConventionNamingStrategy();
    }
    
    @Test
    public void testTableName() {
        Assert.assertEquals("my_table", namingStrategy.tableName("MyTable"));
    }
    
    @Test
    public void testColumnName() {
        Assert.assertEquals("my_column", namingStrategy.columnName("MyColumn"));
    }
    
    @Test
    public void testPropertyToColumnName() {
        Assert.assertEquals("my_property", namingStrategy.propertyToColumnName("MyProperty"));
    }
    
    @Test
    public void testForeignKeyColumnName() {
        Assert.assertEquals("my_property_id", namingStrategy.foreignKeyColumnName("MyProperty", "", "", ""));
    }

}
