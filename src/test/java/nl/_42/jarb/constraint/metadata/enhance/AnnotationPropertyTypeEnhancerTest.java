package nl._42.jarb.constraint.metadata.enhance;

import jakarta.validation.constraints.Email;
import nl._42.jarb.constraint.metadata.PropertyConstraintDescription;
import nl._42.jarb.utils.bean.PropertyReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnnotationPropertyTypeEnhancerTest {
    
    private PropertyConstraintEnhancer enhancer;

    private PropertyConstraintDescription description;

    @BeforeEach
    public void setUp() {
        enhancer = new AnnotationPropertyTypeEnhancer(Email.class, "email");
        description = new PropertyConstraintDescription(new PropertyReference(User.class, "email"), String.class);
    }

    /**
     * Assert that the 'email' type is added whenever the property is annotated as @Email.
     */
    @Test
    public void testEnhance() {
        assertTrue(description.getTypes().isEmpty());

        enhancer.enhance(description);

        assertThat(description.getTypes(), contains("email"));
    }

    /**
     * While the type remains unchanged is no annotation is present.
     */
    @Test
    public void testNoAnnotation() {
        description = new PropertyConstraintDescription(new PropertyReference(User.class, "name"), String.class);

        assertTrue(description.getTypes().isEmpty());

        enhancer.enhance(description);

        assertTrue(description.getTypes().isEmpty());
    }

    public class User {

        @Email
        private String email;

        private String name;

        public String getEmail() {
            return email;
        }

        public String getName() {
            return name;
        }

    }

}
