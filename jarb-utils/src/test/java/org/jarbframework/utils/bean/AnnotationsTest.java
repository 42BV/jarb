package org.jarbframework.utils.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.junit.Test;

public class AnnotationsTest {

    @Test
    public void testFindForClass() {
        assertTrue(Annotations.hasAnnotation(ClassWithAnnotatedProperties.class, Entity.class));
        assertNotNull(Annotations.findAnnotation(ClassWithAnnotatedProperties.class, Entity.class));
    }

    @Test
    public void testFindForField() {
        PropertyReference propertyReference = new PropertyReference(ClassWithAnnotatedProperties.class, "hiddenProperty");
        assertTrue(Annotations.hasAnnotation(propertyReference, Column.class));
        Column columnAnnotation = Annotations.findAnnotation(propertyReference, Column.class);
        assertNotNull(columnAnnotation);
        assertEquals("hidden", columnAnnotation.name());
    }

    @Test
    public void testFindForGetter() {
        PropertyReference propertyReference = new PropertyReference(ClassWithAnnotatedProperties.class, "readableProperty");
        assertTrue(Annotations.hasAnnotation(propertyReference, Column.class));
        Column columnAnnotation = Annotations.findAnnotation(propertyReference, Column.class);
        assertNotNull(columnAnnotation);
        assertEquals("readable", columnAnnotation.name());
    }

    @Entity
    public static class ClassWithAnnotatedProperties {

        @Column(name = "hidden")
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
