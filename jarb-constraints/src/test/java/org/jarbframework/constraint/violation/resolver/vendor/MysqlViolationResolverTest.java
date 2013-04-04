package org.jarbframework.constraint.violation.resolver.vendor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jarbframework.constraint.violation.DatabaseConstraintType;
import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.junit.Test;

public class MysqlViolationResolverTest {
    
    private final MysqlViolationResolver resolver = new MysqlViolationResolver();

    @Test
    public void testNotNull() {
        DatabaseConstraintViolation violation = resolver.resolve("Column 'name' cannot be null");
        assertEquals(DatabaseConstraintType.NOT_NULL, violation.getConstraintType());
        assertEquals("name", violation.getColumnName());
    }

    @Test
    public void testUnique() {
        DatabaseConstraintViolation violation = resolver.resolve("Duplicate entry 'Emp6' for key 'un_employees_first_name'");
        assertEquals(DatabaseConstraintType.UNIQUE_KEY, violation.getConstraintType());
        assertEquals("Emp6", violation.getValue());
        assertEquals("un_employees_first_name", violation.getConstraintName());
    }

    @Test
    public void testLength() {
        DatabaseConstraintViolation violation = resolver.resolve("Data truncation: Data too long for column 'first_name' at row 1");
        assertEquals(DatabaseConstraintType.LENGTH_EXCEEDED, violation.getConstraintType());
        assertEquals("first_name", violation.getColumnName());
    }

    @Test
    public void testType() {
        DatabaseConstraintViolation violation = resolver.resolve("Incorrect integer value: 'Project2' for column 'name' at row 1");
        assertEquals(DatabaseConstraintType.INVALID_TYPE, violation.getConstraintType());
        assertEquals("integer", violation.getExpectedValueType());
        assertEquals("Project2", violation.getValue());
        assertEquals("name", violation.getColumnName());
    }

    @Test
    public void testOther() {
        assertNull(resolver.resolve("unknown"));
    }

}
