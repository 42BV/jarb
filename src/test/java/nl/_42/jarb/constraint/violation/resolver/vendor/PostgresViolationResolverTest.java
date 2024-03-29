package nl._42.jarb.constraint.violation.resolver.vendor;

import nl._42.jarb.constraint.violation.DatabaseConstraintType;
import nl._42.jarb.constraint.violation.DatabaseConstraintViolation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.orm.jpa.JpaSystemException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PostgresViolationResolverTest {

    private final PostgresViolationResolver resolver = new PostgresViolationResolver();

    @Test
    public void testCheck() {
        DatabaseConstraintViolation violation = resolver
                .resolve("ERROR: new row for relation \"employees\" violates check constraint \"ck_employees_salary_min\"");
        assertEquals(DatabaseConstraintType.CHECK_FAILED, violation.getConstraintType());
        assertEquals("employees", violation.getTableName());
        assertEquals("ck_employees_salary_min", violation.getConstraintName());
    }

    @Test
    public void testExclusion() {
        DatabaseConstraintViolation violation = resolver
                .resolve("ERROR: conflicting key value violates exclusion constraint \"ex_gebruiker_gebruikersnaam_for_non_deleted_gebruikers\"\n"
                        + "  Detail: Key (gebruikersnaam)=(piet) conflicts with existing key (gebruikersnaam)=(piet).");
        assertEquals(DatabaseConstraintType.EXCLUSION, violation.getConstraintType());
        assertEquals("ex_gebruiker_gebruikersnaam_for_non_deleted_gebruikers", violation.getConstraintName());
        assertEquals("gebruikersnaam", violation.getColumnName());
        assertEquals("gebruikersnaam", violation.getReferencingColumnName());
        assertEquals("piet", violation.getValue());
    }

    @Test
    public void testNotNull() {
        DatabaseConstraintViolation violation = resolver.resolve("ERROR: null value in column \"license_number\" of relation \"cars\" violates not-null constraint\n"
                + "  Detail: Failing row contains (1, null, null, null, null).");
        assertEquals(DatabaseConstraintType.NOT_NULL, violation.getConstraintType());
        assertEquals("license_number", violation.getColumnName());
        assertEquals("cars", violation.getTableName());
    }

    @Test
    public void testForeignKey1() {
        DatabaseConstraintViolation violation = resolver.resolve(
                "ERROR: insert or update on table \"kantoren\" violates foreign key constraint \"fk_kantoren_rayon_id\"\n"
                + "  Detail: Key (rayon_id)=(4) is not present in table \"rayons\".");
        assertEquals(DatabaseConstraintType.FOREIGN_KEY, violation.getConstraintType());
        assertEquals("fk_kantoren_rayon_id", violation.getConstraintName());
        assertEquals("kantoren", violation.getTableName());
        assertEquals("rayon_id", violation.getColumnName());
        assertEquals("4", violation.getValue());
    }

    @Test
    public void testForeignKey2() {

        DatabaseConstraintViolation violation = resolver.resolve(
                "ERROR: update or delete on table \"rayons\" violates foreign key constraint \"fk_kantoren_rayon_id\" on table \"kantoren\"\n"
                + "  Detail: Key (id)=(1) is still referenced from table \"kantoren\".");
        assertEquals(DatabaseConstraintType.FOREIGN_KEY, violation.getConstraintType());
        assertEquals("fk_kantoren_rayon_id", violation.getConstraintName());
        assertEquals("rayons", violation.getTableName());
        assertEquals("id", violation.getColumnName());
        assertEquals("1", violation.getValue());
    }

    @Test
    public void testUnique() {

        DatabaseConstraintViolation violation = resolver.resolve(
                "ERROR: duplicate key value violates unique constraint \"un_employees_first_name\" Detail: Key (first_name)=(Emp6) already exists.");
        assertEquals(DatabaseConstraintType.UNIQUE_KEY, violation.getConstraintType());
        assertEquals("un_employees_first_name", violation.getConstraintName());
        assertEquals("first_name", violation.getColumnName());
        assertEquals("Emp6", violation.getValue());
    }

    @Test
    public void testLength() {

        DatabaseConstraintViolation violation = resolver.resolve("ERROR: value too long for type character varying(50)");
        assertEquals(DatabaseConstraintType.LENGTH_EXCEEDED, violation.getConstraintType());
        assertEquals("character varying", violation.getExpectedValueType());
        assertEquals(Long.valueOf(50), violation.getMaximumLength());
    }

    @Test
    public void testType() {

        DatabaseConstraintViolation violation = resolver.resolve(
                "ERROR: column \"name\" is of type integer but expression is of type character varying\nHint: You'll need to rewrite or cast the expression.");
        assertEquals(DatabaseConstraintType.INVALID_TYPE, violation.getConstraintType());
        assertEquals("name", violation.getColumnName());
        assertEquals("integer", violation.getExpectedValueType());
        assertEquals("character varying", violation.getValueType());
    }

    @Test
    public void testOther() {

        assertNull(resolver.resolve("unknown"));
    }

    @Test
    public void testFunction() {
        JpaSystemException exception = new JpaSystemException(
            new IllegalStateException("ERROR: My error message\n  Where: PL/pgSQL function fn_validate_table() line 42 at RAISE")
        );

        DatabaseConstraintViolation violation = resolver.resolve(exception);
        Assertions.assertEquals("My error message", violation.getValue());
        Assertions.assertEquals("fn_validate_table", violation.getConstraintName());
        Assertions.assertEquals(DatabaseConstraintType.CHECK_FAILED, violation.getConstraintType());
    }

}
