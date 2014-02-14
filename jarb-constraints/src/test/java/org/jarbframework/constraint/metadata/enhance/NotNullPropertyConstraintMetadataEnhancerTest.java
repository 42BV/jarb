package org.jarbframework.constraint.metadata.enhance;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jarbframework.constraint.domain.Wine;
import org.jarbframework.constraint.metadata.PropertyConstraintDescription;
import org.jarbframework.utils.bean.PropertyReference;
import org.junit.Before;
import org.junit.Test;

public class NotNullPropertyConstraintMetadataEnhancerTest {
    
    private NotNullPropertyConstraintEnhancer notNullEnhancer;

    private PropertyConstraintDescription nameDescription;

    @Before
    public void setUp() {
        notNullEnhancer = new NotNullPropertyConstraintEnhancer();
        PropertyReference nameReference = new PropertyReference(Wine.class, "name");
        nameDescription = new PropertyConstraintDescription(nameReference, String.class);
    }

    @Test
    public void testEnhance() {
        assertFalse(nameDescription.isRequired());

        notNullEnhancer.enhance(nameDescription);

        assertTrue(nameDescription.isRequired());
    }

    @Test
    public void testSkipUnmarkedProperty() {
        PropertyReference priceReference = new PropertyReference(Wine.class, "price");
        PropertyConstraintDescription priceDescription = new PropertyConstraintDescription(priceReference, Double.class);

        notNullEnhancer.enhance(priceDescription);

        assertFalse(priceDescription.isRequired());
    }

}
