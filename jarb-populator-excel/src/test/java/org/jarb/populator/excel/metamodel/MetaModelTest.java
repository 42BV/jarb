package org.jarb.populator.excel.metamodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import domain.entities.Workspace;

public class MetaModelTest {
    private MetaModel metamodel;

    @Before
    public void setUpMetamodel() {
        metamodel = new MetaModel();
    }

    @Test
    public void testFindClassDefinition() {
        assertNull(metamodel.findClassDefinition(Workspace.class));
        ClassDefinition<?> workspaceDefinition = ClassDefinition.forClass(Workspace.class);
        metamodel.addClassDefinition(workspaceDefinition);
        assertEquals(workspaceDefinition, metamodel.findClassDefinition(Workspace.class));
    }

    @Test
    public void testToString() {
        ClassDefinition<?> workspaceDefinition = ClassDefinition.forClass(Workspace.class);
        metamodel.addClassDefinition(workspaceDefinition);
        assertEquals("[domain.entities.Workspace]", metamodel.toString());
    }
}
