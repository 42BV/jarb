package org.jarb.populator.excel.metamodel;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Before;
import org.junit.Test;

public class JoinTableTest {

    private ColumnDefinition joinTable = new JoinTable("projects");
    private Class<?> persistentClass;
    private Field projectsField;

    @Before
    public void testSetUpColumnDefinition() throws SecurityException, NoSuchFieldException, InvalidFormatException, IOException, InstantiationException,
            IllegalAccessException {
        persistentClass = domain.entities.Employee.class;

        projectsField = persistentClass.getDeclaredField("projects");
    }

    @Test
    public void testStoreAnnotationGetColumnNames() throws InstantiationException, IllegalAccessException {
        for (Annotation annotation : projectsField.getAnnotations()) {
            for (AnnotationType annotationType : AnnotationType.values()) {
                if ((annotationType.name().equals("JOIN_TABLE")) && annotationType.getAnnotationClass().isAssignableFrom(annotation.getClass())) {
                    joinTable = annotationType.createColumnDefinition("projects");
                    joinTable.storeAnnotation(projectsField, annotation);
                }
            }
        }
        assertEquals("project_id", ((JoinTable) joinTable).getInverseJoinColumnName());
        assertEquals("employee_id", ((JoinTable) joinTable).getJoinColumnName());
    }
}
