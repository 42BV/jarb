package org.jarbframework.constraint.metadata.enhance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jarbframework.constraint.metadata.PropertyConstraintDescription;
import org.jarbframework.constraint.metadata.domain.Wine;
import org.jarbframework.constraint.metadata.enhance.DigitsPropertyConstraintEnhancer;
import org.jarbframework.utils.bean.PropertyReference;
import org.junit.Before;
import org.junit.Test;

public class DigitsPropertyConstraintMetadataEnhancerTest {
    
    private DigitsPropertyConstraintEnhancer constraintEnhancer;
    private PropertyConstraintDescription priceDescripton;

    @Before
    public void setUp() {
        constraintEnhancer = new DigitsPropertyConstraintEnhancer();
        PropertyReference reference = new PropertyReference(Wine.class, "price");
        priceDescripton = new PropertyConstraintDescription(reference, Double.class);
    }

    @Test
    public void testEnhance() {
        assertNull(priceDescripton.getMaximumLength());
        assertNull(priceDescripton.getFractionLength());
        constraintEnhancer.enhance(priceDescripton);
        assertEquals(Integer.valueOf(5), priceDescripton.getMaximumLength());
        assertEquals(Integer.valueOf(1), priceDescripton.getFractionLength());
    }

    @Test
    public void testSkipUnmarkedProperties() {
        PropertyReference reference = new PropertyReference(Wine.class, "name");
        PropertyConstraintDescription nameDescription = new PropertyConstraintDescription(reference, String.class);
        constraintEnhancer.enhance(nameDescription);
        assertNull(nameDescription.getMaximumLength());
        assertNull(nameDescription.getFractionLength());
    }

}
