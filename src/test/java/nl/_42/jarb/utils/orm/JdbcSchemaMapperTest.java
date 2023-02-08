/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.utils.orm;

import nl._42.jarb.utils.bean.PropertyReference;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class JdbcSchemaMapperTest {
    
    private final JdbcSchemaMapper mapper = new JdbcSchemaMapper();

    @Test
    public void testMap() {
        PropertyReference property = new PropertyReference(MyMappingBean.class, "myProperty");
        ColumnReference column = mapper.getColumnReference(property);
        assertEquals("my_mapping_bean", column.getTableName());
        assertEquals("my_property", column.getColumnName());
    }
    
    @Test
    public void testIsEmbeddable() {
        assertFalse(mapper.isEmbeddable(MyMappingBean.class));
    }
    
    protected static class MyMappingBean {
        
        @SuppressWarnings("unused")
        private String myProperty;
        
    }

}
