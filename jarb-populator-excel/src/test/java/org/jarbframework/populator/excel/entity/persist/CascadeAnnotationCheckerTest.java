package org.jarbframework.populator.excel.entity.persist;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.jarbframework.populator.excel.entity.persist.CascadeAnnotationChecker;
import org.junit.Before;
import org.junit.Test;

public class CascadeAnnotationCheckerTest {

    @Before
    public void setupCascadeAnnotationCheckerTest() throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        //For code coverage purposes:
        Constructor<CascadeAnnotationChecker> constructor = CascadeAnnotationChecker.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testHasNecessaryCascadeAnnotations() throws SecurityException, NoSuchFieldException {
        Class<?> persistentClass = domain.entities.Project.class;
        Field customer = persistentClass.getDeclaredField("customer");
        Annotation annotation = customer.getAnnotation(javax.persistence.ManyToOne.class);
        assertFalse(CascadeAnnotationChecker.hasNecessaryCascadeAnnotations(annotation));
    }

    @Test
    public void testDoesNotHaveNecessaryCascadeAnnotations() throws SecurityException, NoSuchFieldException {
        Class<?> persistentClass = domain.entities.Employee.class;
        Field vehicle = persistentClass.getDeclaredField("vehicle");
        Annotation annotation = vehicle.getAnnotation(javax.persistence.ManyToOne.class);
        assertFalse(CascadeAnnotationChecker.hasNecessaryCascadeAnnotations(annotation));
    }

    @Test
    public void testOtherAnnotation() throws SecurityException, NoSuchFieldException {
        Class<?> persistentClass = domain.entities.Employee.class;
        Field salary = persistentClass.getDeclaredField("salary");
        Annotation annotation = salary.getAnnotation(javax.persistence.Column.class);
        assertFalse(CascadeAnnotationChecker.hasNecessaryCascadeAnnotations(annotation));
    }

    @Test
    public void testAllCascadeTypesOnManyToMany() throws SecurityException, NoSuchFieldException {
        Class<?> persistentClass = domain.entities.VeryImportantCustomer.class;
        Field gifts = persistentClass.getDeclaredField("gifts");
        Annotation annotation = gifts.getAnnotation(javax.persistence.ManyToMany.class);
        assertTrue(CascadeAnnotationChecker.hasNecessaryCascadeAnnotations(annotation));
    }

}
