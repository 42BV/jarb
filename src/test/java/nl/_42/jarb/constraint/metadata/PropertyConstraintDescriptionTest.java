package nl._42.jarb.constraint.metadata;

import nl._42.jarb.domain.Wine;
import nl._42.jarb.utils.bean.PropertyReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test that setters validate the provided values.
 * 
 * @author Jeroen van Schagen
 * @since 03-06-2011
 */
public class PropertyConstraintDescriptionTest {
    
    private PropertyConstraintDescription description;

    @BeforeEach
    public void setUp() {
        PropertyReference reference = new PropertyReference(Wine.class, "name");
        description = new PropertyConstraintDescription(reference, String.class);
    }

    @Test
    public void testModifyLength() {
        description.setMinimumLength(4);
        description.setMaximumLength(42);
        
        assertEquals(Integer.valueOf(4), description.getMinimumLength());
        assertEquals(Integer.valueOf(42), description.getMaximumLength());
    }

    @Test
    public void testNegativeMinimumLength() {
        Assertions.assertThrows(IllegalStateException.class, () -> description.setMinimumLength(-1));
    }

    @Test
    public void testNegativeMaximumLength() {
        Assertions.assertThrows(IllegalStateException.class, () -> description.setMaximumLength(-1));
    }

    @Test
    public void testMinimLengthGreaterThanMaximum() {
        description.setMaximumLength(24);
        Assertions.assertThrows(IllegalStateException.class, () -> description.setMinimumLength(42));
    }

    @Test
    public void testModifyFractionLength() {
        description.setFractionLength(4);

        assertEquals(Integer.valueOf(4), description.getFractionLength());
    }

    @Test
    public void testNegativeFractionLength() {
        Assertions.assertThrows(IllegalStateException.class, () -> description.setFractionLength(-1));
    }

}
