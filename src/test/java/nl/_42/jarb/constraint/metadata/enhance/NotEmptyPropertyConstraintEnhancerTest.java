package nl._42.jarb.constraint.metadata.enhance;

import nl._42.jarb.constraint.domain.Wine;
import nl._42.jarb.constraint.metadata.PropertyConstraintDescription;
import nl._42.jarb.utils.bean.PropertyReference;
import org.junit.Before;
import org.junit.Test;

import javax.validation.constraints.NotEmpty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class NotEmptyPropertyConstraintEnhancerTest {
    
    private NotEmptyPropertyConstraintEnhancer enhancer;

    private PropertyConstraintDescription description;

    @Before
    public void setUp() {
        enhancer = new NotEmptyPropertyConstraintEnhancer(NotEmpty.class);
        
        PropertyReference nameReference = new PropertyReference(Wine.class, "name");
        description = new PropertyConstraintDescription(nameReference, String.class);
    }

    /**
     * Whenever our described minimum length is null, and the property is @NotEmpty,
     * we should change the minimum length to one. This is because we require atleast
     * one character for the property value to not be empty.
     */
    @Test
    public void testEnhanceIfNull() {
        assertNull(description.getMinimumLength());
        
        enhancer.enhance(description);

        assertEquals(Integer.valueOf(1), description.getMinimumLength());
    }

    /**
     * If our described minimum length is zero, but the property is @NotEmpty,
     * we should also change the minimum length to one.
     */
    @Test
    public void testEnhanceIfZero() {
        description.setMinimumLength(0);
        
        enhancer.enhance(description);

        assertEquals(Integer.valueOf(1), description.getMinimumLength());
    }

    /**
     * Whenever our minimum length is already configured, above zero, we do
     * nothing. That is because some other, more specific, enhancer already
     * provided the correct minimum length, and we should not alter it.
     */
    @Test
    public void testSkipIfAlreadyHasPositiveMin() {
        description.setMinimumLength(42);
        
        enhancer.enhance(description);

        assertEquals(Integer.valueOf(42), description.getMinimumLength());
    }

    /**
     * Properties that are not annotated with @NotEmpty should not be altered.
     */
    @Test
    public void testSkipUnmarkedProperty() {
        PropertyReference reference = new PropertyReference(Wine.class, "price");
        PropertyConstraintDescription priceDescription = new PropertyConstraintDescription(reference, String.class);
        
        enhancer.enhance(priceDescription);

        assertNull(priceDescription.getMinimumLength());
    }

}
