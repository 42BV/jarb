package org.jarbframework.populator.excel.metamodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import domain.entities.Customer;
import domain.entities.Workspace;

public class MetaModelTest {
    private EntityDefinition<Workspace> workspaceDefinition;
    private MetaModel metamodel;

    @Before
    public void setUp() {
        workspaceDefinition = EntityDefinition.forClass(Workspace.class).setTableName("workspaces").build();
        metamodel = new MetaModel(Arrays.<EntityDefinition<?>> asList(workspaceDefinition));
    }

    @Test
    public void testFindClassDefinition() {
        assertEquals(workspaceDefinition, metamodel.entity(Workspace.class));
    }

    @Test
    public void testToString() {
        assertEquals("[class domain.entities.Workspace]", metamodel.toString());
    }

    @Test
    public void testContains() {
        assertTrue(metamodel.contains(domain.entities.Workspace.class));
        assertFalse(metamodel.contains(domain.entities.Project.class));

        EntityDefinition<Customer> customerDefinition = EntityDefinition.forClass(Customer.class).setTableName("customers").build();
        metamodel = new MetaModel(Arrays.<EntityDefinition<?>> asList(customerDefinition));
        assertTrue(metamodel.contains(domain.entities.SpecialCustomer.class));
    }

}
