package nl._42.jarb.constraint.violation.resolver.vendor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import nl._42.jarb.constraint.violation.DatabaseConstraintType;
import nl._42.jarb.constraint.violation.DatabaseConstraintViolation;

import nl._42.jarb.constraint.violation.DatabaseConstraintType;
import nl._42.jarb.constraint.violation.DatabaseConstraintViolation;
import org.junit.Assert;
import org.junit.Test;

public class HsqlViolationResolverTest {
    
    private final HsqlViolationResolver resolver = new HsqlViolationResolver();

    @Test
    public void testNotNull() {
        DatabaseConstraintViolation violation = resolver.resolve("integrity constraint violation: NOT NULL check constraint; SYS_CT_10041 table: CARS column: NAME");
        Assert.assertEquals(DatabaseConstraintType.NOT_NULL, violation.getConstraintType());
        assertEquals("sys_ct_10041", violation.getConstraintName());
        assertEquals("cars", violation.getTableName());
        assertEquals("name", violation.getColumnName());
    }

    @Test
    public void testUnique() {
        DatabaseConstraintViolation violation = resolver.resolve("integrity constraint violation: unique constraint or index violation; UK_CARS_LICENSE table: CARS");
        assertEquals(DatabaseConstraintType.UNIQUE_KEY, violation.getConstraintType());
        assertEquals("uk_cars_license", violation.getConstraintName());
        assertEquals("cars", violation.getTableName());
    }

    @Test
    public void testForeignKey() {
        DatabaseConstraintViolation violation = resolver.resolve("integrity constraint violation: foreign key no parent; FK_CARS_OWNER table: CARS");
        assertEquals(DatabaseConstraintType.FOREIGN_KEY, violation.getConstraintType());
        assertEquals("fk_cars_owner", violation.getConstraintName());
        assertEquals("cars", violation.getTableName());
    }

    @Test
    public void testLength() {
        DatabaseConstraintViolation violation = resolver.resolve("data exception: string data, right truncation");
        assertEquals(DatabaseConstraintType.LENGTH_EXCEEDED, violation.getConstraintType());
        assertEquals("string", violation.getValueType());
    }

    @Test
    public void testType() {
        DatabaseConstraintViolation violation = resolver.resolve("data exception: invalid character value for cast");
        assertEquals(DatabaseConstraintType.INVALID_TYPE, violation.getConstraintType());
        assertEquals("character", violation.getValueType());
    }

    @Test
    public void testOther() {
        assertNull(resolver.resolve("unknown"));
    }

}
