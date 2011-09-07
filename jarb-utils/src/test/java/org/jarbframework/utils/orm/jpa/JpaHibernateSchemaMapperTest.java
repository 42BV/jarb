/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarbframework.utils.orm.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.cfg.ImprovedNamingStrategy;
import org.jarbframework.utils.bean.PropertyReference;
import org.jarbframework.utils.orm.ColumnReference;
import org.jarbframework.utils.orm.NotAnEntityException;
import org.jarbframework.utils.orm.jpa.JpaHibernateSchemaMapper;
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

    /**
     * Whenever an entity only has the @Entity annotation, without an entity
     * name. Then the class name should be used.
     */
    @Test
    public void testTableByClassName() {
        assertEquals("ctbl_simple_entity", mapper.table(SimpleEntity.class));
    }

    @Entity
    static class SimpleEntity {

        @Id
        Long id;

    }

    /**
     * However, if the entity has a custom entity name, that name should be used.
     */
    @Test
    public void testTableByEntityName() {
        assertEquals("ctbl_custom", mapper.table(CustomEntity.class));
    }

    @Entity(name = "custom")
    static class CustomEntity {

    }

    /**
     * Also if there is a @Table annotation with an empty name.
     */
    @Test
    public void testTableByEntityNameWithTableEmptyAnnotation() {
        assertEquals("ctbl_custom", mapper.table(CustomEntityWithTable.class));
    }

    @Entity(name = "custom")
    @Table
    static class CustomEntityWithTable {

    }

    /**
     * But whenever the @Table provides a non-empty name, that name should be used.
     * Causing the entity name to be ignored.
     */
    @Test
    public void testTableByTableAnnotation() {
        assertEquals("tbl_custom_table", mapper.table(CustomTable.class));
    }

    @Entity(name = "custom")
    @Table(name = "custom_table")
    static class CustomTable {
    }

    /**
     * A single table inheritance strategy causes sub-classes to have the same
     * table name as their parent class.
     */
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

    /**
     * But table per class inheritance causes sub-classes to have their own
     * table name, different from the parents.
     */
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

    /**
     * Can only request table names for entity classes.
     */
    @Test(expected = NotAnEntityException.class)
    public void testTableForNotAnEntity() {
        mapper.table(String.class);
    }

    // Column

    /**
     * Whenever a property has no @Column annotation, the property name
     * should be used as column name.
     */
    @Test
    public void testColumnByPropertyName() {
        ColumnReference columnReference = mapper.column(new PropertyReference(EntityWithProperties.class, "simpleProperty"));
        assertEquals("ctbl_entity_with_properties", columnReference.getTableName());
        assertEquals("prop_simple_property", columnReference.getColumnName());
    }

    /**
     * Same goes when the @Column has a blank name attribute.
     */
    @Test
    public void testColumnWithEmptyAnnotation() {
        ColumnReference columnReference = mapper.column(new PropertyReference(EntityWithProperties.class, "annotatedProperty"));
        assertEquals("ctbl_entity_with_properties", columnReference.getTableName());
        assertEquals("prop_annotated_property", columnReference.getColumnName());
    }

    /**
     * But when the name attribute is not blank, that name should be used.
     */
    @Test
    public void testColumnByAnnotation() {
        ColumnReference columnReference = mapper.column(new PropertyReference(EntityWithProperties.class, "propertyWithCustomName"));
        assertEquals("ctbl_entity_with_properties", columnReference.getTableName());
        assertEquals("col_custom_property", columnReference.getColumnName());
    }

    /**
     * With @OneToOne we also use the property name.
     */
    @Test
    public void testColumnWithOneToOne() {
        ColumnReference columnReference = mapper.column(new PropertyReference(EntityWithProperties.class, "oneToOneProperty"));
        assertEquals("ctbl_entity_with_properties", columnReference.getTableName());
        assertEquals("fk_one_to_one_property", columnReference.getColumnName());
    }

    /**
     * With @ManyToOne we also use the property name.
     */
    @Test
    public void testColumnWithManyToOne() {
        ColumnReference columnReference = mapper.column(new PropertyReference(EntityWithProperties.class, "manyToOneProperty"));
        assertEquals("ctbl_entity_with_properties", columnReference.getTableName());
        assertEquals("fk_many_to_one_property", columnReference.getColumnName());
    }

    /**
     * Unless a @JoinColumn is provided.
     */
    @Test
    public void testColumnWithManyToOneAndColumnAnnotation() {
        ColumnReference columnReference = mapper.column(new PropertyReference(EntityWithProperties.class, "customManyToOneProperty"));
        assertEquals("ctbl_entity_with_properties", columnReference.getTableName());
        assertEquals("col_custom_reference", columnReference.getColumnName());
    }

    /**
     * Joined inheritance causes properties to be placed inside the
     * table of their declaring class. Thus, properties defined inside
     * the super class, are placed inside the super's table.
     */
    @Test
    public void testColumnWithJoinedInheritanceSuper() {
        ColumnReference columnReference = mapper.column(new PropertyReference(SubJoined.class, "superProperty"));
        assertEquals("ctbl_super_joined", columnReference.getTableName());
        assertEquals("prop_super_property", columnReference.getColumnName());
    }

    /**
     * While properties defined inside the sub class, are placed inside
     * the sub class's table.
     */
    @Test
    public void testColumnWithJoinedInheritanceSub() {
        ColumnReference columnReference = mapper.column(new PropertyReference(SubJoined.class, "subProperty"));
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

    /**
     * Various properties do not map to a (single) column, meaning
     * they will be ignored. This includes:
     * @OneToMany @ManyToMany and @Transient
     */
    @Test
    public void testNotAColumn() {
        assertNull(mapper.column(new PropertyReference(EntityWithProperties.class, "oneToManyProperty")));
        assertNull(mapper.column(new PropertyReference(EntityWithProperties.class, "manyToManyProperty")));
        assertNull(mapper.column(new PropertyReference(EntityWithProperties.class, "elementCollectionProperty")));
        assertNull(mapper.column(new PropertyReference(EntityWithProperties.class, "transientProperty")));
    }

    @Test(expected = NotAnEntityException.class)
    public void testColumnNotAnEntity() {
        mapper.column(new PropertyReference(String.class, "value"));
    }

    @Entity
    static class EntityWithProperties {

        @Id
        Long id;

        String simpleProperty;

        @Column
        String annotatedProperty;

        @Column(name = "custom_property")
        String propertyWithCustomName;

        @OneToOne
        SimpleEntity oneToOneProperty;

        @ManyToOne
        SimpleEntity manyToOneProperty;

        @ManyToOne
        @JoinColumn(name = "custom_reference")
        SimpleEntity customManyToOneProperty;

        @OneToMany
        Collection<SimpleEntity> oneToManyProperty;

        @ManyToMany
        Collection<SimpleEntity> manyToManyProperty;

        @ElementCollection
        Collection<String> elementCollectionProperty;

        @Transient
        String transientProperty;

    }

}
