package org.jarb.constraint.database.column;

import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CachingDatabaseMetadataRepositoryTest {
    private CachingColumnMetadataRepository columnConstraints;
    private ColumnMetadataProvider constraintsProviderMock;

    @Before
    public void setUp() {
        constraintsProviderMock = EasyMock.createMock(ColumnMetadataProvider.class);
        columnConstraints = new CachingColumnMetadataRepository(constraintsProviderMock);
    }

    /**
     * Assert that our constraints are correctly cached. First we start with an empty
     * repository, so we retrieve information from our delegate and store it in the
     * cache. After caching our constraint information, we can retrieve it directly
     * from memory, thus the database is only queried once.
     * <p>
     * Then we clear the cache, and again retrieve information. Now, the database
     * should again be queried, as the cache has to be filled again.
     */
    @Test
    public void testCache() {
        ColumnReference columnReference = new ColumnReference("my_schema", "my_table", "my_column");
        ColumnMetadata columnConstraint = new ColumnMetadata(columnReference);
        List<ColumnMetadata> columnConstraintList = Arrays.asList(columnConstraint);
        EasyMock.expect(constraintsProviderMock.all()).andReturn(columnConstraintList).times(2);
        EasyMock.replay(constraintsProviderMock);

        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(columnConstraint, columnConstraints.getColumnMetadata("my_table", "my_column"));
        }

        columnConstraints.clearCache();

        Assert.assertEquals(columnConstraint, columnConstraints.getColumnMetadata("my_table", "my_column"));

        EasyMock.verify(constraintsProviderMock); // Once initially, once after clearing cache
    }

    /**
     * Return nothing when the column does not exist.
     */
    @Test
    public void testGetColumnConstraintsByUnknownTable() {
        ColumnReference columnReference = new ColumnReference("my_schema", "my_table", "my_column");
        List<ColumnMetadata> columnConstraintList = Arrays.asList(new ColumnMetadata(columnReference));
        EasyMock.expect(constraintsProviderMock.all()).andReturn(columnConstraintList);
        EasyMock.replay(constraintsProviderMock);

        assertNull(columnConstraints.getColumnMetadata("unknown_table", "my_column"));

        EasyMock.verify(constraintsProviderMock);
    }

    /**
     * Return nothing when the column does not exist.
     */
    @Test
    public void testGetColumnConstraintsByUnknownColumn() {
        ColumnReference columnReference = new ColumnReference("my_schema", "my_table", "my_column");
        List<ColumnMetadata> columnConstraintList = Arrays.asList(new ColumnMetadata(columnReference));
        EasyMock.expect(constraintsProviderMock.all()).andReturn(columnConstraintList);
        EasyMock.replay(constraintsProviderMock);

        assertNull(columnConstraints.getColumnMetadata("my_table", "unknown_column"));

        EasyMock.verify(constraintsProviderMock);
    }

}
