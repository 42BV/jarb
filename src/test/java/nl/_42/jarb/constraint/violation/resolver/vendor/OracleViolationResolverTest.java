package nl._42.jarb.constraint.violation.resolver.vendor;

import nl._42.jarb.constraint.violation.DatabaseConstraintType;
import nl._42.jarb.constraint.violation.DatabaseConstraintViolation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class OracleViolationResolverTest {
    
    private final OracleViolationResolver resolver = new OracleViolationResolver();

    @Test
    public void testCheck() {
        final String message = "ORA-02290: check constraint (HIBERNATE.CK_EMPLOYEES_SALARY_MIN) violated\n";
        
        DatabaseConstraintViolation violation = resolver.resolve(message);
        assertEquals(DatabaseConstraintType.CHECK_FAILED, violation.getConstraintType());
        assertEquals("ORA-02290", violation.getNumber());
        assertEquals("CK_EMPLOYEES_SALARY_MIN", violation.getConstraintName());
    }

    @Test
    public void testNotNull() {
        final String message = "ORA-01400: cannot insert NULL into (\"HIBERNATE\".\"PROJECTS\".\"NAME\")\n";
        
        DatabaseConstraintViolation violation = resolver.resolve(message);
        assertEquals(DatabaseConstraintType.NOT_NULL, violation.getConstraintType());
        assertEquals("ORA-01400", violation.getNumber());
        assertEquals("PROJECTS", violation.getTableName());
        assertEquals("NAME", violation.getColumnName());
    }

    @Test
    public void testUnique() {
        final String message = "ORA-00001: unique constraint (S01_PCAT3.UK_COMMODITY_GROUPS_CODE) violated\n";
        
        DatabaseConstraintViolation violation = resolver.resolve(message);
        assertEquals(DatabaseConstraintType.UNIQUE_KEY, violation.getConstraintType());
        assertEquals("ORA-00001", violation.getNumber());
        assertEquals("UK_COMMODITY_GROUPS_CODE", violation.getConstraintName());
    }

    @Test
    public void testForeignKey() {
        final String message = "ORA-02292: integrity constraint (S01_PCAT3.FK_COMMODITIES_COMM_GRP_ID) violated - child record found\n";
        
        DatabaseConstraintViolation violation = resolver.resolve(message);
        assertEquals(DatabaseConstraintType.FOREIGN_KEY, violation.getConstraintType());
        assertEquals("ORA-02292", violation.getNumber());
        assertEquals("FK_COMMODITIES_COMM_GRP_ID", violation.getConstraintName());
    }

    @Test
    public void testLength() {
        final String message = "ORA-12899: value too large for column \"HIBERNATE\".\"CUSTOMERS\".\"FIRST_NAME\" (actual: 72, maximum: 50)\n";
        
        DatabaseConstraintViolation violation = resolver.resolve(message);
        assertEquals(DatabaseConstraintType.LENGTH_EXCEEDED, violation.getConstraintType());
        assertEquals("ORA-12899", violation.getNumber());
        assertEquals("CUSTOMERS", violation.getTableName());
        assertEquals("FIRST_NAME", violation.getColumnName());
        assertEquals(Long.valueOf(50), violation.getMaximumLength());
    }

    @Test
    public void testInvalidType() {
        final String message = "ORA-01722: invalid number\n";
        
        DatabaseConstraintViolation violation = resolver.resolve(message);
        assertEquals(DatabaseConstraintType.INVALID_TYPE, violation.getConstraintType());
        assertEquals("ORA-01722", violation.getNumber());
        assertEquals("number", violation.getExpectedValueType());
    }

    @Test
    public void testOther() {
        assertNull(resolver.resolve("unknown"));
    }

}
