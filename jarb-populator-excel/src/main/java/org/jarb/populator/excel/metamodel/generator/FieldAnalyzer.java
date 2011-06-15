package org.jarb.populator.excel.metamodel.generator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.jarb.populator.excel.metamodel.AnnotationType;
import org.jarb.populator.excel.metamodel.ColumnDefinition;

/**
 * Creates a ColumnDefinition from a field.
 * @author Sander Benschop
 *
 */
public final class FieldAnalyzer {

    /** Private constructor. */
    private FieldAnalyzer() {
    }

    /**
     * Analyse the field's annotations and generate a ColumnDefinition from this field.
     * @param field which contains the annotations
     * @return columnDefinition with annotation, fieldname and field
     * @throws InstantiationException Thrown when function is used on a class that cannot be instantiated (abstract or interface)
     * @throws IllegalAccessException Thrown when function does not have access to the definition of the specified class, field, method or constructor 
     */
    public static ColumnDefinition analyzeField(final Field field) throws InstantiationException, IllegalAccessException {
        if (field.getAnnotation(javax.persistence.GeneratedValue.class) != null) {
            return null;
        }

        Set<Class<?>> annotationClasses = new HashSet<Class<?>>();
        for (Annotation annotation : field.getAnnotations()) {
            for (AnnotationType annotationType : AnnotationType.values()) {
                if (annotationType.getAnnotationClass().isAssignableFrom(annotation.getClass())) {
                    return prepareColumnDefinition(field, annotation, annotationType);
                }
            }
            annotationClasses.add(annotation.annotationType());
        }

        if (!setContainsRelationalAnnotation(annotationClasses)) {
            return prepareColumnDefinition(field);
        }

        return null;
    }

    /**
     * Checks if a set with AnnotationTypeClasses holds a relational annotation.
     * We're looking for either ManyToOne, ManyToMany or OneToMany.
     * @param annotationTypeClasses Set of annotationTypeClasses
     * @return True if any are present, false if not.
     */
    private static boolean setContainsRelationalAnnotation(Set<Class<?>> annotationTypeClasses) {
        return (annotationTypeClasses.contains(javax.persistence.OneToMany.class) || (annotationTypeClasses.contains(javax.persistence.ManyToOne.class) || //
        (annotationTypeClasses.contains(javax.persistence.ManyToMany.class))));
    }

    /**
     * Prepares the ColumnDefinition by adding an annotation and calling the setColumnDefinitionField function.
     * @param field Field the ColumnDefinition is made from
     * @param annotation Annotation the ColumnDefinition is made from
     * @param annotationType AnnotationType of the annotation
     * @return ColumnDefinition ColumnDefinition with Annotation and Field values
     * @throws InstantiationException Thrown when function is used on a class that cannot be instantiated (abstract or interface)
     * @throws IllegalAccessException Thrown when function does not have access to the definition of the specified class, field, method or constructor 
     */
    private static ColumnDefinition prepareColumnDefinition(Field field, Annotation annotation, AnnotationType annotationType) {
        ColumnDefinition columnDefinition = annotationType.createColumnDefinition(field.getName());
        columnDefinition.storeAnnotation(field, annotation);
        columnDefinition.setField(field);
        return columnDefinition;
    }

    private static ColumnDefinition prepareColumnDefinition(Field field) {
        AnnotationType annotationType = AnnotationType.COLUMN;
        ColumnDefinition columnDefinition = annotationType.createColumnDefinition(field.getName());
        columnDefinition.setColumnName(field.getName());
        columnDefinition.setField(field);
        return columnDefinition;
    }

}
