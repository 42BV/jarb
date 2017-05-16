/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.utils.orm;

import org.jarbframework.utils.bean.PropertyReference;
import org.junit.Assert;
import org.junit.Test;

public class JdbcSchemaMapperTest {
    
    private final JdbcSchemaMapper mapper = new JdbcSchemaMapper();

    @Test
    public void testMap() {
        PropertyReference property = new PropertyReference(MyMappingBean.class, "myProperty");
        ColumnReference column = mapper.getColumnReference(property);
        Assert.assertEquals("my_mapping_bean", column.getTableName());
        Assert.assertEquals("my_property", column.getColumnName());
    }
    
    @Test
    public void testIsEmbeddable() {
        Assert.assertFalse(mapper.isEmbeddable(MyMappingBean.class));
    }
    
    protected static class MyMappingBean {
        
        @SuppressWarnings("unused")
        private String myProperty;
        
    }

}
