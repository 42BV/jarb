package org.jarbframework.violation.resolver.vendor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jarbframework.violation.DatabaseConstraintViolation;
import org.jarbframework.violation.DatabaseConstraintViolationType;
import org.junit.Before;
import org.junit.Test;

public class PostgresViolationResolverTest {

    private PostgresViolationResolver resolver;

    @Before
    public void setUp() {

        resolver = new PostgresViolationResolver();
    }

    @Test
    public void testCheck() {

        DatabaseConstraintViolation violation = resolver
                .resolveByMessage("ERROR: new row for relation \"employees\" violates check constraint \"ck_employees_salary_min\"");
        assertEquals(DatabaseConstraintViolationType.CHECK_FAILED, violation.getViolationType());
        assertEquals("employees", violation.getTableName());
        assertEquals("ck_employees_salary_min", violation.getConstraintName());
    }

    @Test
    public void testNotNull() {

        DatabaseConstraintViolation violation = resolver.resolveByMessage("ERROR: null value in column \"name\" violates not-null constraint");
        assertEquals(DatabaseConstraintViolationType.NOT_NULL, violation.getViolationType());
        assertEquals("name", violation.getColumnName());
    }

    @Test
    public void testForeignKey() {

        DatabaseConstraintViolation violation = resolver.resolveByMessage(
                "ERROR: insert or update on table \"kantoren\" violates foreign key constraint \"fk_kantoren_rayon_id\"\n"
                + "  Detail: Key (rayon_id)=(4) is not present in table \"rayons\".");
        assertEquals(DatabaseConstraintViolationType.FOREIGN_KEY, violation.getViolationType());
        assertEquals("fk_kantoren_rayon_id", violation.getConstraintName());
        assertEquals("kantoren", violation.getTableName());
        assertEquals("rayon_id", violation.getColumnName());
        assertEquals("4", violation.getValue());
    }

    @Test
    public void testUnique() {

        DatabaseConstraintViolation violation = resolver.resolveByMessage(
                "ERROR: duplicate key value violates unique constraint \"un_employees_first_name\" Detail: Key (first_name)=(Emp6) already exists.");
        assertEquals(DatabaseConstraintViolationType.UNIQUE_KEY, violation.getViolationType());
        assertEquals("un_employees_first_name", violation.getConstraintName());
        assertEquals("first_name", violation.getColumnName());
        assertEquals("Emp6", violation.getValue());
    }

    @Test
    public void testLength() {

        DatabaseConstraintViolation violation = resolver.resolveByMessage("ERROR: value too long for type character varying(50)");
        assertEquals(DatabaseConstraintViolationType.LENGTH_EXCEEDED, violation.getViolationType());
        assertEquals("character varying", violation.getExpectedType());
        assertEquals(Long.valueOf(50), violation.getMaximumLength());
    }

    @Test
    public void testType() {

        DatabaseConstraintViolation violation = resolver.resolveByMessage(
                "ERROR: column \"name\" is of type integer but expression is of type character varying\nHint: You'll need to rewrite or cast the expression.");
        assertEquals(DatabaseConstraintViolationType.INVALID_TYPE, violation.getViolationType());
        assertEquals("name", violation.getColumnName());
        assertEquals("integer", violation.getExpectedType());
        assertEquals("character varying", violation.getValueType());
    }

    @Test
    public void testOther() {

        assertNull(resolver.resolveByMessage("unknown"));
    }
}
