package org.jarbframework.constraint.metadata.database;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.jarbframework.utils.orm.ColumnReference;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class CachingDatabaseMetadataRepositoryTest {
    
    private ColumnMetadataRepository delegateMetadataRepository;    
    
    private CachingColumnMetadataRepository cachingMetadataRepository;

    @Before
    public void setUp() {
        delegateMetadataRepository = Mockito.mock(ColumnMetadataRepository.class);
        cachingMetadataRepository = new CachingColumnMetadataRepository(delegateMetadataRepository);
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
        ColumnReference columnReference = new ColumnReference("my_table", "my_column");
        ColumnMetadata columnMetadata = new ColumnMetadata(columnReference);

        when(delegateMetadataRepository.getColumnMetadata(columnReference)).thenReturn(columnMetadata);

        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(columnMetadata, cachingMetadataRepository.getColumnMetadata(new ColumnReference("my_table", "my_column")));
        }

        cachingMetadataRepository.clearCache();

        Assert.assertEquals(columnMetadata, cachingMetadataRepository.getColumnMetadata(new ColumnReference("my_table", "my_column")));

        verify(delegateMetadataRepository, times(2)).getColumnMetadata(columnReference);
    }

    /**
     * Return nothing when the column does not exist.
     */
    @Test
    public void testUnknownColumn() {
        ColumnReference columnReference = new ColumnReference("my_table", "my_column");

        assertNull(cachingMetadataRepository.getColumnMetadata(columnReference));

        verify(delegateMetadataRepository, times(1)).getColumnMetadata(columnReference);
    }

}
