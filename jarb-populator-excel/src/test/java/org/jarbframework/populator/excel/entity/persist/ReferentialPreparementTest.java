package org.jarbframework.populator.excel.entity.persist;

import static org.junit.Assert.assertNotNull;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceUnitUtil;

import org.jarbframework.populator.excel.DefaultExcelTestDataCase;
import org.junit.Test;

import domain.entities.Address;
import domain.entities.CompanyCar;
import domain.entities.CompanyVehicle.Gearbox;
import domain.entities.Employee;

public class ReferentialPreparementTest extends DefaultExcelTestDataCase {

    @Test
    public void testPrepareEntityReferences() {
        Employee employee = new Employee();
        Address address = new Address("Schoolstraat 12", "Zoetermeer");
        employee.setAddress(address);
        employee.setDateOfBirth(new Date());
        employee.setName("Jan de Vries");
        employee.setSalary(1234.56);
        employee.setSalaryScale('A');
        employee.setVehicle(new CompanyCar("T-Ford", 800.23, 40, Gearbox.MANUAL, false));

        EntityManager entityManager = getEntityManagerFactory().createEntityManager();
        PersistenceUnitUtil pUUtil = getEntityManagerFactory().getPersistenceUnitUtil();
        EntityTransaction entityTransaction = entityManager.getTransaction();

        entityTransaction.begin();
        new ReferentialPreparement(entityManager).prepareEntityReferences(employee);
        assertNotNull(pUUtil.getIdentifier(employee.getVehicle()));
        entityTransaction.commit();
    }

}
