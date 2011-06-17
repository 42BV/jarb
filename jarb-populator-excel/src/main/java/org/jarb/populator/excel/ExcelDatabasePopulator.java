package org.jarb.populator.excel;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityManagerFactory;

import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.util.Assert;

/**
 * Populates the database with content from an excel file.
 * @author Jeroen van Schagen
 * @since 7-6-2011
 */
public class ExcelDatabasePopulator implements DatabasePopulator {
    private EntityManagerFactory entityManagerFactory;
    private Resource excelResource;

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(Connection connection) throws SQLException {
        Assert.state(excelResource != null, "Excel resource cannot be null");
        Assert.state(excelResource.exists(), "Excel resource has to exist");
        Assert.state(entityManagerFactory != null, "Entity manager factory cannot be null");
        
        try {
            ExcelDataManager excelDataManager = new ExcelDataManagerFactory(entityManagerFactory).build();
            excelDataManager.persistWorkbook(excelResource.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void setExcelResource(Resource excelResource) {
        this.excelResource = excelResource;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }
    

}
