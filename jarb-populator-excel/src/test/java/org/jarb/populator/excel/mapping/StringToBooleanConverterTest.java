package org.jarb.populator.excel.mapping;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class StringToBooleanConverterTest {
    private StringToBooleanConverter converter;

    @Before
    public void setUp() {
        converter = new StringToBooleanConverter();
    }

    @Test
    public void testTrue() {
        assertTrue(converter.convert("tRuE"));
    }

    @Test
    public void testTrueSynonym() {
        assertTrue(converter.convert("oN"));
    }

    @Test
    public void testFalse() {
        assertFalse(converter.convert("FaLsE"));
    }

    @Test
    public void testFalseSynonym() {
        assertFalse(converter.convert("OfF"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalid() {
        converter.convert("unknown");
    }

}
