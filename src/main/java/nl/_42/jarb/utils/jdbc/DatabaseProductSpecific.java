package nl._42.jarb.utils.jdbc;

/**
 * Checks if this strategy supports the product.
 *
 * @author Jeroen van Schagen
 * @since Feb 28, 2014
 */
public interface DatabaseProductSpecific {
    
    /**
     * Determine if we support this database product.
     * @param product the product that we should support
     * @return {@code true} if supported, else {@code false}
     */
    boolean supports(DatabaseProduct product);

}
