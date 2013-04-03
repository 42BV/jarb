package org.jarbframework.constraint.metadata.enhance;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jarbframework.constraint.domain.Wine;
import org.jarbframework.constraint.metadata.PropertyConstraintDescription;
import org.jarbframework.constraint.metadata.enhance.NotNullPropertyConstraintEnhancer;
import org.jarbframework.utils.bean.PropertyReference;
import org.junit.Before;
import org.junit.Test;

public class NotNullPropertyConstraintMetadataEnhancerTest {
    
    private NotNullPropertyConstraintEnhancer constraintEnhancer;
    private PropertyConstraintDescription nameDescription;

    @Before
    public void setUp() {
        constraintEnhancer = new NotNullPropertyConstraintEnhancer();
        PropertyReference reference = new PropertyReference(Wine.class, "name");
        nameDescription = new PropertyConstraintDescription(reference, String.class);
    }

    @Test
    public void testEnhance() {
        assertFalse(nameDescription.isRequired());
        constraintEnhancer.enhance(nameDescription);
        assertTrue(nameDescription.isRequired());
    }

    @Test
    public void testSkipUnmarkedProperty() {
        PropertyReference priceReference = new PropertyReference(Wine.class, "price");
        PropertyConstraintDescription priceDescription = new PropertyConstraintDescription(priceReference, Double.class);
        constraintEnhancer.enhance(priceDescription);
        assertFalse(priceDescription.isRequired());
    }

}
