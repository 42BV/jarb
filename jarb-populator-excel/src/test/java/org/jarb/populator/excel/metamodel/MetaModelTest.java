package org.jarb.populator.excel.metamodel;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import domain.entities.Workspace;

public class MetaModelTest {
    private ClassDefinition<?> workspaceDefinition;
    
    @Before
    public void setUp() {
        workspaceDefinition = ClassDefinition.forClass(Workspace.class).setTableName("workspaces").build();
    }

    @Test
    public void testFindClassDefinition() {
        MetaModel metamodel = new MetaModel(workspaceDefinition);
        assertEquals(workspaceDefinition, metamodel.getClassDefinition(Workspace.class));
    }

    @Test
    public void testToString() {
        MetaModel metamodel = new MetaModel(workspaceDefinition);
        assertEquals("[domain.entities.Workspace]", metamodel.toString());
    }
    
}
