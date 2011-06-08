package org.jarb.constraint.database;

import org.jarb.constraint.MutablePropertyConstraintDescription;
import org.jarb.constraint.PropertyConstraintDescriptionEnhancer;
import org.jarb.constraint.database.column.ColumnMetadata;
import org.jarb.constraint.database.column.EntityAwareColumnMetadataRepository;
import org.jarb.constraint.database.column.UnknownColumnException;
import org.jarb.constraint.database.column.UnknownTableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enhances the property description with database constraint information.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class DatabasePropertyConstraintDescriptionEnhancer implements PropertyConstraintDescriptionEnhancer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabasePropertyConstraintDescriptionEnhancer.class);
    /** Repository used to access column metadata. **/
    private final EntityAwareColumnMetadataRepository columnMetadataRepository;

    public DatabasePropertyConstraintDescriptionEnhancer(EntityAwareColumnMetadataRepository columnMetadataRepository) {
        this.columnMetadataRepository = columnMetadataRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> MutablePropertyConstraintDescription<T> enhance(MutablePropertyConstraintDescription<T> propertyDescription, Class<?> beanClass) {
        final String propertyName = propertyDescription.getPropertyName();
        try {
            ColumnMetadata columnMetadata = columnMetadataRepository.getColumnMetadata(beanClass, propertyName);
            if (columnMetadata != null) {
                // Properties are required if they cannot be null, and the database cannot generate their value
                propertyDescription.setRequired(columnMetadata.isRequired() && !columnMetadata.isGeneratable());
                propertyDescription.setMaximumLength(columnMetadata.getMaximumLength());
                propertyDescription.setFractionLength(columnMetadata.getFractionLength());
                propertyDescription.setRadix(columnMetadata.getRadix());
            } else {
                // Could not resolve column meta information
                LOGGER.debug("Could not resolve column metadata {} ({}).", new Object[] { propertyName, beanClass.getSimpleName() });
            }
        } catch (UnknownTableException e) {
            // Entity has no corresponding table, skip this step
            LOGGER.debug("Could not enhance property description with column metadata, because {} has no table", beanClass.getSimpleName());
        } catch (UnknownColumnException e) {
            // Property has no corresponding column, skip this step
            LOGGER.debug("Could not enhance property description with column metadata, because {} has no column", propertyName);
        }
        return propertyDescription;
    }

}
