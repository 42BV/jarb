package org.jarb.violation.resolver.vendor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jarb.violation.DatabaseConstraintViolation;
import org.jarb.violation.DatabaseConstraintViolationType;
import org.junit.Before;
import org.junit.Test;

public class HsqlConstraintViolationResolverTest {
    private HsqlConstraintViolationResolver resolver;

    @Before
    public void setUp() {
        resolver = new HsqlConstraintViolationResolver();
    }

    @Test
    public void testNotNull() {
        DatabaseConstraintViolation violation = resolver
                .resolveByMessage("integrity constraint violation: NOT NULL check constraint; SYS_CT_10041 table: CARS column: NAME");
        assertEquals(DatabaseConstraintViolationType.NOT_NULL, violation.getViolationType());
        assertEquals("sys_ct_10041", violation.getConstraintName());
        assertEquals("cars", violation.getTableName());
        assertEquals("name", violation.getColumnName());
    }

    @Test
    public void testUnique() {
        DatabaseConstraintViolation violation = resolver
                .resolveByMessage("integrity constraint violation: unique constraint or index violation; UK_CARS_LICENSE table: CARS");
        assertEquals(DatabaseConstraintViolationType.UNIQUE_KEY, violation.getViolationType());
        assertEquals("uk_cars_license", violation.getConstraintName());
        assertEquals("cars", violation.getTableName());
    }

    @Test
    public void testForeignKey() {
        DatabaseConstraintViolation violation = resolver.resolveByMessage("integrity constraint violation: foreign key no action; FK_CARS_OWNER table: CARS");
        assertEquals(DatabaseConstraintViolationType.FOREIGN_KEY, violation.getViolationType());
        assertEquals("fk_cars_owner", violation.getConstraintName());
        assertEquals("cars", violation.getTableName());
    }

    @Test
    public void testLength() {
        DatabaseConstraintViolation violation = resolver.resolveByMessage("data exception: string data, right truncation");
        assertEquals(DatabaseConstraintViolationType.LENGTH_EXCEEDED, violation.getViolationType());
        assertEquals("string", violation.getValueType());
    }

    @Test
    public void testType() {
        DatabaseConstraintViolation violation = resolver.resolveByMessage("data exception: invalid character value for cast");
        assertEquals(DatabaseConstraintViolationType.INVALID_TYPE, violation.getViolationType());
        assertEquals("character", violation.getValueType());
    }

    @Test
    public void testOther() {
        assertNull(resolver.resolveByMessage("unknown"));
    }

}
