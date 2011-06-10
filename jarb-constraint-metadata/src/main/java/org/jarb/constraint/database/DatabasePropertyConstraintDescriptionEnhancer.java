package org.jarb.constraint.database;

import org.jarb.constraint.MutablePropertyConstraintMetadata;
import org.jarb.constraint.PropertyConstraintMetadataEnhancer;
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
public class DatabasePropertyConstraintDescriptionEnhancer implements PropertyConstraintMetadataEnhancer {
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
    public <T> MutablePropertyConstraintMetadata<T> enhance(MutablePropertyConstraintMetadata<T> propertyMetadata, Class<?> beanClass) {
        final String propertyName = propertyMetadata.getPropertyName();
        try {
            ColumnMetadata columnMetadata = columnMetadataRepository.getColumnMetadata(beanClass, propertyName);
            if (columnMetadata != null) {
                // Properties are required if they cannot be null, and the database cannot generate their value
                propertyMetadata.setRequired(columnMetadata.isRequired() && !columnMetadata.isGeneratable());
                propertyMetadata.setMaximumLength(columnMetadata.getMaximumLength());
                propertyMetadata.setFractionLength(columnMetadata.getFractionLength());
                propertyMetadata.setRadix(columnMetadata.getRadix());
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
        return propertyMetadata;
    }

}
