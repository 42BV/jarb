package org.jarb.populator.excel.metamodel.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.metamodel.ClassDefinition;
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
    public void testGenerateAll() {
        MetaModel metaModel = metaModelGenerator.generate();
        assertFalse(metaModel.getClassDefinitions().isEmpty());
    }

    @Test
    public void testGenerateFor() {
        MetaModel metaModel = metaModelGenerator.generateFor(Release.class);
        assertEquals(1, metaModel.getClassDefinitions().size());
        ClassDefinition<?> releaseDefinition = metaModel.describeClass(Release.class);
        assertEquals("releases", releaseDefinition.getTableName());
    }

    // TODO: Write extensive tests

}
