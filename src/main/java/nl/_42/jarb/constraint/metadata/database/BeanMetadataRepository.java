/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.constraint.metadata.database;

import nl._42.jarb.utils.bean.PropertyReference;

/**
 * Repository that retrieves bean property metadata from the data source.
 *
 * @author Jeroen van Schagen
 * @since Feb 11, 2014
 */
public interface BeanMetadataRepository {
    
    /**
     * Retrieve the meta-data of a specific database column.
     * 
     * @param propertyReference reference to a property
     * @return constraint of the column, or {@code null}
     */
    ColumnMetadata getColumnMetadata(PropertyReference propertyReference);
    
    /**
     * Determine if our property is embeddable.
     * 
     * @param propertyType the property type
     * @return {@code true} if embeddable, else {@code false}
     */
    boolean isEmbeddable(Class<?> propertyType);

}
