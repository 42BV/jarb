package org.jarbframework.constraint.metadata.enhance;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.hibernate.validator.constraints.Email;
import org.jarbframework.constraint.metadata.PropertyConstraintDescription;
import org.jarbframework.utils.bean.PropertyReference;
import org.junit.Before;
import org.junit.Test;

public class AnnotationPropertyTypeEnhancerTest {
    
    private PropertyConstraintEnhancer emailTypeEnhancer;

    private PropertyConstraintDescription emailDescription;

    @Before
    public void setUp() {
        emailTypeEnhancer = new AnnotationPropertyTypeEnhancer(Email.class, "email");
        emailDescription = new PropertyConstraintDescription(new PropertyReference(User.class, "email"), String.class);
    }

    /**
     * Assert that the 'email' type is added whenever the property is annotated as @Email.
     */
    @Test
    public void testEnhance() {
        assertTrue(emailDescription.getTypes().isEmpty());

        emailTypeEnhancer.enhance(emailDescription);

        assertThat(emailDescription.getTypes(), contains("email"));
    }

    /**
     * While the type remains unchanged is no annotation is present.
     */
    @Test
    public void testNoAnnotation() {
        emailDescription = new PropertyConstraintDescription(new PropertyReference(User.class, "name"), String.class);

        assertTrue(emailDescription.getTypes().isEmpty());

        emailTypeEnhancer.enhance(emailDescription);

        assertTrue(emailDescription.getTypes().isEmpty());
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
