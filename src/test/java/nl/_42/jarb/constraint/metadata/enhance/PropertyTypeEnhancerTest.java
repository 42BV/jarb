/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.constraint.metadata.enhance;

import javax.persistence.Column;
import nl._42.jarb.constraint.metadata.PropertyConstraintDescription;
import nl._42.jarb.constraint.metadata.types.Currency;
import nl._42.jarb.constraint.metadata.types.PropertyType;
import nl._42.jarb.utils.bean.PropertyReference;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PropertyTypeEnhancerTest {
    
    @Test
    public void testPropertyType() {
        PropertyTypeEnhancer enhancer = new PropertyTypeEnhancer();
        PropertyReference reference = new PropertyReference(PropertyTypeBean.class, "myCustomValue");
        PropertyConstraintDescription description = new PropertyConstraintDescription(reference, String.class);
        enhancer.enhance(description);
        assertTrue(description.getTypes().contains("custom"));
    }
    
    @Test
    public void testCustomAnnotation() {
        PropertyTypeEnhancer enhancer = new PropertyTypeEnhancer();
        PropertyReference reference = new PropertyReference(PropertyTypeBean.class, "myCurrencyValue");
        PropertyConstraintDescription description = new PropertyConstraintDescription(reference, String.class);
        enhancer.enhance(description);
        assertTrue(description.getTypes().contains("currency"));
    }
    
    @Test
    public void testCustomAnnotationWithoutType() {
        PropertyTypeEnhancer enhancer = new PropertyTypeEnhancer();
        PropertyReference reference = new PropertyReference(PropertyTypeBean.class, "myOtherValue");
        PropertyConstraintDescription description = new PropertyConstraintDescription(reference, String.class);
        enhancer.enhance(description);
        assertTrue(description.getTypes().isEmpty());
    }
    
    protected static class PropertyTypeBean {
        
        @PropertyType("custom")
        private String myCustomValue;
        
        @Currency
        private String myCurrencyValue;
        
        @Column
        private String myOtherValue;

    }

}
