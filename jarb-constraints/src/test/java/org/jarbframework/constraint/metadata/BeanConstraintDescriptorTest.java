package org.jarbframework.constraint.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.persistence.ManyToOne;

import org.jarbframework.constraint.EnableDatabaseConstraintsConfigurer;
import org.jarbframework.constraint.TestConstraintsConfig;
import org.jarbframework.constraint.domain.Country;
import org.jarbframework.constraint.domain.Wine;
import org.jarbframework.constraint.metadata.BeanConstraintDescription;
import org.jarbframework.constraint.metadata.BeanConstraintDescriptor;
import org.jarbframework.constraint.metadata.PropertyConstraintDescription;
import org.jarbframework.constraint.metadata.BeanConstraintDescriptorTest.CustomConstraintsConfig;
import org.jarbframework.constraint.metadata.enhance.AnnotationPropertyTypeEnhancer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Sets;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("hsqldb")
@ContextConfiguration(classes = { TestConstraintsConfig.class, CustomConstraintsConfig.class })
public class BeanConstraintDescriptorTest {

    @Autowired
    private BeanConstraintDescriptor descriptor;

    @Test
    public void testDefaultEnhancers() throws Exception {
        BeanConstraintDescription wineDescription = descriptor.describe(Wine.class);
        
        // Retrieved by introspection
        assertEquals(Long.class, wineDescription.getProperty("id").getJavaType());
        assertEquals(Double.class, wineDescription.getProperty("price").getJavaType());
        assertEquals(Country.class, wineDescription.getProperty("country").getJavaType());
        assertEquals(Class.class, wineDescription.getProperty("class").getJavaType());

        PropertyConstraintDescription nameDescription = wineDescription.getProperty("name");
        assertEquals(String.class, nameDescription.getJavaType());
        assertTrue(nameDescription.isRequired()); // Retrieved from database
        assertEquals(Integer.valueOf(6), nameDescription.getMinimumLength()); // Retrieved from @Length
        assertEquals(Integer.valueOf(6), nameDescription.getMaximumLength()); // Merged @Length and database
    }

    @Test
    public void testCustomEnhancer() {
        BeanConstraintDescription wineDescription = descriptor.describe(Wine.class);
        assertEquals(Sets.newHashSet("reference"), wineDescription.getProperty("country").getTypes());
    }
    
    @Configuration
    public static class CustomConstraintsConfig extends EnableDatabaseConstraintsConfigurer {
        
        @Override
        public void addPropertyEnhancers(BeanConstraintDescriptor descriptor) {
            descriptor.registerEnhancer(new AnnotationPropertyTypeEnhancer(ManyToOne.class, "reference"));
        }
        
    }

}
