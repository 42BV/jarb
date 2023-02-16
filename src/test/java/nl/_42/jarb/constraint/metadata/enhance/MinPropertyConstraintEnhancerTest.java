package nl._42.jarb.constraint.metadata.enhance;

import javax.validation.constraints.Min;
import nl._42.jarb.constraint.metadata.PropertyConstraintDescription;
import nl._42.jarb.utils.bean.PropertyReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MinPropertyConstraintEnhancerTest {
    
    private PropertyConstraintEnhancer enhancer;

    private PropertyConstraintDescription description;

    @BeforeEach
    public void setUp() {
        enhancer = new MinPropertyConstraintEnhancer<>(Min.class, Min::value);
        description = new PropertyConstraintDescription(new PropertyReference(User.class, "age"), Long.class);
    }

    @Test
    public void testEnhance() {
        assertNull(description.getMin());

        enhancer.enhance(description);

        assertEquals(Long.valueOf(1), description.getMin());
    }

    @Test
    public void testUnknownType() {
        description = new PropertyConstraintDescription(new PropertyReference(User.class, "name"), String.class);

        assertNull(description.getMin());

        enhancer.enhance(description);

        assertNull(description.getMin());
    }

    public static class User {

        @Min(0)
        private long age;

        @Min(42)
        private String name;

        @Min(1)
        public long getAge() {
            return age;
        }

        public String getName() {
            return name;
        }

    }

}
