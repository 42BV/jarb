package org.jarbframework.populator.excel;

import static org.jarbframework.populator.excel.mapping.ValueConversionService.defaultConversions;

import java.io.IOException;

import javax.persistence.EntityManagerFactory;

import org.jarbframework.populator.DatabaseUpdater;
import org.jarbframework.populator.condition.ConditionalDatabaseUpdater;
import org.jarbframework.populator.condition.ResourceExistsCondition;
import org.jarbframework.populator.excel.mapping.ValueConversionService;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * Populates the database with content from an excel file.
 * 
 * @author Jeroen van Schagen
 * @since 7-6-2011
 */
public class ExcelDatabaseUpdater implements DatabaseUpdater {
    private ValueConversionService valueConversionService;
    private EntityManagerFactory entityManagerFactory;
    private Resource excelResource;
    private boolean strict = true;

    public void setExcelResource(Resource excelResource) {
        this.excelResource = excelResource;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void setValueConversionService(ValueConversionService valueConversionService) {
        this.valueConversionService = valueConversionService;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    /**
     * Construct a new {@link ExcelDatabaseUpdater} that skips
     * whenever the specified resource does not exist. Use this type of
     * database populator whenever it is uncertain if a resource exists.
     * 
     * @param excelResource reference to the excel workbook resource
     * @param entityManagerFactory JPA entity manager used to inspect and persist data
     * @return database populator that will persist all entities declared inside the workbook
     */
    public static ConditionalDatabaseUpdater ignoreIfResourceMissing(Resource excelResource, EntityManagerFactory entityManagerFactory) {
        ExcelDatabaseUpdater populator = new ExcelDatabaseUpdater();
        populator.setExcelResource(excelResource);
        populator.setEntityManagerFactory(entityManagerFactory);
        return new ConditionalDatabaseUpdater(populator, new ResourceExistsCondition(excelResource));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        Assert.state(excelResource != null, "Excel resource cannot be null");
        Assert.state(entityManagerFactory != null, "Entity manager factory cannot be null");

        ExcelDataManager excelDataManager = new ExcelDataManagerFactory(entityManagerFactory, loadValueConversionService()).build();
        excelDataManager.setStrict(strict);
        
        try {
            excelDataManager.load(excelResource).persist();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ValueConversionService loadValueConversionService() {
        return valueConversionService != null ? valueConversionService : defaultConversions();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("Excel populator '%s'", excelResource);
    }

}
