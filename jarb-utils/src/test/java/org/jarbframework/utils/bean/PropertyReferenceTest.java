package org.jarbframework.utils.bean;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class PropertyReferenceTest {

    @Test
    public void testGetPath() {
        PropertyReference propertyRef = new PropertyReference(Object.class, "some.property");
        assertArrayEquals(new String[] { "some", "property" }, propertyRef.getPath());
    }

}
