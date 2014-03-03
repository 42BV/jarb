package org.jarbframework.constraint.violation.resolver.vendor;

import static org.jarbframework.constraint.violation.DatabaseConstraintType.FOREIGN_KEY;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.INVALID_TYPE;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.LENGTH_EXCEEDED;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.NOT_NULL;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.UNIQUE_KEY;
import static org.jarbframework.constraint.violation.DatabaseConstraintViolation.builder;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.resolver.PatternViolationResolver;
import org.jarbframework.utils.DatabaseProduct;

/**
 * Hypersonic 2 based constraint violation resolver.
 * 
 * @author Jeroen van Schagen
 * @since 03-04-2013
 */
public class H2ViolationResolver extends PatternViolationResolver implements DatabaseProductSpecific {

    private static final String REGEX_SUFFIX = " SQL statement:\n(.*) \\[(.*)\\]";
    
    public H2ViolationResolver() {
        register(new NotNullPattern());
        register(new UniqueKeyPattern());
        register(new ForeignKeyPattern());
        register(new LengthPattern());
        register(new InvalidTypePattern());
    }

    @Override
    public boolean supports(DatabaseProduct product) {
        return "H2".equals(product.getName());
    }

    private static class NotNullPattern extends ViolationPattern {
        
        public NotNullPattern() {
            super("NULL not allowed for column \"(.+)\";" + REGEX_SUFFIX);
        }
        
        @Override
        public DatabaseConstraintViolation build(VariableAccessor variables) {
            return builder(NOT_NULL)
                    .column(variables.get(1).toLowerCase())
                    .statement(variables.get(2).trim())
                    .number(variables.get(3).trim())
                        .build();
        }
        
    }

    private static class UniqueKeyPattern extends ViolationPattern {
        
        public UniqueKeyPattern() {
            super("Unique index or primary key violation: \"(\\w+)_INDEX_\\d+ ON (.+)\\.(.+)\\((.+)\\)\";" + REGEX_SUFFIX);
        }
        
        @Override
        public DatabaseConstraintViolation build(VariableAccessor variables) {
            return builder(UNIQUE_KEY)
                    .constraint(variables.get(1).toLowerCase())
                    .table(variables.get(3).toLowerCase())
                    .column(variables.get(4).toLowerCase())
                    .statement(variables.get(5).trim())
                    .number(variables.get(6).trim())
                        .build();
        }
        
    }
    
    private static class ForeignKeyPattern extends ViolationPattern {

        public ForeignKeyPattern() {
            super("Referential integrity constraint violation: \"(.+): (.+)\\.(.+) FOREIGN KEY\\((.+)\\) REFERENCES (.+)\\.(.+)\\((.+)\\) \\((.+)\\)\";" + REGEX_SUFFIX);
        }
        
        @Override
        public DatabaseConstraintViolation build(VariableAccessor variables) {
            return builder(FOREIGN_KEY)
                    .constraint(variables.get(1).toLowerCase())
                    .table(variables.get(3).toLowerCase())
                    .column(variables.get(4).toLowerCase())
                    .referencingTable(variables.get(6).toLowerCase())
                    .referencingColumn(variables.get(7).toLowerCase())
                    .value(variables.get(8))
                    .statement(variables.get(9).trim())
                    .number(variables.get(10).trim())
                        .build();
        }
        
    }
    
    private static class LengthPattern extends ViolationPattern {
        
        public LengthPattern() {
            super("Value too long for column \"(.+) (.+)\\((.+)\\).*\": \"'(.+)' \\((.+)\\)\";" + REGEX_SUFFIX);
        }
        
        @Override
        public DatabaseConstraintViolation build(VariableAccessor variables) {
            return builder(LENGTH_EXCEEDED)
                    .column(variables.get(1).toLowerCase())
                    .valueType(variables.get(2).toLowerCase())
                    .maximumLength(variables.get(3))
                    .value(variables.get(4))
                    .statement(variables.get(6).trim())
                    .number(variables.get(7).trim())
                        .build();
        }
        
    }
    
    private static class InvalidTypePattern extends ViolationPattern {
        
        public InvalidTypePattern() {
            super("Data conversion error converting \"'(.+)' \\((.+): (.+) (.+)\\)\";" + REGEX_SUFFIX);
        }
        
        @Override
        public DatabaseConstraintViolation build(VariableAccessor variables) {
            return builder(INVALID_TYPE)
                    .table(variables.get(2).toLowerCase())
                    .column(variables.get(3).toLowerCase())
                    .expectedValueType(variables.get(4).toLowerCase())
                    .statement(variables.get(5).trim())
                    .number(variables.get(6).trim())
                        .build();
        }
        
    }
    
}
