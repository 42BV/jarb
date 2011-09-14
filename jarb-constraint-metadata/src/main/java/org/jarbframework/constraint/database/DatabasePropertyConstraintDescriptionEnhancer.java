package org.jarbframework.constraint.database;

import static org.jarbframework.utils.Asserts.notNull;

import org.jarbframework.constraint.PropertyConstraintDescription;
import org.jarbframework.constraint.PropertyConstraintEnhancer;
import org.jarbframework.constraint.database.column.ColumnMetadata;
import org.jarbframework.utils.bean.PropertyReference;
import org.jarbframework.utils.orm.NotAnEntityException;
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
    public PropertyConstraintDescription enhance(PropertyConstraintDescription propertyConstraints) {
        PropertyReference propertyReference = propertyConstraints.toPropertyReference();
        try {
            ColumnMetadata columnMetadata = databaseConstraintRepository.getColumnMetadata(propertyReference);
            if (columnMetadata != null) {
                propertyConstraints.setRequired(columnMetadata.isRequired() && !columnMetadata.isGeneratable());
                propertyConstraints.setMaximumLength(columnMetadata.getMaximumLength());
                propertyConstraints.setFractionLength(columnMetadata.getFractionLength());
                propertyConstraints.setRadix(columnMetadata.getRadix());
            } else {
                logger.debug("Could not resolve column metadata {} ({}).", new Object[] {
                        propertyReference.getName(),
                        propertyReference.getBeanClass().getSimpleName()
                });
            }
        } catch (NotAnEntityException e) {
            logger.debug("Could not enhance with column metadata, because {} is not an entity", propertyReference.getBeanClass().getSimpleName());
        } catch (CouldNotBeMappedToColumnException e) {
            logger.debug("Could not enhance with column metadata, because {} has no column", propertyReference.getName());
        }
        return propertyConstraints;
    }

}
