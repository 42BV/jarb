package org.jarbframework.constraint.violation.resolver.vendor;

import static org.jarbframework.constraint.violation.DatabaseConstraintType.INVALID_TYPE;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.LENGTH_EXCEEDED;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.NOT_NULL;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.UNIQUE_KEY;
import static org.jarbframework.constraint.violation.DatabaseConstraintViolation.builder;

import org.apache.commons.lang3.StringUtils;
import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.resolver.PatternViolationResolver;
import org.jarbframework.utils.DatabaseProduct;

/**
 * MySQL based constraint violation resolver.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class MysqlViolationResolver extends PatternViolationResolver implements DatabaseProductSpecific {

    public MysqlViolationResolver() {
        registerNotNull();
        registerUniqueKey();        
        registerLengthExceeded();
        registerInvalidType();
    }
    
    private void registerNotNull() {
        register("Column '(.+)' cannot be null", new ViolationBuilder() {
            
            @Override
            public DatabaseConstraintViolation build(VariableAccessor variables) {
                return builder(NOT_NULL).column(variables.get(1)).build();
            }
            
        });
    }
    
    private void registerUniqueKey() {
        register("Duplicate entry '(.+)' for key '(.+)'", new ViolationBuilder() {
            
            @Override
            public DatabaseConstraintViolation build(VariableAccessor variables) {
                return builder(UNIQUE_KEY).value(variables.get(1)).constraint(variables.get(2)).build();
            }
            
        });
    }

    private void registerLengthExceeded() {
        register("Data truncation: Data too long for column '(.+)' at row (\\d+)", new ViolationBuilder() {
            
            @Override
            public DatabaseConstraintViolation build(VariableAccessor variables) {
                return builder(LENGTH_EXCEEDED).column(variables.get(1)).build();
            }
            
        });
    }

    private void registerInvalidType() {
        register("Incorrect (\\w+) value: '(.+)' for column '(.+)' at row (\\d+)", new ViolationBuilder() {
            
            @Override
            public DatabaseConstraintViolation build(VariableAccessor variables) {
                return builder(INVALID_TYPE)
                        .expectedValueType(variables.get(1))
                        .value(variables.get(2))
                        .column(variables.get(3))
                            .build();
            }
            
        });
    }
    
    @Override
    public boolean supports(DatabaseProduct product) {
        return StringUtils.startsWithIgnoreCase(product.getName(), "mysql");
    }

}
