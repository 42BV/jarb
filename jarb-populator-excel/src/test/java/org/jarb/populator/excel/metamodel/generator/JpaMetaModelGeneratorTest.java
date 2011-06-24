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
    public void testGenerate() {
        MetaModel metamodel = metaModelGenerator.generate();
        assertFalse(metamodel.getKnownClasses().isEmpty());
        ClassDefinition<?> releaseDefinition = metamodel.describe(Release.class);
        assertEquals(Release.class, releaseDefinition.getPersistentClass());
        assertEquals("releases", releaseDefinition.getTableName());
    }

}
