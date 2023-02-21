package nl._42.jarb.constraint.metadata.enhance;

import nl._42.jarb.domain.Wine;
import nl._42.jarb.constraint.metadata.PropertyConstraintDescription;
import nl._42.jarb.utils.bean.PropertyReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DigitsPropertyConstraintEnhancerTest {
    
    private DigitsPropertyConstraintEnhancer enhancer;

    private PropertyConstraintDescription description;

    @BeforeEach
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
