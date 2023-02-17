package nl._42.jarb.constraint.metadata.enhance;

import nl._42.jarb.domain.Wine;
import nl._42.jarb.constraint.metadata.PropertyConstraintDescription;
import nl._42.jarb.utils.bean.PropertyReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NotNullPropertyConstraintEnhancerTest {
    
    private NotNullPropertyConstraintEnhancer enhancer;

    private PropertyConstraintDescription description;

    @BeforeEach
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
