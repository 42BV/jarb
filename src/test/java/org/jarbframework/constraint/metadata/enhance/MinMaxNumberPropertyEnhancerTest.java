package org.jarbframework.constraint.metadata.enhance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jarbframework.constraint.metadata.PropertyConstraintDescription;
import org.jarbframework.utils.bean.PropertyReference;
import org.junit.Before;
import org.junit.Test;

public class MinMaxNumberPropertyEnhancerTest {
    
    private PropertyConstraintEnhancer enhancer;

    private PropertyConstraintDescription description;

    @Before
    public void setUp() {
        enhancer = new MinMaxNumberPropertyEnhancer(Long.class, -42, 42);
        description = new PropertyConstraintDescription(new PropertyReference(User.class, "age"), Long.class);
        description.setMin(-100);
        description.setMax(100);
    }

    @Test
    public void testEnhance() {
        enhancer.enhance(description);

        assertEquals(Long.valueOf(-42), description.getMin());
        assertEquals(Long.valueOf(42), description.getMax());
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

        private long age;
        
        private String name;

        public long getAge() {
            return age;
        }
        
        public String getName() {
            return name;
        }

    }

}
