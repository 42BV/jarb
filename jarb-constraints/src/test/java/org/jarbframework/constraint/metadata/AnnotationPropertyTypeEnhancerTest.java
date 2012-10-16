package org.jarbframework.constraint.metadata;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.hibernate.validator.constraints.Email;
import org.jarbframework.utils.bean.PropertyReference;
import org.junit.Before;
import org.junit.Test;

public class AnnotationPropertyTypeEnhancerTest {
    
    private PropertyConstraintDescription description;
    private PropertyConstraintEnhancer enhancer;

    @Before
    public void setUp() {
        description = new PropertyConstraintDescription(new PropertyReference(UserBean.class, "email"), String.class);
        enhancer = new AnnotationPropertyTypeEnhancer(Email.class, "email");
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
        description = new PropertyConstraintDescription(new PropertyReference(UserBean.class, "name"), String.class);

        assertTrue(description.getTypes().isEmpty());
        enhancer.enhance(description);
        assertTrue(description.getTypes().isEmpty());
    }

    public class UserBean {

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
