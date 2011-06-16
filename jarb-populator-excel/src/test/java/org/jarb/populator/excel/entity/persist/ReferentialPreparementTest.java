package org.jarb.populator.excel.entity.persist;

import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceUnitUtil;

import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.util.ReflectionUtils;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Address;
import domain.entities.CompanyCar;
import domain.entities.CompanyVehicle;
import domain.entities.CompanyVehicle.Gearbox;
import domain.entities.Employee;

public class ReferentialPreparementTest extends DefaultExcelTestDataCase {

    @Before
    public void setupReferentialPreparementTest() throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        //For code coverage purposes:
        Constructor<ReferentialPreparement> constructor = ReferentialPreparement.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testPrepareEntityReferences() {
        CompanyVehicle vehicle = new CompanyCar("T-Ford", 800.23, 40, Gearbox.MANUAL, false);

        Employee employee = new Employee();
        Address address = new Address("Schoolstraat 12", "Zoetermeer");
        employee.setAddress(address);
        employee.setDateOfBirth(new Date());
        employee.setName("Jan de Vries");
        employee.setSalary(1234.56);
        employee.setSalaryScale('A');
        employee.setVehicle(vehicle);

        EntityManager entityManager = getEntityManagerFactory().createEntityManager();
        PersistenceUnitUtil pUUtil = getEntityManagerFactory().getPersistenceUnitUtil();
        Object employeeObject = employee;
        EntityTransaction entityTransaction = entityManager.getTransaction();

        entityTransaction.begin();
        employeeObject = ReferentialPreparement.prepareEntityReferences(employeeObject, entityManager, new HashSet<Object>());
        assertNotNull(pUUtil.getIdentifier(ReflectionUtils.getFieldValue(employeeObject, "vehicle")));
        entityTransaction.commit();
    }
}
