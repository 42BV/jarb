package org.jarb.populator.excel;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.util.Assert;

/**
 * Populates the database with content from an excel file.
 * @author Jeroen van Schagen
 * @since 7-6-2011
 */
public class ExcelDatabasePopulator implements DatabasePopulator {
    /** Represents the excel file resource. **/
    private Resource excelResource;
    /** Allows us to populate the database with excel content. **/
    private ExcelDataManager excelDataManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(Connection connection) throws SQLException {
        Assert.state(excelResource != null && excelResource.exists(), "An existing excel resource is required.");
        Assert.state(excelDataManager != null, "An excel data manager is required.");
        try {
            excelDataManager.persistWorkbook(excelResource.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Could not read excel resource", e);
        }
    }

    public void setExcelResource(Resource excelResource) {
        this.excelResource = excelResource;
    }

    public void setExcelDataManager(ExcelDataManager excelDataManager) {
        this.excelDataManager = excelDataManager;
    }

    public void setExcelDataManagerFactory(ExcelDataManagerFactory excelDataManagerFactory) {
        setExcelDataManager(excelDataManagerFactory.build());
    }
}
