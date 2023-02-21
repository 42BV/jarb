package nl._42.jarb.constraint.violation.resolver.vendor;

import nl._42.jarb.constraint.violation.DatabaseConstraintViolation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class H2ViolationResolverTest {

    private final H2ViolationResolver resolver = new H2ViolationResolver();
    
    @Test
    public void testNotNull() {
        final String message = "NULL not allowed for column \"LICENSE_NUMBER\"; SQL statement:\n"
                + "insert into cars (id, active, license_number, price) values (default, ?, ?, ?) [23502-171]";
        
        DatabaseConstraintViolation violation = resolver.resolve(message);
        Assertions.assertNotNull(violation);
        Assertions.assertEquals("license_number", violation.getColumnName());
        Assertions.assertEquals("insert into cars (id, active, license_number, price) values (default, ?, ?, ?)", violation.getStatement());
        Assertions.assertEquals("23502-171", violation.getNumber());
    }

    @Test
    public void testUniqueKey() {
        final String message = "Unique index or primary key violation: \"PUBLIC.UK_CARS_LICENSE_NUMBER_INDEX_1 ON PUBLIC.CARS(LICENSE_NUMBER NULLS FIRST) VALUES ( /* 2 */ '123456' )\"; SQL statement:\n"
                + "insert into cars (id, license_number, owner_id, price) values (default, ?, ?, ?) [23505-214]";
        
        DatabaseConstraintViolation violation = resolver.resolve(message);
        Assertions.assertNotNull(violation);
        Assertions.assertEquals("uk_cars_license_number", violation.getConstraintName());
        Assertions.assertEquals("cars", violation.getTableName());
        Assertions.assertEquals("license_number", violation.getColumnName());
        Assertions.assertEquals("insert into cars (id, license_number, owner_id, price) values (default, ?, ?, ?)", violation.getStatement());
        Assertions.assertEquals("23505-214", violation.getNumber());
    }

    @Test
    public void testForeignKey() {
        final String message = "Referential integrity constraint violation: \"FK_CARS_OWNER: PUBLIC.CARS FOREIGN KEY(OWNER_ID) REFERENCES PUBLIC.PERSONS(ID) (-1)\"; SQL statement:\n"
                + "insert into cars (id, active, license_number, owner_id, price) values (default, ?, ?, ?, ?) [23506-171]";
        
        DatabaseConstraintViolation violation = resolver.resolve(message);
        Assertions.assertNotNull(violation);
        Assertions.assertEquals("fk_cars_owner", violation.getConstraintName());
        Assertions.assertEquals("cars", violation.getTableName());
        Assertions.assertEquals("owner_id", violation.getColumnName());
        Assertions.assertEquals("persons", violation.getReferencingTableName());
        Assertions.assertEquals("id", violation.getReferencingColumnName());
        Assertions.assertEquals("-1", violation.getValue());
        Assertions.assertEquals("insert into cars (id, active, license_number, owner_id, price) values (default, ?, ?, ?, ?)", violation.getStatement());
        Assertions.assertEquals("23506-171", violation.getNumber());
    }

    @Test
    public void testLengthExceeded() {
        final String message = "Value too long for column \"LICENSE_NUMBER VARCHAR(6) NOT NULL\": \"'1234567' (7)\"; SQL statement:\n"
                + "insert into cars (id, active, license_number, price) values (default, ?, ?, ?) [22001-171]";
        
        DatabaseConstraintViolation violation = resolver.resolve(message);
        Assertions.assertNotNull(violation);
        Assertions.assertEquals("license_number", violation.getColumnName());
        Assertions.assertEquals("varchar", violation.getValueType());
        Assertions.assertEquals(Long.valueOf(6), violation.getMaximumLength());
        Assertions.assertEquals("insert into cars (id, active, license_number, price) values (default, ?, ?, ?)", violation.getStatement());
        Assertions.assertEquals("22001-171", violation.getNumber());
    }

    @Test
    public void testInvalidType() {
        final String message = "Data conversion error converting \"'Not a boolean' (CARS: ACTIVE BOOLEAN)\"; SQL statement:\n" +
        		"insert into cars (id, active, license_number, owner_id, price) values (default, ?, ?, ?, ?) -- (, ?1, ?2, ?3, ?4) [22018-171]";

        DatabaseConstraintViolation violation = resolver.resolve(message);
        Assertions.assertNotNull(violation);
        Assertions.assertEquals("cars", violation.getTableName());
        Assertions.assertEquals("active", violation.getColumnName());
        Assertions.assertEquals("boolean", violation.getExpectedValueType());
        Assertions.assertEquals("insert into cars (id, active, license_number, owner_id, price) values (default, ?, ?, ?, ?) -- (, ?1, ?2, ?3, ?4)", violation.getStatement());
        Assertions.assertEquals("22018-171", violation.getNumber());
    }
    
}
