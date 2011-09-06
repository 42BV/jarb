package org.jarb.constraint.database;

import org.jarb.constraint.MutablePropertyConstraintMetadata;
import org.jarb.constraint.PropertyConstraintMetadataEnhancer;
import org.jarb.constraint.database.column.ColumnMetadata;
import org.jarb.utils.bean.PropertyReference;
import org.jarb.utils.orm.NotAnEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enhances the property description with database constraint information.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class DatabasePropertyConstraintDescriptionEnhancer implements PropertyConstraintMetadataEnhancer {
    private final Logger logger = LoggerFactory.getLogger(DatabasePropertyConstraintDescriptionEnhancer.class);

    /** Repository used to access column meta-data. **/
    private final DatabaseConstraintRepository databaseConstraintRepository;

    public DatabasePropertyConstraintDescriptionEnhancer(DatabaseConstraintRepository columnMetadataRepository) {
        this.databaseConstraintRepository = columnMetadataRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> MutablePropertyConstraintMetadata<T> enhance(MutablePropertyConstraintMetadata<T> propertyMetadata, Class<?> beanClass) {
        final String propertyName = propertyMetadata.getName();
        try {
            PropertyReference propertyReference = new PropertyReference(beanClass, propertyName);
            ColumnMetadata columnMetadata = databaseConstraintRepository.getColumnMetadata(propertyReference);
            if (columnMetadata != null) {
                // Properties are required if they cannot be null, and the database cannot generate their value
                propertyMetadata.setRequired(columnMetadata.isRequired() && !columnMetadata.isGeneratable());
                propertyMetadata.setMaximumLength(columnMetadata.getMaximumLength());
                propertyMetadata.setFractionLength(columnMetadata.getFractionLength());
                propertyMetadata.setRadix(columnMetadata.getRadix());
            } else {
                // Could not resolve column meta information
                logger.debug("Could not resolve column metadata {} ({}).", new Object[] { propertyName, beanClass.getSimpleName() });
            }
        } catch (NotAnEntityException e) {
            // Property has no corresponding column, skip this step
            logger.debug("Could not enhance property description with column metadata, because {} is not an entity", beanClass.getSimpleName());
        } catch (CouldNotBeMappedToColumnException e) {
            // Property has no corresponding column, skip this step
            logger.debug("Could not enhance property description with column metadata, because {} has no column", propertyName);
        }
        return propertyMetadata;
    }

}
