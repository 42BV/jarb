package org.jarbframework.constraint.database.named;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jarbframework.constraint.database.named.NamedConstraintMetadata;
import org.jarbframework.constraint.database.named.NamedConstraintType;
import org.junit.Test;

public class NamedConstraintMetadataTest {

    @Test
    public void testEquals() {
        final String name = "pk_cars_id";
        NamedConstraintMetadata metadata = new NamedConstraintMetadata(name, NamedConstraintType.PRIMARY_KEY);
        assertTrue(metadata.equals(metadata)); // Same reference
        assertTrue(metadata.equals(new NamedConstraintMetadata(name, NamedConstraintType.PRIMARY_KEY))); // Same name
        assertFalse(metadata.equals(null)); // Cannot be equal to null, no null pointer
        assertFalse(metadata.equals(new NamedConstraintMetadata("other", NamedConstraintType.PRIMARY_KEY))); // Different name
    }

    @Test
    public void testToString() {
        final String name = "pk_cars_id";
        NamedConstraintMetadata metadata = new NamedConstraintMetadata(name, NamedConstraintType.PRIMARY_KEY);
        assertTrue(metadata.toString().contains(name)); // Name has to be in the string description
    }

}
