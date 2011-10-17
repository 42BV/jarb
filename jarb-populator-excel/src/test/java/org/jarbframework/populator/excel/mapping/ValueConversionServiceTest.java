package org.jarbframework.populator.excel.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Test;

public class ValueConversionServiceTest {
    private final ValueConversionService conversionService = ValueConversionService.defaultConversions();

    /**
     * Check that we can invoke our registered converters.
     */
    @Test
    public void testConvert() {
        // Spring conversion logic
        assertEquals(new BigDecimal("99.42"), conversionService.convert(new Double("99.42"), BigDecimal.class));
        // Custom conversion logic
        assertTrue(conversionService.convert("ja", Boolean.class));
    }

    /**
     * Invalid conversions result in a runtime exception.
     */
    @Test
    public void testConversionException() {
        try {
            conversionService.convert(Double.NaN, BigDecimal.class);
            fail("Expected a conversion exception!");
        } catch (CouldNotConvertException e) {
            assertEquals(NumberFormatException.class, e.getCause().getClass());
            assertEquals(Double.NaN, e.getSource());
            assertEquals(BigDecimal.class, e.getTargetType());
        }
    }

}
