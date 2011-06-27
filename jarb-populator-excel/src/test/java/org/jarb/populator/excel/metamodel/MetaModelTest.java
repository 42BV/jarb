package org.jarb.populator.excel.metamodel;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

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
    
}
