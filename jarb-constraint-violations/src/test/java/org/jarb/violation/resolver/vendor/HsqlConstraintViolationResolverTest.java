package org.jarb.violation.resolver.vendor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jarb.violation.ConstraintViolation;
import org.jarb.violation.ConstraintViolationType;
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
        ConstraintViolation violation = resolver
                .resolveByMessage("integrity constraint violation: NOT NULL check constraint; SYS_CT_10041 table: CARS column: NAME");
        assertEquals(ConstraintViolationType.NOT_NULL, violation.getType());
        assertEquals("sys_ct_10041", violation.getConstraintName());
        assertEquals("cars", violation.getTableName());
        assertEquals("name", violation.getColumnName());
    }

    @Test
    public void testUnique() {
        ConstraintViolation violation = resolver
                .resolveByMessage("integrity constraint violation: unique constraint or index violation; UK_CARS_LICENSE table: CARS");
        assertEquals(ConstraintViolationType.UNIQUE_KEY, violation.getType());
        assertEquals("uk_cars_license", violation.getConstraintName());
        assertEquals("cars", violation.getTableName());
    }

    @Test
    public void testForeignKey() {
        ConstraintViolation violation = resolver.resolveByMessage("integrity constraint violation: foreign key no action; FK_CARS_OWNER table: CARS");
        assertEquals(ConstraintViolationType.FOREIGN_KEY, violation.getType());
        assertEquals("fk_cars_owner", violation.getConstraintName());
        assertEquals("cars", violation.getTableName());
    }

    @Test
    public void testLength() {
        ConstraintViolation violation = resolver.resolveByMessage("data exception: string data, right truncation");
        assertEquals(ConstraintViolationType.LENGTH_EXCEEDED, violation.getType());
        assertEquals("string", violation.getValueType());
    }

    @Test
    public void testType() {
        ConstraintViolation violation = resolver.resolveByMessage("data exception: invalid character value for cast");
        assertEquals(ConstraintViolationType.INVALID_TYPE, violation.getType());
        assertEquals("character", violation.getValueType());
    }

    @Test
    public void testOther() {
        assertNull(resolver.resolveByMessage("unknown"));
    }

}
