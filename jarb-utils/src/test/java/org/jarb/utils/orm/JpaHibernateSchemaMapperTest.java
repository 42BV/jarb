/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarb.utils.orm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.cfg.ImprovedNamingStrategy;
import org.junit.Before;
import org.junit.Test;

public class JpaHibernateSchemaMapperTest {
    private JpaHibernateSchemaMapper mapper;

    @Before
    public void setUp() {
        mapper = new JpaHibernateSchemaMapper(new ImprovedNamingStrategy() {

            // Custom naming strategy, just used for assertion purposes

            @Override
            public String classToTableName(String className) {
                return "ctbl_" + className;
            }

            @Override
            public String tableName(String tableName) {
                return "tbl_" + tableName;
            }

            @Override
            public String propertyToColumnName(String propertyName) {
                return "prop_" + propertyName;
            }

            @Override
            public String foreignKeyColumnName(String propertyName, String propertyEntityName, String propertyTableName, String referencedColumnName) {
                return "fk_" + propertyName;
            }

            @Override
            public String columnName(String columnName) {
                return "col_" + columnName;
            }

        });
    }

    // Table naming

    @Test
    public void testClassName() {
        assertEquals("ctbl_SimpleEntity", mapper.table(SimpleEntity.class));
    }

    @Entity
    static class SimpleEntity {

    }

    @Test
    public void testEntityName() {
        assertEquals("ctbl_custom", mapper.table(CustomEntity.class));
    }

    @Entity(name = "custom")
    static class CustomEntity {

    }

    @Test
    public void testEntityNameWithTableAnnotation() {
        assertEquals("ctbl_custom", mapper.table(CustomEntityWithTable.class));
    }

    @Entity(name = "custom")
    @Table
    static class CustomEntityWithTable {

    }

    @Test
    public void testCustomTableName() {
        assertEquals("tbl_custom_table", mapper.table(CustomTable.class));
    }

    @Entity(name = "custom")
    @Table(name = "custom_table")
    static class CustomTable {

    }

    // Column naming

    @Test
    public void testPropertyName() {
        assertEquals("prop_simpleProperty", mapper.column(EntityWithColumns.class, "simpleProperty"));
    }

    @Test
    public void testPropertyNameWithColumnAnnotation() {
        assertEquals("prop_annotatedProperty", mapper.column(EntityWithColumns.class, "annotatedProperty"));
    }

    @Test
    public void testCustomColumnName() {
        assertEquals("col_custom_property", mapper.column(EntityWithColumns.class, "propertyWithCustomName"));
    }

    @Test
    public void testManyToOne() {
        assertEquals("fk_manyToOneProperty", mapper.column(EntityWithColumns.class, "manyToOneProperty"));
    }

    @Test
    public void testManyToOneCustomColumnName() {
        assertEquals("col_custom_many_to_one_property", mapper.column(EntityWithColumns.class, "customManyToOneProperty"));
    }

    @Test
    public void testUnsupportedMappings() {
        assertNull(mapper.column(EntityWithColumns.class, "oneToManyProperty"));
        assertNull(mapper.column(EntityWithColumns.class, "manyToManyProperty"));
    }

    @Entity
    static class EntityWithColumns {

        @Id
        Long id;

        String simpleProperty;

        @Column
        String annotatedProperty;

        @Column(name = "custom_property")
        String propertyWithCustomName;

        @ManyToOne
        OtherEntityWithColumns manyToOneProperty;

        @ManyToOne
        @JoinColumn(name = "custom_many_to_one_property")
        OtherEntityWithColumns customManyToOneProperty;

        @OneToMany
        OtherEntityWithColumns oneToManyProperty;

        @ManyToMany
        OtherEntityWithColumns manyToManyProperty;

    }

    @Entity
    static class OtherEntityWithColumns {

        @Id
        Long id;

    }

}
