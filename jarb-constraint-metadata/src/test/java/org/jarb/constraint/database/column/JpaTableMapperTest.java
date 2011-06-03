package org.jarb.constraint.database.column;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.junit.Before;
import org.junit.Test;

public class JpaTableMapperTest {
    private JpaTableMapper tableMapper;

    @Before
    public void setUp() {
        tableMapper = new JpaTableMapper();
    }

    @Test
    public void testGetTableNameByEntityClassName() {
        assertEquals("SomeEntity", tableMapper.getTableName(SomeEntity.class));
    }

    @Test
    public void testGetTableNameByEntityAnnotation() {
        assertEquals("my_entity_name", tableMapper.getTableName(SomeEntityWithSpecificName.class));
    }

    @Test
    public void testGetTableNameByTableAnnotation() {
        assertEquals("my_table_name", tableMapper.getTableName(SomeEntityWithSpecificTableName.class));
    }

    @Test
    public void testCannotGetTableName() {
        assertNull(tableMapper.getTableName(NotAnEntity.class));
    }

    /**
     * Use the field name as column name whenever a @Column is missing, or it has no name attribute.
     */
    @Test
    public void testGetColumnNameByFieldName() {
        assertEquals("someProperty", tableMapper.getColumnName(SomeEntity.class, "someProperty"));
        assertEquals("columnizedProperty", tableMapper.getColumnName(SomeEntity.class, "columnizedProperty"));
    }

    @Test
    public void testGetColumnNameByColumnAnnotation() {
        assertEquals("my_column_name", tableMapper.getColumnName(SomeEntity.class, "customColumnNameProperty"));
    }

    @Test
    public void testNonExistingField() {
        assertNull(tableMapper.getColumnName(SomeEntity.class, "unknownProperty"));
    }

    // Entity classes used for testing

    @Entity
    @SuppressWarnings("unused")
    public static class SomeEntity {
        @Id
        private Long id;
        private String someProperty;
        @Column
        private String columnizedProperty;
        @Column(name = "my_column_name")
        private String customColumnNameProperty;
    }

    @Entity(name = "my_entity_name")
    @SuppressWarnings("unused")
    public static class SomeEntityWithSpecificName {
        @Id
        private Long id;
    }

    @Entity
    @Table(name = "my_table_name")
    @SuppressWarnings("unused")
    public static class SomeEntityWithSpecificTableName {
        @Id
        private Long id;
    }

    public static class NotAnEntity {
        // No properties
    }

}
