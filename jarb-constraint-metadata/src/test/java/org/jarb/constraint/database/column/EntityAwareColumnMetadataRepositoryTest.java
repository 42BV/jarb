package org.jarb.constraint.database.column;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.easymock.EasyMock;
import org.jarb.constraint.domain.Person;
import org.jarb.utils.orm.SchemaMapper;
import org.junit.Before;
import org.junit.Test;

/**
 * Ensures that our repository functions according to contract, using mocking.
 * 
 * @author Jeroen van Schagen
 * @since 20-05-2011
 */
public class EntityAwareColumnMetadataRepositoryTest {
    private EntityAwareColumnMetadataRepository columnConstraints;

    private ColumnMetadataRepository columnConstraintsMock;
    private SchemaMapper schemaMapperMock;

    @Before
    public void setUp() {
        columnConstraintsMock = EasyMock.createMock(ColumnMetadataRepository.class);
        schemaMapperMock = EasyMock.createMock(SchemaMapper.class);
        columnConstraints = new EntityAwareColumnMetadataRepository(columnConstraintsMock);
        columnConstraints.setSchemaMapper(schemaMapperMock);
    }

    /**
     * Entity class and property are converted into "persons.name" and constraint is retrieved.
     */
    @Test
    public void testForProperty() {
        EasyMock.expect(schemaMapperMock.table(Person.class)).andReturn("persons");
        EasyMock.expect(schemaMapperMock.column(Person.class, "name")).andReturn("name");
        ColumnReference columnReference = new ColumnReference("my_schema", "my_table", "my_column");
        ColumnMetadata columnConstraint = new ColumnMetadata(columnReference);
        EasyMock.expect(columnConstraintsMock.getColumnMetadata("persons", "name")).andReturn(columnConstraint);
        EasyMock.replay(schemaMapperMock, columnConstraintsMock);

        ColumnMetadata result = columnConstraints.getColumnMetadata(Person.class, "name");

        EasyMock.verify(schemaMapperMock, columnConstraintsMock);

        assertEquals(columnConstraint, result);
    }

    /**
     * Table name could not be retrieved, thus an exception is thrown.
     */
    @Test
    public void testForPropertyNoMappedTable() {
        EasyMock.expect(schemaMapperMock.table(Person.class)).andReturn(null);
        EasyMock.replay(schemaMapperMock, columnConstraintsMock);

        try {
            columnConstraints.getColumnMetadata(Person.class, "name");
            fail("Should throw an illegal argument exception");
        } catch (UnknownTableException e) {
            assertEquals("Could not resolve the table name of 'Person'", e.getMessage());
        }

        EasyMock.verify(schemaMapperMock, columnConstraintsMock);
    }

    /**
     * Column name could not be retrieved, thus an exception is thrown.
     */
    @Test
    public void testForPropertyNoMappedColumn() {
        EasyMock.expect(schemaMapperMock.table(Person.class)).andReturn("persons");
        EasyMock.expect(schemaMapperMock.column(Person.class, "name")).andReturn(null);
        EasyMock.replay(schemaMapperMock, columnConstraintsMock);

        try {
            columnConstraints.getColumnMetadata(Person.class, "name");
            fail("Should throw an illegal argument exception");
        } catch (UnknownColumnException e) {
            assertEquals("Could not resolve the column name of 'name' (Person)", e.getMessage());
        }

        EasyMock.verify(schemaMapperMock, columnConstraintsMock);
    }

}
