package org.jarb.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link ReflectionsUtil}.
 * @author Jeroen van Schagen
 */
public class ReflectionUtilsTest {

    /**
     * String-based field used for testing the reflection util.
     */
    private String modifiable;

    @Before
    public void setUp() {
        // Reset the field value to "unmodified" before each test
        this.modifiable = "unmodified";
    }

    @Test
    public void testGetFieldType() {
        Assert.assertEquals(String.class, ReflectionUtils.getFieldType(getClass(), "modifiable"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetFieldTypeUnknownField() {
        Assert.assertEquals(String.class, ReflectionUtils.getFieldType(getClass(), "unknownField"));
    }

    /**
     * Retrieve the field value of instance variable "modifiable". Assert that the returned value
     * is indeed equal to the instance variable's value.
     */
    @Test
    public void testGetFieldValue() {
        Assert.assertEquals(this.modifiable, ReflectionUtils.getFieldValue(this, "modifiable"));
    }

    /**
     * Retrieve the field value of a non-existing field. Expect an illegal argument exception
     * to be thrown, as we cannot read non-existing fields.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetFieldValueUnknownField() {
        ReflectionUtils.getFieldValue(this, "unknownField");
    }

    /**
     * Attempt to modify the value of instance variable "modifiable", from "unmodified" to
     * "modified". Assert that the field value has indeed changed.
     */
    @Test
    public void testSetFieldValue() {
        final String newValue = "modified";
        ReflectionUtils.setFieldValue(this, "modifiable", newValue);
        Assert.assertEquals(newValue, this.modifiable);
    }

    /**
     * Attempt to modify the field value of a non-existing field. Expect an illegal argument
     * exception to be thrown, as we cannot modify non-existing fields.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetFieldValueUnknownField() {
        ReflectionUtils.setFieldValue(this, "unknownField", null);
    }

    /**
     * Invoke the method "returnTrue" using reflection, which returns true. Assert that
     * the method is called by comparing the output to boolean literal <b>true</b>.
     */
    @Test
    public void testInvokeMethod() {
        Assert.assertEquals(Boolean.valueOf(true), ReflectionUtils.invokeMethod(this, "returnTrue"));
    }

    /**
     * Method invoked with reflection.
     * @return <b>true</b>, at all times, no exceptions
     */
    @SuppressWarnings("unused")
    private Boolean returnTrue() {
        return true;
    }

    /**
     * Invoke the method "returnSameValue" using reflection, which returns the same value as
     * its input argument. Assert that the method is called by comparing the input to the output.
     */
    @Test
    public void testInvokeMethodWithArg() {
        final String arg = "my special argument value";
        Assert.assertEquals(arg, ReflectionUtils.invokeMethod(this, "returnSameValue", arg));
    }

    /**
     * Method invoked with reflection.
     * @param arg argument that should be returned
     * @return same argument as specified
     */
    @SuppressWarnings("unused")
    private String returnSameValue(String arg) {
        return arg;
    }

    /**
     * Attempt to invoke the non-existing method "unknownMethod" using reflection. Expect an illegal
     * argument exception to be thrown, as we cannot invoke non-existing methods, even with reflection.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvokeUnknownMethod() {
        ReflectionUtils.invokeMethod(this, "unknownMethod");
    }

}