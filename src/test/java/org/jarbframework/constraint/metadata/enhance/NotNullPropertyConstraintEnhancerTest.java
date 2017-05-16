package org.jarbframework.constraint.metadata.enhance;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jarbframework.constraint.domain.Wine;
import org.jarbframework.constraint.metadata.PropertyConstraintDescription;
import org.jarbframework.utils.bean.PropertyReference;
import org.junit.Before;
import org.junit.Test;

public class NotNullPropertyConstraintEnhancerTest {
    
    private NotNullPropertyConstraintEnhancer enhancer;

    private PropertyConstraintDescription description;

    @Before
    public void setUp() {
        enhancer = new NotNullPropertyConstraintEnhancer();
        PropertyReference nameReference = new PropertyReference(Wine.class, "name");
        description = new PropertyConstraintDescription(nameReference, String.class);
    }

    @Test
    public void testEnhance() {
        assertFalse(description.isRequired());

        enhancer.enhance(description);

        assertTrue(description.isRequired());
    }

    @Test
    public void testSkipUnmarkedProperty() {
        PropertyReference priceReference = new PropertyReference(Wine.class, "price");
        PropertyConstraintDescription priceDescription = new PropertyConstraintDescription(priceReference, Double.class);

        enhancer.enhance(priceDescription);

        assertFalse(priceDescription.isRequired());
    }

}
