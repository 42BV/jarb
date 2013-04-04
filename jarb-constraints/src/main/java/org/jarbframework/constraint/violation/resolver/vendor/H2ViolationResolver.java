package org.jarbframework.constraint.violation.resolver.vendor;

import static org.jarbframework.constraint.violation.DatabaseConstraintType.FOREIGN_KEY;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.INVALID_TYPE;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.LENGTH_EXCEEDED;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.NOT_NULL;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.UNIQUE_KEY;
import static org.jarbframework.constraint.violation.DatabaseConstraintViolation.violaton;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * NULL not allowed for column "LICENSE_NUMBER"; SQL statement:
     * insert into cars (id, active, license_number, price) values (default, ?, ?, ?) [23502-171]
     */
    private static final Pattern CANNOT_BE_NULL_PATTERN = Pattern.compile("NULL not allowed for column \"(.+)\"; SQL statement:(.*) \\[(.*)\\]");

    /*
     * Unique index or primary key violation: "UK_CARS_LICENSE_NUMBER_INDEX_1 ON PUBLIC.CARS(LICENSE_NUMBER)"; SQL statement:
     * insert into cars (id, active, license_number, price) values (default, ?, ?, ?) [23505-171]
     */
    private static final Pattern UNIQUE_VIOLATION_PATTERN = Pattern.compile("Unique index or primary key violation: \"(.+)(_INDEX_\\d+)? ON (.+).(.+)\\((.+)\\)\"; SQL statement:(.*) \\[(.*)\\]");

    /*
     * Referential integrity constraint violation: "FK_CARS_OWNER: PUBLIC.CARS FOREIGN KEY(OWNER_ID) REFERENCES PUBLIC.PERSONS(ID) (-1)"; SQL statement:
     * insert into cars (id, active, license_number, owner_id, price) values (default, ?, ?, ?, ?) [23506-171]
     */
    private static final Pattern FK_VIOLATION_PATTERN = Pattern.compile("Referential integrity constraint violation: \"(.+): (.+).(.+) FOREIGN KEY\\((.+)\\) REFERENCES (.+).(.+)\\((.+)\\) \\((.+)\\)\"; SQL statement:(.*) \\[(.*)\\]");

    /*
     * Value too long for column "LICENSE_NUMBER VARCHAR(6) NOT NULL": "'1234567' (7)"; SQL statement:
     * insert into cars (id, active, license_number, price) values (default, ?, ?, ?) [22001-171]
     */
    private static final Pattern LENGTH_EXCEEDED_PATTERN = Pattern.compile("Value too long for column \"(.+) (.+)\\((\\d+)\\)\": \"'(.+)' \\((.+)\\)\"; SQL statement:(.*) \\[(.*)\\]");

    /*
     * Data conversion error converting "'not a boolean' (CARS: ACTIVE BOOLEAN)"; SQL statement:
     * insert into cars (id, active, license_number, price) values (default, ?, ?, ?) -- (, ?1, ?2, ?3) [22018-171]
     */
    private static final Pattern INVALID_TYPE_PATTERN = Pattern.compile("Data conversion error converting \"'(.+)' \\((.+): (.+)\\)\"; SQL statement:(.*) \\[(.*)\\]");
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DatabaseConstraintViolation resolveByMessage(String message) {
        DatabaseConstraintViolation violation = null;
        
        Matcher matcher;
        if ((matcher = CANNOT_BE_NULL_PATTERN.matcher(message)).matches()) {
            violation = resolveNotNullViolation(matcher);
        } else if ((matcher = UNIQUE_VIOLATION_PATTERN.matcher(message)).matches()) {
            violation = resolveUniqueViolation(matcher);
        } else if ((matcher = FK_VIOLATION_PATTERN.matcher(message)).matches()) {
            violation = resolveForeignKeyViolation(matcher);
        } else if ((matcher = LENGTH_EXCEEDED_PATTERN.matcher(message)).matches()) {
            violation = resolveLengthViolation(matcher);
        } else if ((matcher = INVALID_TYPE_PATTERN.matcher(message)).matches()) {
            violation = resolveTypeViolation(matcher);
        }
        
        return violation;
    }

    private DatabaseConstraintViolation resolveNotNullViolation(Matcher matcher) {
        return violaton(NOT_NULL)
                .column(matcher.group(1).toLowerCase())
                .statement(matcher.group(2).trim())
                .number(matcher.group(3).trim())
                    .build();
    }

    private DatabaseConstraintViolation resolveUniqueViolation(Matcher matcher) {
        return violaton(UNIQUE_KEY)
                .constraint(matcher.group(1).toLowerCase())
                .table(matcher.group(4).toLowerCase())
                .column(matcher.group(5).toLowerCase())
                .statement(matcher.group(6).trim())
                .number(matcher.group(7).trim())
                    .build();
    }

    private DatabaseConstraintViolation resolveForeignKeyViolation(Matcher matcher) {
        return violaton(FOREIGN_KEY)
                .constraint(matcher.group(1).toLowerCase())
                .table(matcher.group(3).toLowerCase())
                .column(matcher.group(4).toLowerCase())
                .referencingTable(matcher.group(6).toLowerCase())
                .referencingColumn(matcher.group(7).toLowerCase())
                .value(matcher.group(8))
                .statement(matcher.group(9).trim())
                .number(matcher.group(10).trim())
                    .build();
    }

    private DatabaseConstraintViolation resolveLengthViolation(Matcher matcher) {
        return violaton(LENGTH_EXCEEDED)
                .column(matcher.group(1).toLowerCase())
                .valueType(matcher.group(2))
                .maximumLength(matcher.group(3))
                .value(matcher.group(4))
                .statement(matcher.group(6).trim())
                .number(matcher.group(7).trim())
                    .build();
    }
    
    private DatabaseConstraintViolation resolveTypeViolation(Matcher matcher) {
        return violaton(INVALID_TYPE)
                .table(matcher.group(2).toLowerCase())
                .valueType(matcher.group(3))
                .statement(matcher.group(4).trim())
                .number(matcher.group(5).trim())
                    .build();
    }

}
