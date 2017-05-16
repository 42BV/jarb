package org.jarbframework.constraint.metadata.enhance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.jarbframework.constraint.metadata.PropertyConstraintDescription;
import org.jarbframework.utils.bean.PropertyReference;
import org.junit.Before;
import org.junit.Test;

public class MinMaxNumberAnnotationPropertyEnhancerTest {
    
    private PropertyConstraintEnhancer enhancer;

    private PropertyConstraintDescription description;

    @Before
    public void setUp() {
        enhancer = new MinMaxNumberAnnotationPropertyEnhancer();
        description = new PropertyConstraintDescription(new PropertyReference(User.class, "age"), Long.class);
    }

    @Test
    public void testEnhance() {
        assertNull(description.getMin());
        assertNull(description.getMax());

        enhancer.enhance(description);

        assertEquals(Long.valueOf(1), description.getMin());
        assertEquals(Long.valueOf(120), description.getMax());
    }

    @Test
    public void testUnknownType() {
        description = new PropertyConstraintDescription(new PropertyReference(User.class, "name"), String.class);

        assertNull(description.getMin());
        assertNull(description.getMax());

        enhancer.enhance(description);

        assertNull(description.getMin());
        assertNull(description.getMax());
    }

    public static class User {

        @Min(0)
        @Max(120)
        private long age;
        
        @Min(42)
        private String name;

        @Min(1)
        @Max(130)
        public long getAge() {
            return age;
        }
        
        public String getName() {
            return name;
        }

    }

}
