package org.jarb.populator.excel.metamodel;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Abstract class containing a columnName, fieldName and a field.
 * @author Willem Eppen
 * @author Sander Benschop
 * 
 */
public abstract class ColumnDefinition {
    /** The name of the field belonging to the columnDefinition. */
    private final String fieldName;
    /** The name of the column belonging to the columnDefinition. */
    private String columnName;
    /** The name of the Embedded object if applicable. */
    private String embeddedObjectName;
    /** The field Object belonging to the columnDefinition. */
    private Field field;
    /** True if it's an Embedded attribute, in this case the data will have to be stored into an Object inside Excelrecord. */
    private boolean embeddedAttribute;
    /** True if it's a discriminator column. */
    private boolean discriminatorColumn;

    public ColumnDefinition(String fieldName) {
        this.fieldName = fieldName;
    }

    /** Abstract void to store an annotation.
     * @param annotation annotation
     */
    public abstract void storeAnnotation(final Field field, final Annotation annotation);

    /**
     * Used to determine if a certain columnDefinitions is an associative table.
     * For example: if a many-to-many relation between employees and projects is present,
     * the associative table might be employees_projects. This is seen by checking the annotationTypes.
     * @return True if ColumnDefinition is of annotationType ManyToMany or JoinTable, if not it returns as false.
     */
    public boolean isAssociativeTable() {
        boolean returnValue = false;
        if (!isDiscriminatorColumn()) {
            for (Annotation annotation : this.getField().getAnnotations()) {
                if (("javax.persistence.ManyToMany").equals(annotation.annotationType().getName())
                        || ("javax.persistence.JoinTable".equals(annotation.annotationType().getName()))) {
                    returnValue = true;
                }
            }
        }
        return returnValue;
    }

    /**
     * Sets the field belonging to the columnDefinition.
     * @param field Object
     */
    public void setField(final Field field) {
        this.field = field;
    }

    /**
     * Returns the field object belonging to the columnDefinition.
     * @return field Object
     */
    public Field getField() {
        return this.field;
    }

    /**
     * Sets the columnName belonging to the columnDefinition.
     * @param columnName String
     */
    public void setColumnName(final String columnName) {
        this.columnName = columnName;
    }

    /**
     * Returns the columnName belonging to the columnDefinition.
     * @return name of column
     */
    public String getColumnName() {
        return this.columnName;
    }

    /**
     * Returns the fieldName belonging to the columnDefinition.
     * @return fieldName String
     */
    public String getFieldName() {
        return this.fieldName;
    }

    /**
     * Sets embeddedAttribute to true, this is necessary when adding data to an Excelrecord with an embedded attribute object.
     * @param embeddedAttribute Set to true if ColumnDefinition holds an Embedded attribute
     */
    public void setEmbeddedAttribute(boolean embeddedAttribute) {
        this.embeddedAttribute = embeddedAttribute;
    }

    /**
     * Returns true if ColumnDefinition holds an embedded attribute. This is necessary when adding data to an Excelrecord.
     * @return True if ColumnDeftinition hold an embedded attribute.
     */
    public boolean isEmbeddedAttribute() {
        return embeddedAttribute;
    }

    /**
     * Sets the name of the embedded object.
     * @param embeddedObjectName Name of the embedded object
     */
    public void setEmbeddedObjectName(String embeddedObjectName) {
        this.embeddedObjectName = embeddedObjectName;
    }

    /**
     * Returns the name of the embedded object.
     * @return Name of embedded object
     */
    public String getEmbeddedObjectName() {
        return embeddedObjectName;
    }

    /** Set to true if the column is a discriminator column.
     * @param discriminatorColumn Whether or not it's a discriminator column
     */
    public void setDiscriminatorColumn(boolean discriminatorColumn) {
        this.discriminatorColumn = discriminatorColumn;
    }

    /** Returns true if the column is a discriminator column.
     * @return True or false, depending on if it's a discriminator column or not
     */
    public boolean isDiscriminatorColumn() {
        return discriminatorColumn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return fieldName;
    }

}
