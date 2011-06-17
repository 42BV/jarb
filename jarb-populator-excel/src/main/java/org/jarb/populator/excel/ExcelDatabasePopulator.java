package org.jarb.populator.excel;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.jarb.populator.ConditionalDatabasePopulator;
import org.jarb.populator.excel.util.JpaUtils;
import org.springframework.core.io.Resource;

/**
 * Populates the database with content from an excel file.
 * @author Jeroen van Schagen
 * @since 7-6-2011
 */
public class ExcelDatabasePopulator extends ConditionalDatabasePopulator {
    /** Represents the excel file resource. **/
    private Resource excelResource;
    private EntityManagerFactory entityManagerFactory;
    
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
    protected SupportsResult supports(final Connection connection) {       
        final SupportsResult result = new SupportsResult();
        
        if(excelResource == null) {
            result.addError("Excel resource cannot be null");
        } else if(!excelResource.exists()) {
            result.addError("Excel resource has to exist");
        }
        
        if(entityManagerFactory == null) {
            result.addError("Entity manager factory cannot be null");
        } else {
            // Ensure the entity manager factory connects to the same database as our JDBC connection
            EntityManager entityManager = JpaUtils.createEntityManager(entityManagerFactory);
            Object delegate = entityManager.getDelegate();
            if(delegate instanceof Session) {
                Session session = (Session) delegate;
                session.doWork(new Work() {

                    @Override
                    public void execute(Connection hibernateConnection) throws SQLException {
                        if(!isConnectionToSameDatabase(connection, hibernateConnection)) {
                            result.addError("Entity manager is connected to another database");
                        }
                    }
                    
                });
            } else {
                result.addError("Entity manager is not hibernate session aware");
            }
        }
        
        return result;
    }
    
    private boolean isConnectionToSameDatabase(Connection left, Connection right) throws SQLException {
        return left.getMetaData().getURL().equals(right.getMetaData().getURL());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doPopulate(Connection connection) throws SQLException {
        try {
            ExcelDataManager excelDataManager = new ExcelDataManagerFactory(entityManagerFactory).build();
            excelDataManager.persistWorkbook(excelResource.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
