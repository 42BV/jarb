package org.jarb.constraint.database;

import static org.jarb.utils.Conditions.notNull;

import org.jarb.constraint.PropertyConstraintDescription;
import org.jarb.constraint.PropertyConstraintEnhancer;
import org.jarb.constraint.database.column.ColumnMetadata;
import org.jarb.utils.orm.NotAnEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enhances the property description with database constraint information.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class DatabasePropertyConstraintDescriptionEnhancer implements PropertyConstraintEnhancer {
    private final Logger logger = LoggerFactory.getLogger(DatabasePropertyConstraintDescriptionEnhancer.class);

    /** Repository used to access database constraint information. **/
    private final DatabaseConstraintRepository databaseConstraintRepository;

    public DatabasePropertyConstraintDescriptionEnhancer(DatabaseConstraintRepository databaseConstraintRepository) {
        this.databaseConstraintRepository = notNull(databaseConstraintRepository, "Database constraint repository cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyConstraintDescription enhance(PropertyConstraintDescription propertyMetadata) {
        final String propertyName = propertyMetadata.getName();
        try {
            ColumnMetadata columnMetadata = databaseConstraintRepository.getColumnMetadata(propertyMetadata.getPropertyReference());
            if (columnMetadata != null) {
                propertyMetadata.setRequired(columnMetadata.isRequired() && !columnMetadata.isGeneratable());
                propertyMetadata.setMaximumLength(columnMetadata.getMaximumLength());
                propertyMetadata.setFractionLength(columnMetadata.getFractionLength());
                propertyMetadata.setRadix(columnMetadata.getRadix());
            } else {
                logger.debug("Could not resolve column metadata {} ({}).", new Object[] { propertyName, propertyMetadata.getBeanClass().getSimpleName() });
            }
        } catch (NotAnEntityException e) {
            logger.debug("Could not enhance with column metadata, because {} is not an entity", propertyMetadata.getBeanClass().getSimpleName());
        } catch (CouldNotBeMappedToColumnException e) {
            logger.debug("Could not enhance with column metadata, because {} has no column", propertyName);
        }
        return propertyMetadata;
    }

}
