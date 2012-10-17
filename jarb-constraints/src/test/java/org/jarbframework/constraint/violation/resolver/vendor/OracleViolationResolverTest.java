package org.jarbframework.constraint.violation.resolver.vendor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.DatabaseConstraintType;
import org.junit.Before;
import org.junit.Test;

public class OracleViolationResolverTest {
    
    private OracleViolationResolver resolver;

    @Before
    public void setUp() {
        resolver = new OracleViolationResolver();
    }

    @Test
    public void testCheck() {
        DatabaseConstraintViolation violation = resolver.resolveByMessage("ORA-02290: check constraint (HIBERNATE.CK_EMPLOYEES_SALARY_MIN) violated\n");
        assertEquals(DatabaseConstraintType.CHECK_FAILED, violation.getConstraintType());
        assertEquals("CK_EMPLOYEES_SALARY_MIN", violation.getConstraintName());
    }

    @Test
    public void testNotNull() {
        DatabaseConstraintViolation violation = resolver.resolveByMessage("ORA-01400: cannot insert NULL into (\"HIBERNATE\".\"PROJECTS\".\"NAME\")\n");
        assertEquals(DatabaseConstraintType.NOT_NULL, violation.getConstraintType());
        assertEquals("PROJECTS", violation.getTableName());
        assertEquals("NAME", violation.getColumnName());
    }

    @Test
    public void testUnique() {
        DatabaseConstraintViolation violation = resolver.resolveByMessage("ORA-00001: unique constraint (S01_PCAT3.UK_COMMODITY_GROUPS_CODE) violated\n");
        assertEquals(DatabaseConstraintType.UNIQUE_KEY, violation.getConstraintType());
        assertEquals("UK_COMMODITY_GROUPS_CODE", violation.getConstraintName());
    }

    @Test
    public void testForeignKey() {
        DatabaseConstraintViolation violation = resolver
                .resolveByMessage("ORA-02292: integrity constraint (S01_PCAT3.FK_COMMODITIES_COMM_GRP_ID) violated - child record found\n");
        assertEquals(DatabaseConstraintType.FOREIGN_KEY, violation.getConstraintType());
        assertEquals("FK_COMMODITIES_COMM_GRP_ID", violation.getConstraintName());
    }

    @Test
    public void testLength() {
        DatabaseConstraintViolation violation = resolver
                .resolveByMessage("ORA-12899: value too large for column \"HIBERNATE\".\"CUSTOMERS\".\"FIRST_NAME\" (actual: 72, maximum: 50)\n");
        assertEquals(DatabaseConstraintType.LENGTH_EXCEEDED, violation.getConstraintType());
        assertEquals("CUSTOMERS", violation.getTableName());
        assertEquals("FIRST_NAME", violation.getColumnName());
        assertEquals(Long.valueOf(50), violation.getMaximumLength());
    }

    @Test
    public void testInvalidType() {
        DatabaseConstraintViolation violation = resolver.resolveByMessage("ORA-01722: invalid number\n");
        assertEquals(DatabaseConstraintType.INVALID_TYPE, violation.getConstraintType());
        assertEquals("number", violation.getExpectedValueType());
    }

    @Test
    public void testOther() {
        assertNull(resolver.resolveByMessage("unknown"));
    }

}
