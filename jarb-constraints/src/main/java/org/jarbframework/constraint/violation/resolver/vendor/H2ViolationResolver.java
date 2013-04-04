package org.jarbframework.constraint.violation.resolver.vendor;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.resolver.ViolationMessageResolver;

/**
 * H2 based constraint violation resolver.
 * 
 * @author Jeroen van Schagen
 * @since 03-04-2013
 */
public class H2ViolationResolver implements ViolationMessageResolver {

    /*
        Data conversion error converting "'not a boolean' (CARS: ACTIVE BOOLEAN)"; SQL statement:
        insert into cars (id, active, license_number, price) values (default, ?, ?, ?) -- (, ?1, ?2, ?3) [22018-171]
        
        Value too long for column "LICENSE_NUMBER VARCHAR(6) NOT NULL": "'1234567' (7)"; SQL statement:
        insert into cars (id, active, license_number, price) values (default, ?, ?, ?) [22001-171]
        
        Unique index or primary key violation: "UK_CARS_LICENSE_NUMBER_INDEX_1 ON PUBLIC.CARS(LICENSE_NUMBER)"; SQL statement:
        insert into cars (id, active, license_number, price) values (default, ?, ?, ?) [23505-171]
        
        NULL not allowed for column "LICENSE_NUMBER"; SQL statement:
        insert into cars (id, active, license_number, price) values (default, ?, ?, ?) [23502-171]
     */
    
    @Override
    public DatabaseConstraintViolation resolveByMessage(String message) {
        // TODO: Create implementation
        return null;
    }

}
