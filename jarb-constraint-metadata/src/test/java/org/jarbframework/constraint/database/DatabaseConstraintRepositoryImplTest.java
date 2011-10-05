package org.jarbframework.constraint.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.easymock.EasyMock;
import org.jarbframework.constraint.database.CouldNotBeMappedToColumnException;
import org.jarbframework.constraint.database.DatabaseConstraintRepositoryImpl;
import org.jarbframework.constraint.database.column.ColumnMetadata;
import org.jarbframework.constraint.database.column.ColumnMetadataRepository;
import org.jarbframework.constraint.domain.Person;
import org.jarbframework.utils.bean.PropertyReference;
import org.jarbframework.utils.orm.ColumnReference;
import org.jarbframework.utils.orm.SchemaMapper;
import org.junit.Before;
import org.junit.Test;

/**
 * Ensures that our repository functions according to contract, using mocking.
 * 
 * @author Jeroen van Schagen
 * @since 20-05-2011
 */
public class DatabaseConstraintRepositoryImplTest {
    private DatabaseConstraintRepositoryImpl databaseConstraintRepository;

    private ColumnMetadataRepository columnMetadataRepository;
    private SchemaMapper schemaMapper;

    @Before
    public void setUp() {
        columnMetadataRepository = EasyMock.createMock(ColumnMetadataRepository.class);
        schemaMapper = EasyMock.createMock(SchemaMapper.class);
        databaseConstraintRepository = new DatabaseConstraintRepositoryImpl();
        databaseConstraintRepository.setColumnMetadataRepository(columnMetadataRepository);
        databaseConstraintRepository.setSchemaMapper(schemaMapper);
    }

    /**
     * Entity class and property are converted into "persons.name" and constraint is retrieved.
     */
    @Test
    public void testForProperty() {
        ColumnReference columnReference = new ColumnReference("persons", "name");
        EasyMock.expect(schemaMapper.columnOf(new PropertyReference(Person.class, "name"))).andReturn(columnReference);
        ColumnMetadata columnConstraint = new ColumnMetadata(columnReference);
        EasyMock.expect(columnMetadataRepository.getColumnMetadata(new ColumnReference("persons", "name"))).andReturn(columnConstraint);
        EasyMock.replay(schemaMapper, columnMetadataRepository);

        ColumnMetadata result = databaseConstraintRepository.getColumnMetadata(new PropertyReference(Person.class, "name"));

        EasyMock.verify(schemaMapper, columnMetadataRepository);

        assertEquals(columnConstraint, result);
    }

    /**
     * Column name could not be retrieved, thus an exception is thrown.
     */
    @Test
    public void testForPropertyNoMappedColumn() {
        EasyMock.expect(schemaMapper.columnOf(new PropertyReference(Person.class, "name"))).andReturn(null);
        EasyMock.replay(schemaMapper, columnMetadataRepository);

        try {
            databaseConstraintRepository.getColumnMetadata(new PropertyReference(Person.class, "name"));
            fail("Should throw an illegal argument exception");
        } catch (CouldNotBeMappedToColumnException e) {
            assertEquals("Property 'Person.name' could not be mapped to a column.", e.getMessage());
        }

        EasyMock.verify(schemaMapper, columnMetadataRepository);
    }
}
