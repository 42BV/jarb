package nl._42.jarb.constraint.metadata.enhance;

import nl._42.jarb.constraint.metadata.PropertyConstraintDescription;
import nl._42.jarb.utils.bean.PropertyReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MinMaxNumberPropertyEnhancerTest {
    
    private PropertyConstraintEnhancer enhancer;

    private PropertyConstraintDescription description;

    @BeforeEach
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
