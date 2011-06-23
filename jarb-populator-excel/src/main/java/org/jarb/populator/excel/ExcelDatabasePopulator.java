package org.jarb.populator.excel;

import javax.persistence.EntityManagerFactory;

import org.jarb.populator.DatabasePopulator;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * Populates the database with content from an excel file.
 * 
 * @author Jeroen van Schagen
 * @since 7-6-2011
 */
public class ExcelDatabasePopulator implements DatabasePopulator {
    private EntityManagerFactory entityManagerFactory;
    private Resource excelResource;
    
    public void setExcelResource(Resource excelResource) {
        this.excelResource = excelResource;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void populate() throws Exception {
        Assert.state(excelResource != null, "Excel resource cannot be null");
        Assert.state(entityManagerFactory != null, "Entity manager factory cannot be null");
        
        ExcelDataManager excelDataManager = new ExcelDataManagerFactory(entityManagerFactory).build();
        excelDataManager.load(excelResource).persist();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("Excel populator '%s'", excelResource);
    }

}
