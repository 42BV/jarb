/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.populate.predicates;

import java.io.File;
import java.util.function.Predicate;

import javax.sql.DataSource;

import nl._42.jarb.utils.StringUtils;
import nl._42.jarb.utils.jdbc.DatabaseProduct;

/**
 * Determines if a file name has a product suffix.
 * For example, an SQL file for HSQL should be: 001_bla@hsql.sql
 *
 * @author Jeroen van Schagen
 * @since Apr 10, 2015
 */
public class IsNotForOtherProduct implements Predicate<File> {
    
    private final DatabaseProduct product;
    
    public IsNotForOtherProduct(DataSource dataSource) {
        this(DatabaseProduct.fromDataSource(dataSource));
    }

    public IsNotForOtherProduct(DatabaseProduct product) {
        this.product = product;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean test(File input) {
        String baseResourcePath = StringUtils.substringBeforeLast(input.getName(), ".");
        String resourceProductName = StringUtils.substringAfterLast(baseResourcePath, "@");
        return StringUtils.isBlank(resourceProductName) || product.getShortName().equalsIgnoreCase(resourceProductName);
    }
    
}
