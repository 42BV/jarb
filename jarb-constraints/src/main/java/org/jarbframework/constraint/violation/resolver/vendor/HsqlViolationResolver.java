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
 * Hypersonic SQL based constraint violation resolver.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class HsqlViolationResolver extends PatternViolationResolver implements DatabaseProductSpecific {

    public HsqlViolationResolver() {
        register(new NotNullPattern());
        register(new UniqueKeyPattern());
        register(new ForeignKeyPattern());
        register(new LengthPattern());
        register(new InvalidTypePattern());
    }
    
    @Override
    public boolean supports(DatabaseProduct product) {
        return "HSQL Database Engine".equals(product.getName());
    }

    private static class NotNullPattern extends ViolationPattern {
        
        public NotNullPattern() {
            super("integrity constraint violation: NOT NULL check constraint; (.+) table: (.+) column: (.+)");
        }
        
        @Override
        public DatabaseConstraintViolation build(VariableAccessor variables) {
            return builder(NOT_NULL)
                        .constraint(variables.get(1).toLowerCase())
                        .table(variables.get(2).toLowerCase())
                        .column(variables.get(3).toLowerCase())
                            .build();
        }
        
    }

    private static class UniqueKeyPattern extends ViolationPattern {
        
        public UniqueKeyPattern() {
            super("integrity constraint violation: unique constraint or index violation; (.+) table: (.+)");
        }
        
        @Override
        public DatabaseConstraintViolation build(VariableAccessor variables) {
            return builder(UNIQUE_KEY)
                        .constraint(variables.get(1).toLowerCase())
                        .table(variables.get(2).toLowerCase())
                            .build();
        }

    }

    private static class ForeignKeyPattern extends ViolationPattern {
        
        public ForeignKeyPattern() {
            super("integrity constraint violation: foreign key no \\w+; (.+) table: (.+)");
        }

        @Override
        public DatabaseConstraintViolation build(VariableAccessor variables) {
            return builder(FOREIGN_KEY)
                    .constraint(variables.get(1).toLowerCase())
                    .table(variables.get(2).toLowerCase())
                        .build();
        }

    }
    
    private static class LengthPattern extends ViolationPattern {
        
        public LengthPattern() {
            super("data exception: (.+) data, right truncation");
        }

        @Override
        public DatabaseConstraintViolation build(VariableAccessor variables) {
            return builder(LENGTH_EXCEEDED).valueType(variables.get(1).toLowerCase()).build();
        }

    }
    
    private static class InvalidTypePattern extends ViolationPattern {
        
        public InvalidTypePattern() {
            super("data exception: invalid (.+) value for cast");
        }

        @Override
        public DatabaseConstraintViolation build(VariableAccessor variables) {
            return builder(INVALID_TYPE).valueType(variables.get(1).toLowerCase()).build();
        }

    }

}
