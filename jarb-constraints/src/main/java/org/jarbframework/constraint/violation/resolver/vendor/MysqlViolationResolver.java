package org.jarbframework.constraint.violation.resolver.vendor;

import static org.jarbframework.constraint.violation.DatabaseConstraintType.INVALID_TYPE;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.LENGTH_EXCEEDED;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.NOT_NULL;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.UNIQUE_KEY;
import static org.jarbframework.constraint.violation.DatabaseConstraintViolation.violaton;

import org.apache.commons.lang3.StringUtils;
import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.resolver.product.DatabaseProduct;
import org.jarbframework.constraint.violation.resolver.vendor.ViolationMessagePatterns.VariableAccessor;
import org.jarbframework.constraint.violation.resolver.vendor.ViolationMessagePatterns.ViolationBuilder;

/**
 * MySQL based constraint violation resolver.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class MysqlViolationResolver extends VendorViolationResolver {

    public MysqlViolationResolver() {
        registerNotNull();
        registerUniqueKey();        
        registerLengthExceeded();
        registerInvalidType();
    }
    
    private void registerNotNull() {
        registerPattern("Column '(.+)' cannot be null", new ViolationBuilder() {
            
            @Override
            public DatabaseConstraintViolation build(VariableAccessor variables) {
                return violaton(NOT_NULL).column(variables.get(1)).build();
            }
            
        });
    }
    
    private void registerUniqueKey() {
        registerPattern("Duplicate entry '(.+)' for key '(.+)'", new ViolationBuilder() {
            
            @Override
            public DatabaseConstraintViolation build(VariableAccessor variables) {
                return violaton(UNIQUE_KEY).value(variables.get(1)).constraint(variables.get(2)).build();
            }
            
        });
    }

    private void registerLengthExceeded() {
        registerPattern("Data truncation: Data too long for column '(.+)' at row (\\d+)", new ViolationBuilder() {
            
            @Override
            public DatabaseConstraintViolation build(VariableAccessor variables) {
                return violaton(LENGTH_EXCEEDED).column(variables.get(1)).build();
            }
            
        });
    }

    private void registerInvalidType() {
        registerPattern("Incorrect (\\w+) value: '(.+)' for column '(.+)' at row (\\d+)", new ViolationBuilder() {
            
            @Override
            public DatabaseConstraintViolation build(VariableAccessor variables) {
                return violaton(INVALID_TYPE)
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
