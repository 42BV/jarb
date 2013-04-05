package org.jarbframework.constraint.violation.resolver.vendor;

import org.jarbframework.constraint.violation.resolver.recognize.DatabaseProduct;

public interface DatabaseProductSpecific {
    
    boolean supports(DatabaseProduct product);
    
}
