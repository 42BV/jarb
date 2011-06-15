package org.jarb.populator.excel.metamodel;

/**
 * Enumeration AnnotationType, can either be a column, join_column or a join_table annotation.
 * 
 * @author Willem Eppen
 * @author Sander Benschop
 */
public enum AnnotationType {
    /**
     * Regular column.
     */
    COLUMN(javax.persistence.Column.class, Column.class),

    /**
     * Column used for joining another table.
     */
    JOIN_COLUMN(javax.persistence.JoinColumn.class, JoinColumn.class),

    /**
     * An associative table.
     */
    JOIN_TABLE(javax.persistence.JoinTable.class, JoinTable.class);

    /** Instance of the annotationClass to be stored. */
    private Class<?> annotationClass;
    /** Instance of the columnDefinition class to be stored. */
    private Class<?> columnDefinition;

    /**
     * Private constructor of AnnotationType enumeration.
     * @param annotationClass an instance of the annotationClass which is to be set
     * @param columnDefinition an instance of the annotationClass which is to be set
     */
    private AnnotationType(final Class<?> annotationClass, final Class<?> columnDefinition) {
        this.annotationClass = annotationClass;
        this.columnDefinition = columnDefinition;
    }

    /**
     * Function to return an instance of the annotated class.
     * @return Annotated class
     */
    public Class<?> getAnnotationClass() {
        return this.annotationClass;
    }

    /**
     * Function to return a new instance of the columnDefinition containing an annotation.
     * @return A new instance of the columnDefinition with a columnName, field and fieldName
     */
    public ColumnDefinition createColumnDefinition(String fieldName) {
        try {
            return (ColumnDefinition) this.columnDefinition.getConstructor(String.class).newInstance(fieldName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
