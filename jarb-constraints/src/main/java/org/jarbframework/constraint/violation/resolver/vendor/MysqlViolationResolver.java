package org.jarbframework.constraint.violation.resolver.vendor;

import static org.jarbframework.constraint.violation.DatabaseConstraintType.FOREIGN_KEY;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.INVALID_TYPE;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.LENGTH_EXCEEDED;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.NOT_NULL;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.UNIQUE_KEY;
import static org.jarbframework.constraint.violation.DatabaseConstraintViolation.builder;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.resolver.PatternViolationResolver;
import org.jarbframework.utils.jdbc.DatabaseProduct;
import org.jarbframework.utils.jdbc.DatabaseProductSpecific;
import org.jarbframework.utils.jdbc.DatabaseProductType;

/**
 * MySQL based constraint violation resolver.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class MysqlViolationResolver extends PatternViolationResolver implements DatabaseProductSpecific {

    public MysqlViolationResolver() {
        register(new NotNullPattern());
        register(new UniqueKeyPattern());
        register(new LengthPattern());
        register(new InvalidTypePattern());
        register(new ForeignKeyPattern());
    }

    @Override
    public boolean supports(DatabaseProduct product) {
        return DatabaseProductType.MYSQL.equals(product.getType());
    }

    private static class NotNullPattern extends ViolationPattern {
        
        public NotNullPattern() {
            super("Column '(.+)' cannot be null");
        }
        
        @Override
        public DatabaseConstraintViolation build(VariableAccessor variables) {
            return builder(NOT_NULL).column(variables.get(1)).build();
        }
        
    }

    private static class UniqueKeyPattern extends ViolationPattern {
        
        public UniqueKeyPattern() {
            super("Duplicate entry '(.+)' for key '(.+)'");
        }
        
        @Override
        public DatabaseConstraintViolation build(VariableAccessor variables) {
            return builder(UNIQUE_KEY).value(variables.get(1)).constraint(variables.get(2)).build();
        }
        
    }
    
    private static class LengthPattern extends ViolationPattern {
        
        public LengthPattern() {
            super("Data truncation: Data too long for column '(.+)' at row (\\d+)");
        }
        
        @Override
        public DatabaseConstraintViolation build(VariableAccessor variables) {
            return builder(LENGTH_EXCEEDED).column(variables.get(1)).build();
        }
        
    }

    private static class InvalidTypePattern extends ViolationPattern {
        
        public InvalidTypePattern() {
            super("Incorrect (\\w+) value: '(.+)' for column '(.+)' at row (\\d+)");
        }
        
        @Override
        public DatabaseConstraintViolation build(VariableAccessor variables) {
            return builder(INVALID_TYPE)
                    .expectedValueType(variables.get(1))
                    .value(variables.get(2))
                    .column(variables.get(3))
                        .build();
        }
        
    }
    
    private static class ForeignKeyPattern extends ViolationPattern {
        
        public ForeignKeyPattern() {
            super(
                    "Cannot add or update a child row: a foreign key constraint fails \\(`(.+)`.`(.+)`, CONSTRAINT `(.+)` FOREIGN KEY \\(`(.+)`\\) REFERENCES `(.+)` \\(`(.+)`\\)\\)");
        }
        
        @Override
        public DatabaseConstraintViolation build(VariableAccessor variables) {
            return builder(FOREIGN_KEY)
                    .constraint(variables.get(3))
                    .column(variables.get(4))
                    .referencingTable(variables.get(5))
                    .referencingColumn(variables.get(6))
                        .build();
        }
        
    }

}
