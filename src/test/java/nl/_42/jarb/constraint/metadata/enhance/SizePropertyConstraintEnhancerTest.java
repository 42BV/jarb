package nl._42.jarb.constraint.metadata.enhance;

import nl._42.jarb.constraint.domain.AwesomeCar;
import nl._42.jarb.constraint.metadata.PropertyConstraintDescription;
import nl._42.jarb.utils.bean.PropertyReference;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SizePropertyConstraintEnhancerTest {
    
    private SizePropertyConstraintEnhancer enhancer;

    private PropertyConstraintDescription description;

    @Before
    public void setUp() {
        enhancer = new SizePropertyConstraintEnhancer();

        PropertyReference inspections = new PropertyReference(AwesomeCar.class, "inspections");
        description = new PropertyConstraintDescription(inspections, List.class);
        description.setRequired(true);
    }

    @Test
    public void testEnhance() {
        assertTrue(description.isRequired());

        enhancer.enhance(description);

        assertFalse(description.isRequired());
    }

}
