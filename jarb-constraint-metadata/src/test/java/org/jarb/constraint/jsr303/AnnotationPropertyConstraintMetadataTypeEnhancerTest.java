package org.jarb.constraint.jsr303;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.validator.constraints.Email;
import org.jarb.constraint.MutablePropertyConstraintMetadata;
import org.jarb.constraint.PropertyType;
import org.junit.Before;
import org.junit.Test;

public class AnnotationPropertyConstraintMetadataTypeEnhancerTest {
    private MutablePropertyConstraintMetadata<String> emailMetadata;
    private AnnotationPropertyConstraintMetadataTypeEnhancer enhancer;
    
    @Before
    public void setUp() {
        enhancer = new AnnotationPropertyConstraintMetadataTypeEnhancer();
        emailMetadata = new MutablePropertyConstraintMetadata<String>("email", String.class);
    }
    
    /**
     * Ensure that an @Email annotation will cause the email type to be added.
     */
    @Test
    public void testEnhance() {
        assertEquals(asTypesSet(PropertyType.TEXT), emailMetadata.getTypes());
        enhancer.enhance(emailMetadata, UserBean.class);
        assertEquals(asTypesSet(PropertyType.EMAIL, PropertyType.TEXT), emailMetadata.getTypes());
    }
    
    private Set<PropertyType> asTypesSet(PropertyType... types) {
        Set<PropertyType> set = new HashSet<PropertyType>(types.length);
        for(PropertyType type : types) {
            set.add(type);
        }
        return Collections.unmodifiableSet(set);
    }
    
    public class UserBean {
        @Email
        private String email;

        public String getEmail() {
            return email;
        }
    }
    
}
