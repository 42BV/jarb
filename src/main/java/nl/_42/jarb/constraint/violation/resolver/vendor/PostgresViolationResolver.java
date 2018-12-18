package nl._42.jarb.constraint.violation.resolver.vendor;

import static nl._42.jarb.constraint.violation.DatabaseConstraintType.CHECK_FAILED;
import static nl._42.jarb.constraint.violation.DatabaseConstraintType.FOREIGN_KEY;
import static nl._42.jarb.constraint.violation.DatabaseConstraintType.INVALID_TYPE;
import static nl._42.jarb.constraint.violation.DatabaseConstraintType.LENGTH_EXCEEDED;
import static nl._42.jarb.constraint.violation.DatabaseConstraintType.NOT_NULL;
import static nl._42.jarb.constraint.violation.DatabaseConstraintType.UNIQUE_KEY;
import static nl._42.jarb.constraint.violation.DatabaseConstraintViolation.builder;

import nl._42.jarb.constraint.violation.DatabaseConstraintViolation;
import nl._42.jarb.constraint.violation.resolver.PatternViolationResolver;
import nl._42.jarb.utils.jdbc.DatabaseProduct;
import nl._42.jarb.utils.jdbc.DatabaseProductSpecific;
import nl._42.jarb.utils.jdbc.DatabaseProductType;

/**
 * PostgreSQL based constraint violation resolver.
 *
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class PostgresViolationResolver extends PatternViolationResolver implements DatabaseProductSpecific {

    public PostgresViolationResolver() {
        register(new CheckPattern());
        register(new NotNullPattern());
        register(new UniqueKeyPattern());
        register(new ForeignKeyNotPresentPattern());
        register(new ForeignKeyStillReferencedPattern());
        register(new LengthPattern());
        register(new InvalidTypePattern());
    }

    @Override
    public boolean supports(DatabaseProduct product) {
        return DatabaseProductType.PSQL.equals(product.getType());
    }

    private static class CheckPattern extends ViolationPattern {
        
        public CheckPattern() {
            super("ERROR: new row for relation \"(.+)\" violates check constraint \"(.+)\"");
        }
        
        @Override
        public DatabaseConstraintViolation build(VariableAccessor variables) {
            return builder(CHECK_FAILED)
                    .constraint(variables.get(2))
                    .table(variables.get(1))
                        .build();
        }
        
    }
    
    private static class NotNullPattern extends ViolationPattern {
        
        public NotNullPattern() {
            super("ERROR: null value in column \"(.+)\" violates not-null constraint");
        }
        
        @Override
        public DatabaseConstraintViolation build(VariableAccessor variables) {
            return builder(NOT_NULL).column(variables.get(1)).build();
        }
        
    }
    
    private static class UniqueKeyPattern extends ViolationPattern {
        
        public UniqueKeyPattern() {
            super("ERROR: duplicate key value violates unique constraint \"(.+)\"\\s+"
                    + "Detail: Key \\((.+)\\)=\\((.+)\\) already exists\\.");
        }
        
        @Override
        public DatabaseConstraintViolation build(VariableAccessor variables) {
            return builder(UNIQUE_KEY)
                    .constraint(variables.get(1))
                    .column(variables.get(2))
                    .value(variables.get(3))
                        .build();
        }
        
    }
    
    private static class ForeignKeyNotPresentPattern extends ViolationPattern {
        
        public ForeignKeyNotPresentPattern() {
            super("ERROR: insert or update on table \"(.+)\" violates foreign key constraint \"(.+)\"\\s+"
                    + "Detail: Key \\((.+)\\)=\\((.+)\\) is not present in table \"(.+)\"\\.");
        }
        
        @Override
        public DatabaseConstraintViolation build(VariableAccessor variables) {
            return builder(FOREIGN_KEY)
                    .table(variables.get(1))
                    .constraint(variables.get(2))
                    .column(variables.get(3))
                    .referencingTable(variables.get(5))
                    .value(variables.get(4))
                        .build();
        }
        
    }
    
    private static class ForeignKeyStillReferencedPattern extends ViolationPattern {
        
        public ForeignKeyStillReferencedPattern() {
            super("ERROR: update or delete on table \"(.+)\" violates foreign key constraint \"(.+)\" on table \"(.+)\"\\s+"
                    + "Detail: Key \\((.+)\\)=\\((.+)\\) is still referenced from table \".+\"\\.");
        }
        
        @Override
        public DatabaseConstraintViolation build(VariableAccessor variables) {
            return builder(FOREIGN_KEY)
                    .table(variables.get(1))
                    .constraint(variables.get(2))
                    .referencingTable(variables.get(3))
                    .column(variables.get(4))
                    .value(variables.get(5))
                        .build();
        }
        
    }
    
    private static class LengthPattern extends ViolationPattern {
        
        public LengthPattern() {
            super("ERROR: value too long for type (.+)\\((.+)\\)");
        }
        
        @Override
        public DatabaseConstraintViolation build(VariableAccessor variables) {
            return builder(LENGTH_EXCEEDED).expectedValueType(variables.get(1)).maximumLength(variables.get(2)).build();
        }

    }
    
    private static class InvalidTypePattern extends ViolationPattern {
        
        public InvalidTypePattern() {
            super("ERROR: column \"(.+)\" is of type (.+) but expression is of type (.+)\\nHint: .*");
        }
        
        @Override
        public DatabaseConstraintViolation build(VariableAccessor variables) {
            return builder(INVALID_TYPE).column(variables.get(1)).expectedValueType(variables.get(2)).valueType(variables.get(3)).build();
        }

    }

}
