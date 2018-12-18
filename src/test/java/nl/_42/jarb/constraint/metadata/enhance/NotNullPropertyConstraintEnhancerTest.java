package nl._42.jarb.constraint.metadata.enhance;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import nl._42.jarb.constraint.metadata.PropertyConstraintDescription;
import nl._42.jarb.utils.bean.PropertyReference;

import nl._42.jarb.constraint.domain.Wine;
import nl._42.jarb.constraint.metadata.PropertyConstraintDescription;
import nl._42.jarb.utils.bean.PropertyReference;
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
