package org.jarb.populator.excel.metamodel;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import domain.entities.Workspace;

public class MetaModelTest {

    @Test
    public void testFindClassDefinition() {
        final ClassDefinition<?> workspaceDefinition = ClassDefinition.forClass(Workspace.class);
        MetaModel metamodel = new MetaModel(workspaceDefinition);
        assertEquals(workspaceDefinition, metamodel.findClassDefinition(Workspace.class));
    }

    @Test
    public void testToString() {
        final ClassDefinition<?> workspaceDefinition = ClassDefinition.forClass(Workspace.class);
        MetaModel metamodel = new MetaModel(workspaceDefinition);
        assertEquals("[domain.entities.Workspace]", metamodel.toString());
    }
    
}
