package org.jarb.populator.excel.metamodel.generator;

import static org.junit.Assert.assertEquals;

import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.metamodel.EntityDefinition;
import org.jarb.populator.excel.metamodel.MetaModel;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Release;

public class JpaMetaModelGeneratorTest extends DefaultExcelTestDataCase {
    private JpaMetaModelGenerator metaModelGenerator;

    @Before
    public void buildMetaModelGenerator() {
        metaModelGenerator = new JpaMetaModelGenerator(getEntityManagerFactory());
    }

    @Test
    public void testGenerate() {
        MetaModel metamodel = metaModelGenerator.generate();
        assertEquals(13, metamodel.getKnownClasses().size());
        
        EntityDefinition<?> releaseDefinition = metamodel.entity(Release.class);
        assertEquals(Release.class, releaseDefinition.getEntityClass());
        assertEquals("releases", releaseDefinition.getTableName());
    }

}
