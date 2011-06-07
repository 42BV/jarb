package org.jarb.constraint.database.named;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class CachingNamedConstraintMetadataRepositoryTest {
    private CachingNamedConstraintMetadataRepository constraintMetadataRepository;
    private NamedConstraintMetadataProvider constraintMetadataProviderMock;

    @Before
    public void setUp() {
        constraintMetadataProviderMock = EasyMock.createMock(NamedConstraintMetadataProvider.class);
        constraintMetadataRepository = new CachingNamedConstraintMetadataRepository(constraintMetadataProviderMock);
    }

    /**
     * Metadata should only be requested once, unless the cache is cleared.
     */
    @Test
    public void testCaching() {
        NamedConstraintMetadata constraintMetadata = new NamedConstraintMetadata("pk_cars_id", NamedConstraintType.PRIMARY_KEY);
        Set<NamedConstraintMetadata> constraintMetadataSet = new HashSet<NamedConstraintMetadata>();
        constraintMetadataSet.add(constraintMetadata);
        EasyMock.expect(constraintMetadataProviderMock.all()).andReturn(constraintMetadataSet).times(2);
        EasyMock.replay(constraintMetadataProviderMock);

        for (int i = 0; i < 10; i++) {
            assertEquals(constraintMetadata, constraintMetadataRepository.named("pk_cars_id"));
        }

        constraintMetadataRepository.clearCache();

        assertEquals(constraintMetadata, constraintMetadataRepository.named("pk_cars_id"));

        EasyMock.verify(constraintMetadataProviderMock);
    }

    /**
     * Ensure that all provided metadata entries are returned, and remain in the same order.
     */
    @Test
    public void testAll() {
        NamedConstraintMetadata primaryKey = new NamedConstraintMetadata("pk_cars_id", NamedConstraintType.PRIMARY_KEY);
        NamedConstraintMetadata uniqueKey = new NamedConstraintMetadata("uk_cars_license_number", NamedConstraintType.UNIQUE_INDEX);
        Set<NamedConstraintMetadata> constraintMetadataSet = new LinkedHashSet<NamedConstraintMetadata>();
        constraintMetadataSet.add(primaryKey);
        constraintMetadataSet.add(uniqueKey);
        EasyMock.expect(constraintMetadataProviderMock.all()).andReturn(constraintMetadataSet);
        EasyMock.replay(constraintMetadataProviderMock);

        List<NamedConstraintMetadata> constraintMetadataResult = constraintMetadataRepository.all();
        assertEquals(2, constraintMetadataResult.size());
        assertEquals(primaryKey, constraintMetadataResult.get(0));
        assertEquals(uniqueKey, constraintMetadataResult.get(1));

        EasyMock.verify(constraintMetadataProviderMock);
    }

    /**
     * Unknown constraint requests should result in {@code null}.
     */
    @Test
    public void testNamedDoesNotExist() {
        NamedConstraintMetadata constraintMetadata = new NamedConstraintMetadata("pk_cars_id", NamedConstraintType.PRIMARY_KEY);
        Set<NamedConstraintMetadata> constraintMetadataSet = new HashSet<NamedConstraintMetadata>();
        constraintMetadataSet.add(constraintMetadata);
        EasyMock.expect(constraintMetadataProviderMock.all()).andReturn(constraintMetadataSet);
        EasyMock.replay(constraintMetadataProviderMock);

        assertNull(constraintMetadataRepository.named("unknown"));

        EasyMock.verify(constraintMetadataProviderMock);
    }

}
