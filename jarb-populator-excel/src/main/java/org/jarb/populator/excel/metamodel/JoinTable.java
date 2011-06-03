package org.jarb.populator.excel.metamodel;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Used for joining a many-to-many table. Adds a list of foreign keys in the associative class which reference a certain record on the "one"-side.
 * Extends from ColumnDefinition.
 * @author Willem Eppen
 * @author Sander Benschop
 */
public class JoinTable extends PropertyDefinition {

    public JoinTable(String fieldName) {
        super(fieldName);
    }

    /** Name of foreign key columm in associative class. */
    private javax.persistence.JoinColumn inverseJoinColumn;
    /** Name of primary key column on the "one" side. */
    private javax.persistence.JoinColumn joinColumn;

    /**
     * Sets inverseJoinColumn and joinColumn.
     * @param annotation Annotation containing the annotationtype, which is in this case JoinTable.
     */
    @Override
    public void storeAnnotation(Field field, Annotation annotation) {
        setColumnName(((javax.persistence.JoinTable) annotation).name());
        this.inverseJoinColumn = Arrays.asList(((javax.persistence.JoinTable) annotation).inverseJoinColumns()).get(0);
        this.joinColumn = Arrays.asList(((javax.persistence.JoinTable) annotation).joinColumns()).get(0);
    }

    /**
     * Returns foreign key columnName.
     * @return Name of foreign key columm in associative class.
     */
    public String getInverseJoinColumnName() {
        return inverseJoinColumn.name();
    }

    /**
     * Returns primary key columnName.
     * @return Name of primary key column on the one-side.
     */
    public String getJoinColumnName() {
        return joinColumn.name();
    }
}
