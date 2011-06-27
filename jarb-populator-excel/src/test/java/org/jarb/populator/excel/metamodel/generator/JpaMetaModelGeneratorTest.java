package org.jarb.populator.excel.metamodel.generator;

import static org.junit.Assert.assertEquals;

import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.metamodel.EntityDefinition;
import org.jarb.populator.excel.metamodel.MetaModel;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Release;

public class JpaMetaModelGeneratorTest extends DefaultExcelTestDataCase {
    private JpaMetaModelGenerator generator;

    @Before
    public void buildMetaModelGenerator() {
        generator = new JpaMetaModelGenerator(getEntityManagerFactory());
    }

    @Test
    public void testGenerate() {
        MetaModel metamodel = generator.generate();
        assertEquals(13, metamodel.entities().size());
        
        EntityDefinition<?> releaseDefinition = metamodel.entity(Release.class);
        assertEquals(Release.class, releaseDefinition.getEntityClass());
        assertEquals("releases", releaseDefinition.getTableName());
    }

}
