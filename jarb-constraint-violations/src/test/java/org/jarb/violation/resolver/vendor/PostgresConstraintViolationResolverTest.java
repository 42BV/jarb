package org.jarb.violation.resolver.vendor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jarb.violation.ConstraintViolation;
import org.jarb.violation.ConstraintViolationType;
import org.junit.Before;
import org.junit.Test;

public class PostgresConstraintViolationResolverTest {
    private PostgresConstraintViolationResolver resolver;

    @Before
    public void setUp() {
        resolver = new PostgresConstraintViolationResolver();
    }

    @Test
    public void testCheck() {
        ConstraintViolation violation = resolver
                .resolveByMessage("ERROR: new row for relation \"employees\" violates check constraint \"ck_employees_salary_min\"");
        assertEquals(ConstraintViolationType.CHECK_FAILED, violation.getType());
        assertEquals("employees", violation.getTableName());
        assertEquals("ck_employees_salary_min", violation.getConstraintName());
    }

    @Test
    public void testNotNull() {
        ConstraintViolation violation = resolver.resolveByMessage("ERROR: null value in column \"name\" violates not-null constraint");
        assertEquals(ConstraintViolationType.NOT_NULL, violation.getType());
        assertEquals("name", violation.getColumnName());
    }

    @Test
    public void testUnique() {
        ConstraintViolation violation = resolver
                .resolveByMessage("ERROR: duplicate key value violates unique constraint \"un_employees_first_name\" Detail: Key (first_name)=(Emp6) already exists.");
        assertEquals(ConstraintViolationType.UNIQUE_KEY, violation.getType());
        assertEquals("un_employees_first_name", violation.getConstraintName());
        assertEquals("first_name", violation.getColumnName());
        assertEquals("Emp6", violation.getValue());
    }

    @Test
    public void testLength() {
        ConstraintViolation violation = resolver.resolveByMessage("ERROR: value too long for type character varying(50)");
        assertEquals(ConstraintViolationType.LENGTH_EXCEEDED, violation.getType());
        assertEquals("character varying", violation.getExpectedType());
        assertEquals(Long.valueOf(50), violation.getMaximumLength());
    }

    @Test
    public void testType() {
        ConstraintViolation violation = resolver
                .resolveByMessage("ERROR: column \"name\" is of type integer but expression is of type character varying\nHint: You will need to rewrite or cast the expression.");
        assertEquals(ConstraintViolationType.INVALID_TYPE, violation.getType());
        assertEquals("name", violation.getColumnName());
        assertEquals("integer", violation.getExpectedType());
        assertEquals("character varying", violation.getValueType());
    }

    @Test
    public void testOther() {
        assertNull(resolver.resolveByMessage("unknown"));
    }

}
