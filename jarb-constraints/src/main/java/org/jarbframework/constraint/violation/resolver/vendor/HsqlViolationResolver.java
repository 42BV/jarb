package org.jarbframework.constraint.violation.resolver.vendor;

import static org.jarbframework.constraint.violation.DatabaseConstraintType.FOREIGN_KEY;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.INVALID_TYPE;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.LENGTH_EXCEEDED;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.NOT_NULL;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.UNIQUE_KEY;
import static org.jarbframework.constraint.violation.DatabaseConstraintViolation.violaton;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.resolver.RegexViolationResolver;

/**
 * Hypersonic SQL based constraint violation resolver.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class HsqlViolationResolver extends RegexViolationResolver {

    public HsqlViolationResolver() {
        registerNotNull();
        registerUniqueKey();
        registerForeignKey();
        registerLengthExceeded();
        registerInvalidType();
    }

    private void registerNotNull() {
        registerPattern("integrity constraint violation: NOT NULL check constraint; (.+) table: (.+) column: (.+)", new DatabaseConstraintViolationBuilder() {
            
            @Override
            public DatabaseConstraintViolation build(VariableAccessor variables) {
                return violaton(NOT_NULL)
                            .constraint(variables.get(1).toLowerCase())
                            .table(variables.get(2).toLowerCase())
                            .column(variables.get(3).toLowerCase())
                                .build();
            }
            
        });
    }

    private void registerUniqueKey() {
        registerPattern("integrity constraint violation: unique constraint or index violation; (.+) table: (.+)", new DatabaseConstraintViolationBuilder() {
            
            @Override
            public DatabaseConstraintViolation build(VariableAccessor variables) {
                return violaton(UNIQUE_KEY)
                            .constraint(variables.get(1).toLowerCase())
                            .table(variables.get(2).toLowerCase())
                                .build();
            }
            
        });
    }

    private void registerForeignKey() {
        registerPattern("integrity constraint violation: foreign key no \\w+; (.+) table: (.+)", new DatabaseConstraintViolationBuilder() {
            
            @Override
            public DatabaseConstraintViolation build(VariableAccessor variables) {
                return violaton(FOREIGN_KEY)
                        .constraint(variables.get(1).toLowerCase())
                        .table(variables.get(2).toLowerCase())
                            .build();
            }
            
        });
    }

    private void registerLengthExceeded() {
        registerPattern("data exception: (.+) data, right truncation", new DatabaseConstraintViolationBuilder() {
            
            @Override
            public DatabaseConstraintViolation build(VariableAccessor variables) {
                return violaton(LENGTH_EXCEEDED).valueType(variables.get(1).toLowerCase()).build();
            }
            
        });
    }

    private void registerInvalidType() {
        registerPattern("data exception: invalid (.+) value for cast", new DatabaseConstraintViolationBuilder() {
            
            @Override
            public DatabaseConstraintViolation build(VariableAccessor variables) {
                return violaton(INVALID_TYPE).valueType(variables.get(1).toLowerCase()).build();
            }
            
        });
    }

}
