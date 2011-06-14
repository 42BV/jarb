package org.jarb.populator.excel.util;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
import org.junit.Before;
import org.junit.Test;

public class ClassDefinitionNameComparatorTest extends DefaultExcelTestDataCase {

    private List<ClassDefinition<?>> classDefinitions;

    @Before
    public void setupTestCompare() throws SecurityException, ClassNotFoundException, NoSuchFieldException, InstantiationException, IllegalAccessException {
        classDefinitions = ClassDefinitionsGenerator.createClassDefinitionsFromMetamodel(getEntityManagerFactory());
    }

    @Test
    public void testCompare() {
        Collections.sort(classDefinitions, new ClassDefinitionNameComparator());
        assertEquals("NoTableAnnotation", classDefinitions.get(0).getTableName());
        assertEquals("customers", classDefinitions.get(1).getTableName());
        assertEquals("departments", classDefinitions.get(2).getTableName());
        assertEquals("document_revisions", classDefinitions.get(3).getTableName());
        assertEquals("documents", classDefinitions.get(4).getTableName());
        assertEquals("employees", classDefinitions.get(5).getTableName());
        assertEquals("employees_projects_workspaces", classDefinitions.get(6).getTableName());
        assertEquals("gifts", classDefinitions.get(7).getTableName());
        assertEquals("projects", classDefinitions.get(8).getTableName());
        assertEquals("releases", classDefinitions.get(9).getTableName());
        assertEquals("sla", classDefinitions.get(10).getTableName());
        assertEquals("vehicles", classDefinitions.get(11).getTableName());
        assertEquals("workspaces", classDefinitions.get(12).getTableName());
    }

}
