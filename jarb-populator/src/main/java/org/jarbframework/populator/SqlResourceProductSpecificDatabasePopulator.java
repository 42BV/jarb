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
 * @author jeroen
 * @since Jan 17, 2014
 */
public class SqlResourceProductSpecificDatabasePopulator implements DatabasePopulator {

    private final DataSource dataSource;

    private final String resourcePath;

    public SqlResourceProductSpecificDatabasePopulator(DataSource dataSource, String resourcePath) {
        this.dataSource = dataSource;
        this.resourcePath = resourcePath;
    }

    @Override
    public void populate() {
        DatabaseProduct product = DatabaseProduct.fromDataSource(dataSource);
        String productResourcePath = getProductSpecificResourcePath(product);
        
        populateFromClassPath(resourcePath);
        populateFromClassPath(productResourcePath);
    }
    
    private String getProductSpecificResourcePath(DatabaseProduct product) {
        String start = StringUtils.substringBeforeLast(resourcePath, ".");
        String end = StringUtils.substringAfterLast(resourcePath, ".");
        String productIdentifier = StringUtils.substringBefore(product.getName(), " ").toLowerCase();
        return start + "-" + productIdentifier + "." + end;
    }
    
    private void populateFromClassPath(String resourcePath) {
        ClassPathResource resource = new ClassPathResource(resourcePath);
        if (resource.exists()) {
            new SqlResourceDatabasePopulator(dataSource, resource).populate();
        }
    }

}
