package org.jarbframework.constraint.metadata.database;

import static org.jarbframework.utils.Asserts.notNull;

import org.jarbframework.constraint.metadata.PropertyConstraintDescription;
import org.jarbframework.constraint.metadata.PropertyConstraintEnhancer;
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

    @Override
    public PropertyConstraintDescription enhance(PropertyConstraintDescription description) {
        PropertyReference propertyReference = description.toPropertyReference();

        try {
            ColumnMetadata columnMetadata = databaseConstraintRepository.getColumnMetadata(propertyReference);
            if (columnMetadata != null) {
                description.setRequired(columnMetadata.isRequired() && !columnMetadata.isGeneratable());
                description.setMaximumLength(columnMetadata.getMaximumLength());
                description.setFractionLength(columnMetadata.getFractionLength());
                description.setRadix(columnMetadata.getRadix());
            } else {
                logger.debug("Could not resolve column metadata for '{}'.", propertyReference);
            }
        } catch (NotAnEntityException e) {
            logger.debug("Did not include database property constraints, because '{}' is not an entity", propertyReference.getBeanClass().getSimpleName());
        } catch (CouldNotBeMappedToColumnException e) {
            logger.debug("Did not include database property constraints, because '{}' is not a column.", propertyReference);
        }

        return description;
    }

}
