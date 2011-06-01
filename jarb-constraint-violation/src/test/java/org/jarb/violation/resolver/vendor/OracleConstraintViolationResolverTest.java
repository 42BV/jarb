package org.jarb.violation.resolver.vendor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jarb.violation.ConstraintViolation;
import org.jarb.violation.ConstraintViolationType;
import org.junit.Before;
import org.junit.Test;

public class OracleConstraintViolationResolverTest {
    private OracleConstraintViolationResolver resolver;

    @Before
    public void setUp() {
        resolver = new OracleConstraintViolationResolver();
    }

    @Test
    public void testCheck() {
        ConstraintViolation violation = resolver.resolveByMessage("ORA-02290: check constraint (HIBERNATE.CK_EMPLOYEES_SALARY_MIN) violated");
        assertEquals(ConstraintViolationType.CHECK_FAILED, violation.getType());
        assertEquals("CK_EMPLOYEES_SALARY_MIN", violation.getConstraintName());
    }

    @Test
    public void testNotNull() {
        ConstraintViolation violation = resolver.resolveByMessage("ORA-01400: cannot insert NULL into (\"HIBERNATE\".\"PROJECTS\".\"NAME\")");
        assertEquals(ConstraintViolationType.CANNOT_BE_NULL, violation.getType());
        assertEquals("PROJECTS", violation.getTableName());
        assertEquals("NAME", violation.getColumnName());
    }

    @Test
    public void testUnique() {
        ConstraintViolation violation = resolver.resolveByMessage("ORA-00001: unique constraint (HIBERNATE.SYS_C004253) violated");
        assertEquals(ConstraintViolationType.UNIQUE_VIOLATION, violation.getType());
        assertEquals("SYS_C004253", violation.getConstraintName());
    }

    @Test
    public void testLength() {
        ConstraintViolation violation = resolver
                .resolveByMessage("ORA-12899: value too large for column \"HIBERNATE\".\"CUSTOMERS\".\"FIRST_NAME\" (actual: 72, maximum: 50)");
        assertEquals(ConstraintViolationType.LENGTH_EXCEEDED, violation.getType());
        assertEquals("CUSTOMERS", violation.getTableName());
        assertEquals("FIRST_NAME", violation.getColumnName());
        assertEquals(Long.valueOf(50), violation.getMaximumLength());
    }

    @Test
    public void testType() {
        ConstraintViolation violation = resolver.resolveByMessage("ORA-01722: invalid number");
        assertEquals(ConstraintViolationType.INVALID_TYPE, violation.getType());
        assertEquals("number", violation.getExpectedType());
    }

    @Test
    public void testOther() {
        assertNull(resolver.resolveByMessage("unknown"));
    }

}
