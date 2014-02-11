package org.jarbframework.constraint.metadata.enhance;

import static org.jarbframework.utils.Asserts.notNull;

import org.jarbframework.constraint.metadata.PropertyConstraintDescription;
import org.jarbframework.constraint.metadata.database.BeanMetadataRepository;
import org.jarbframework.constraint.metadata.database.ColumnMetadata;
import org.jarbframework.utils.bean.PropertyReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enhances the property description with database constraint information.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class DatabaseSchemaPropertyConstraintEnhancer implements PropertyConstraintEnhancer {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final BeanMetadataRepository beanMetadataRepository;

    public DatabaseSchemaPropertyConstraintEnhancer(BeanMetadataRepository beanMetadataRepository) {
        this.beanMetadataRepository = notNull(beanMetadataRepository, "Bean metadata repository cannot be null");
    }

    @Override
    public PropertyConstraintDescription enhance(PropertyConstraintDescription propertyDescription) {
        if (! beanMetadataRepository.isEmbeddable(propertyDescription.getJavaType())) {
            PropertyReference propertyReference = propertyDescription.toReference();
            ColumnMetadata columnMetadata = beanMetadataRepository.getColumnMetadata(propertyReference);
            if (columnMetadata != null) {
                doEnhance(propertyDescription, columnMetadata);
            } else {
                logger.debug("Could not resolve column metadata for '{}'.", propertyReference);
            }
        }

        return propertyDescription;
    }

    private void doEnhance(PropertyConstraintDescription propertyDescription, ColumnMetadata columnMetadata) {
        propertyDescription.setRequired(isValueRequired(columnMetadata));
        propertyDescription.setMaximumLength(columnMetadata.getMaximumLength());
        propertyDescription.setFractionLength(columnMetadata.getFractionLength());
        propertyDescription.setRadix(columnMetadata.getRadix());
    }

    private boolean isValueRequired(ColumnMetadata columnMetadata) {
        return columnMetadata.isRequired() && ! columnMetadata.isGeneratable();
    }

}
