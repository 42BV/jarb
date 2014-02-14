package org.jarbframework.constraint.metadata.enhance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jarbframework.constraint.domain.Wine;
import org.jarbframework.constraint.metadata.PropertyConstraintDescription;
import org.jarbframework.utils.bean.PropertyReference;
import org.junit.Before;
import org.junit.Test;

public class DigitsPropertyConstraintMetadataEnhancerTest {
    
    private DigitsPropertyConstraintEnhancer digitsContraintsEnhancer;

    private PropertyConstraintDescription priceDescripton;

    @Before
    public void setUp() {
        digitsContraintsEnhancer = new DigitsPropertyConstraintEnhancer();
        PropertyReference reference = new PropertyReference(Wine.class, "price");
        priceDescripton = new PropertyConstraintDescription(reference, Double.class);
    }

    @Test
    public void testEnhance() {
        assertNull(priceDescripton.getMaximumLength());
        assertNull(priceDescripton.getFractionLength());
        
        digitsContraintsEnhancer.enhance(priceDescripton);

        assertEquals(Integer.valueOf(5), priceDescripton.getMaximumLength());
        assertEquals(Integer.valueOf(1), priceDescripton.getFractionLength());
    }

    @Test
    public void testSkipUnmarkedProperties() {
        PropertyReference nameReference = new PropertyReference(Wine.class, "name");
        PropertyConstraintDescription nameDescription = new PropertyConstraintDescription(nameReference, String.class);
        
        digitsContraintsEnhancer.enhance(nameDescription);

        assertNull(nameDescription.getMaximumLength());
        assertNull(nameDescription.getFractionLength());
    }

}
