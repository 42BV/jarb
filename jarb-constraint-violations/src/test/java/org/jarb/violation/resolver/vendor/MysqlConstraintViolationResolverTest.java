package org.jarb.violation.resolver.vendor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jarb.violation.ConstraintViolation;
import org.jarb.violation.ConstraintViolationType;
import org.junit.Before;
import org.junit.Test;

public class MysqlConstraintViolationResolverTest {
    private MysqlConstraintViolationResolver resolver;

    @Before
    public void setUp() {
        resolver = new MysqlConstraintViolationResolver();
    }

    @Test
    public void testNotNull() {
        ConstraintViolation violation = resolver.resolveByMessage("Column 'name' cannot be null");
        assertEquals(ConstraintViolationType.CANNOT_BE_NULL, violation.getType());
        assertEquals("name", violation.getColumnName());
    }

    @Test
    public void testUnique() {
        ConstraintViolation violation = resolver.resolveByMessage("Duplicate entry 'Emp6' for key 'un_employees_first_name'");
        assertEquals(ConstraintViolationType.UNIQUE_VIOLATION, violation.getType());
        assertEquals("Emp6", violation.getValue());
        assertEquals("un_employees_first_name", violation.getConstraintName());
    }

    @Test
    public void testLength() {
        ConstraintViolation violation = resolver.resolveByMessage("Data truncation: Data too long for column 'first_name' at row 1");
        assertEquals(ConstraintViolationType.LENGTH_EXCEEDED, violation.getType());
        assertEquals("first_name", violation.getColumnName());
    }

    @Test
    public void testType() {
        ConstraintViolation violation = resolver.resolveByMessage("Incorrect integer value: 'Project2' for column 'name' at row 1");
        assertEquals(ConstraintViolationType.INVALID_TYPE, violation.getType());
        assertEquals("integer", violation.getExpectedType());
        assertEquals("Project2", violation.getValue());
        assertEquals("name", violation.getColumnName());
    }

    @Test
    public void testOther() {
        assertNull(resolver.resolveByMessage("unknown"));
    }

}
