/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.populator;

import javax.sql.DataSource;

import org.jarbframework.utils.DatabaseProduct;
import org.jarbframework.utils.StringUtils;
import org.springframework.core.io.ClassPathResource;

/**
 * Database populator that runs product specific.
 * 
 * @author Jeroen van Schagen
 * @since Jan 17, 2014
 */
public class SqlProductSpecificDatabasePopulator implements DatabasePopulator {

    private final DataSource dataSource;

    private final String resourcePath;

    public SqlProductSpecificDatabasePopulator(DataSource dataSource, String resourcePath) {
        this.dataSource = dataSource;
        this.resourcePath = resourcePath;
    }

    @Override
    public void populate() {
        populateFromClassPathIfExists(resourcePath);

        String productResourcePath = withProductName(dataSource, resourcePath);
        populateFromClassPathIfExists(productResourcePath);
    }
    
    private void populateFromClassPathIfExists(String resourcePath) {
        ClassPathResource resource = new ClassPathResource(resourcePath);
        new SqlDatabasePopulator(dataSource, resource).ifExists().populate();
    }

    private static String withProductName(DataSource dataSource, String resourcePath) {
        DatabaseProduct product = DatabaseProduct.fromDataSource(dataSource);

        String baseResourcePath = StringUtils.substringBeforeLast(resourcePath, ".");
        String productName = StringUtils.substringBefore(product.getSimpleName(), " ").toLowerCase();
        String extension = StringUtils.substringAfterLast(resourcePath, ".");
        return baseResourcePath + "@" + productName + "." + extension;
    }

}
