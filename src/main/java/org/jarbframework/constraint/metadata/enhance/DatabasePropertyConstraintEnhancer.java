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
public class DatabasePropertyConstraintEnhancer implements PropertyConstraintEnhancer {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final BeanMetadataRepository beanMetadataRepository;

    public DatabasePropertyConstraintEnhancer(BeanMetadataRepository beanMetadataRepository) {
        this.beanMetadataRepository = notNull(beanMetadataRepository, "Bean metadata repository cannot be null");
    }

    @Override
    public void enhance(PropertyConstraintDescription description) {
        if (! beanMetadataRepository.isEmbeddable(description.getJavaType())) {
            PropertyReference reference = description.toReference();
            ColumnMetadata metadata = beanMetadataRepository.getColumnMetadata(reference);
            if (metadata != null) {
                doEnhance(description, metadata);
            } else {
                logger.debug("Could not resolve column metadata for '{}'.", reference);
            }
        }
    }

    private void doEnhance(PropertyConstraintDescription description, ColumnMetadata metadata) {
        description.setRequired(isValueRequired(metadata));
        description.setMaximumLength(metadata.getMaximumLength());
        description.setFractionLength(metadata.getFractionLength());
        description.setRadix(metadata.getRadix());
    }

    private boolean isValueRequired(ColumnMetadata columnMetadata) {
        return columnMetadata.isRequired() && ! columnMetadata.isGeneratable();
    }

}
