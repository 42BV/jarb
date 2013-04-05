package org.jarbframework.constraint.violation.resolver.vendor;

import static org.jarbframework.constraint.violation.DatabaseConstraintType.CHECK_FAILED;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.FOREIGN_KEY;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.INVALID_TYPE;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.LENGTH_EXCEEDED;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.NOT_NULL;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.UNIQUE_KEY;
import static org.jarbframework.constraint.violation.DatabaseConstraintViolation.violaton;

import org.apache.commons.lang3.StringUtils;
import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.resolver.recognize.DatabaseProduct;
import org.jarbframework.constraint.violation.resolver.vendor.ViolationMessagePatterns.VariableAccessor;
import org.jarbframework.constraint.violation.resolver.vendor.ViolationMessagePatterns.ViolationBuilder;

/**
 * PostgreSQL based constraint violation resolver.
 *
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class PostgresViolationResolver extends VendorViolationResolver {

    public PostgresViolationResolver() {
        registerNotNull();
        registerLengthExceeded();
        registerForeignKey();
        registerUniqueKey();
        registerCheck();
        registerInvalidType();
    }

    private void registerNotNull() {
        registerPattern("ERROR: null value in column \"(.+)\" violates not-null constraint", new ViolationBuilder() {
            
            @Override
            public DatabaseConstraintViolation build(VariableAccessor variables) {
                return violaton(NOT_NULL).column(variables.get(1)).build();
            }
            
        });
    }

    private void registerLengthExceeded() {
        registerPattern("ERROR: value too long for type (.+)\\((.+)\\)", new ViolationBuilder() {
            
            @Override
            public DatabaseConstraintViolation build(VariableAccessor variables) {
                return violaton(LENGTH_EXCEEDED)
                        .expectedValueType(variables.get(1))
                        .maximumLength(variables.get(2))
                            .build();
            }
            
        });
    }

    private void registerForeignKey() {
        registerPattern(
                "ERROR: insert or update on table \"(.+)\" violates foreign key constraint \"(.+)\"\\s+"
                + "Detail: Key \\((.+)\\)=\\((.+)\\) is not present in table \"(.+)\"\\.", new ViolationBuilder() {
                    
                    @Override
                    public DatabaseConstraintViolation build(VariableAccessor variables) {
                        return violaton(FOREIGN_KEY)
                                .table(variables.get(1))
                                .constraint(variables.get(2))
                                .column(variables.get(3))
                                .referencingTable(variables.get(5))
                                .value(variables.get(4))
                                    .build();
                    }
                    
                });
        
        registerPattern(
                "ERROR: update or delete on table \"(.+)\" violates foreign key constraint \"(.+)\" on table \"(.+)\"\\s+"
                + "Detail: Key \\((.+)\\)=\\((.+)\\) is still referenced from table \".+\"\\.", new ViolationBuilder() {
                    
                    @Override
                    public DatabaseConstraintViolation build(VariableAccessor variables) {
                        return violaton(FOREIGN_KEY)
                                .table(variables.get(1))
                                .constraint(variables.get(2))
                                .referencingTable(variables.get(3))
                                .column(variables.get(4))
                                .value(variables.get(5))
                                    .build();
                    }
                    
                });
    }

    private void registerUniqueKey() {
        registerPattern(
                "ERROR: duplicate key value violates unique constraint \"(.+)\"\\s+"
                + "Detail: Key \\((.+)\\)=\\((.+)\\) already exists\\.", new ViolationBuilder() {
            
            @Override
            public DatabaseConstraintViolation build(VariableAccessor variables) {
                return violaton(UNIQUE_KEY)
                        .constraint(variables.get(1))
                        .column(variables.get(2))
                        .value(variables.get(3))
                            .build();
            }
            
        });
    }

    private void registerCheck() {
        registerPattern("ERROR: new row for relation \"(.+)\" violates check constraint \"(.+)\"", new ViolationBuilder() {
            
            @Override
            public DatabaseConstraintViolation build(VariableAccessor variables) {
                return violaton(CHECK_FAILED)
                        .constraint(variables.get(2))
                        .table(variables.get(1))
                            .build();
            }
            
        });
    }

    private void registerInvalidType() {
        registerPattern("ERROR: column \"(.+)\" is of type (.+) but expression is of type (.+)\\nHint: .*", new ViolationBuilder() {
            
            @Override
            public DatabaseConstraintViolation build(VariableAccessor variables) {
                return violaton(INVALID_TYPE)
                        .column(variables.get(1))
                        .expectedValueType(variables.get(2))
                        .valueType(variables.get(3))
                            .build();
            }
            
        });
    }
    
    @Override
    public boolean supports(DatabaseProduct product) {
        return StringUtils.startsWithIgnoreCase(product.getName(), "postgres");
    }

}
