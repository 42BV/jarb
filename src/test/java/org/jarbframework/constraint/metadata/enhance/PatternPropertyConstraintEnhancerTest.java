/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.constraint.metadata.enhance;

import javax.validation.constraints.Pattern;

import org.jarbframework.constraint.metadata.PropertyConstraintDescription;
import org.jarbframework.utils.bean.PropertyReference;
import org.junit.Assert;
import org.junit.Test;

public class PatternPropertyConstraintEnhancerTest {
    
    private PatternPropertyConstraintEnhancer enhancer = new PatternPropertyConstraintEnhancer();
    
    @Test
    public void testEnhance() {
        PropertyReference reference = new PropertyReference(MyPatternBean.class, "patterned");
        PropertyConstraintDescription description = new PropertyConstraintDescription(reference, String.class);

        enhancer.enhance(description);
        
        Assert.assertEquals("test", description.getPattern());
    }
    
    @Test
    public void testNoAnnotation() {
        PropertyReference reference = new PropertyReference(MyPatternBean.class, "empty");
        PropertyConstraintDescription description = new PropertyConstraintDescription(reference, String.class);
        
        enhancer.enhance(description);
        
        Assert.assertNull(description.getPattern());
    }
    
    public static class MyPatternBean {
        
        @Pattern(regexp = "test")
        private String patterned;
        
        @SuppressWarnings("unused")
        private String empty;
        
    }

}
