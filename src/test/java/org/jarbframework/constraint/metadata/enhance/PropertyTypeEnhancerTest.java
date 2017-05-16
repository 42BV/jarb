/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.constraint.metadata.enhance;

import javax.persistence.Column;

import org.jarbframework.constraint.metadata.PropertyConstraintDescription;
import org.jarbframework.constraint.metadata.types.Currency;
import org.jarbframework.constraint.metadata.types.PropertyType;
import org.jarbframework.utils.bean.PropertyReference;
import org.junit.Assert;
import org.junit.Test;

public class PropertyTypeEnhancerTest {
    
    @Test
    public void testPropertyType() {
        PropertyTypeEnhancer enhancer = new PropertyTypeEnhancer();
        PropertyReference reference = new PropertyReference(PropertyTypeBean.class, "myCustomValue");
        PropertyConstraintDescription description = new PropertyConstraintDescription(reference, String.class);
        enhancer.enhance(description);
        Assert.assertTrue(description.getTypes().contains("custom"));
    }
    
    @Test
    public void testCustomAnnotation() {
        PropertyTypeEnhancer enhancer = new PropertyTypeEnhancer();
        PropertyReference reference = new PropertyReference(PropertyTypeBean.class, "myCurrencyValue");
        PropertyConstraintDescription description = new PropertyConstraintDescription(reference, String.class);
        enhancer.enhance(description);
        Assert.assertTrue(description.getTypes().contains("currency"));
    }
    
    @Test
    public void testCustomAnnotationWithoutType() {
        PropertyTypeEnhancer enhancer = new PropertyTypeEnhancer();
        PropertyReference reference = new PropertyReference(PropertyTypeBean.class, "myOtherValue");
        PropertyConstraintDescription description = new PropertyConstraintDescription(reference, String.class);
        enhancer.enhance(description);
        Assert.assertTrue(description.getTypes().isEmpty());
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
