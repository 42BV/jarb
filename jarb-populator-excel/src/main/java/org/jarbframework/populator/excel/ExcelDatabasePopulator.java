package org.jarbframework.populator.excel;

import javax.persistence.EntityManagerFactory;

import org.jarbframework.populator.ConditionalDatabasePopulator;
import org.jarbframework.populator.DatabasePopulator;
import org.jarbframework.populator.condition.ResourceExistsCondition;
import org.jarbframework.populator.excel.mapping.ValueConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * Populates the database with content from an excel file.
 * 
 * @author Jeroen van Schagen
 * @since 7-6-2011
 */
public class ExcelDatabasePopulator implements DatabasePopulator {
    private GenericConversionService conversionService;
    private EntityManagerFactory entityManagerFactory;
    private Resource excelResource;

    public void setExcelResource(Resource excelResource) {
        this.excelResource = excelResource;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void setConversionService(GenericConversionService conversionService) {
        this.conversionService = conversionService;
    }

    /**
     * Construct a new {@link ExcelDatabasePopulator} that skips
     * whenever the specified resource does not exist. Use this type of
     * database populator whenever it is uncertain if a resource exists.
     * 
     * @param excelResource reference to the excel workbook resource
     * @param entityManagerFactory JPA entity manager used to inspect and persist data
     * @return database populator that will persist all entities declared inside the workbook
     */
    public static ConditionalDatabasePopulator ignoreIfResourceMissing(Resource excelResource, EntityManagerFactory entityManagerFactory) {
        ExcelDatabasePopulator excelPopulator = new ExcelDatabasePopulator();
        excelPopulator.setExcelResource(excelResource);
        excelPopulator.setEntityManagerFactory(entityManagerFactory);
        return new ConditionalDatabasePopulator(excelPopulator, new ResourceExistsCondition(excelResource));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate() throws Exception {
        Assert.state(excelResource != null, "Excel resource cannot be null");
        Assert.state(entityManagerFactory != null, "Entity manager factory cannot be null");

        ExcelDataManager excelDataManager = new ExcelDataManagerFactory(entityManagerFactory, buildValueConversionService()).build();
        excelDataManager.persist(excelDataManager.loadWorkbook(excelResource));
    }

    private ValueConversionService buildValueConversionService() {
        return conversionService != null ? new ValueConversionService(conversionService) : new ValueConversionService();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("Excel populator '%s'", excelResource);
    }

}
