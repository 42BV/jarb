/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.populator;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.jarbframework.utils.DatabaseProduct;
import org.springframework.core.io.ClassPathResource;

/**
 * Database populator that runs product specific.
 * 
 * @author Jeroen van Schagen
 * @since Jan 17, 2014
 */
public class SqlProductSpecificResourceDatabasePopulator implements DatabasePopulator {

    private final DataSource dataSource;

    private final String resourcePath;

    public SqlProductSpecificResourceDatabasePopulator(DataSource dataSource, String resourcePath) {
        this.dataSource = dataSource;
        this.resourcePath = resourcePath;
    }

    @Override
    public void populate() {
        DatabaseProduct product = DatabaseProduct.fromDataSource(dataSource);
        String productResourcePath = appendProductNameBeforeExtension(product);
        
        populateFromClassPathIfExists(resourcePath);
        populateFromClassPathIfExists(productResourcePath);
    }
    
    private String appendProductNameBeforeExtension(DatabaseProduct product) {
        String baseResourcePath = StringUtils.substringBeforeLast(resourcePath, ".");
        String productName = StringUtils.substringBefore(product.getName(), " ").toLowerCase();
        String extension = StringUtils.substringAfterLast(resourcePath, ".");
        return baseResourcePath + "-" + productName + "." + extension;
    }
    
    private void populateFromClassPathIfExists(String resourcePath) {
        ClassPathResource resource = new ClassPathResource(resourcePath);
        new SqlDatabasePopulator(dataSource, resource).ifExists().populate();
    }

}
