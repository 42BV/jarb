/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarb.utils;

import static org.jarb.utils.BeanAnnotationUtils.getAnnotation;
import static org.jarb.utils.BeanAnnotationUtils.hasAnnotation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.persistence.Column;

import org.junit.Test;

public class BeanAnnotationUtilsTest {

    @Test
    public void testFindForField() {
        assertTrue(hasAnnotation(ClassWithAnnotatedProperties.class, "hiddenProperty", Column.class));
        Column columnAnnotation = getAnnotation(ClassWithAnnotatedProperties.class, "hiddenProperty", Column.class);
        assertNotNull(columnAnnotation);
        assertEquals("hidden", columnAnnotation.name());
    }

    @Test
    public void testFindForGetter() {
        assertTrue(hasAnnotation(ClassWithAnnotatedProperties.class, "readableProperty", Column.class));
        Column columnAnnotation = getAnnotation(ClassWithAnnotatedProperties.class, "readableProperty", Column.class);
        assertNotNull(columnAnnotation);
        assertEquals("readable", columnAnnotation.name());
    }

    @Test
    public void testFindForSetter() {
        assertTrue(hasAnnotation(ClassWithAnnotatedProperties.class, "writableProperty", Column.class, true, true));
        Column columnAnnotation = getAnnotation(ClassWithAnnotatedProperties.class, "writableProperty", Column.class, true, true);
        assertNotNull(columnAnnotation);
        assertEquals("writable", columnAnnotation.name());
    }

    public static class ClassWithAnnotatedProperties {

        @Column(name = "hidden")
        @SuppressWarnings("unused")
        private String hiddenProperty;

        private String readableProperty;

        @SuppressWarnings("unused")
        private String writableProperty;

        @Column(name = "readable")
        public String getReadableProperty() {
            return readableProperty;
        }

        @Column(name = "writable")
        public void setWritableProperty(String writableProperty) {
            this.writableProperty = writableProperty;
        }

    }

}
