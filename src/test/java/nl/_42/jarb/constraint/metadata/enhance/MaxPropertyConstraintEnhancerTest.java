package nl._42.jarb.constraint.metadata.enhance;

import javax.validation.constraints.Max;
import nl._42.jarb.constraint.metadata.PropertyConstraintDescription;
import nl._42.jarb.utils.bean.PropertyReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MaxPropertyConstraintEnhancerTest {
    
    private PropertyConstraintEnhancer enhancer;

    private PropertyConstraintDescription description;

    @BeforeEach
    public void setUp() {
        enhancer = new MaxPropertyConstraintEnhancer<>(Max.class, Max::value);
        description = new PropertyConstraintDescription(new PropertyReference(User.class, "age"), Long.class);
    }

    @Test
    public void testEnhance() {
        assertNull(description.getMax());

        enhancer.enhance(description);

        assertEquals(Long.valueOf(120), description.getMax());
    }

    @Test
    public void testUnknownType() {
        description = new PropertyConstraintDescription(new PropertyReference(User.class, "name"), String.class);

        assertNull(description.getMax());

        enhancer.enhance(description);

        assertNull(description.getMax());
    }

    public static class User {

        @Max(120)
        private long age;
        
        private String name;

        @Max(130)
        public long getAge() {
            return age;
        }
        
        public String getName() {
            return name;
        }

    }

}
