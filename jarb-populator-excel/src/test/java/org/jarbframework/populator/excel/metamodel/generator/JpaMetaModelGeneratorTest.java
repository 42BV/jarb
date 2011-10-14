package org.jarbframework.populator.excel.metamodel.generator;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertEquals;

import org.hamcrest.Matchers;
import org.jarbframework.populator.excel.DefaultExcelTestDataCase;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.populator.excel.metamodel.MetaModel;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Currency;
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
        assertEquals(14, metamodel.entities().size());

        EntityDefinition<?> releaseDefinition = metamodel.entity(Release.class);
        assertEquals(Release.class, releaseDefinition.getEntityClass());
        assertEquals("releases", releaseDefinition.getTableName());
    }

    @Test
    public void testInheritPropertiesFromSuperclass() {
        MetaModel metamodel = generator.generate();
        EntityDefinition<? super Currency> currencyDef = metamodel.entity(Currency.class);
        Assert.assertThat(currencyDef.properties(), Matchers.<PropertyDefinition> hasItem(hasProperty("name", equalTo("code"))));
    }

}
