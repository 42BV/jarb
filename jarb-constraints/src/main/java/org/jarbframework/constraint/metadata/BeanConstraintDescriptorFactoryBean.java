package org.jarbframework.constraint.metadata;

import java.util.Date;

import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.Email;
import org.jarbframework.constraint.metadata.database.ColumnMetadataRepository;
import org.jarbframework.constraint.metadata.enhance.AnnotationPropertyTypeEnhancer;
import org.jarbframework.constraint.metadata.enhance.ClassPropertyTypeEnhancer;
import org.jarbframework.constraint.metadata.enhance.DatabaseGeneratedPropertyConstraintEnhancer;
import org.jarbframework.constraint.metadata.enhance.DatabaseSchemaPropertyConstraintEnhancer;
import org.jarbframework.constraint.metadata.enhance.DigitsPropertyConstraintEnhancer;
import org.jarbframework.constraint.metadata.enhance.LengthPropertyConstraintEnhancer;
import org.jarbframework.constraint.metadata.enhance.NotEmptyPropertyConstraintEnhancer;
import org.jarbframework.constraint.metadata.enhance.NotNullPropertyConstraintEnhancer;
import org.jarbframework.utils.orm.SchemaMapper;
import org.jarbframework.utils.spring.SingletonFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Builds a default bean constraint metadata generator.
 * 
 * @author Jeroen van Schagen
 * @since 8-6-2011
 */
public class BeanConstraintDescriptorFactoryBean extends SingletonFactoryBean<BeanConstraintDescriptor> {
    
    private SchemaMapper schemaMapper;
    private ColumnMetadataRepository columnMetadataRepository;

    @Autowired
    public void setColumnMetadataRepository(ColumnMetadataRepository columnMetadataRepository) {
        this.columnMetadataRepository = columnMetadataRepository;
    }
    
    @Autowired
    public void setSchemaMapper(SchemaMapper schemaMapper) {
        this.schemaMapper = schemaMapper;
    }
    
    @Override
    protected BeanConstraintDescriptor createObject() throws Exception {
        BeanConstraintDescriptorImpl accessor = new BeanConstraintDescriptorImpl();
        accessor.registerEnhancer(new DatabaseSchemaPropertyConstraintEnhancer(schemaMapper, columnMetadataRepository));
        accessor.registerEnhancer(new DatabaseGeneratedPropertyConstraintEnhancer());
        
        // Basic constraint annotations
        accessor.registerEnhancer(new LengthPropertyConstraintEnhancer());
        accessor.registerEnhancer(new DigitsPropertyConstraintEnhancer());
        accessor.registerEnhancer(new NotNullPropertyConstraintEnhancer());
        accessor.registerEnhancer(new NotEmptyPropertyConstraintEnhancer());
        
        // Type recognition
        accessor.registerEnhancer(new ClassPropertyTypeEnhancer(String.class, "text"));
        accessor.registerEnhancer(new ClassPropertyTypeEnhancer(Date.class, "date"));
        accessor.registerEnhancer(new ClassPropertyTypeEnhancer(Number.class, "number"));
        accessor.registerEnhancer(new AnnotationPropertyTypeEnhancer(Email.class, "email"));
        accessor.registerEnhancer(new AnnotationPropertyTypeEnhancer(CreditCardNumber.class, "credid_card"));
        return accessor;
    }

}
