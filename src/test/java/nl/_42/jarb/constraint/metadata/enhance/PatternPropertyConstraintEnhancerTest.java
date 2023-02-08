/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.constraint.metadata.enhance;

import javax.validation.constraints.Pattern;
import nl._42.jarb.constraint.metadata.PropertyConstraintDescription;
import nl._42.jarb.utils.bean.PropertyReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PatternPropertyConstraintEnhancerTest {
    
    private PatternPropertyConstraintEnhancer enhancer = new PatternPropertyConstraintEnhancer();
    
    @Test
    public void testEnhance() {
        PropertyReference reference = new PropertyReference(MyPatternBean.class, "patterned");
        PropertyConstraintDescription description = new PropertyConstraintDescription(reference, String.class);

        enhancer.enhance(description);

        Assertions.assertEquals("test", description.getPattern());
    }
    
    @Test
    public void testNoAnnotation() {
        PropertyReference reference = new PropertyReference(MyPatternBean.class, "empty");
        PropertyConstraintDescription description = new PropertyConstraintDescription(reference, String.class);
        
        enhancer.enhance(description);

        Assertions.assertNull(description.getPattern());
    }
    
    public static class MyPatternBean {
        
        @Pattern(regexp = "test")
        private String patterned;
        
        @SuppressWarnings("unused")
        private String empty;
        
    }

}
