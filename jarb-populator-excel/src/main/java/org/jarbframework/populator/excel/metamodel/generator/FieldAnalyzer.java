package org.jarbframework.populator.excel.metamodel.generator;

import java.lang.reflect.Field;
import java.util.Collection;

import javax.persistence.ElementCollection;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.jarbframework.populator.excel.metamodel.PropertyDatabaseType;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.utils.bean.BeanProperties;
import org.jarbframework.utils.bean.PropertyReference;
import org.jarbframework.utils.orm.ColumnReference;
import org.jarbframework.utils.orm.SchemaMapper;
import org.jarbframework.utils.orm.jpa.JpaMetaModelUtils;

/**
 * Creates a ColumnDefinition from a field.
 * @author Sander Benschop
 */
public class FieldAnalyzer {
    private final SchemaMapper schemaMapper;

    public FieldAnalyzer(SchemaMapper schemaMapper) {
        this.schemaMapper = schemaMapper;
    }

    public PropertyDefinition.Builder analyzeField(PropertyReference propertyReference) {
        PropertyDefinition.Builder columnDefinitionBuilder = null;
        Field field = BeanProperties.findPropertyField(propertyReference);
        if (isCollection(field)) {
            JoinTable joinTable = field.getAnnotation(JoinTable.class);
            ElementCollection elementCollection = field.getAnnotation(ElementCollection.class);
            if (joinTable != null) {
                columnDefinitionBuilder = joinTableDefinition(joinTable, field);
            } else if (elementCollection != null) {
                columnDefinitionBuilder = elementCollectionDefinition(elementCollection, field);
            }
        } else {
            String referencedColumnName = "";
            referencedColumnName = schemaMapper.columnOf(propertyReference).getColumnName();
            
            if (!referencedColumnName.isEmpty()) {
                columnDefinitionBuilder = PropertyDefinition.forField(field).setColumnName(referencedColumnName);
                if (isAssociation(field)) {
                    columnDefinitionBuilder.setDatabaseType(PropertyDatabaseType.REFERENCE);
                }
                if (isGeneratedValue(field)) {
                    columnDefinitionBuilder.valueIsGenerated();
                }
                if (isIdColumn(field)) {
                    columnDefinitionBuilder.columnIsIdColumn();
                }
            }
        }
        return columnDefinitionBuilder;
    }

    private boolean isCollection(Field field) {
        return Collection.class.isAssignableFrom(field.getType());
    }

    private boolean isAssociation(Field field) {
        return field.getAnnotation(ManyToOne.class) != null || field.getAnnotation(OneToOne.class) != null;
    }

    private boolean isGeneratedValue(Field field) {
        return field.getAnnotation(GeneratedValue.class) != null;
    }

    private boolean isIdColumn(Field field) {
        return field.getAnnotation(Id.class) != null;
    }

    private PropertyDefinition.Builder joinTableDefinition(JoinTable annotation, Field field) {
        String joinColumnName = null;
        if (annotation.joinColumns().length != 0) {
            joinColumnName = annotation.joinColumns()[0].name();
        } else {
            throw new UnsupportedOperationException("JoinTable annotations with implicit JoinColumns are not yet supported by JaRB.");
        }
        String inverseJoinColumnName = null;
        if (annotation.inverseJoinColumns().length != 0) {
            inverseJoinColumnName = annotation.inverseJoinColumns()[0].name();
        } else {
            throw new UnsupportedOperationException("JoinTable annotations with implicit JoinColumns are not yet supported by JaRB.");
        }
        return PropertyDefinition.forField(field).setDatabaseType(PropertyDatabaseType.COLLECTION_REFERENCE).setJoinTableName(annotation.name())
                .setJoinColumnName(joinColumnName).setInverseJoinColumnName(inverseJoinColumnName);
    }

    private PropertyDefinition.Builder elementCollectionDefinition(ElementCollection annotation, Field field) {
        PropertyDefinition.Builder propertyDefinition = PropertyDefinition.forField(field);
        propertyDefinition.setDatabaseType(PropertyDatabaseType.INVERSED_REFERENCE);
        return propertyDefinition;
    }

}
