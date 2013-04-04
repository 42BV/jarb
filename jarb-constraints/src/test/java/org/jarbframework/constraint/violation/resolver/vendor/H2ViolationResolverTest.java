package org.jarbframework.constraint.violation.resolver.vendor;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.junit.Assert;
import org.junit.Test;

public class H2ViolationResolverTest {

    private final H2ViolationResolver resolver = new H2ViolationResolver();
    
    @Test
    public void testNotNull() {
        final String message = "NULL not allowed for column \"LICENSE_NUMBER\"; SQL statement:" +
                               "insert into cars (id, active, license_number, price) values (default, ?, ?, ?) [23502-171]";
        
        DatabaseConstraintViolation violation = resolver.resolve(message);
        Assert.assertNotNull("Could not resolve violation.", violation);
        Assert.assertEquals("license_number", violation.getColumnName());
        Assert.assertEquals("insert into cars (id, active, license_number, price) values (default, ?, ?, ?)", violation.getStatement());
        Assert.assertEquals("23502-171", violation.getNumber());
    }

    @Test
    public void testUniqueKey() {
        final String message = "Unique index or primary key violation: \"UK_CARS_LICENSE_NUMBER_INDEX_1 ON PUBLIC.CARS(LICENSE_NUMBER)\"; SQL statement:" +
                               "insert into cars (id, active, license_number, price) values (default, ?, ?, ?) [23505-171]";
        
        DatabaseConstraintViolation violation = resolver.resolve(message);
        Assert.assertNotNull("Could not resolve violation.", violation);
        Assert.assertEquals("uk_cars_license_number", violation.getConstraintName());
        Assert.assertEquals("cars", violation.getTableName());
        Assert.assertEquals("license_number", violation.getColumnName());
        Assert.assertEquals("insert into cars (id, active, license_number, price) values (default, ?, ?, ?)", violation.getStatement());
        Assert.assertEquals("23505-171", violation.getNumber());
    }

    @Test
    public void testForeignKey() {
        final String message = "Referential integrity constraint violation: \"FK_CARS_OWNER: PUBLIC.CARS FOREIGN KEY(OWNER_ID) REFERENCES PUBLIC.PERSONS(ID) (-1)\"; SQL statement:" +
                               "insert into cars (id, active, license_number, owner_id, price) values (default, ?, ?, ?, ?) [23506-171]";
        
        DatabaseConstraintViolation violation = resolver.resolve(message);
        Assert.assertNotNull("Could not resolve violation.", violation);
        Assert.assertEquals("fk_cars_owner", violation.getConstraintName());
        Assert.assertEquals("cars", violation.getTableName());
        Assert.assertEquals("owner_id", violation.getColumnName());
        Assert.assertEquals("persons", violation.getReferencingTableName());
        Assert.assertEquals("id", violation.getReferencingColumnName());
        Assert.assertEquals("-1", violation.getValue());
        Assert.assertEquals("insert into cars (id, active, license_number, owner_id, price) values (default, ?, ?, ?, ?)", violation.getStatement());
        Assert.assertEquals("23506-171", violation.getNumber());
    }

    @Test
    public void testLengthExceeded() {
        final String message = "Value too long for column \"LICENSE_NUMBER VARCHAR(6) NOT NULL\": \"'1234567' (7)\"; SQL statement:" +
                               "insert into cars (id, active, license_number, price) values (default, ?, ?, ?) [22001-171]";
        
        DatabaseConstraintViolation violation = resolver.resolve(message);
        Assert.assertNotNull("Could not resolve violation.", violation);
        Assert.assertEquals("license_number", violation.getColumnName());
        Assert.assertEquals("varchar", violation.getValueType());
        Assert.assertEquals(Long.valueOf(6), violation.getMaximumLength());
        Assert.assertEquals("insert into cars (id, active, license_number, price) values (default, ?, ?, ?)", violation.getStatement());
        Assert.assertEquals("22001-171", violation.getNumber());
    }

    @Test
    public void testInvalidType() {
        final String message = "Data conversion error converting \"'not a boolean' (CARS: ACTIVE BOOLEAN)\"; SQL statement:" +
                               "insert into cars (id, active, license_number, price) values (default, ?, ?, ?) -- (, ?1, ?2, ?3) [22018-171]";
        
        DatabaseConstraintViolation violation = resolver.resolve(message);
        Assert.assertNotNull("Could not resolve violation.", violation);
        Assert.assertEquals("cars", violation.getTableName());
        Assert.assertEquals("active", violation.getColumnName());
        Assert.assertEquals("boolean", violation.getExpectedValueType());
        Assert.assertEquals("insert into cars (id, active, license_number, price) values (default, ?, ?, ?) -- (, ?1, ?2, ?3)", violation.getStatement());
        Assert.assertEquals("22018-171", violation.getNumber());
    }
    
}
