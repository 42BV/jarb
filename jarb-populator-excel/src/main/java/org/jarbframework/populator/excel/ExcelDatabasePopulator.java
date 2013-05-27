package org.jarbframework.populator.excel;

import static org.jarbframework.populator.excel.mapping.ValueConversionService.defaultConversions;

import java.io.IOException;

import javax.persistence.EntityManagerFactory;

import org.jarbframework.populator.DatabasePopulator;
import org.jarbframework.populator.excel.mapping.ValueConversionService;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * Populates the database with content from an excel file.
 * 
 * @author Jeroen van Schagen
 * @since 7-6-2011
 */
public class ExcelDatabasePopulator implements DatabasePopulator {
        
    private final EntityManagerFactory entityManagerFactory;
    
    private final Resource resource;
    
    private ValueConversionService valueConversionService;
    
    private boolean strict = true;
    
    public ExcelDatabasePopulator(EntityManagerFactory entityManagerFactory, Resource resource) {
        Assert.state(resource != null, "Excel resource cannot be null");
        Assert.state(entityManagerFactory != null, "Entity manager factory cannot be null");
        
        this.entityManagerFactory = entityManagerFactory;
        this.resource = resource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate() {
        ExcelDataManager excelDataManager = buildExcelDataManager();
        excelDataManager.setStrict(strict);

        try {
            excelDataManager.load(resource).persist();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ExcelDataManager buildExcelDataManager() {
        ValueConversionService valueConversionService = this.valueConversionService != null ? this.valueConversionService : defaultConversions();
        return new ExcelDataManagerFactory(entityManagerFactory, valueConversionService).build();
    }

    public void setValueConversionService(ValueConversionService valueConversionService) {
        this.valueConversionService = valueConversionService;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

}
