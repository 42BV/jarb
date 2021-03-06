package nl._42.jarb.constraint.metadata.enhance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import nl._42.jarb.constraint.metadata.PropertyConstraintDescription;
import nl._42.jarb.utils.bean.PropertyReference;

import nl._42.jarb.constraint.domain.Wine;
import nl._42.jarb.constraint.metadata.PropertyConstraintDescription;
import nl._42.jarb.utils.bean.PropertyReference;
import org.junit.Before;
import org.junit.Test;

public class DigitsPropertyConstraintEnhancerTest {
    
    private DigitsPropertyConstraintEnhancer enhancer;

    private PropertyConstraintDescription description;

    @Before
    public void setUp() {
        enhancer = new DigitsPropertyConstraintEnhancer();
        PropertyReference reference = new PropertyReference(Wine.class, "price");
        description = new PropertyConstraintDescription(reference, Double.class);
    }

    @Test
    public void testEnhance() {
        assertNull(description.getMaximumLength());
        assertNull(description.getFractionLength());
        
        enhancer.enhance(description);

        assertEquals(Integer.valueOf(5), description.getMaximumLength());
        assertEquals(Integer.valueOf(1), description.getFractionLength());
    }

    @Test
    public void testSkipUnmarkedProperties() {
        PropertyReference nameReference = new PropertyReference(Wine.class, "name");
        PropertyConstraintDescription nameDescription = new PropertyConstraintDescription(nameReference, String.class);
        
        enhancer.enhance(nameDescription);

        assertNull(nameDescription.getMaximumLength());
        assertNull(nameDescription.getFractionLength());
    }

}
