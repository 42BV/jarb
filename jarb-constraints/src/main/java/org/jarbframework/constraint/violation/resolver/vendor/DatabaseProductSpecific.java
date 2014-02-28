package org.jarbframework.constraint.violation.resolver.vendor;

import org.jarbframework.utils.DatabaseProduct;

/**
 * Checks if this strategy supports the product.
 *
 * @author Jeroen van Schagen
 * @since Feb 28, 2014
 */
public interface DatabaseProductSpecific {
    
    boolean supports(DatabaseProduct product);
    
}
