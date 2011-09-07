package org.jarbframework.populator.excel.metamodel.generator;

/**
 * Creates a discriminator column. Uses default values if annotations are not present in persistent class.
 * @author Sander Benschop
 *
 */
public final class DiscriminatorColumnGenerator {

    /**
     * Return the discriminator column's name from the peristent class' annotation.
     * @param persistentClass Persistent class to read the annotation from
     * @return Discriminator column name.
     */
    public static String getDiscriminatorColumnName(Class<?> persistentClass) {
        String discriminatorColumnName;
        if (persistentClass.getAnnotation(javax.persistence.DiscriminatorColumn.class) != null) {
            discriminatorColumnName = persistentClass.getAnnotation(javax.persistence.DiscriminatorColumn.class).name();
        } else {
            //DTYPE is Hibernate's default name for Discriminator columns. If none is provided this one will be used.
            discriminatorColumnName = "dtype";
        }
        return discriminatorColumnName;
    }
    
    /** Private constructor. */
    private DiscriminatorColumnGenerator() {
    }

}
