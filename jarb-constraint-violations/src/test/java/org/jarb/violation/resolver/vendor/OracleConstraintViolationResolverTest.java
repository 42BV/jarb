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
        assertEquals(ConstraintViolationType.NOT_NULL, violation.getType());
        assertEquals("PROJECTS", violation.getTableName());
        assertEquals("NAME", violation.getColumnName());
    }

    @Test
    public void testUnique() {
        ConstraintViolation violation = resolver.resolveByMessage("ORA-00001: unique constraint (S01_PCAT3.UK_COMMODITY_GROUPS_CODE) violated");
        assertEquals(ConstraintViolationType.UNIQUE, violation.getType());
        assertEquals("UK_COMMODITY_GROUPS_CODE", violation.getConstraintName());
    }

    @Test
    public void testForeignKey() {
        ConstraintViolation violation = resolver
                .resolveByMessage("ORA-02292: integrity constraint (S01_PCAT3.FK_COMMODITIES_COMM_GRP_ID) violated - child record found");
        assertEquals(ConstraintViolationType.FOREIGN_KEY, violation.getType());
        assertEquals("FK_COMMODITIES_COMM_GRP_ID", violation.getConstraintName());
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
    public void testInvalidType() {
        ConstraintViolation violation = resolver.resolveByMessage("ORA-01722: invalid number");
        assertEquals(ConstraintViolationType.INVALID_TYPE, violation.getType());
        assertEquals("number", violation.getExpectedType());
    }

    @Test
    public void testOther() {
        assertNull(resolver.resolveByMessage("unknown"));
    }

}
