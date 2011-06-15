package org.jarb.populator.excel.metamodel.generator;

import org.jarb.populator.excel.metamodel.ColumnDefinition;

/**
 * Creates a discriminator column. Uses default values if annotations are not present in persistent class.
 * @author Sander Benschop
 *
 */
public final class DiscriminatorColumnGenerator {

    /** Private constructor. */
    private DiscriminatorColumnGenerator() {
    }

    /**
     * Returns a discriminator columnDefinition.
     * @param persistentClass Persistent class to search the discriminator values in.
     * @return ColumnDefinition with discriminator column
     */
    public static ColumnDefinition createDiscriminatorColumnDefinition(Class<?> persistentClass) {
        //Add a discriminator column, use the one annotated in the superclass if available.
        //Field name will be equal to column name cause this is necessary upon persisting
        String discriminatorColumnName = getDiscriminatorColumnName(persistentClass);
        return ColumnDefinition.discriminator(discriminatorColumnName);
    }

    /**
     * Return the discriminator column's name from the peristent class' annotation.
     * @param persistentClass Persistent class to read the annotation from
     * @return Discriminator column name.
     */
    private static String getDiscriminatorColumnName(Class<?> persistentClass) {
        String discriminatorColumnName;
        if (persistentClass.getAnnotation(javax.persistence.DiscriminatorColumn.class) != null) {
            discriminatorColumnName = persistentClass.getAnnotation(javax.persistence.DiscriminatorColumn.class).name();
        } else {
            //DTYPE is Hibernate's default name for Discriminator columns. If none is provided this one will be used.
            discriminatorColumnName = "dtype";
        }
        return discriminatorColumnName;
    }

}
