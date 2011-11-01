package org.jarbframework.populator.excel.metamodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import domain.entities.Customer;
import domain.entities.Project;
import domain.entities.SpecialCustomer;
import domain.entities.Workspace;

public class MetaModelTest {
    private EntityDefinition<Workspace> workspaceDefinition;
    private EntityDefinition<Customer> customerDefinition;
    private MetaModel metamodel;
    private MetaModel customerMetamodel;

    @Before
    public void setUp() {
        workspaceDefinition = EntityDefinition.forClass(Workspace.class).setTableName("workspaces").build();
        customerDefinition = EntityDefinition.forClass(Customer.class).setTableName("customers").build();
        metamodel = new MetaModel(Arrays.<EntityDefinition<?>> asList(workspaceDefinition));
        customerMetamodel = new MetaModel(Arrays.<EntityDefinition<?>> asList(customerDefinition));
    }

    @Test
    public void testFindClassDefinition() {
        assertEquals(workspaceDefinition, metamodel.entity(Workspace.class));
        assertEquals(customerDefinition, customerMetamodel.entity(SpecialCustomer.class));
    }

    @Test
    public void testEntities() {
        assertTrue(metamodel.entities().contains(workspaceDefinition));
    }

    @Test
    public void testIterator() {
        Iterator<EntityDefinition<?>> iterator = metamodel.iterator();
        assertEquals(workspaceDefinition, iterator.next());
    }

    @Test
    public void testToString() {
        assertEquals("[class domain.entities.Workspace]", metamodel.toString());
    }

    @Test
    public void testContains() {
        assertTrue(metamodel.contains(Workspace.class));
        assertFalse(metamodel.contains(Project.class));

        assertTrue(customerMetamodel.contains(SpecialCustomer.class));
    }

}
