package org.jarbframework.constraint.violation.resolver.vendor;

import static org.jarbframework.constraint.violation.DatabaseConstraintType.CHECK_FAILED;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.FOREIGN_KEY;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.INVALID_TYPE;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.LENGTH_EXCEEDED;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.NOT_NULL;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.UNIQUE_KEY;
import static org.jarbframework.constraint.violation.DatabaseConstraintViolation.builder;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.resolver.PatternViolationResolver;
import org.jarbframework.utils.DatabaseProduct;
import org.jarbframework.utils.StringUtils;

/**
 * Oracle based constraint violation resolver.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class OracleViolationResolver extends PatternViolationResolver implements DatabaseProductSpecific {

    public OracleViolationResolver() {
        register(new CheckPattern());
        register(new NotNullPattern());
        register(new UniqueKeyPattern());
        register(new ForeignKeyPattern());
        register(new LengthPattern());
        register(new InvalidTypePattern());
    }
    
    @Override
    public boolean supports(DatabaseProduct product) {
        return StringUtils.startsWithIgnoreCase(product.getName(), "oracle");
    }

    private static class CheckPattern extends ViolationPattern {
        
        public CheckPattern() {
            super("(.+): check constraint \\((.+)\\.(.+)\\) violated\n");
        }
        
        @Override
        public DatabaseConstraintViolation build(VariableAccessor variables) {
            return builder(CHECK_FAILED)
                    .number(variables.get(1))
                    .constraint(variables.get(3))
                        .build();
        }
        
    }

    private static class NotNullPattern extends ViolationPattern {
        
        public NotNullPattern() {
            super("(.+): cannot insert NULL into \\(\"(.+)\"\\.\"(.+)\"\\.\"(.+)\"\\)\n");
        }
        
        @Override
        public DatabaseConstraintViolation build(VariableAccessor variables) {
            return builder(NOT_NULL)
                    .number(variables.get(1))
                    .table(variables.get(3))
                    .column(variables.get(4))
                        .build();
        }
        
    }

    private static class UniqueKeyPattern extends ViolationPattern {
        
        public UniqueKeyPattern() {
            super("(.+): unique constraint \\((.+)\\.(.+)\\) violated\n");
        }
        
        @Override
        public DatabaseConstraintViolation build(VariableAccessor variables) {
            return builder(UNIQUE_KEY)
                    .number(variables.get(1))
                    .constraint(variables.get(3))
                        .build();
        }
        
    }

    private static class ForeignKeyPattern extends ViolationPattern {
        
        public ForeignKeyPattern() {
            super("(.+): integrity constraint \\((.+)\\.(.+)\\) violated - child record found\n");
        }
        
        @Override
        public DatabaseConstraintViolation build(VariableAccessor variables) {
            return builder(FOREIGN_KEY)
                    .number(variables.get(1))
                    .constraint(variables.get(3))
                        .build();
        }
        
    }
    
    private static class LengthPattern extends ViolationPattern {
        
        public LengthPattern() {
            super("(.+): value too large for column \"(.+)\"\\.\"(.+)\"\\.\"(.+)\" \\(actual: (\\d+), maximum: (\\d+)\\)\n");
        }
        
        @Override
        public DatabaseConstraintViolation build(VariableAccessor variables) {
            return builder(LENGTH_EXCEEDED).number(variables.get(1)).table(variables.get(3)).column(variables.get(4)).maximumLength(variables.get(6)).build();
        }

    }

    private static class InvalidTypePattern extends ViolationPattern {
        
        public InvalidTypePattern() {
            super("(.+): invalid (.+)\n");
        }
        
        @Override
        public DatabaseConstraintViolation build(VariableAccessor variables) {
            return builder(INVALID_TYPE).number(variables.get(1)).expectedValueType(variables.get(2)).build();
        }

    }

}
