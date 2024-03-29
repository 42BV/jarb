package nl._42.jarb.constraint.violation.resolver.vendor;

import nl._42.jarb.constraint.violation.DatabaseConstraintType;
import nl._42.jarb.constraint.violation.DatabaseConstraintViolation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
    public void testForeignKey() {
        DatabaseConstraintViolation violation = resolver.resolve("Cannot add or update a child row: a foreign key constraint fails (`flutweets`.`refs`, CONSTRAINT `refs_ibfk_1` FOREIGN KEY (`id`) REFERENCES `tweets` (`id`))");
        assertEquals(DatabaseConstraintType.FOREIGN_KEY, violation.getConstraintType());
        assertEquals("refs_ibfk_1", violation.getConstraintName());
        assertEquals("id", violation.getColumnName());
        assertEquals("tweets", violation.getReferencingTableName());
        assertEquals("id", violation.getReferencingColumnName());
    }
    
    @Test
    public void testOther() {
        assertNull(resolver.resolve("unknown"));
    }

}
