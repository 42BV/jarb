/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarb.utils.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.junit.Before;
import org.junit.Test;

public class BeanAnnotationScannerImplTest {
    private BeanAnnotationScanner scanner;

    @Before
    public void setUp() {
        scanner = new BeanAnnotationScannerImpl(true, true);
    }

    @Test
    public void testFindForClass() {
        assertTrue(scanner.hasAnnotation(ClassWithAnnotatedProperties.class, Entity.class));
        assertNotNull(scanner.findAnnotation(ClassWithAnnotatedProperties.class, Entity.class));
    }

    @Test
    public void testFindForField() {
        PropertyReference propertyReference = new PropertyReference(ClassWithAnnotatedProperties.class, "hiddenProperty");
        assertTrue(scanner.hasAnnotation(propertyReference, Column.class));
        Column columnAnnotation = scanner.findAnnotation(propertyReference, Column.class);
        assertNotNull(columnAnnotation);
        assertEquals("hidden", columnAnnotation.name());
    }

    @Test
    public void testFindForGetter() {
        PropertyReference propertyReference = new PropertyReference(ClassWithAnnotatedProperties.class, "readableProperty");
        assertTrue(scanner.hasAnnotation(propertyReference, Column.class));
        Column columnAnnotation = scanner.findAnnotation(propertyReference, Column.class);
        assertNotNull(columnAnnotation);
        assertEquals("readable", columnAnnotation.name());
    }

    @Test
    public void testFindForSetter() {
        PropertyReference propertyReference = new PropertyReference(ClassWithAnnotatedProperties.class, "writableProperty");
        assertTrue(scanner.hasAnnotation(propertyReference, Column.class));
        Column columnAnnotation = scanner.findAnnotation(propertyReference, Column.class);
        assertNotNull(columnAnnotation);
        assertEquals("writable", columnAnnotation.name());
    }

    @Entity
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
