package org.jarb.populator.excel;

import javax.persistence.EntityManagerFactory;

import org.jarb.utils.SingletonFactoryBean;
import org.springframework.util.Assert;

/**
 * Spring factory bean for excel data manager instances.
 * 
 * @author Jeroen van Schagen
 * @since 7-6-2011
 */
public class ExcelDataManagerFactoryBean extends SingletonFactoryBean<ExcelDataManager> {
    private ExcelDataManagerFactory excelDataManagerFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    protected ExcelDataManager createObject() throws Exception {
        Assert.state(excelDataManagerFactory != null, "Excel data manager factory is required.");
        return excelDataManagerFactory.build();
    }

    /**
     * Configure the excel data manager factory that should be used for construction.
     * @param excelDataManagerFactory excel data manager factory
     */
    public void setExcelDataManagerFactory(ExcelDataManagerFactory excelDataManagerFactory) {
        this.excelDataManagerFactory = excelDataManagerFactory;
    }

    /**
     * Configure the default excel data manager factory, using a JPA entity manager factory.
     * @param entityManagerFactory entity manager factory used for storing and querying entities
     */
    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        setExcelDataManagerFactory(new ExcelDataManagerFactory(entityManagerFactory));
    }
}
