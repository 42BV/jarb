package nl._42.jarb.utils.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnnotationsTest {

    @Test
    public void testFindForField() {
        PropertyReference propertyReference = new PropertyReference(ClassWithAnnotatedProperties.class, "hiddenProperty");
        assertTrue(Annotations.hasAnnotation(propertyReference, Column.class));
    }

    @Test
    public void testFindForGetter() {
        PropertyReference propertyReference = new PropertyReference(ClassWithAnnotatedProperties.class, "readableProperty");
        assertTrue(Annotations.hasAnnotation(propertyReference, Column.class));
    }

    @Entity
    public static class ClassWithAnnotatedProperties {

        @Id
        private Long id;

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
