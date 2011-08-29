/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarb.utils.orm.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.cfg.ImprovedNamingStrategy;
import org.jarb.utils.bean.PropertyReference;
import org.jarb.utils.orm.ColumnReference;
import org.jarb.utils.orm.NotAnEntityException;
import org.junit.Before;
import org.junit.Test;

public class JpaHibernateSchemaMapperTest {
    private JpaHibernateSchemaMapper mapper;

    @Before
    public void setUp() {
        mapper = new JpaHibernateSchemaMapper(new ImprovedNamingStrategy() {
            @Override
            public String classToTableName(String className) {
                return "ctbl_" + super.classToTableName(className);
            }

            @Override
            public String tableName(String tableName) {
                return "tbl_" + super.tableName(tableName);
            }

            @Override
            public String propertyToColumnName(String propertyName) {
                return "prop_" + super.propertyToColumnName(propertyName);
            }

            @Override
            public String foreignKeyColumnName(String propertyName, String propertyEntityName, String propertyTableName, String referencedColumnName) {
                return "fk_" + super.columnName(propertyName);
            }

            @Override
            public String columnName(String columnName) {
                return "col_" + super.columnName(columnName);
            }
        });
    }

    // Table

    @Test
    public void testTableByClassName() {
        assertEquals("ctbl_simple_entity", mapper.table(SimpleEntity.class));
    }

    @Entity
    static class SimpleEntity {

        @Id
        Long id;

    }

    @Test
    public void testTableByEntityName() {
        assertEquals("ctbl_custom", mapper.table(CustomEntity.class));
    }

    @Entity(name = "custom")
    static class CustomEntity {

    }

    @Test
    public void testTableByEntityNameWithTableEmptyAnnotation() {
        assertEquals("ctbl_custom", mapper.table(CustomEntityWithTable.class));
    }

    @Entity(name = "custom")
    @Table
    static class CustomEntityWithTable {

    }

    @Test
    public void testTableByTableAnnotation() {
        assertEquals("tbl_custom_table", mapper.table(CustomTable.class));
    }

    @Entity(name = "custom")
    @Table(name = "custom_table")
    static class CustomTable {
    }

    @Test
    public void testTableBySingleTableInheritance() {
        assertEquals("ctbl_super_single_table", mapper.table(SubSingleTable.class));
    }

    @Entity
    @Inheritance
    static abstract class SuperSingleTable {

    }

    @Entity
    static abstract class SubSingleTable extends SuperSingleTable {

    }

    @Test
    public void testTableByTablePerClassInheritance() {
        assertEquals("ctbl_sub_table_per_class", mapper.table(SubTablePerClass.class));
    }

    @Entity
    @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
    static abstract class SuperTablePerClass {

    }

    @Entity
    static abstract class SubTablePerClass extends SuperTablePerClass {

    }

    @Test(expected = NotAnEntityException.class)
    public void testTableForNotAnEntity() {
        mapper.table(String.class);
    }

    // Column

    @Test
    public void testColumnByPropertyName() {
        ColumnReference columnReference = mapper.column(new PropertyReference("simpleProperty", EntityWithColumns.class));
        assertEquals("ctbl_entity_with_columns", columnReference.getTableName());
        assertEquals("prop_simple_property", columnReference.getColumnName());
    }

    @Test
    public void testColumnWithEmptyAnnotation() {
        ColumnReference columnReference = mapper.column(new PropertyReference("annotatedProperty", EntityWithColumns.class));
        assertEquals("ctbl_entity_with_columns", columnReference.getTableName());
        assertEquals("prop_annotated_property", columnReference.getColumnName());
    }

    @Test
    public void testColumnByAnnotation() {
        ColumnReference columnReference = mapper.column(new PropertyReference("propertyWithCustomName", EntityWithColumns.class));
        assertEquals("ctbl_entity_with_columns", columnReference.getTableName());
        assertEquals("col_custom_property", columnReference.getColumnName());
    }

    @Test
    public void testColumnWithManyToOne() {
        ColumnReference columnReference = mapper.column(new PropertyReference("manyToOneProperty", EntityWithColumns.class));
        assertEquals("ctbl_entity_with_columns", columnReference.getTableName());
        assertEquals("fk_many_to_one_property", columnReference.getColumnName());
    }

    @Test
    public void testColumnWithManyToOneAndColumnAnnotation() {
        ColumnReference columnReference = mapper.column(new PropertyReference("customManyToOneProperty", EntityWithColumns.class));
        assertEquals("ctbl_entity_with_columns", columnReference.getTableName());
        assertEquals("col_custom_many_to_one_property", columnReference.getColumnName());
    }

    @Test
    public void testColumnWithJoinedInheritanceSuper() {
        ColumnReference columnReference = mapper.column(new PropertyReference("superProperty", SubJoined.class));
        assertEquals("ctbl_super_joined", columnReference.getTableName());
        assertEquals("prop_super_property", columnReference.getColumnName());
    }

    @Test
    public void testColumnWithJoinedInheritanceSub() {
        ColumnReference columnReference = mapper.column(new PropertyReference("subProperty", SubJoined.class));
        assertEquals("ctbl_sub_joined", columnReference.getTableName());
        assertEquals("prop_sub_property", columnReference.getColumnName());
    }

    @Entity
    @Inheritance(strategy = InheritanceType.JOINED)
    static abstract class SuperJoined {
        String superProperty;
    }

    @Entity
    static abstract class SubJoined extends SuperJoined {
        String subProperty;
    }

    @Test
    public void testNotAColumn() {
        assertNull(mapper.column(new PropertyReference("oneToManyProperty", EntityWithColumns.class)));
        assertNull(mapper.column(new PropertyReference("manyToManyProperty", EntityWithColumns.class)));
    }

    @Test(expected = NotAnEntityException.class)
    public void testColumnNotAnEntity() {
        mapper.column(new PropertyReference("value", String.class));
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
        SimpleEntity manyToOneProperty;

        @ManyToOne
        @JoinColumn(name = "custom_many_to_one_property")
        SimpleEntity customManyToOneProperty;

        @OneToMany
        SimpleEntity oneToManyProperty;

        @ManyToMany
        SimpleEntity manyToManyProperty;

    }

}
