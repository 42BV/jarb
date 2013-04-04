package org.jarbframework.constraint.violation.resolver.vendor;

import static org.jarbframework.constraint.violation.DatabaseConstraintType.CHECK_FAILED;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.FOREIGN_KEY;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.INVALID_TYPE;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.LENGTH_EXCEEDED;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.NOT_NULL;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.UNIQUE_KEY;
import static org.jarbframework.constraint.violation.DatabaseConstraintViolation.violaton;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.resolver.RegexViolationResolver;

/**
 * Oracle based constraint violation resolver.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class OracleViolationResolver extends RegexViolationResolver {

    public OracleViolationResolver() {
        registerCheck();
        registerNotNull();
        registerUniqueKey();
        registerForeignKey();
        registerLengthExceeded();
        registerInvalidType();
    }

    private void registerCheck() {
        registerPattern("(.+): check constraint \\((.+)\\.(.+)\\) violated\n", new DatabaseConstraintViolationBuilder() {
            
            @Override
            public DatabaseConstraintViolation build(VariableAccessor variables) {
                return violaton(CHECK_FAILED)
                        .number(variables.get(1))
                        .constraint(variables.get(3))
                            .build();
            }
            
        });
    }

    private void registerNotNull() {
        registerPattern("(.+): cannot insert NULL into \\(\"(.+)\"\\.\"(.+)\"\\.\"(.+)\"\\)\n", new DatabaseConstraintViolationBuilder() {
            
            @Override
            public DatabaseConstraintViolation build(VariableAccessor variables) {
                return violaton(NOT_NULL)
                        .number(variables.get(1))
                        .table(variables.get(3))
                        .column(variables.get(4))
                            .build();
            }
            
        });
    }

    private void registerUniqueKey() {
        registerPattern("(.+): unique constraint \\((.+)\\.(.+)\\) violated\n", new DatabaseConstraintViolationBuilder() {
            
            @Override
            public DatabaseConstraintViolation build(VariableAccessor variables) {
                return violaton(UNIQUE_KEY)
                        .number(variables.get(1))
                        .constraint(variables.get(3))
                            .build();
            }
            
        });
    }

    private void registerForeignKey() {
        registerPattern("(.+): integrity constraint \\((.+)\\.(.+)\\) violated - child record found\n", new DatabaseConstraintViolationBuilder() {
            
            @Override
            public DatabaseConstraintViolation build(VariableAccessor variables) {
                return violaton(FOREIGN_KEY)
                        .number(variables.get(1))
                        .constraint(variables.get(3))
                            .build();
            }
            
        });
    }

    private void registerLengthExceeded() {
        registerPattern("(.+): value too large for column \"(.+)\"\\.\"(.+)\"\\.\"(.+)\" \\(actual: (\\d+), maximum: (\\d+)\\)\n", new DatabaseConstraintViolationBuilder() {
            
            @Override
            public DatabaseConstraintViolation build(VariableAccessor variables) {
                return violaton(LENGTH_EXCEEDED)
                        .number(variables.get(1))
                        .table(variables.get(3))
                        .column(variables.get(4))
                        .maximumLength(variables.get(6))
                            .build();
            }
            
        });
    }

    private void registerInvalidType() {
        registerPattern("(.+): invalid (.+)\n", new DatabaseConstraintViolationBuilder() {
            
            @Override
            public DatabaseConstraintViolation build(VariableAccessor variables) {
                return violaton(INVALID_TYPE)
                        .number(variables.get(1))
                        .expectedValueType(variables.get(2))
                            .build();
            }
            
        });
    }

}
